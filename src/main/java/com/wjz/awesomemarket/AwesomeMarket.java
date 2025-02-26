package com.wjz.awesomemarket;

import com.wjz.awesomemarket.command.MyTabCompleter;
import com.wjz.awesomemarket.listener.*;
import com.wjz.awesomemarket.sql.Mysql;
import com.wjz.awesomemarket.utils.*;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AwesomeMarket extends JavaPlugin {
    private static AwesomeMarket pluginInstance = null;

    //启用插件时
    @Override
    public void onEnable() {
        pluginInstance = this;//设置实例

        //下面这里先保存默认配置
        saveDefaultConfig();//保存config.yml到插件文件夹。如果已有则不做任何事
        FileConfiguration config = getConfig();//获取文件夹中的config.yml

        //初始化各种类
        Log.Initialize();

        getLogger().info("use Locale_" + config.getString("language") + ".yml");
        Log.info("load_plugin");//插件载入输出

        //配置指令
        CommandHandler.handleCommand(this);

        //注册tab补全器
        getCommand("awesomemarket").setTabCompleter(new MyTabCompleter());

        //尝试连接数据库
        Mysql.setConfig(config.getConfigurationSection("mysql-data-base"));
        Mysql.tryToConnect();
        //注册Vault插件
        if (!VaultTools.setupEconomy()) {//如果没有Vault插件
            Log.severe("no_vault");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //注册PlayerPoints插件
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            CurrencyManager.ppAPI = PlayerPoints.getInstance().getAPI();
        } else {
            Log.severe("no_player_points");
            return;
        }
        //注册事件监听器
        Bukkit.getPluginManager().registerEvents(new MarketListener(),this);
        Bukkit.getPluginManager().registerEvents(new ConfirmListener(),this);
        Bukkit.getPluginManager().registerEvents(new StorageListener(),this);
        Bukkit.getPluginManager().registerEvents(new LoginListener(),this);
        Bukkit.getPluginManager().registerEvents(new TransactionListener(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Mysql.closeDataSource();
    }

    public static AwesomeMarket getInstance() {
        return pluginInstance;
    }

}


/**
 * §0	黑色	\u00A70
 * §1	深蓝色	\u00A71
 * §2	深绿色	\u00A72
 * §3	湖蓝色	\u00A73
 * §4	深红色	\u00A74
 * §5	紫色	\u00A75
 * §6	金色	\u00A76
 * §7	灰色	\u00A77
 * §8	深灰色	\u00A78
 * §9	蓝色	\u00A79
 * §a	绿色	\u00A7a
 * §b	天蓝色	\u00A7b
 * §c	红色	\u00A7c
 * §d	粉红色	\u00A7d
 * §e	黄色	\u00A7e
 * §f	白色	\u00A7f
 * §k	随机字符	\u00A7k
 * §l	粗体	\u00A7l
 * §m	删除线	\u00A7m
 * §n	下划线	\u00A7n
 * §o	斜体	\u00A7o
 * §r	重置	\u00A7r
 * \n	换行	\n
 */