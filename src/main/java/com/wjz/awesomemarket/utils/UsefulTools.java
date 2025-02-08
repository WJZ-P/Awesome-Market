package com.wjz.awesomemarket.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.wjz.awesomemarket.constants.SkullType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

public class UsefulTools {
    //1.20.5版本和以上获取自定义头颅的方法
    public static ItemStack getCustomSkull(String textureValue) {

        //说明是新版
        if(isVersionNewerThan("1.20.4")){
            ItemStack skull=MarketTools.deserializeItem(String.format(SkullType.CUSTOM_SKULL,UUID.randomUUID(),textureValue));
            return skull;
        }

        else{//旧版获取方法
            ItemStack head = new ItemStack(Material.LEGACY_SKULL);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            //使用反射设置texture
            try {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);

                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", textureValue));

                profileField.set(meta, profile);
                head.setItemMeta(meta);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return head;
        }
    }


    //♡~版本比较方法
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
}
