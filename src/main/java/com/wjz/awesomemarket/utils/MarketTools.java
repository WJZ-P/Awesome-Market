package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.constants.PriceType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;

/**
 * 和指令相关的处理类
 */
public class MarketTools {

    /**
     * 把物品上架到全球市场
     * /amt sell money(point) price
     *
     * @param args
     */
    public static void sellItems(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Log.getString("args_error_sell"));
            return;
        }
        //支付类型
        String paymentType = args[1];
        //上架价格
        double price = Double.parseDouble(args[2]);
        //先获取玩家手中的物品。
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        //上架时间

        //先判断货币类型是否符合要求
        if (!(paymentType.equalsIgnoreCase("money") || paymentType.equalsIgnoreCase("point"))) {
            player.sendMessage(Log.getString("payment_type_error"));
            return;
        }
        //再判断price是不是正常的
        if (price <= 0) {
            player.sendMessage(Log.getString("price_error"));
            return;
        }


        //得判断手中有没有物品
        if (itemStack.isEmpty()) {
            player.sendMessage(Log.getString("empty_item_error_sell"));
            return;
        }

        //上架到全球市场,下面准备数据
        String seller = player.getName();
        String itemDetail = serializeItem(itemStack);
        String itemType = String.valueOf(itemStack.getType());
        long onSellTime = Instant.now().getEpochSecond();
        int durationTime = AwesomeMarket.getInstance().getConfig().getInt("market-item-expiry");
        //计算货币类型
        PriceType priceType = PriceType.getType(paymentType);
        //收税
        double tax = priceType.calculateTax(price);
        //处理游戏币上架的逻辑
        double playerBalance = priceType.look(player);
        if (tax > playerBalance) {
            player.sendMessage(String.format(Log.getString("pay_tax_fail"), playerBalance, tax));
            return;
        }
        //扣款
        priceType.take(player, price);

        //把物品放到数据库
        Mysql.InsertItemsToMarket(itemDetail, itemType, seller, paymentType, price, onSellTime, onSellTime + (long) durationTime * 24 * 3600);
        //然后把玩家手中的物品清除
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        //上架成功，发送回馈消息。
        player.sendMessage(String.format(Log.getString("withdraw_tax"),
                tax, priceType.getName()));
        //还要发送声音
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.8f);

    }

    /**
     * 从全球市场购买物品
     */
    public static void buyItems(Player player, String[] args) {

    }

    public static String serializeItem(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", itemStack);
        return config.saveToString();
    }

    public static ItemStack deserializeItem(String itemDetail) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(itemDetail);
        } catch (Exception e) {
            return null;
        }
        return config.getItemStack("item", null);
    }


}
