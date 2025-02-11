package com.wjz.awesomemarket.utils.CommandExecutors;

import com.wjz.awesomemarket.utils.GUI;
import com.wjz.awesomemarket.utils.MarketTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 处理awesomemarket指令
 */
public class AwesomeMarketExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (command.getName().equalsIgnoreCase("awesomemarket")) {//判断输入的指令
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c你必须是一名玩家才能使用该指令！");
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
            switch (strings[0]) {
                //把物品上架到全球商店
                case "sell":
                    MarketTools.sellItems((Player) sender,strings);
                    break;
                case "buy":
                    MarketTools.buyItems((Player) sender,strings);
                    break;
            }
        }
        return false;
    }
}
