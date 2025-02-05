package com.wjz.awesomemarket.entity;

import com.wjz.awesomemarket.constants.PriceType;
import org.bukkit.inventory.ItemStack;

public class MarketItem {
    private ItemStack itemStack;

    private double price;//价格
    private PriceType priceType;//价格类型
    private long id;//id是数据库中的id。根据这个索引物品。


    public MarketItem(ItemStack itemStack, double price, PriceType priceType, long id) {
        this.itemStack = itemStack;
        this.id=id;
        this.price=price;
    }

}
