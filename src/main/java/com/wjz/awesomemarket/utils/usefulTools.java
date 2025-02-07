package com.wjz.awesomemarket.utils;

import com.mojang.authlib.GameProfile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class usefulTools {
    public static ItemStack getPlayerHead(UUID playerId, String textureValue){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        //设置玩家头颅的数据
        GameProfile profile
    }
}
