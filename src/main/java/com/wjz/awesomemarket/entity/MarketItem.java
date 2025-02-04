package com.wjz.awesomemarket.entity;

import com.wjz.awesomemarket.utils.MarketHolder;
import org.bukkit.inventory.ItemStack;

public class MarketItem {
    private ItemStack itemStack;

    private String currency;//货币类型
    private double price;//价格
    private long id;//id是数据库中的id。根据这个索引物品。


    MarketItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

}
