package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.SortType;
import com.wjz.awesomemarket.entity.StorageItem;
import com.wjz.awesomemarket.entity.TransactionItem;
import com.wjz.awesomemarket.sql.Mysql;
import com.wjz.awesomemarket.sql.MysqlType;
import com.wjz.awesomemarket.sql.SQLFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionHolder implements InventoryHolder {
    private final Inventory transactionGUI;
    private final MarketHolder marketHolder;
    private int currentPage=1;
    private final Player opener;//这个holder的打开者
    private final OfflinePlayer owner;//这个容器的拥有者
    private final int maxPage;
    private boolean showBuyIn=true;//默认展示买入的物品
    private PriceType priceType=PriceType.ALL;//默认展示所有货币的交易记录
    private SortType sortType=SortType.TIME_DESC;//默认是时间降序
    private List<TransactionItem> transactionItems;
    public static final String MARKET_KEY = "market";
    public static final String PREV_PAGE_KEY = "prev_page";
    public static final String NEXT_PAGE_KEY = "next_page";
    public static final String TRANSACTION_KEY = "transaction";
    public static final NamespacedKey GUI_ACTION_KEY = new NamespacedKey
            (AwesomeMarket.getPlugin(AwesomeMarket.class), MarketHolder.ACTION_KEY);
    private final AtomicBoolean canTurnPage = new AtomicBoolean(true);

    @Override
    public @NotNull Inventory getInventory() {
        return transactionGUI;
    }

    public TransactionHolder(Inventory transactionGUI, MarketHolder marketHolder, Player opener, OfflinePlayer owner) {
        this.transactionGUI = transactionGUI;
        this.marketHolder = marketHolder;
        this.opener = opener;
        this.owner = owner;
        this.maxPage = Mysql.getItemsCountWithFilter(MysqlType.TRANSACTIONS_TABLE,
                //默认是购入排序
                new SQLFilter(null,owner.getName(),sortType,));
    }
}
