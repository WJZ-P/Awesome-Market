package com.wjz.awesomemarket.listener;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.gui.MarketGUIAction;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MarketListener implements Listener {
//    @EventHandler
//    public void onMarketClose(InventoryCloseEvent event) {
//        if (!(event.getInventory().getHolder() instanceof MarketHolder)) return;//不是全球市场就返回
//
//        Player player = (Player) event.getPlayer();
//        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0f, 0.8f);
//        ((MarketHolder) event.getInventory().getHolder()).clean();
//    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !(clickedInventory.getHolder() instanceof MarketHolder)) return;//点击的容器是否是全球市场
        if (event.getCurrentItem() == null) return;//如果点击的是空的地方
        Player player = (Player) event.getWhoClicked();
        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

        event.setCancelled(true);//取消点击事件防止玩家移动物品

        //获取物品的标识
        String actionString = itemMeta.getPersistentDataContainer().get(
                new NamespacedKey(AwesomeMarket.getInstance(), MarketHolder.ACTION_KEY),
                PersistentDataType.STRING
        );
        if (actionString == null) return;//没标识就不做动作
        MarketGUIAction action = MarketGUIAction.getType(actionString);
        action.action(player, event.getSlot(),event);
    }
}
