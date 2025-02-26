package com.wjz.awesomemarket.entity;

import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.StorageType;
import org.bukkit.inventory.ItemStack;

public class StorageItem {
    long id;
    ItemStack itemStack;
    String seller;
    long purchaseTime;
    double price;
    PriceType priceType;
    StorageType storageType;

    public StorageItem(long id, ItemStack itemStack, String seller, long purchaseTime, double price,PriceType priceType,StorageType storageType){
        this.id=id;
        this.itemStack=itemStack;
        this.seller=seller;
        this.purchaseTime=purchaseTime;
        this.price=price;
        this.priceType=priceType;
        this.storageType=storageType;
    }
    public ItemStack getItemStack(){return itemStack;}
    public String getSeller(){return seller;}
    public long getPurchaseTime(){return purchaseTime;}
    public double getPrice(){return price;}
    public PriceType getPriceType(){return priceType;}
    public long getId(){return id;}
    public StorageType getStorageType(){return storageType;}
}
