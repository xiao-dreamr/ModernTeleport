package org.modernTeleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class HubCommand implements CommandExecutor {
    ModernTeleport plugin;

    public HubCommand(ModernTeleport plugin) {
        this.plugin = plugin;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.SayToConsole(plugin.getConfig().getString("Console-Cant-Use"));
            return false;
        }
        Player Sender = (Player) sender;
        Sender.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("Teleport-To-Hub")));
        // 若制定了主城，则返回主城；否则主世界出生点
        Location hubLocation =
                plugin.getConfig().getInt("Hub-Location.x")==0?
                Bukkit.getWorlds().getFirst().getSpawnLocation()
                :
                new Location(
                    Bukkit.getWorlds().getFirst(),
                    plugin.getConfig().getInt("Hub-Location.x"),
                    plugin.getConfig().getInt("Hub-Location.y"),
                    plugin.getConfig().getInt("Hub-Location.z"));

        Sender.teleport(hubLocation);
        Sender.getWorld().playSound(Sender, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1,1);
        return true;
    }
}
