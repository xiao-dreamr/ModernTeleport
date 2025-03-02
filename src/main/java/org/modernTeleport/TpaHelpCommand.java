package org.modernTeleport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TpaHelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("tpahelp指令还没写完~，但总之你输入的这条指令肯定有毛病");
        return false;
    }
}
