package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.cache.MarketCache;
import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.SortType;
import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.entity.StatisticInfo;
import com.wjz.awesomemarket.sql.Mysql;
import com.wjz.awesomemarket.sql.SQLFilter;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.UsefulTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
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
    public static final String HELP_BOOK_KEY = "help_book";
    private static final String SORT_TYPE_KEY = "sort_type";
    private static final String PRICE_TYPE_KEY = "price_type";
    private static final String STATISTIC_KEY = "statistic";
    public static final String ACTION_KEY = "gui_action";
    public static final NamespacedKey GUI_ACTION_KEY = new NamespacedKey
            (AwesomeMarket.getPlugin(AwesomeMarket.class), MarketHolder.ACTION_KEY);
    private static final int PREV_PAGE_SLOT = 45;
    private static final int STORAGE_SLOT = 46;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int SORT_TYPE_SLOT = 47;
    private static final int CURRENCY_TYPE_SLOT = 51;
    private static final int HELP_BOOK_SLOT = 52;
    public static final int STATISTIC_SLOT = 49;

    //非static变量
    private final AtomicBoolean canTurnPage = new AtomicBoolean(true);
    private final Inventory marketGUI;
    private int currentPage = 1;//默认打开第一页
    private List<MarketItem> marketItemList = new ArrayList<>();//存放物品。
    private PriceType priceType = PriceType.ALL;//根据物品货币类型筛选
    private SortType sortType = SortType.TIME_DESC;//默认查询时间倒序
    private String sellerName;//用于筛选特定玩家的物品
    private String itemType;//用于筛选特定的物品类型
    private final OfflinePlayer owner;//这个容器的拥有者，查看别人的时候别人不一定在线
    private final Player marketOpener;//这个容器的打开者，必定在线
    private int maxPage = MarketCache.getTotalPages(new SQLFilter(sortType, priceType, sellerName, itemType, currentPage), false);

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

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    //获得价格类型
    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public MarketHolder(Player owner, int currentPage) {
        this.currentPage = currentPage;
        this.marketGUI = Bukkit.createInventory(this, 54, Log.getString("market_name"));
        this.owner = owner;
        this.marketOpener = owner;
        //下面对marketGUI做初始化处理
        loadBackground(0, 54);
        //加载功能栏
        this.loadFuncBar();
        //加载物品
        this.loadMarketItems();
    }

    public MarketHolder(OfflinePlayer viewedPlayer, Player opener, int currentPage) {
        this.sellerName = viewedPlayer.getName();
        this.currentPage = currentPage;
        this.marketGUI = Bukkit.createInventory(this, 54, Log.getString("market_name"));
        this.owner = viewedPlayer;
        this.marketOpener = opener;
        //下面对marketGUI做初始化处理
        loadBackground(0, 54);
        //加载功能栏
        this.loadFuncBar();
        //加载物品
        this.loadMarketItems();
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
            int newMaxPage = MarketCache.getTotalPages(new SQLFilter(sortType, priceType, sellerName, itemType, currentPage), true);
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

    public void reload() {
        this.currentPage = 1;//重新设置成第一页
        this.loadFuncBar();
        this.loadMarketItems();
    }

    //加载功能栏,非static是因为页数每个对象不一样
    private void loadFuncBar() {
        this.maxPage = getTotalPages(new SQLFilter(sortType, priceType, sellerName, itemType, currentPage), false);
        ItemStack prevBtn = createNavItemStack(new ItemStack(Material.ARROW), MarketHolder.PREV_PAGE_KEY, Log.getString("market-GUI.name.prev-page"),
                Collections.singletonList(String.format(Log.getString("market-GUI.name.prev-page-lore"), this.currentPage,
                        maxPage)), GUI_ACTION_KEY);
        ItemStack nextBtn = createNavItemStack(new ItemStack(Material.ARROW), MarketHolder.NEXT_PAGE_KEY, Log.getString("market-GUI.name.next-page"),
                Collections.singletonList(String.format(Log.getString("market-GUI.name.next-page-lore"), this.currentPage,
                        maxPage)), GUI_ACTION_KEY);
        ItemStack storageBtn = createNavItemStack(new ItemStack(Material.ENDER_CHEST), MarketHolder.STORAGE_KEY, Log.getString("market-GUI.name.storage"),
                Collections.singletonList(Log.getString("market-GUI.name.storage-lore")), GUI_ACTION_KEY);
        ItemStack helpBook = createNavItemStack(new ItemStack(Material.KNOWLEDGE_BOOK), HELP_BOOK_KEY, Log.getString("market-GUI.name.help-book"),
                Log.getStringList("market-GUI.name.help-book-lore"), GUI_ACTION_KEY);

        //这里设置对应的lore
        List<String> sortLore = Log.getStringList("market-GUI.name.sort-type-lore");
        sortLore.replaceAll(s -> s.replace("%sort%", sortType.getString()));
        List<String> priceLore = Log.getStringList("market-GUI.name.currency-type-lore");
        priceLore.replaceAll(s -> s.replace("%currency%", priceType.getName()));

        //判断当前玩家有没有权限打开交易记录
        boolean canLookTransaction = marketOpener.hasPermission("awesomemarket.transaction.look") || marketOpener.getUniqueId() == owner.getUniqueId();

        ItemStack sortTypeBtn = createNavItemStack(new ItemStack(Material.SUNFLOWER), MarketHolder.SORT_TYPE_KEY, Log.getString("market-GUI.name.sort-type"),
                sortLore, GUI_ACTION_KEY);
        ItemStack currencyTypeBtn = createNavItemStack(new ItemStack(Material.EMERALD), MarketHolder.PRICE_TYPE_KEY, Log.getString("market-GUI.name.currency-type"),
                priceLore, GUI_ACTION_KEY);
        ItemStack statisticItem = createNavItemStack(sellerName == null ? UsefulTools.getPlayerHead(marketOpener) :
                        UsefulTools.getPlayerHead(Bukkit.getOfflinePlayer(sellerName)),
                STATISTIC_KEY, Log.getString("market-GUI.name.statistic")
                        .replace("%player%", sellerName == null ? owner.getName() : sellerName),
                Log.getStringList("market-GUI.name.statistic-loading"), GUI_ACTION_KEY);//展示统计信息

        //如果不是默认排序。物品就带附魔颜色
        if (sortType != SortType.TIME_DESC) {
            ItemMeta meta = sortTypeBtn.getItemMeta();
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            sortTypeBtn.setItemMeta(meta);
        }
        if (priceType != PriceType.ALL) {
            ItemMeta meta = currencyTypeBtn.getItemMeta();
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            currencyTypeBtn.setItemMeta(meta);
        }

        this.marketGUI.setItem(MarketHolder.PREV_PAGE_SLOT, prevBtn);
        this.marketGUI.setItem(MarketHolder.NEXT_PAGE_SLOT, nextBtn);
        this.marketGUI.setItem(MarketHolder.STORAGE_SLOT, storageBtn);
        this.marketGUI.setItem(HELP_BOOK_SLOT, helpBook);
        this.marketGUI.setItem(SORT_TYPE_SLOT, sortTypeBtn);
        this.marketGUI.setItem(CURRENCY_TYPE_SLOT, currencyTypeBtn);
        this.marketGUI.setItem(STATISTIC_SLOT, statisticItem);

        //这里用异步任务来异步更新统计数据
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            //获取统计数据
            StatisticInfo statisticInfo = Mysql.searchStatistic(sellerName == null ? owner : Bukkit.getOfflinePlayer(sellerName));
            List<String> statisticLore = Log.getStringList("market-GUI.name.statistic-lore");
            statisticLore.replaceAll(s -> s.replace("%buy_count%", String.valueOf(statisticInfo.buy_count))
                    .replace("%sell_count%", String.valueOf(statisticInfo.sell_count))
                    .replace("%money%", String.format("%.2f", statisticInfo.cost_money))
                    .replace("%point%", String.format("%.2f", statisticInfo.cost_point))
                    .replace("%currency_money%", PriceType.MONEY.getName())
                    .replace("%currency_point%", PriceType.POINT.getName())
                    .replace("%money_get%", String.format("%.2f", statisticInfo.buy_money))
                    .replace("%point_get%", String.format("%.2f", statisticInfo.buy_point))
                    .replace("%operation%", canLookTransaction ?
                            Log.getString("market-GUI.name.statistic-look-up") :
                            Log.getString("statistic-no-permission-look-up"))
            );
            ItemMeta meta = statisticItem.getItemMeta();
            meta.setLore(statisticLore);
            statisticItem.setItemMeta(meta);
            //设置完成后使用同步进行物品栏的更新
            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                marketGUI.setItem(STATISTIC_SLOT, statisticItem);
            });
        });
    }

    //加载物品
    private void loadMarketItems() {
        this.canTurnPage.set(false);//加载物品的时候不可以翻页
        //使用异步方法来填充物品。
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            //获取物品
            this.marketItemList = Mysql.getMarketItems(new SQLFilter(sortType, priceType, sellerName, itemType, currentPage));
            //创建一个临时的itemStack存放队列
            List<ItemStack> itemStacks = new ArrayList<>();

            int slot = 0;
            for (MarketItem marketItem : this.marketItemList) {
                if (slot >= 45) break;
                //下面开始设置UI里面的物品
                ItemStack itemStack = marketItem.getItemStack().clone();
                ItemMeta meta = itemStack.getItemMeta();
                List<String> oldLore = itemStack.getLore();
                if (oldLore == null) oldLore = new ArrayList<>();

                //要给物品上描述信息
                List<String> commodityLore = Log.getStringList("market-GUI.name.commodity.lore");
                //添加lore
                //price,currency,player,on_sell_time

                //这里格式化时间
                long timeStamp = marketItem.getOnSellTime();
                LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.systemDefault());

                //下面获取一些信息
                String seller = marketItem.getSellerName();
                double price = marketItem.getPrice();
                PriceType priceType = marketItem.getPriceType();
                boolean isSelfItem = marketOpener.getName().equals(marketItem.getSellerName());//是否是自己的物品

                //修改要展示到UI上的物品描述
                commodityLore.replaceAll(s -> s.replace("%player%", seller)
                        .replace("%price%", String.format("%.2f", price))
                        .replace("%currency%", priceType.getName())
                        .replace("%operation%", isSelfItem ? Log.getString("market-GUI.name.commodity.unlisted")
                                : Log.getString("market-GUI.name.commodity.buy"))
                        .replace("%on_sell_time%", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                //商品lore添加完毕后追加到原lore后
                oldLore.addAll(commodityLore);
                meta.setLore(oldLore);
                //添加商品的NBT标签
                meta.getPersistentDataContainer().set(MarketHolder.GUI_ACTION_KEY, PersistentDataType.STRING, MarketHolder.COMMODITY_KEY);
                //设置好的meta数据写入到item中
                itemStack.setItemMeta(meta);
                itemStacks.add(itemStack);
                slot++;
            }

            //切换回主线程更新UI
            int finalSlot = slot;
            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                for (int i=0;i<itemStacks.size();i++)
                    marketGUI.setItem(i, itemStacks.get(i));
                if (finalSlot < 45) {//物品不足一页时填充
                    loadBackground(finalSlot, 45);
                }
                this.canTurnPage.set(true);
            });
        });
    }

    private void loadBackground(int startSlot, int endSlot) {
        //以灰色玻璃板作为默认填充
        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = background.getItemMeta();
        bgMeta.setDisplayName(" ");
        background.setItemMeta(bgMeta);
        //填充背景板
        for (int i = startSlot; i < endSlot; i++) {
            marketGUI.setItem(i, background);
        }
    }
}
