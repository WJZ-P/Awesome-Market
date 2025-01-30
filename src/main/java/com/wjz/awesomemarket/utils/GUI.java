package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.AwesomeMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class GUI {
    private static final NamespacedKey GUI_ACTION_KEY = new NamespacedKey
            (AwesomeMarket.getPlugin(AwesomeMarket.class), "gui_action");
    public static final int PREV_PAGE_SLOT = 45;
    public static final int NEXT_PAGE_SLOT = 53;
    public static final int SORT_TYPE_SLOT = 47;
    public static final int PAGE_INFO_SLOT = 49;
    public static final int CURRENCY_TYPE_SLOT = 51;

    public static void openMarket(Player player) {
        Inventory globalMktGUI = Bukkit.createInventory(null, 54, Log.getString("market_name"));
        //以灰色玻璃板作为默认填充
        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = background.getItemMeta();
        bgMeta.setDisplayName(" ");
        background.setItemMeta(bgMeta);
        //填充背景板
        for (int i = 0; i < 54; i++) {
            globalMktGUI.setItem(i, background);
        }

        //添加功能按钮
        globalMktGUI.setItem(PREV_PAGE_SLOT,createNavItem(Material.ARROW, "prev_page",
                Log.getString("market-GUI.name.prev-page"),
                Arrays.asList(Log.getString("prev_page_lore"))));

        player.openInventory(globalMktGUI);
    }

    private static ItemStack createNavItem(Material material, String action, String name, List<String> lores) {
        ItemStack navItem = new ItemStack(material);
        ItemMeta meta = navItem.getItemMeta();

        //设置基础属性
        meta.setDisplayName(ChatColor.RESET + name);
        meta.setLore(lores);

        //添加NBT标识
        meta.getPersistentDataContainer().set(GUI_ACTION_KEY, PersistentDataType.STRING, action);
        //隐藏默认属性
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        navItem.setItemMeta(meta);
        return navItem;
    }
}