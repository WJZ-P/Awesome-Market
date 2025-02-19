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

    int isClaimed;

    public TransactionItem(ItemStack itemStack, long id, String seller, String buyer, long tradeTime, double price, PriceType priceType, int isClaimed) {
        this.itemStack = itemStack;
        this.id = id;
        this.seller = seller;
        this.buyer = buyer;
        this.tradeTime = tradeTime;
        this.price = price;
        this.priceType = priceType;
        this.isClaimed = isClaimed;
    }


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

    public int getIsClaimed() {
        return isClaimed;
    }
}
