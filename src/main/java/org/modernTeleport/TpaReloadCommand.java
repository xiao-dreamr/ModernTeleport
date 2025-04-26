package org.modernTeleport;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.ParametersAreNonnullByDefault;

public class TpaReloadCommand implements CommandExecutor {
    ModernTeleport plugin;

    public TpaReloadCommand(ModernTeleport _plugin){
        plugin = _plugin;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.SayToConsole(plugin.getConfig().getString("Plugin-Reloading"));
        sender.sendMessage(plugin.getConfig().getString("Plugin-Reloading"));
        try {
            plugin.reloadConfig();
        } catch (Exception e) {
            plugin.SayToConsole(plugin.getConfig().getString("Plugin-Reload-Failed"));
            sender.sendMessage(plugin.getConfig().getString("Plugin-Reload-Failed"));
            plugin.SayToConsole(ChatColor.RED + e.toString());
        }

        plugin.SayToConsole(plugin.getConfig().getString("Plugin-Reload-Successful"));
        sender.sendMessage(plugin.getConfig().getString("Plugin-Reload-Successful"));
        return true;
    }
}
