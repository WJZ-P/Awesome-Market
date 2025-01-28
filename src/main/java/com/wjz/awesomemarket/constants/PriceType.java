package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.utils.CurrencyManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static com.wjz.awesomemarket.utils.CurrencyManager.economy;
import static com.wjz.awesomemarket.utils.CurrencyManager.ppAPI;

public enum PriceType {
    MONEY {
        public boolean take(Player player, double price) {
            double playerBalance = economy.getBalance(player);
            if (playerBalance < price) return false;
            economy.withdrawPlayer(player, price);
            return true;
        }

        public double look(Player player) {
            return economy.getBalance(player);
        }

        public double calculateTax(double price) {
            ConfigurationSection taxConfig = AwesomeMarket.getInstance().getConfig().
                    getConfigurationSection("currency.tax");
            return price * taxConfig.getDouble("money");
        }
    },

    POINT {
        public boolean take(Player player, double price) {
            double playerBalance = ppAPI.look(player.getUniqueId());
            if (playerBalance < price) return false;
            ppAPI.take(player.getUniqueId(), (int) price);
            return true;
        }

        public double look(Player player) {
            return ppAPI.look(player.getUniqueId());
        }
        public double calculateTax(double price) {
            ConfigurationSection taxConfig = AwesomeMarket.getInstance().getConfig().
                    getConfigurationSection("currency.tax");
            return price * taxConfig.getDouble("point");
        }
    };

    /**
     * 根据货币类型计算税款
     * @param price
     * @return
     */
    public abstract double calculateTax(double price);

    public static PriceType getType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("货币类型不能为空");
        }
        return PriceType.valueOf(type);
    }

    public String getName() {
        return AwesomeMarket.getInstance().getConfig().getString("currency.name." + this.toString().toLowerCase());
    }

}
