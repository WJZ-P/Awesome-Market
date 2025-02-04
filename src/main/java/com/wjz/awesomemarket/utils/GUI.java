package com.wjz.awesomemarket.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GUI {

    public static void openMarket(Player player) {

        //播放声音
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 0.6f);
        player.openInventory(new MarketHolder().getInventory());
    }

}