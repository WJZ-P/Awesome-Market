package com.wjz.awesomemarket.Listeners;

import com.wjz.awesomemarket.inventoryHolder.ConfirmHolder;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ConfirmListener implements Listener {
    @EventHandler
    public void onMarketClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConfirmHolder)) return;
        ConfirmHolder confirmHolder= (ConfirmHolder) event.getInventory().getHolder();
        //要返回原打开商店页，以实现继续购买。
        event.getPlayer().openInventory(confirmHolder.getMarketHolder().getInventory());
    }
}
