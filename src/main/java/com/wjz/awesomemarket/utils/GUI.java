package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.AwesomeMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static com.wjz.awesomemarket.cache.MarketCache.getTotalPages;

public class GUI {
    private static final NamespacedKey GUI_ACTION_KEY = new NamespacedKey
            (AwesomeMarket.getPlugin(AwesomeMarket.class), "gui_action");

    private static final Map<UUID, Integer> playerPageMap = new HashMap<>();

    public static final int PREV_PAGE_SLOT = 45;
    public static final int NEXT_PAGE_SLOT = 53;
    public static final int SORT_TYPE_SLOT = 47;
    public static final int PAGE_INFO_SLOT = 49;
    public static final int CURRENCY_TYPE_SLOT = 51;

    public static void openMarket(Player player) {
        Inventory marketGUI = Bukkit.createInventory(new MarketHolder(), 54, Log.getString("market_name"));
        //以灰色玻璃板作为默认填充
        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = background.getItemMeta();
        bgMeta.setDisplayName(" ");
        background.setItemMeta(bgMeta);
        //填充背景板
        for (int i = 0; i < 54; i++) {
            marketGUI.setItem(i, background);
        }

        //添加功能按钮
        marketGUI.setItem(PREV_PAGE_SLOT, createPrevBtn(player));
        marketGUI.setItem(NEXT_PAGE_SLOT, createNextBtn(player));

        //下面使用异步方法来填充物品。
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            //获取物品
            List<ItemStack> items = Mysql.getItemsByPage(getPlayerPage(player));
            //获取完毕后，切换回主线程更新UI
            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                int slot = 0;
                for (ItemStack itemStack : items) {
                    if (slot >= 45) break;
                    marketGUI.setItem(slot, itemStack);
                    slot++;
                }
            });
        });

        player.openInventory(marketGUI);
    }

    public static Map<UUID, Integer> getPlayerPageMap() {
        return playerPageMap;
    }

    /**
     * 返回符合格式的lore
     *
     * @param path
     * @return
     */
    private static List<String> getLore(String path) {
        //以换行符为行分割字符串
        return Arrays.asList(Log.getString(path).split("\n"));
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

    private static int getPlayerPage(Player player) {
        return playerPageMap.getOrDefault(player.getUniqueId(), 1);
    }

    private static void setPlayerPage(Player player, int page) {
        playerPageMap.put(player.getUniqueId(), page);
    }


    /**
     * 创建上一页按钮
     *
     * @return
     */
    private static ItemStack createPrevBtn(Player player) {
        List<String> lore = getLore("market-GUI.name.prev-page-lore");

        String newSingleLore = String.format(lore.getFirst(), getPlayerPage(player), getTotalPages(false));
        lore.set(0, newSingleLore);
        return createNavItem(Material.ARROW, "prev_page", Log.getString("market-GUI.name.prev-page"), lore);
    }

    /**
     * 创建下一页按钮
     *
     * @param player
     * @return
     */
    private static ItemStack createNextBtn(Player player) {
        List<String> lore = getLore("market-GUI.name.next-page-lore");
        String newSingleLore = String.format(lore.getFirst(), getPlayerPage(player), getTotalPages(false));
        lore.set(0, newSingleLore);
        return createNavItem(Material.ARROW, "next_page", Log.getString("market-GUI.name.next-page"), lore);
    }


}