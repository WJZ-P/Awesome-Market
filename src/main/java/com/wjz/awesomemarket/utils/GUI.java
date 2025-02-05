package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.inventoryHolder.ConfirmHolder;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GUI {

    public static void openMarket(Player player) {

        //播放声音
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 0.6f);
        player.openInventory(new MarketHolder().getInventory());
    }

    public static void openConfirm(Player player, ItemStack itemStack) {
        //播放声音
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        player.openInventory(new ConfirmHolder(itemStack).getInventory());
    }

}