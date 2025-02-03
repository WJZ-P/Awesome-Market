package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.AwesomeMarket;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static com.wjz.awesomemarket.cache.MarketCache.getTotalPages;

public class GUI {
    public static final String PREV_PAGE_KEY = "prev_page";
    public static final String NEXT_PAGE_KEY = "next_page";
    public static final String COMMODITY_KEY = "commodity";
    public static final String ACTION_KEY = "gui_action";
    private static final FileConfiguration langConfig = Log.langConfig;
    private static final NamespacedKey GUI_ACTION_KEY = new NamespacedKey
            (AwesomeMarket.getPlugin(AwesomeMarket.class), GUI.ACTION_KEY);
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

        //加载功能栏
        loadFuncBar(marketGUI, player);

        //下面使用异步方法来填充物品。
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            //获取物品
            List<ItemStack> items = Mysql.getItemsByPage(getPlayerPageMap(player));
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
        //播放声音
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 0.6f);
        player.openInventory(marketGUI);
    }

    public static Map<UUID, Integer> getPlayerPageMap() {
        return playerPageMap;
    }

    public static int getPlayerPageMap(Player player) {
        return playerPageMap.getOrDefault(player.getUniqueId(), 1);
    }

    public static void setPlayerPageMap(Player player, int page) {
        playerPageMap.put(player.getUniqueId(), page);
    }

    //设置游戏内玩家商场界面页数
    public static void setPlayerPage(Player player, int newPage) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            List<ItemStack> itemStacks = Mysql.getItemsByPage(newPage);

            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                int slot = 0;
                for (ItemStack itemStack : itemStacks) {
                    inventory.setItem(slot, itemStack);
                    slot++;
                }
                if (slot < 44) {
                    ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta bgMeta = background.getItemMeta();
                    bgMeta.setDisplayName(" ");
                    background.setItemMeta(bgMeta);
                    //如果物品不满一页，后面的用背景块填充
                    for (int i = slot; i < 45; i++) {
                        inventory.setItem(i, background);
                    }
                }
            });
        });

    }

    public static void loadFuncBar(Inventory inventory, Player player) {
        //添加功能按钮
        inventory.setItem(PREV_PAGE_SLOT, createPrevBtn(player));
        inventory.setItem(NEXT_PAGE_SLOT, createNextBtn(player));
    }

    private static ItemStack createNavItem(Material material, String action, String name, List<String> lore) {
        ItemStack navItem = new ItemStack(material);
        ItemMeta meta = navItem.getItemMeta();

        //设置基础属性
        meta.setDisplayName(ChatColor.RESET + name);
        meta.setLore(lore);

        //添加NBT标识
        meta.getPersistentDataContainer().set(GUI_ACTION_KEY, PersistentDataType.STRING, action);
        //隐藏默认属性
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        navItem.setItemMeta(meta);
        return navItem;
    }

    /**
     * 创建上一页按钮
     *
     * @return
     */
    private static ItemStack createPrevBtn(Player player) {
        String lore = langConfig.getString("market-GUI.name.prev-page-lore");
        lore = String.format(lore, getPlayerPageMap(player), getTotalPages(false));

        return createNavItem(Material.ARROW, GUI.PREV_PAGE_KEY, Log.getString("market-GUI.name.prev-page"), Collections.singletonList(lore));
    }

    /**
     * 创建下一页按钮
     *
     * @param player
     * @return
     */
    private static ItemStack createNextBtn(Player player) {
        String lore = langConfig.getString("market-GUI.name.next-page-lore");
        lore = String.format(lore, getPlayerPageMap(player), getTotalPages(false));

        return createNavItem(Material.ARROW, GUI.NEXT_PAGE_KEY, Log.getString("market-GUI.name.next-page"), Collections.singletonList(lore));
    }


}