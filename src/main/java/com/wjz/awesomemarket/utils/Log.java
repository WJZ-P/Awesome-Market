package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.AwesomeMarket;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class Log {
    private static Logger logger;
    public static FileConfiguration langConfig;

    /**
     * 初始化Log类
     */
    public static void Initialize() {
        //设置插件输出
        logger = AwesomeMarket.getInstance().getLogger();
        //设置插件的语言路径，不存在的话就创建
        String langFileName = "Locale_" + AwesomeMarket.getInstance().getConfig().getString("language") + ".yml";
        File langFile = new File(AwesomeMarket.getInstance().getDataFolder(), langFileName);
        if (!langFile.exists()) {
            //不存在，要创建语言文件
            langFile.getParentFile().mkdirs();//确保所有父目录是存在的
            AwesomeMarket.getInstance().saveResource(langFileName, false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public static void info(String name) {
        logger.info(langConfig.getString(name));
    }

    public static void severe(String name) {
        logger.severe(langConfig.getString(name));
    }

    public static void warning(String name) {
        logger.warning(langConfig.getString(name));
    }

    public static String getString(String name) {
        return langConfig.getString(name);
    }

    public static void infoDirectly(String info) {
        logger.info(info);
    }

    public static void severeDirectly(String severe) {
        logger.severe(severe);
    }

    public static List<String> getStringList(String path){
        return langConfig.getStringList(path);
    }
}
