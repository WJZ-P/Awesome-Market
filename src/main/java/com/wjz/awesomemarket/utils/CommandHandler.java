package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.Command.AwesomeMarketExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHandler {

    /**
     * 在这里进行插件所有指令的注册。
     * @return
     */
    public static void handleCommand(JavaPlugin myPlugin) {
        myPlugin.getCommand("awesomemarket").setExecutor(new AwesomeMarketExecutor());
    }
}
