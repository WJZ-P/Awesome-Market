package com.wjz.awesomemarket.constants;

public enum StorageType {
    //购买等待领取的物品
    WAITING_FOR_CLAIM,
    //被下架的物品
    DELISTED,
    //物品被购买后，金额放在此处。
    RECEIPT;

    public static StorageType getType(String type) {
        return valueOf(type.toUpperCase());
    }
}
