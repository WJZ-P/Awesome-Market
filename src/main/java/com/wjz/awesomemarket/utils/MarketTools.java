package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.AwesomeMarket;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.Arrays;

import static com.wjz.awesomemarket.utils.CurrencyManager.economy;
import static com.wjz.awesomemarket.utils.CurrencyManager.ppAPI;

public class MarketTools {

    /**
     * 把物品上架到全球市场
     * /amt sell money(point) price
     *
     * @param args
     */
    public static void sellItems(Player player, String[] args) {
        if(args.length<3){
            player.sendMessage(Log.getString("args_error_sell"));
            return;
        }
        //支付类型
        String paymentType = args[1];
        //上架价格
        double price = Double.parseDouble(args[2]);

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

        //先获取玩家手中的物品。
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        //得判断手中有没有物品
        if(itemStack.isEmpty()){
            player.sendMessage(Log.getString("empty_item_error_sell"));
            return;
        }

        //上架到全球市场,下面准备数据
        String seller = player.getName();
        String itemDetail = serializeItem(itemStack);
        String itemType = String.valueOf(itemStack.getType());
        long onSellTime = Instant.now().getEpochSecond();
        int durationTime = AwesomeMarket.getInstance().getConfig().getInt("market-item-expiry");

        //根据配置进行收税
        ConfigurationSection taxConfig = AwesomeMarket.getInstance().getConfig().getConfigurationSection("tax");

        double tax = paymentType.equalsIgnoreCase("money")
                ? price * taxConfig.getDouble("money") : price * taxConfig.getDouble("point");
        //处理游戏币上架的逻辑
        if (paymentType.equalsIgnoreCase("money")) {
            double balanceMoney = economy.getBalance(player);//获取当前玩家的游戏币余额
            if (tax > balanceMoney) {
                player.sendMessage(String.format(Log.getString("pay_tax_fail"),balanceMoney,tax));
                return;
            }
            //从玩家账户扣除税款
            economy.withdrawPlayer(player, tax);
        }
        else {
            double balancePoint = ppAPI.look(player.getUniqueId());
            if(tax>balancePoint){
                player.sendMessage(String.format(Log.getString("pay_tax_fail"),balancePoint,tax));
                return;
            }
            //扣款
            ppAPI.take(player.getUniqueId(), (int) tax);
        }

        //把物品放到数据库
        Mysql.InsertItemsToMarket(itemDetail, itemType, seller, paymentType, price, onSellTime, onSellTime + (long) durationTime * 24 * 3600);
        //然后把玩家手中的物品清除
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        //上架成功，发送回馈消息。
        player.sendMessage(String.format(Log.getString("withdraw_tax"),
                tax, paymentType.equalsIgnoreCase("money") ? "元" : "点券"));

    }

    /**
     * 从全球市场购买物品
     */
    public static void buyItems(Player player, String[] args) {

    }

    private static String serializeItem(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", itemStack);
        return config.saveToString();
    }

    private static ItemStack deserializeItem(String itemDetail) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(itemDetail);
        } catch (Exception e) {
            return null;
        }
        return config.getItemStack("item", null);
    }


}
