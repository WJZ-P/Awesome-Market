package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.cache.MarketCache;
import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.Mysql;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.wjz.awesomemarket.cache.MarketCache.getTotalPages;

public class MarketHolder implements InventoryHolder {
    public static final String PREV_PAGE_KEY = "prev_page";
    public static final String NEXT_PAGE_KEY = "next_page";
    public static final String COMMODITY_KEY = "commodity";
    public static final String ACTION_KEY = "gui_action";
    public static final NamespacedKey GUI_ACTION_KEY = new NamespacedKey
            (AwesomeMarket.getPlugin(AwesomeMarket.class), MarketHolder.ACTION_KEY);
    private static final int PREV_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int SORT_TYPE_SLOT = 47;
    private static final int PAGE_INFO_SLOT = 49;
    private static final int CURRENCY_TYPE_SLOT = 51;
    private static final FileConfiguration langConfig = Log.langConfig;

    //非static变量
    private final AtomicBoolean canTurnPage = new AtomicBoolean(true);
    private final Inventory marketGUI;
    private int currentPage = 1;//默认打开第一页
    private int maxPage = MarketCache.getTotalPages(false);
    private List<MarketItem> marketItemList = new ArrayList<>();//存放物品。

    @Override
    public Inventory getInventory() {
        return marketGUI;
    }

    public MarketHolder(int currentPage) {
        this.currentPage=currentPage;
        this.marketGUI = Bukkit.createInventory(this, 54, Log.getString("market_name"));
        //下面对marketGUI做初始化处理

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
        this.loadFuncBar();
        //加载物品
        this.loadMarketItems();
    }

    public void clean() {
        marketGUI.clear();
    }

    public boolean turnPrevPage() {
        if (currentPage <= 1 || !canTurnPage.get()) return false;
        currentPage -= 1;
        //重新渲染物品和功能栏,渲染功能栏是为了修改当前页数
        this.loadMarketItems();
        this.loadFuncBar();
        return true;
    }

    public boolean turnNextPage() {
        if (!canTurnPage.get()) return false;//不允许翻页就直接返回false

        if (currentPage >= maxPage) {
            int newMaxPage = MarketCache.getTotalPages(true);
            if (maxPage != newMaxPage) {
                //如果是最后一页，但是还有下一页，说明页数更新了
                maxPage = newMaxPage;
            } else
                return false;
        }
        currentPage += 1;
        //重新渲染物品和功能栏,渲染功能栏是为了修改当前页数
        this.loadMarketItems();
        this.loadFuncBar();
        return true;
    }

    public MarketItem getMarketItem(int slot){
        return this.marketItemList.get(slot);
    }

    public int getCurrentPage(){
        return this.currentPage;
    }

    //加载功能栏,非static是因为页数每个对象不一样
    private void loadFuncBar() {
        ItemStack prevBtn = createNavItemStack(Material.ARROW, MarketHolder.PREV_PAGE_KEY, Log.getString("market-GUI.name.prev-page"),
                Collections.singletonList(String.format(langConfig.getString("market-GUI.name.prev-page-lore"), this.currentPage, getTotalPages(false))));
        ItemStack nextBtn = createNavItemStack(Material.ARROW, MarketHolder.NEXT_PAGE_KEY, Log.getString("market-GUI.name.next-page"),
                Collections.singletonList(String.format(langConfig.getString("market-GUI.name.next-page-lore"), this.currentPage, getTotalPages(false))));

        this.marketGUI.setItem(MarketHolder.PREV_PAGE_SLOT, prevBtn);
        this.marketGUI.setItem(MarketHolder.NEXT_PAGE_SLOT, nextBtn);
    }

    private static ItemStack createNavItemStack(Material material, String action, String name, List<String> lore) {
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

    //加载物品
    private void loadMarketItems() {
        this.canTurnPage.set(false);//加载物品的时候不可以翻页
        //使用异步方法来填充物品。
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            //获取物品
            List<ItemStack> items = Mysql.getAndSetItemsByPage(this.currentPage,this.marketItemList);
            //这里要对传入的items做处理

            //获取完毕后，切换回主线程更新UI
            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                int slot = 0;
                for (ItemStack itemStack : items) {
                    if (slot >= 45) break;
                    marketGUI.setItem(slot, itemStack);
                    slot++;
                }
                if (slot < 45) {//物品不足一页时填充
                    //以灰色玻璃板作为默认填充
                    ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta bgMeta = background.getItemMeta();
                    bgMeta.setDisplayName(" ");
                    background.setItemMeta(bgMeta);
                    for (int i = slot; i < 45; i++) {
                        marketGUI.setItem(i, background);
                    }
                }
                this.canTurnPage.set(true);
            });
        });
    }
}
