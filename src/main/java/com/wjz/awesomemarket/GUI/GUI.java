package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.inventoryHolder.ConfirmHolder;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.inventoryHolder.StorageHolder;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GUI {
    public static void openMarket(Player player, int page) {
        //播放声音
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 0.6f);
        player.openInventory(new MarketHolder(player,page).getInventory());
    }
    public static void openConfirm(Player player,MarketHolder marketHolder, int slot) {
        //播放声音
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 0.8f);
        player.openInventory(new ConfirmHolder(marketHolder,slot).getInventory());
    }
    public static void openStorage(Player player){
        player.playSound(player.getLocation(),Sound.BLOCK_CHEST_OPEN,1.0f,1.0f);
        player.openInventory(new StorageHolder(player,(MarketHolder) player.getOpenInventory().getTopInventory().getHolder()).getInventory());
    }
    public static void openStatistic(Player player, OfflinePlayer owner){
        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,1.0f,1.0f);
        player.openInventory(new )
    }
}