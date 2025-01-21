package com.wjz.awesomemarket.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CommandHandler {

    /**
     * 处理传入的指令
     * @param sender 指令发送者
     * @param command 指令
     * @param label 命令发送者实际输入的指令
     * @param args 命令后所跟的参数 如/test a b c
     * @return
     */
    public static boolean handleCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        String commandName=command.getName();//获取指令名
        if (commandName.equalsIgnoreCase("awesomemarket")) {//判断输入的指令
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c你必须是一名玩家才能使用该指令！");
                return true;
            }
            //已经判断是玩家了
            Player player = (Player) sender;
            player.sendMessage("§d指令执行成功辣");
            return true;
        }

        else if(commandName.equalsIgnoreCase("test")){//这是一个测试指令
            if(!(sender instanceof Player)) {
                sender.sendMessage("§c你必须是一名玩家才能使用该指令！");
                return true;
            }
            sender.sendMessage("§b准备打开GUI辣！");
            GUI.openMarket((Player) sender);
        }

        sender.sendMessage("§c指令执行错误！");
        return true;
    }
}
