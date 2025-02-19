package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.cache.MarketCache;
import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.SkullType;
import com.wjz.awesomemarket.constants.SortType;
import com.wjz.awesomemarket.entity.StorageItem;
import com.wjz.awesomemarket.entity.TransactionItem;
import com.wjz.awesomemarket.sql.Mysql;
import com.wjz.awesomemarket.sql.MysqlType;
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
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.wjz.awesomemarket.utils.UsefulTools.createNavItemStack;

public class TransactionHolder implements InventoryHolder {
    private final Inventory transactionGUI;
    private final MarketHolder marketHolder;
    private int currentPage = 1;
    private final Player opener;//这个holder的打开者
    private final OfflinePlayer owner;//这个容器的拥有者
    private int maxPage;
    private boolean showBuyIn = true;//默认展示买入的物品
    private PriceType priceType = PriceType.ALL;//默认展示所有货币的交易记录
    private SortType sortType = SortType.TIME_DESC;//默认是时间降序
    private List<TransactionItem> transactionItems;
    public static final String MARKET_KEY = "market";
    public static final String PREV_PAGE_KEY = "prev_page";
    public static final String NEXT_PAGE_KEY = "next_page";
    public static final String TRANSACTION_KEY = "transaction";
    private static final int SORT_TYPE_SLOT = 47;
    private static final int CURRENCY_TYPE_SLOT = 51;
    private static final int PREV_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int MARKET_SLOT = 49;
    public static final NamespacedKey GUI_ACTION_KEY = new NamespacedKey
            (AwesomeMarket.getPlugin(AwesomeMarket.class), MarketHolder.ACTION_KEY);
    private final AtomicBoolean canTurnPage = new AtomicBoolean(true);

    @Override
    public @NotNull Inventory getInventory() {
        return transactionGUI;
    }

    public TransactionHolder(MarketHolder marketHolder, Player opener, OfflinePlayer owner) {
        this.transactionGUI = Bukkit.createInventory(this, 54, Log.getString("transaction-GUI.title").replace("%player%", owner.getName()));
        this.marketHolder = marketHolder;
        this.opener = opener;
        this.owner = owner;
        //默认是购入排序,最大页数应该用异步更新。
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getPlugin(AwesomeMarket.class), () -> this.maxPage = Mysql.getItemsCountWithFilter(MysqlType.TRANSACTIONS_TABLE,
                new SQLFilter(null, owner.getName(), sortType, priceType, currentPage)));
        loadBackground(0, 54);
        loadFuncBar();
        this.loadAndSetStorageItems(owner);
    }

    private void loadFuncBar() {
        //加载功能栏
        ItemStack prevBtn = createNavItemStack(new ItemStack(Material.ARROW), PREV_PAGE_KEY, Log.getString("transaction-GUI.name.prev-page"),
                Collections.singletonList(String.format(Log.getString("transaction-GUI.name.prev-page-lore"), this.currentPage,
                        maxPage)), GUI_ACTION_KEY);
        ItemStack nextBtn = createNavItemStack(new ItemStack(Material.ARROW), NEXT_PAGE_KEY, Log.getString("transaction-GUI.name.next-page"),
                Collections.singletonList(String.format(Log.getString("transaction-GUI.name.next-page-lore"), this.currentPage,
                        maxPage)), GUI_ACTION_KEY);
        ItemStack marketBtn = UsefulTools.createNavItemStack(UsefulTools.getCustomSkull(SkullType.YELLOW_MARKET_DATA), MARKET_KEY, Log.getString("transaction-GUI.market"), null, GUI_ACTION_KEY);

        //这里设置对应的lore
        List<String> sortLore = Log.getStringList("transaction-GUI.name.sort-type-lore");
        sortLore.replaceAll(s -> s.replace("%sort%", sortType.getString()));
        List<String> priceLore = Log.getStringList("transaction-GUI.name.currency-type-lore");
        priceLore.replaceAll(s -> s.replace("%currency%", priceType.getName()));

        ItemStack sortTypeBtn = createNavItemStack(new ItemStack(Material.SUNFLOWER), MarketHolder.SORT_TYPE_KEY, Log.getString("market-GUI.name.sort-type"),
                sortLore, GUI_ACTION_KEY);
        ItemStack currencyTypeBtn = createNavItemStack(new ItemStack(Material.EMERALD), MarketHolder.PRICE_TYPE_KEY, Log.getString("market-GUI.name.currency-type"),
                priceLore, GUI_ACTION_KEY);

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

        this.transactionGUI.setItem(PREV_PAGE_SLOT, prevBtn);
        this.transactionGUI.setItem(NEXT_PAGE_SLOT, nextBtn);
        this.transactionGUI.setItem(SORT_TYPE_SLOT, sortTypeBtn);
        this.transactionGUI.setItem(CURRENCY_TYPE_SLOT, currencyTypeBtn);
    }

    private void loadBackground(int startSlot, int endSlot) {
        //以灰色玻璃板作为默认填充
        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = background.getItemMeta();
        bgMeta.setDisplayName(" ");
        background.setItemMeta(bgMeta);
        //填充背景板
        for (int i = startSlot; i < endSlot; i++) {
            transactionGUI.setItem(i, background);
        }
    }

    public boolean turnPrevPage() {
        if (currentPage <= 1 || !canTurnPage.get()) return false;
        currentPage -= 1;
        //重新渲染物品和功能栏,渲染功能栏是为了修改当前页数
        this.loadItems();
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
        this.loadItems();
        this.loadFuncBar();
        return true;
    }

    private void loadItems(){
        //加载物品

    }
}
