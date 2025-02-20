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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    private static final String PRICE_TYPE_KEY = "price_type";
    private static final String HELP_BOOK_KEY = "help_book";
    private static final int SORT_TYPE_SLOT = 47;
    private static final int TRADE_TYPE_SLOT = 46;
    private static final int CURRENCY_TYPE_SLOT = 51;
    private static final int PREV_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int MARKET_SLOT = 49;
    private static final int HELP_BOOK_SLOT = 52;
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
        loadBackground(0, 54);
        loadFuncBar();
        loadAndSetItems();
    }

    private void loadFuncBar() {
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getPlugin(AwesomeMarket.class), () -> {
            //设置页数
            this.maxPage = Mysql.getItemsCountWithFilter(MysqlType.TRANSACTIONS_TABLE,
                    new SQLFilter(owner.getName(), null, sortType, priceType, tradeType, 1)) / 45 + 1;

            //加载功能栏
            ItemStack prevBtn = createNavItemStack(new ItemStack(Material.ARROW), PREV_PAGE_KEY, Log.getString("transaction-GUI.prev-page"),
                    Collections.singletonList(String.format(Log.getString("transaction-GUI.prev-page-lore"), this.currentPage,
                            maxPage)), GUI_ACTION_KEY);
            ItemStack nextBtn = createNavItemStack(new ItemStack(Material.ARROW), NEXT_PAGE_KEY, Log.getString("transaction-GUI.next-page"),
                    Collections.singletonList(String.format(Log.getString("transaction-GUI.next-page-lore"), this.currentPage,
                            maxPage)), GUI_ACTION_KEY);
            ItemStack marketBtn = createNavItemStack(UsefulTools.getCustomSkull(SkullType.YELLOW_MARKET_DATA), MARKET_KEY, Log.getString("transaction-GUI.market"), null, GUI_ACTION_KEY);

            //这里设置对应的lore
            List<String> sortLore = Log.getStringList("transaction-GUI.sort-type-lore");
            sortLore.replaceAll(s -> s.replace("%sort%", sortType.getString()));
            List<String> tradeLore = Log.getStringList("transaction-GUI.trade-type-lore");
            tradeLore.replaceAll(s -> s.replace("%tradeType%", tradeType.getName()));
            List<String> currencyLore = Log.getStringList("transaction-GUI.currency-type-lore");
            currencyLore.replaceAll(s -> s.replace("%currency%", priceType.getName()));

            ItemStack sortTypeBtn = createNavItemStack(new ItemStack(Material.SUNFLOWER), SORT_TYPE_KEY, Log.getString("transaction-GUI.sort-type"),
                    sortLore, GUI_ACTION_KEY);
            ItemStack currencyTypeBtn = createNavItemStack(new ItemStack(Material.EMERALD), PRICE_TYPE_KEY, Log.getString("transaction-GUI.currency-type"),
                    currencyLore, GUI_ACTION_KEY);
            ItemStack tradeTypeBtn = createNavItemStack(new ItemStack(Material.COMPASS), TRADE_TYPE_KEY, Log.getString("transaction-GUI.trade-type"),
                    tradeLore, GUI_ACTION_KEY);
            ItemStack helpBook = createNavItemStack(new ItemStack(Material.KNOWLEDGE_BOOK), HELP_BOOK_KEY, Log.getString("transaction-GUI.help-book"), Log.getStringList("transaction-GUI.help-book-lore"), GUI_ACTION_KEY);

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
            //最后回到主线程设置物品
            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                this.transactionGUI.setItem(PREV_PAGE_SLOT, prevBtn);
                this.transactionGUI.setItem(NEXT_PAGE_SLOT, nextBtn);
                this.transactionGUI.setItem(SORT_TYPE_SLOT, sortTypeBtn);
                this.transactionGUI.setItem(CURRENCY_TYPE_SLOT, currencyTypeBtn);
                this.transactionGUI.setItem(MARKET_SLOT, marketBtn);
                this.transactionGUI.setItem(TRADE_TYPE_SLOT, tradeTypeBtn);
                this.transactionGUI.setItem(HELP_BOOK_SLOT, helpBook);
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
            this.transactionItems = Mysql.getTransactionItems(new SQLFilter(owner.getName(), viewer == null ? null : viewer.getName(), sortType, priceType, tradeType, currentPage));
            //下面进行物品的设置
            List<ItemStack> tempItemList = new ArrayList<>();
            int slot = 0;
            for (TransactionItem transactionItem : transactionItems) {
                if (slot >= 45) break;
                //下面开始设置UI里面的物品
                ItemStack itemStack = transactionItem.getItemStack();
                ItemMeta meta = itemStack.getItemMeta();
                //要给物品上描述信息
                List<String> lore = Log.getStringList("transaction-GUI.transaction-item-lore");
                //修改lore
                lore.replaceAll(s -> s
                        .replace("%seller%", transactionItem.getSeller())
                        .replace("%buyer%", transactionItem.getBuyer())
                        .replace("%price%", String.format("%.2f", transactionItem.getPrice()))
                        .replace("%priceType%", transactionItem.getPriceType().getName())
                        .replace("%trade_time%", UsefulTools.getFormatTime(transactionItem.getTradeTime()))
                        .replace("%isClaimed%", transactionItem.getIsClaimed() == 1 ?
                                Log.getString("transaction-GUI.is-claimed") : Log.getString("transaction-GUI.not-claimed")));
                meta.setLore(lore);
                //添加商品的NBT标签
                meta.getPersistentDataContainer().set(GUI_ACTION_KEY, PersistentDataType.STRING, TRANSACTION_KEY);
                //设置好的meta数据写入到item中
                itemStack.setItemMeta(meta);
                tempItemList.add(itemStack);
                slot++;
            }

            //切换回主线程更新UI
            int finalSlot = slot;
            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                for (int i = 0; i < tempItemList.size(); i++)
                    transactionGUI.setItem(i, tempItemList.get(i));
                if (finalSlot < 45) {//物品不足一页时填充
                    loadBackground(finalSlot, 45);
                }
                this.canTurnPage.set(true);
            });
        });
    }

    public MarketHolder getMarketHolder() {
        return marketHolder;
    }

    public Player getOpener() {
        return opener;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public void reload() {
        loadFuncBar();
        loadAndSetItems();
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setViewer(OfflinePlayer viewer) {
        this.viewer = viewer;
    }

    public TransactionItem getTransactionItem(int slot) {
        return transactionItems.get(slot);
    }
}
