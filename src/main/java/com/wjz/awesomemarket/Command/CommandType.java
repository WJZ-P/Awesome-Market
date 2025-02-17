package com.wjz.awesomemarket.Command;

import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.MarketTools;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
            //参数的第第二个就是玩家ID
            if (strings.length < 2) {
                sender.sendMessage(Log.getString("command.view.error.lack-of-name"));
            } else {
                String targetName = strings[1];
                OfflinePlayer player = Bukkit.getOfflinePlayer(targetName);
                if (!player.hasPlayedBefore()) {
                    sender.sendMessage(Log.getString("command.view.error.name-not-exist"));
                } else {
                    //给sender玩家打开这个人的背包
                    Player playerSender = (Player) sender;
                    playerSender.openInventory(new MarketHolder(playerSender, player.getName(), 1).getInventory());
                }
            }
        }
    };

    public abstract void execute(CommandSender sender, Command command, String s, String[] strings);

    public CommandType getType(String command) {
        return CommandType.valueOf(command.toUpperCase());
    }
}
