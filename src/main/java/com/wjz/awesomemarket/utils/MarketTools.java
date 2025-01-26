package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.AwesomeMarket;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.Cleaner;
import java.time.Instant;

public class MarketTools {
    private static Economy economy = null;

    public static void setEconomy(Economy eco) {
        economy = eco;
    }

    /**
     * 把物品上架到全球市场
     * /amt sell money(point) price
     *
     * @param args
     */
    public static void sellItems(Player player, String[] args) {
        //支付类型
        String paymentType = args[2];
        //上架价格
        double price = Double.parseDouble(args[3]);

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
        //上架到全球市场,下面准备数据
        String seller = player.getName();
        String itemDetail = serializeItem(itemStack);
        String itemType = String.valueOf(itemStack.getType());
        long onSellTime = Instant.now().getEpochSecond();
        int durationTime = AwesomeMarket.getInstance().getConfig().getInt("market-item-expiry");

        //把物品放到数据库
        Mysql.InsertItemsToMarket(itemDetail, itemType, seller, paymentType, price, onSellTime, onSellTime + (long) durationTime * 24 * 3600);
        //然后把玩家手中的物品清除
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        //根据配置进行收税
        ConfigurationSection taxConfig = AwesomeMarket.getInstance().getConfig().getConfigurationSection("tax");

        double tax = paymentType.equalsIgnoreCase("money")
                ? price * taxConfig.getDouble("money") : price * taxConfig.getDouble("point");
        double balanceMoney =economy.getBalance(player);//获取当前玩家的游戏币余额
        double balancePoint=economy.getBalance(player,"券");

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
