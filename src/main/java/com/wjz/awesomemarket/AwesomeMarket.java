package com.wjz.awesomemarket;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("awesomemarket")) {//判断输入的指令是什么
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c你必须是一名玩家！");
                return true;
            }
            //已经判断是玩家了
            Player player = (Player) sender;
            player.sendMessage("§d指令执行成功辣");
            return true;
        }
        sender.sendMessage("§c指令执行错误！");
        return true;
    }
}
