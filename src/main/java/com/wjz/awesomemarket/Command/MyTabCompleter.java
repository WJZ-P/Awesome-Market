package com.wjz.awesomemarket.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTabCompleter implements TabCompleter {
    private static final String[] COMMANDS = Arrays.stream(CommandType.class.getEnumConstants())
            .map(enumConstant -> enumConstant.name().toLowerCase()).toArray(String[]::new);

    /**
     * 指令补全函数
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        //这里可以先对用户进行权限检查，暂时不写。

        List<String> completions = new ArrayList<>();//这个是用于返回的补全指令集合。

        String partialplugin;
        if (args.length == 1) {
            partialplugin = args[0];
            //这行生成基础指令的补全列表
            StringUtil.copyPartialMatches(partialplugin, Arrays.asList(COMMANDS), completions);

        } else if (args.length == 2) {
            //下面是第二个指令的补全列表

            String arg1 = args[0];
            if (arg1.equals("sell")) {
                StringUtil.copyPartialMatches(args[1], Arrays.asList("money", "point"), completions);
            } else if (arg1.equals("view")) {
                StringUtil.copyPartialMatches(args[1], Arrays.asList("market", "storage","transaction"), completions);
            }

        } else if (args.length == 3) {
            //指令长度为3时候的补全列表
            String arg2 = args[1];
            if (arg2.equalsIgnoreCase("market") || arg2.equalsIgnoreCase("storage")) {
                StringUtil.copyPartialMatches(args[2], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), completions);
            }
        }

        return completions;
    }
}
