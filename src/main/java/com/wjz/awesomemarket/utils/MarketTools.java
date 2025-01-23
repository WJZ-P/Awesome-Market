package com.wjz.awesomemarket.utils;

import org.bukkit.entity.Player;

public class MarketTools {
    /**
     * 把物品上架到全球市场
     * /amt sell money(point) amount days(上架期限)
     *
     * @param args
     */
    public static void sellItems(Player player, String[] args) {
        //先获取玩家手中的物品。
        player.getInventory().getItemInMainHand();
        //支付类型

    }

    /**
     * 从全球时长购买物品
     */
    public static void buyItems(Player player, String[] args) {

    }
}
