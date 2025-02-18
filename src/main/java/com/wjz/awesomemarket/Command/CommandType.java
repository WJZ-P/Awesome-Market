package com.wjz.awesomemarket.Command;

import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.inventoryHolder.StorageHolder;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.MarketTools;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum CommandType {
    SELL {
        @Override
        public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            MarketTools.sellItems((Player) sender, strings);
        }
    },
    VIEW {
        @Override
        public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            //view 有两个备选参数，分别是market和storage，分别代表查看市场物品和查看暂存库物品

            if (strings.length < 3) { //view market(storage) %name%
                sender.sendMessage(Log.getString("command.general.error.lack-of-params"));
                return;
            }

            switch (strings[1].toLowerCase()) {
                case "market" -> {  //处理市场指令
                    String targetName = strings[2];
                    OfflinePlayer player = Bukkit.getOfflinePlayer(targetName);
                    if (!player.hasPlayedBefore()) {
                        sender.sendMessage(Log.getString("command.general.error.name-not-exist"));
                    } else {
                        //给sender玩家打开这个人的背包
                        Player playerSender = (Player) sender;
                        playerSender.openInventory(new MarketHolder(player, playerSender, 1).getInventory());
                        playerSender.playSound(playerSender.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.2f);
                    }
                }
                case "storage" -> {
                    //处理暂存库指令
                    String targetName = strings[2];
                    OfflinePlayer player = Bukkit.getOfflinePlayer(targetName);
                    if (!player.hasPlayedBefore()) {
                        sender.sendMessage(Log.getString("command.general.error.name-not-exist"));
                    } else {
                        //给sender玩家打开这个人的暂存库
                        Player playerSender = (Player) sender;
                        playerSender.openInventory(new StorageHolder(player, playerSender, new MarketHolder(playerSender, 1)).getInventory());
                    }
                }
            }


        }
    },
    ;

    public abstract void execute(CommandSender sender, Command command, String s, String[] strings);

    public static CommandType getType(String command) {
        return CommandType.valueOf(command.toUpperCase());
    }
}
