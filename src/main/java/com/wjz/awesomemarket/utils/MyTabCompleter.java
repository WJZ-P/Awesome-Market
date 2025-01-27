package com.wjz.awesomemarket.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTabCompleter implements TabCompleter {
    private static final String[] COMMANDS = new String[]{"sell","buy"};


    /**
     * @param commandSender
     * @param command
     * @param s
     * @param strings
     * @return
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        //这里可以先对用户进行权限检查，暂时不写。

        List<String> completions = new ArrayList<>();//这个是用于返回的补全指令集合。

        String partialplugin;
        if (args.length == 1) {
            partialplugin = args[0];
            //这行生成补全列表
            StringUtil.copyPartialMatches(partialplugin, Arrays.asList(COMMANDS), completions);
        }
        else if(args.length==2){
            String arg1=args[0];
            if(arg1.equals("sell")){
                StringUtil.copyPartialMatches(args[1],Arrays.asList("money","point"),completions);
            }
        }

        return completions;
    }
}
