package org.modernTeleport;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class TprCommand implements CommandExecutor {
    ModernTeleport plugin;

    public TprCommand(ModernTeleport _plugin){
        this.plugin=_plugin;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player Sender = (Player) sender;
        int randomRange = plugin.getConfig().getInt("Random-Range");
        Random random = new Random();
        int x = (int) (random.nextInt(2*randomRange-1)+Sender.getLocation().getX())-randomRange;
        int z = (int) (random.nextInt(2*randomRange-1)+Sender.getLocation().getZ())-randomRange;
        Location location = Sender.getWorld().getHighestBlockAt(x,z).getLocation();
        Sender.teleport(location.add(new Vector(0,1,0)));
        Sender.getWorld().playSound(Sender, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1,1);
        return true;
    }
}
