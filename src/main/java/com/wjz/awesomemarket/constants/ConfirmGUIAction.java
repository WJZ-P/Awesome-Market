package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.utils.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public enum ConfirmGUIAction {
    CONFIRM {
        @Override
        public void action(Player player,MarketItem marketItem) {
            //确认购买
            if(marketItem.purchase(player)){

            }
        }

    }, CANCEL {
        @Override
        public void action(Player player,MarketItem marketItem) {
            //取消购买
        }
    };

    public abstract void action(Player player, MarketItem marketItem);

    public static ConfirmGUIAction getType(String type) {
        return ConfirmGUIAction.valueOf(type.toUpperCase());//返回实例
    }

    private static final FileConfiguration langConfig = Log.langConfig;
}
