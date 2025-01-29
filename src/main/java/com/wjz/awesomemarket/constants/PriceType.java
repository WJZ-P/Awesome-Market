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
        @Override
        public boolean take(Player player, double amount) {
            double playerBalance = economy.getBalance(player);
            if (playerBalance < amount) return false;
            economy.withdrawPlayer(player, amount);
            return true;
        }

        @Override
        public double look(Player player) {
            return economy.getBalance(player);
        }

        @Override
        public double calculateTax(double amount) {
            ConfigurationSection taxConfig = AwesomeMarket.getInstance().getConfig().
                    getConfigurationSection("currency.tax");
            return amount * taxConfig.getDouble("money");
        }
    },

    POINT {
        @Override
        public boolean take(Player player, double amount) {
            double playerBalance = ppAPI.look(player.getUniqueId());
            if (playerBalance < amount) return false;
            ppAPI.take(player.getUniqueId(), (int) amount);
            return true;
        }

        @Override
        public double look(Player player) {
            return ppAPI.look(player.getUniqueId());
        }

        @Override
        public double calculateTax(double price) {
            ConfigurationSection taxConfig = AwesomeMarket.getInstance().getConfig().
                    getConfigurationSection("currency.tax");
            return price * taxConfig.getDouble("point");
        }
    };

    public abstract double calculateTax(double price);
    public abstract double look(Player player);
    public abstract boolean take(Player player, double amount);

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
