package com.wjz.awesomemarket.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.wjz.awesomemarket.constants.SkullType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UsefulTools {
    //1.20.5版本和以上获取自定义头颅的方法
    public static ItemStack getCustomSkull(String textureValue) {

        //说明是新版
        if (isVersionNewerThan("1.20.4")) {
            ItemStack skull = MarketTools.deserializeItem(String.format(SkullType.CUSTOM_SKULL, UUID.randomUUID(), textureValue));
            return skull;
        } else {//旧版获取方法
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            // **更严谨的方式获取 SkullMeta，使用 Bukkit.getItemFactory() 创建**
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            // **添加判空检查，确保 meta 对象不为 null**
            if (meta != null) {
                //使用反射设置texture
                try {
                    Field profileField = meta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);

                    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                    profile.getProperties().put("textures", new Property("textures", textureValue));

                    profileField.set(meta, profile);
                    head.setItemMeta(meta); // 设置修改后的 meta
                } catch (Exception e) {
                    e.printStackTrace(); // 异常处理，打印堆栈信息
                    return null; // **重要：反射设置失败时，返回 null 或者一个默认物品，避免后续代码继续使用可能出错的 head 对象**
                }
                return head; // **正常情况下返回 head**
            } else {
                // **如果 meta 为 null，记录警告信息，并返回 null 或者一个默认物品**
                return new ItemStack(Material.PLAYER_HEAD); // **返回 null，表示创建头颅失败**
            }
        }
    }

    public static ItemStack createNavItemStack(ItemStack navItem, String action, String name, List<String> lore, NamespacedKey key) {
        ItemMeta meta = navItem.getItemMeta();

        //设置基础属性
        meta.setDisplayName(ChatColor.RESET + name);
        meta.setLore(lore);

        //添加NBT标识
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, action);
        //隐藏默认属性
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        navItem.setItemMeta(meta);
        return navItem;
    }

    //获取物品名称，返回翻译键或者


    //版本比较方法
    public static boolean isVersionNewerThan(String targetVersion) {
        //获取当前版本
        String currentVersion = Bukkit.getBukkitVersion().split("-")[0];

        //分割版本号
        int[] current = parseVersion(currentVersion);
        int[] target = parseVersion(targetVersion);

        //逐个比较
        for (int i = 0; i < Math.min(current.length, target.length); i++) {
            if (current[i] > target[i]) return true;
            if (current[i] < target[i]) return false;
        }

        //如果前面都相等，长度更长的版本更新
        return current.length > target.length;
    }

    //辅助方法：解析版本号
    private static int[] parseVersion(String version) {
        return Arrays.stream(version.split("\\."))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    //1.16+
    public static ItemStack getPlayerHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        //1.16+用playerProfile
        PlayerProfile profile = player.getPlayerProfile();
        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack getPlayerHead(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        //1.16+用playerProfile
        meta.setOwner(player.getName());
        head.setItemMeta(meta);
        return head;
    }

    public static String getFormatTime(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    //判断itemStack是否为空，因为ItemStack.isEmpty在1.17以上才有
    public static boolean isItemStackEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
    }
}
