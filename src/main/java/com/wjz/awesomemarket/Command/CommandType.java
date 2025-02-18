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
                    //首先检查是否有权限
                    if (!sender.hasPermission("awesomemarket.market.lookOthers")) {
                        sender.sendMessage(Log.getString("command.general.error.no-permission"));
                        return;
                    }

                    String targetName = strings[2];
                    OfflinePlayer player = Bukkit.getOfflinePlayer(targetName);
                    if (!player.hasPlayedBefore()) {
                        sender.sendMessage(Log.getString("command.general.error.name-not-exist"));
                    } else {
                        //给sender玩家打开这个人的背包
                        Player playerSender = (Player) sender;
                        playerSender.openInventory(new MarketHolder(player, playerSender, 1).getInventory());
                        playerSender.playSound(playerSender.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
                    }
                }
                case "storage" -> {
                    //首先检查是否有权限
                    if (!sender.hasPermission("awesomemarket.storage.lookOthers")) {
                        sender.sendMessage(Log.getString("command.general.error.no-permission"));
                        return;
                    }

                    //处理暂存库指令
                    String targetName = strings[2];
                    OfflinePlayer player = Bukkit.getOfflinePlayer(targetName);
                    if (!player.hasPlayedBefore()) {
                        sender.sendMessage(Log.getString("command.general.error.name-not-exist"));
                    } else {
                        //给sender玩家打开这个人的暂存库
                        Player playerSender = (Player) sender;
                        playerSender.openInventory(new StorageHolder(player, playerSender, new MarketHolder(playerSender, 1)).getInventory());
                        playerSender.playSound(playerSender.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
                    }
                }
            }


        }
    },
    FIND {
        @Override
        public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            //先检查有没有权限
            if (!sender.hasPermission("awesomemarket.find")) {
                sender.sendMessage(Log.getString("command.general.error.no-permission"));
                return;
            }
            //创建一个新的市场容器，指定物品类型。
            Player player = (Player) sender;
            MarketHolder marketHolder = new MarketHolder(player, 1);
            marketHolder.setItemType(String.valueOf(player.getInventory().getItemInMainHand().getType()));
            marketHolder.reload();
            player.openInventory(marketHolder.getInventory());
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 0.8f);
        }
    };

    public abstract void execute(CommandSender sender, Command command, String s, String[] strings);

    public static CommandType getType(String command) {
        return CommandType.valueOf(command.toUpperCase());
    }
}
