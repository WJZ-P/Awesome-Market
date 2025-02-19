package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.SkullType;
import com.wjz.awesomemarket.constants.SortType;
import com.wjz.awesomemarket.constants.TradeType;
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
    private OfflinePlayer viewer;//想要查询交易记录的目标玩家
    private int maxPage;
    private boolean showBuyIn = true;//默认展示购买的物品
    private PriceType priceType = PriceType.ALL;//默认展示所有货币的交易记录
    private TradeType tradeType = TradeType.ALL;//默认展示所有交易记录
    private SortType sortType = SortType.TIME_DESC;//默认是时间降序
    private List<TransactionItem> transactionItems;
    public static final String MARKET_KEY = "market";
    public static final String PREV_PAGE_KEY = "prev_page";
    public static final String NEXT_PAGE_KEY = "next_page";
    public static final String TRANSACTION_KEY = "transaction";
    private static final String SORT_TYPE_KEY = "sort_type";
    private static final String TRADE_TYPE_KEY = "trade_type";
    private static final String PRICE_TYPE_KEY="price_type";
    private static final int SORT_TYPE_SLOT = 47;
    private static final int TRADE_TYPE_SLOT = 48;
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
        //默认是购入排序,最大页数应该用异步更新。
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getPlugin(AwesomeMarket.class), () -> this.maxPage = Mysql.getItemsCountWithFilter(MysqlType.TRANSACTIONS_TABLE,
                new SQLFilter(owner.getName(),null, sortType, priceType, tradeType, 1)) / 45 + 1);
        //这个放在最前面，因为耗时应该较久并且后面要用到

        this.transactionGUI = Bukkit.createInventory(this, 54, Log.getString("transaction-GUI.title").replace("%player%", owner.getName()));
        this.marketHolder = marketHolder;
        this.opener = opener;
        this.owner = owner;
        loadBackground(0, 54);
        loadFuncBar();
        this.loadAndSetItems();
    }

    private void loadFuncBar() {
        //加载功能栏
        ItemStack prevBtn = createNavItemStack(new ItemStack(Material.ARROW), PREV_PAGE_KEY, Log.getString("transaction-GUI.name.prev-page"),
                Collections.singletonList(String.format(Log.getString("transaction-GUI.name.prev-page-lore"), this.currentPage,
                        maxPage)), GUI_ACTION_KEY);
        ItemStack nextBtn = createNavItemStack(new ItemStack(Material.ARROW), NEXT_PAGE_KEY, Log.getString("transaction-GUI.name.next-page"),
                Collections.singletonList(String.format(Log.getString("transaction-GUI.name.next-page-lore"), this.currentPage,
                        maxPage)), GUI_ACTION_KEY);
        ItemStack marketBtn = createNavItemStack(UsefulTools.getCustomSkull(SkullType.YELLOW_MARKET_DATA), MARKET_KEY, Log.getString("transaction-GUI.market"), null, GUI_ACTION_KEY);

        //这里设置对应的lore
        List<String> sortLore = Log.getStringList("transaction-GUI.name.sort-type-lore");
        sortLore.replaceAll(s -> s.replace("%sort%", sortType.getString()));
        List<String> tradeLore = Log.getStringList("transaction-GUI.name.trade-type-lore");
        tradeLore.replaceAll(s -> s.replace("%tradeType%", tradeType.getName()));

        ItemStack sortTypeBtn = createNavItemStack(new ItemStack(Material.SUNFLOWER), SORT_TYPE_KEY, Log.getString("market-GUI.name.sort-type"),
                sortLore, GUI_ACTION_KEY);
        ItemStack currencyTypeBtn = createNavItemStack(new ItemStack(Material.EMERALD), PRICE_TYPE_KEY, Log.getString("market-GUI.name.currency-type"),
                tradeLore, GUI_ACTION_KEY);
        ItemStack tradeTypeBtn=createNavItemStack(new ItemStack(Material.COMPASS), TRADE_TYPE_KEY, Log.getString("market-GUI.name.trade-type"), Log.getStringList("market-GUI.name.trade-type-lore"), GUI_ACTION_KEY);

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
        if (tradeType != TradeType.ALL) {
            ItemMeta meta = tradeTypeBtn.getItemMeta();
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            tradeTypeBtn.setItemMeta(meta);
        }

        this.transactionGUI.setItem(PREV_PAGE_SLOT, prevBtn);
        this.transactionGUI.setItem(NEXT_PAGE_SLOT, nextBtn);
        this.transactionGUI.setItem(SORT_TYPE_SLOT, sortTypeBtn);
        this.transactionGUI.setItem(CURRENCY_TYPE_SLOT, currencyTypeBtn);
        this.transactionGUI.setItem(MARKET_SLOT, marketBtn);
        this.transactionGUI.setItem(TRADE_TYPE_SLOT, tradeTypeBtn);
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
        this.loadAndSetItems();
        this.loadFuncBar();
        return true;
    }
    public boolean turnNextPage() {
        if (!canTurnPage.get()) return false;//不允许翻页就直接返回false

        if (currentPage >= maxPage) return false;

        currentPage += 1;
        //重新渲染物品和功能栏,渲染功能栏是为了修改当前页数
        this.loadAndSetItems();
        this.loadFuncBar();
        return true;
    }

    private void loadAndSetItems() {
        this.canTurnPage.set(false);//加载物品的时候不可以翻页
        //加载物品
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            this.transactionItems=Mysql.getTransactionItems(new SQLFilter(owner.getName(),viewer.getName(), sortType, priceType, tradeType, currentPage));
            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                //转回主线程设置物品
            });
        });
    }
}
