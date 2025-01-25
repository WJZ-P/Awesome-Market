package com.wjz.awesomemarket.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketTools {
    /**
     * 把物品上架到全球市场
     * /amt sell money(point) amount days(上架期限)
     *
     * @param args
     */
    public static void sellItems(Player player, String[] args) {
        //支付类型
        String paymentType = args[2];
        //上架数量
        int amount = Integer.parseInt(args[3]);
        //上架时间
        int days = Integer.parseInt(args[4]);

        //先判断货币类型是否符合要求
        if (!(paymentType.equals("money") || paymentType.equals("point"))) {
            player.sendMessage(Log.getString("payment_type_error"));
        }

        //先获取玩家手中的物品。
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        //上架到全球市场

    }

    /**
     * 从全球市场购买物品
     */
    public static void buyItems(Player player, String[] args) {

    }

//    private static void Seri


}
