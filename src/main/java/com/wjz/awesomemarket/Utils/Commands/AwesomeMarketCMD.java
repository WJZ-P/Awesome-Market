package com.wjz.awesomemarket.Utils.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 处理awesomemarket指令
 */
public class AwesomeMarketCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (command.getName().equalsIgnoreCase("awesomemarket")) {//判断输入的指令
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c你必须是一名玩家才能使用该指令！");
                return true;
            }
            //已经判断是玩家了
            sender.sendMessage("§d指令执行成功辣");
            return true;
        }
        return false;
    }
}
