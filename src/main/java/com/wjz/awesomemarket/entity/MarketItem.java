package com.wjz.awesomemarket.entity;

import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.utils.Mysql;
import org.bukkit.entity.Player;
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

    /**
     * 购买商品，成功返回true失败返回false。
     */
    public boolean purchase(Player player){
        //要先判断玩家是否满足购买需求
        double playerEconomy=priceType.look(player);//查询玩家当前货币
        if(playerEconomy<price) {
            player.sendMessage();
        }

        if(Mysql.deleteMarketItem(this.id)){//成功删除了物品，那么要从玩家处扣款

        }
    }
}
