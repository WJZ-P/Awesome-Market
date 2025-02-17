package com.wjz.awesomemarket.Command;

import com.wjz.awesomemarket.utils.GUI;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.MarketTools;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 处理指令
 */
public class AwesomeMarketExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        //s是第一个词，command一般不用，strings是第一个词后面的所有参数

        if (command.getName().equalsIgnoreCase("awesomemarket")) {//判断输入的指令
            if (!(sender instanceof Player)) {
                sender.sendMessage(Log.getString("command.amt.error.not-player"));
                return true;
            }
            //已经判断是玩家了
            if (strings.length == 0)//说明没有额外的参数，就直接打开全球市场
            {
                //sender.sendMessage("§b准备打开GUI辣！");
                GUI.openMarket((Player) sender);
                //sender.sendMessage("§d指令执行成功辣");
                return true;
            }

            //下面说明有额外参数存在
            CommandType type = CommandType.valueOf(strings[0]);
            type.execute(sender, command, s, strings);
            return true;
        }
        return false;
    }
}
