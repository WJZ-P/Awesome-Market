package com.wjz.awesomemarket.utils;

import java.util.logging.Logger;

public class Log {
    public static Logger logger;
    public static String language;

    /**
     * 载入插件的log
     */
    public static void loadPlugin() {
        // Plugin startup logic
        switch (language) {
            case "zh":
                logger.info("""
                        §a==============================
                        §bAwesomeMarket 插件已启用!
                        §e欢迎使用 WJZ_P 的插件(✧ω✧)!
                        §e任何问题请联系 QQ1369727119
                        §a==============================
                        """);
                break;
            case "en":
                logger.info("""
                        §a==============================
                        §bAwesomeMarket plugin has been loaded!
                        §eWelcome to use plugin made by WJZ_P!(✧ω✧)!
                        §eAny question please contact 1369727119@qq.com or WJZ-P on github
                        §a==============================
                        """);
        }
    }
}
