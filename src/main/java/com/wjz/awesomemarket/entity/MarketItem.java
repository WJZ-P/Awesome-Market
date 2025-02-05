package com.wjz.awesomemarket.entity;

import com.wjz.awesomemarket.constants.PriceType;
import org.bukkit.inventory.ItemStack;

public class MarketItem {
    private ItemStack itemStack;
    private String seller;//出售者
    private double price;//价格
    private PriceType priceType;//价格类型
    private long id;//id是数据库中的id。根据这个索引物品。


    public MarketItem(ItemStack itemStack, String seller, double price, PriceType priceType, long id) {
        this.itemStack = itemStack;
        this.id = id;
        this.price = price;
        this.seller = seller;
        this.priceType=priceType;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public double getPrice() {
        return this.price;
    }

    public long getId() {
        return this.id;
    }

    public String getPriceTypeName() {
        return this.priceType.getName();
    }

    public String getSellerName() {
        return this.seller;
    }

}
