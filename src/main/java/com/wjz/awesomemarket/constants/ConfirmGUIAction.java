package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.utils.Log;
import org.bukkit.configuration.file.FileConfiguration;

public enum ConfirmGUIAction {
    CONFIRM {
        @Override
        public void action(MarketItem marketItem) {

        }//确认购买

    }, CANCEL {//取消购买

        @Override
        public void action(MarketItem marketItem) {

        }
    };

    public abstract void action(MarketItem marketItem);

    public static ConfirmGUIAction getType(String type) {
        return ConfirmGUIAction.valueOf(type.toUpperCase());//返回实例
    }

    private static final FileConfiguration langConfig = Log.langConfig;
}
