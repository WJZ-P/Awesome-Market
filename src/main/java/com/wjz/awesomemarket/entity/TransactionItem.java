package com.wjz.awesomemarket.entity;

import com.wjz.awesomemarket.constants.PriceType;
import org.bukkit.inventory.ItemStack;

public class TransactionItem {
    private ItemStack itemStack;
    long id;
    String seller;
    String buyer;
    long tradeTime;
    double price;
    PriceType priceType;

    public ItemStack getItemStack() {
        return itemStack;
    }

    public long getId() {
        return id;
    }

    public String getSeller() {
        return seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public long getTradeTime() {
        return tradeTime;
    }

    public double getPrice() {
        return price;
    }

    public PriceType getPriceType() {
        return priceType;
    }
}
