package org.modernTeleport;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import javax.annotation.ParametersAreNonnullByDefault;

public class TpaCommand implements TabExecutor {
    ModernTeleport plugin;
    final List<String> FIRST_COMMANDS = List.of(
            "go", "come", "refuse",
            "accept", "cancel", "auto", "ignore", "random");

    public TpaCommand(ModernTeleport plugin){
        this.plugin = plugin;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            // 控制台无法使用
            plugin.SayToConsole(plugin.getConfig().getString("Console-Cant-Use"));
            return false;
        }
        Player Sender = (Player) sender;
        Player Target;
        if(args.length<2){
            // 若args小于等于1，且如果有参数，则不能为random
            // 至少需要两个参数
            // 即/tpa [go | here | refuse | accept | cancel | ignore | auto] <player> (accept|refuse)
            Sender.performCommand("tpahelp");
            return false;
        } else if (Bukkit.getPlayer(args[1]) == null) {
            // 如果tpa的玩家不存在
            Sender.sendMessage(plugin.getConfig().getString("Player-Not-Found"));
            return false;
        }else{
            Target = Objects.requireNonNull(Bukkit.getPlayer(args[1]));
        }

        TeleportRequest request;
        switch (args[0]){
            case "go":
                // TODO: 是否允许一个玩家同时发出多个请求
                plugin.NewRequests.add(
                    new TeleportRequest(
                        Sender,
                        Target,
                        plugin.LIFETIME,
                        plugin.getConfig().getInt("Teleport-Delay"),
                        RequestType.Go));
                Sender.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString("Send-Go-Request"))
                            .replace("%player%", Target.getName())
                            .replace("%lifetime%",String.valueOf(plugin.LIFETIME)));
                Target.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString("Receive-Go-Request"))
                            .replace("%player%", Sender.getName())
                            .replace("%lifetime%",String.valueOf(plugin.LIFETIME)));
                // 播放提示音
                Target.getWorld().playSound(Target, Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
                if(plugin.getConfig().getBoolean("Shortcut-Shown")){
                    TextComponent acceptButton = new TextComponent("【接受请求】");
                    TextComponent refuseButton = new TextComponent("【拒绝请求】");
                    acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa accept "+Sender.getName()));
                    refuseButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa refuse "+Sender.getName()));
                    acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("点击接受"+Sender.getName()+"的传送请求")));
                    refuseButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("点击拒绝"+Sender.getName()+"的传送请求")));
                    acceptButton.setColor(ChatColor.DARK_GREEN);
                    refuseButton.setColor(ChatColor.RED);
                    Target.spigot().sendMessage(acceptButton);
                    Target.spigot().sendMessage(refuseButton);
                }
                return true;
            case "come":
                plugin.NewRequests.add(
                new TeleportRequest(
                        Sender,
                        Target,
                        plugin.LIFETIME,
                        plugin.getConfig().getInt("Teleport-Delay"),
                        RequestType.Come));
                Sender.getRespawnLocation();
                Sender.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString("Send-Come-Request"))
                                .replace("%player%", Target.getName())
                                .replace("%lifetime%",String.valueOf(plugin.LIFETIME)));
                Target.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString("Receive-Come-Request"))
                                .replace("%player%", Sender.getName())
                                .replace("%lifetime%",String.valueOf(plugin.LIFETIME)));
                // 播放提示音
                Target.getWorld().playSound(Target, Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
                if(plugin.getConfig().getBoolean("Shortcut-Shown")){
                    TextComponent acceptButton = new TextComponent("【接受请求】");
                    TextComponent refuseButton = new TextComponent("【拒绝请求】");
                    acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa accept "+Sender.getName()));
                    refuseButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa refuse "+Sender.getName()));
                    acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("点击接受"+Sender.getName()+"的传送请求")));
                    refuseButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("点击拒绝"+Sender.getName()+"的传送请求")));
                    acceptButton.setColor(ChatColor.DARK_GREEN);
                    refuseButton.setColor(ChatColor.RED);
                    Target.spigot().sendMessage(acceptButton);
                    Target.spigot().sendMessage(refuseButton);
                }
                return true;
            case "accept":
                request = plugin.Requests.stream()
                        .filter(r -> Sender.getName().equals(r.target.getName()))
                        .filter(r -> Target.getName().equals(r.requester.getName()))
                        .findAny()
                        .orElse(null);
                // 此处接受时，Sender -> 目标；Target -> 需求者
                if (request==null){
                    Sender.sendMessage(Objects.requireNonNull(plugin.getConfig().getString("Request-Not-Found")));
                    return false;
                }
                request.Wait();
                int delay = plugin.getConfig().getInt("Teleport-Delay");
                Sender.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString(delay==0?"Accept-Request-Without-Delay":"Accept-Request"))
                                .replace("%delay%",String.valueOf(plugin.getConfig().getInt("Teleport-Delay"))));
                Target.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString(delay==0?"Request-Be-Accepted-Without-Delay":"Request-Be-Accepted"))
                            .replace("%delay%",String.valueOf(plugin.getConfig().getInt("Teleport-Delay")))
                            .replace("%player%", Sender.getName()));
                // 播放提示音
                Target.getWorld().playSound(Target, Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
                return true;
            case "refuse":
                request = plugin.Requests.stream()
                        .filter(r -> Sender.getName().equals(r.target.getName()))
                        .filter(r -> Target.getName().equals(r.requester.getName()))
                        .findAny()
                        .orElse(null);
                if (request==null){
                    Sender.sendMessage(plugin.getConfig().getString("Request-Not-Found"));
                    return false;
                }
                request.Disable();
                Sender.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString("Refuse-Request"))
                                .replace("%player%",Target.getName()));
                Target.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString("Request-Be-Refused"))
                                .replace("%player%", Sender.getName()));
                // 播放提示音
                Target.getWorld().playSound(Target.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                return true;
            case "cancel":
                request = plugin.Requests.stream()
                        .filter(r -> Sender.getName().equals(r.requester.getName()))
                        .filter(r -> Target.getName().equals(r.target.getName()))
                        .findAny()
                        .orElse(null);
                // 此处取消时Sender -> 需求者；Target -> 目标
                if (request==null){
                    Sender.sendMessage(plugin.getConfig().getString("Request-Not-Found"));
                    return false;
                }
                request.Disable();
                Sender.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString("Cancel-Request"))
                                .replace("%player%",Target.getName()));
                Target.sendMessage(
                        Objects.requireNonNull(plugin.getConfig().getString("Request-Be-Canceled"))
                                .replace("%player%", Sender.getName()));
                // 播放提示音
                Target.getWorld().playSound(Target.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
                return true;
            default:
                Sender.performCommand("tpahelp");
                return false;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> OutArgs = new ArrayList<>();
        if (args.length == 1) {
            OutArgs.addAll(FIRST_COMMANDS.stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[0]))
                    .toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("go")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    OutArgs.add(player.getName());
                }
            } else if (args[0].equalsIgnoreCase("come")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    OutArgs.add(player.getName());
                }
            } else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("refuse")) {
                for (TeleportRequest request : plugin.Requests) {
                    if (request.target.getName().equals(sender.getName())) {
                        // 查找到与要接受的发送者和自己对应的TeleportRequest，并且不能为disabled
                        // 此处由于是接受，所以target->自己
                        if (request.status == RequestStatus.Disabled) {
                            continue;
                        }
                        OutArgs.add(request.requester.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("cancel")) {
                for (TeleportRequest request : plugin.Requests) {
                    if (request.requester.getName().equals(sender.getName())) {
                        // 查找到与要接受的发送者和自己对应的TeleportRequest，并且不能为disabled
                        // 此处由于是取消，所以requester->自己
                        if (request.status == RequestStatus.Disabled) {
                            continue;
                        }
                        OutArgs.add(request.target.getName());
                    }
                }
            }
            // 找到与玩家输入内容匹配的玩家
            return OutArgs.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[1]))
                .toList();
        }
        return OutArgs;
    }
}
