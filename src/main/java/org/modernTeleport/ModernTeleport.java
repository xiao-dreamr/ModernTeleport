package org.modernTeleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ModernTeleport extends JavaPlugin {
    final List<TeleportRequest> Requests = new ArrayList<>();
    final List<TeleportRequest> DeadRequests = new ArrayList<>();
    final List<TeleportRequest> NewRequests = new ArrayList<>();
    final int LIFETIME = this.getConfig().getInt("Request-Lifetime");
    @Override
    public void onEnable() {
        SayToConsole("插件已启动！");
        Objects.requireNonNull(Bukkit.getPluginCommand("tpa")).setExecutor(new TpaCommand(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("hub")).setExecutor(new HubCommand(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("tpr")).setExecutor(new TprCommand(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("tpareload")).setExecutor(new TpaReloadCommand(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("tpahelp")).setExecutor(new TpaHelpCommand());
        new TeleportRequestsUpdater(this).start();
        this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        SayToConsole("插件已关闭！");
    }

    /**
     * 向控制台发送已格式化的内容.
     */
    public void SayToConsole(String text){

        CommandSender consoleSender = Bukkit.getConsoleSender();
        consoleSender.sendMessage(ChatColor.RED+"[MT]"+ChatColor.GOLD+text);
    }

    public void DebugToConsole(String text){
        if(this.getConfig().getBoolean("Debug")){
            CommandSender consoleSender = Bukkit.getConsoleSender();
            consoleSender.sendMessage(ChatColor.YELLOW+"[MTDebug]"+ChatColor.GREEN+text);
        }
    }
}
