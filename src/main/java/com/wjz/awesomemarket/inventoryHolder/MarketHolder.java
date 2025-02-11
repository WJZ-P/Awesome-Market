package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.cache.MarketCache;
import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.SortType;
import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.sql.SQLFilter;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.sql.Mysql;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.wjz.awesomemarket.cache.MarketCache.getTotalPages;
import static com.wjz.awesomemarket.utils.UsefulTools.createNavItemStack;

public class MarketHolder implements InventoryHolder {
    public static final String PREV_PAGE_KEY = "prev_page";
    public static final String NEXT_PAGE_KEY = "next_page";
    public static final String COMMODITY_KEY = "commodity";
    public static final String STORAGE_KEY = "storage";
    public static final String HELP_BOOK_KEY="help_book";
    private static final String SORT_TYPE_KEY = "sort_type";
    private static final String PRICE_TYPE_KEY = "price_type";
    public static final String ACTION_KEY = "gui_action";
    public static final NamespacedKey GUI_ACTION_KEY = new NamespacedKey
            (AwesomeMarket.getPlugin(AwesomeMarket.class), MarketHolder.ACTION_KEY);
    private static final int PREV_PAGE_SLOT = 45;
    private static final int STORAGE_SLOT = 46;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int SORT_TYPE_SLOT = 47;
    private static final int CURRENCY_TYPE_SLOT = 51;
    private static final int HELP_BOOK_SLOT=52;

    //非static变量
    private final AtomicBoolean canTurnPage = new AtomicBoolean(true);
    private final Inventory marketGUI;
    private int currentPage = 1;//默认打开第一页
    private List<MarketItem> marketItemList = new ArrayList<>();//存放物品。
    private PriceType priceType=PriceType.ALL;//根据物品货币类型筛选
    private SortType sortType=SortType.TIME_DESC;//默认查询时间倒序
    private int maxPage = MarketCache.getTotalPages(sortType,priceType,false);
    private String sellerName;//用于筛选特定玩家的物品
    private String itemType;//用于筛选特定的物品类型
    @Override
    public Inventory getInventory() {
        return marketGUI;
    }
    //获得排序类型
    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    //获得价格类型
    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public MarketHolder(int currentPage) {
        this.currentPage = currentPage;
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
            int newMaxPage = MarketCache.getTotalPages(sortType,priceType,true);
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

    public MarketItem getMarketItem(int slot) {
        return this.marketItemList.get(slot);
    }

    public int getCurrentPage() {
        return this.currentPage;
    }
    public void reload(){
        this.loadFuncBar();
        this.loadMarketItems();
    }

    //加载功能栏,非static是因为页数每个对象不一样
    private void loadFuncBar() {
        ItemStack prevBtn = createNavItemStack(new ItemStack(Material.ARROW), MarketHolder.PREV_PAGE_KEY, Log.getString("market-GUI.name.prev-page"),
                Collections.singletonList(String.format(Log.getString("market-GUI.name.prev-page-lore"), this.currentPage, getTotalPages(sortType,priceType,false))),GUI_ACTION_KEY);
        ItemStack nextBtn = createNavItemStack(new ItemStack(Material.ARROW), MarketHolder.NEXT_PAGE_KEY, Log.getString("market-GUI.name.next-page"),
                Collections.singletonList(String.format(Log.getString("market-GUI.name.next-page-lore"), this.currentPage, getTotalPages(sortType,priceType,false))),GUI_ACTION_KEY);

        ItemStack storageBtn = createNavItemStack(new ItemStack(Material.ENDER_CHEST), MarketHolder.STORAGE_KEY, Log.getString("market-GUI.name.storage"),
                Collections.singletonList(Log.getString("market-GUI.name.storage-lore")),GUI_ACTION_KEY);
        ItemStack helpBook=createNavItemStack(new ItemStack(Material.KNOWLEDGE_BOOK),HELP_BOOK_KEY,Log.getString("market-GUI.name.help-book"),
                Log.getStringList("market-GUI.name.help-book-lore"),GUI_ACTION_KEY);

        //这里设置对应的lore
        List<String> sortLore=Log.getStringList("market-GUI.name.sort-type-lore");
        sortLore.replaceAll(s -> s.replace("%sort%", sortType.getString()));
        List<String> priceLore=Log.getStringList("market-GUI.name.currency-type-lore");
        priceLore.replaceAll(s -> s.replace("%currency%",priceType.getName()));

        ItemStack sortTypeBtn = createNavItemStack(new ItemStack(Material.SUNFLOWER), MarketHolder.SORT_TYPE_KEY, Log.getString("market-GUI.name.sort-type"),
                sortLore,GUI_ACTION_KEY);

        ItemStack currencyTypeBtn = createNavItemStack(new ItemStack(Material.EMERALD), MarketHolder.PRICE_TYPE_KEY, Log.getString("market-GUI.name.currency-type"),
                priceLore,GUI_ACTION_KEY);
        this.marketGUI.setItem(MarketHolder.PREV_PAGE_SLOT, prevBtn);
        this.marketGUI.setItem(MarketHolder.NEXT_PAGE_SLOT, nextBtn);
        this.marketGUI.setItem(MarketHolder.STORAGE_SLOT, storageBtn);
        this.marketGUI.setItem(HELP_BOOK_SLOT,helpBook);
        this.marketGUI.setItem(SORT_TYPE_SLOT,sortTypeBtn);
        this.marketGUI.setItem(CURRENCY_TYPE_SLOT,currencyTypeBtn);
    }

    //加载物品
    private void loadMarketItems() {
        this.canTurnPage.set(false);//加载物品的时候不可以翻页
        //使用异步方法来填充物品。
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            //获取物品
            this.marketItemList = Mysql.getMarketItems(new SQLFilter(sortType,priceType,sellerName,itemType,currentPage));

            //获取完毕后，切换回主线程更新UI
            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                int slot = 0;
                for (MarketItem marketItem : this.marketItemList) {
                    if (slot >= 45) break;
                    //下面开始设置UI里面的物品
                    ItemStack itemStack=marketItem.getItemStack().clone();
                    ItemMeta meta = itemStack.getItemMeta();
                    List<String> oldLore = itemStack.getLore();
                    if (oldLore == null) oldLore = new ArrayList<>();

                    //要给物品上描述信息
                    List<String> commodityLore = Log.langConfig.getStringList("market-GUI.name.commodity");
                    //添加lore
                    //price,currency,player,on_sell_time

                    //这里格式化时间
                    long timeStamp = marketItem.getOnSellTime();
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.systemDefault());

                    //下面获取一些信息
                    String seller = marketItem.getSellerName();
                    double price = marketItem.getPrice();
                    PriceType priceType = marketItem.getPriceType();

                    //修改要展示到UI上的物品描述
                    commodityLore.replaceAll(s -> s.replace("%player%", seller)
                            .replace("%price%", String.format("%.2f", price))
                            .replace("%currency%", priceType.getName())
                            .replace("%on_sell_time%", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                    //商品lore添加完毕后追加到原lore后
                    oldLore.addAll(commodityLore);
                    meta.setLore(oldLore);
                    //添加商品的NBT标签
                    meta.getPersistentDataContainer().set(MarketHolder.GUI_ACTION_KEY, PersistentDataType.STRING, MarketHolder.COMMODITY_KEY);
                    //设置好的meta数据写入到item中
                    itemStack.setItemMeta(meta);
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
