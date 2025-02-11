package com.wjz.awesomemarket.Listeners;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.GUI.StorageGUIAction;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.inventoryHolder.StorageHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class StorageListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !(clickedInventory.getHolder() instanceof StorageHolder)) return;//点击的容器是否是暂存库
        if (event.getCurrentItem() == null) return;//如果点击的是空的地方
        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

        event.setCancelled(true);//取消点击事件防止玩家移动物品

        //获取物品的标识
        String actionString = itemMeta.getPersistentDataContainer().get(
                new NamespacedKey(AwesomeMarket.getInstance(), MarketHolder.ACTION_KEY),
                PersistentDataType.STRING
        );
        if (actionString == null) return;//没标识就不做动作
        StorageGUIAction action = StorageGUIAction.getType(actionString);
        action.action((StorageHolder) event.getInventory().getHolder(), event.getSlot());
    }
}
