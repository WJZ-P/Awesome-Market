package com.wjz.awesomemarket.utils;

import java.util.logging.Logger;

public class Log {
    public static Logger logger;

    public static void loadPlugin() {
        // Plugin startup logic
        logger.info("§a==============================");
        logger.info("§bAwesomeMarket 插件已启用!");
        logger.info("§e欢迎使用 WJZ_P 的插件(✧ω✧)!");
        logger.info("§e任何问题请联系 QQ1369727119");
        logger.info("§a==============================");
    }
}
