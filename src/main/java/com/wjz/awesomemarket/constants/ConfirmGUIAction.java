package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.utils.Log;
import org.bukkit.configuration.file.FileConfiguration;

public enum ConfirmGUIAction {
    BUY {//购买

        @Override
        public String getTitle() {
            return langConfig.getString("confirm-GUI.buy.title");
        }

    };

    public abstract String getTitle();

    public static ConfirmGUIAction getType(String type) {
        return ConfirmGUIAction.valueOf(type.toUpperCase());//返回实例
    }

    private static final FileConfiguration langConfig = Log.langConfig;
}
