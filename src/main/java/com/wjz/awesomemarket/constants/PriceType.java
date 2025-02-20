package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.AwesomeMarket;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
        public boolean give(OfflinePlayer player, double amount) {
            return economy.depositPlayer(player,amount).transactionSuccess();
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

        public boolean give(OfflinePlayer player, double amount) {
            return ppAPI.give(player.getUniqueId(), (int) amount);
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
    },
    ALL {//所有货币类型。只需要重载toSQL方法即可。

        public double calculateTax(double price) {
            return 0;
        }

        public double look(Player player) {
            return 0;
        }

        public boolean take(Player player, double amount) {
            return false;
        }

        @Override
        public boolean give(OfflinePlayer player, double amount) {
            return false;
        }

        @Override
        public String toSQL() {
            return "";
        }
    };
    private static final PriceType[] VALUES = values();
    public abstract double calculateTax(double price);

    public abstract double look(Player player);

    public abstract boolean take(Player player, double amount);
    public abstract boolean give(OfflinePlayer player,double amount);

    public String toSQL() {
        return "AND payment = '" + this.name().toLowerCase() + "' ";
    }

    public static PriceType getType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("货币类型不能为空");
        }
        return PriceType.valueOf(type.toUpperCase());
    }

    public String getName() {
        return AwesomeMarket.getInstance().getConfig().getString("currency." + this.toString().toLowerCase());
    }

    public PriceType next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
}
