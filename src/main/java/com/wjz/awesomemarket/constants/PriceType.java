package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.AwesomeMarket;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public enum PriceType {
    MONEY{
        public boolean take(Player player){


            return false;
        }
    };
    POINT{};

    public static PriceType getType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("货币类型不能为空");
        }
        return PriceType.valueOf(type);
    }

    public String getName(){
        return AwesomeMarket.getInstance().getConfig().getString("currency.name."+this.toString().toLowerCase());
    }

}
