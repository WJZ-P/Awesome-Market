package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.AwesomeMarket;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public enum PriceType {
    MONEY, POINT;
    private static final Map<String, PriceType> MAPPINGS = new HashMap<>();

    static {
        //自动注册枚举类
        for (PriceType type : values()) {
            MAPPINGS.put(type.name().toLowerCase(), type);
        }
    }

    public static PriceType getType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("货币类型不能为空");
        }
        PriceType result = MAPPINGS.get(type.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("无效的货币类型: " + type
                    + "，支持类型: " + MAPPINGS.keySet());
        }

        return result;
    }

    public String getName(){
        return AwesomeMarket.getInstance().getConfig().getString("currency.name."+this.toString().toLowerCase());
    }

    public
}
