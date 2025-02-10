package com.wjz.awesomemarket.constants;

public class SkullType {
    public static String STORAGE_DATA="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc0ZWUxNTQyYzQ1NjNmZDZlN2Q3MmRlMjZlNzM3Y2YxOGZiZDA0Y2NhYjFiOGIyODM1M2RhODczNDhlY2ZiIn19fQ==";
    public static String CHECK_MARK_DATA="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=";
    public static String YELLOW_MARKET_DATA="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFiZjZhODM4NjYxZTE1ZGY2NjE5OTA5NDE2NWI3NTM4MzJjNzIzNDcxZDJiMjA0ZDRkNTA3NGFhNjA4Yzc4NCJ9fX0=";
    public static String CUSTOM_SKULL="item:\n" +
            "  ==: org.bukkit.inventory.ItemStack\n" +
            "  v: 4189\n" +
            "  type: PLAYER_HEAD\n" +
            "  meta:\n" +
            "    ==: ItemMeta\n" +
            "    meta-type: SKULL\n" +
            "    display-name: '{\"text\":\"Check Mark\",\"bold\":true,\"italic\":false,\"underlined\":true,\"color\":\"gold\"}'\n" +
            "    lore:\n" +
            "    - '{\"text\":\"Custom Head ID: 56787\",\"italic\":false,\"color\":\"gray\"}'\n" +
            "    - '{\"text\":\"www.minecraft-heads.com\",\"italic\":false,\"color\":\"blue\"}'\n" +
            "    skull-owner:\n" +
            "      ==: PlayerProfile\n" +
            "      uniqueId: %s\n" +
            "      properties:\n" +
            "      - name: textures\n" +
            "        value: %s\n";

}
