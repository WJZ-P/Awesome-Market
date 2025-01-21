package com.wjz.awesomemarket;

import com.wjz.awesomemarket.Utils.CommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AwesomeMarket extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("§a==============================");
        getLogger().info("§bAwesomeMarket 插件已启用!");
        getLogger().info("§e欢迎使用 WJZ_P 的插件(✧ω✧)!");
        getLogger().info("§e任何问题请联系 QQ1369727119");
        getLogger().info("§a==============================");

        //下面这里先保存默认配置
        saveDefaultConfig();//保存config.yml到插件文件夹。如果已有则不做任何事
        FileConfiguration config=getConfig();//获取文件夹中的插件
        getLogger().info("§b author:"+config.getString("author"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("§c==============================");
        getLogger().info("§bAwesomeMarket 插件已关闭!");
        getLogger().info("§e感谢使用(｡•̀ᴗ-)✧!");
        getLogger().info("§c==============================");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        //交给CommandHandler处理指令
        return CommandHandler.handleCommand(sender, command, label, args);
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