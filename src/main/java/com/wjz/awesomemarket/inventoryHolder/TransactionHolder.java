package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.entity.StorageItem;
import com.wjz.awesomemarket.entity.TransactionItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransactionHolder implements InventoryHolder {
    private final Inventory transactionGUI;
    private final MarketHolder marketHolder;
    private int currentPage=1;
    private final Player opener;//这个holder的打开者
    private final OfflinePlayer owner;//这个容器的拥有者
    private final int maxPage;
    private List<TransactionItem> transactionItems;

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

}
