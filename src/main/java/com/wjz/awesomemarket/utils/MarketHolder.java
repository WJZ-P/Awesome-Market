package com.wjz.awesomemarket.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MarketHolder implements InventoryHolder {
    /**
     * @return
     */
    @Override
    public @NotNull Inventory getInventory() {
        return null;//不需要实现，bukkit会自动处理
    }


}
