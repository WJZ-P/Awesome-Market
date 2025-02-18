package com.wjz.awesomemarket.Listeners;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.GUI.ConfirmGUIAction;
import com.wjz.awesomemarket.inventoryHolder.ConfirmHolder;
import com.wjz.awesomemarket.utils.Log;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ConfirmListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !(clickedInventory.getHolder() instanceof ConfirmHolder)) return;//点击的容器是否是确认界面
        if (event.getCurrentItem() == null) return;//如果点击的是空的地方
        Player player = (Player) event.getWhoClicked();
        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

        event.setCancelled(true);//取消点击事件防止玩家移动物品
        //获取物品的标识
        String actionString = itemMeta.getPersistentDataContainer().get(
                new NamespacedKey(AwesomeMarket.getInstance(), ConfirmHolder.ACTION_KEY),
                PersistentDataType.STRING
        );
        if (actionString == null) return;//没标识就不做动作

        ConfirmGUIAction action = ConfirmGUIAction.getType(actionString);
        ConfirmHolder confirmHolder = (ConfirmHolder) event.getInventory().getHolder();
        //传入marketHolder
        action.action(player, confirmHolder.getMarketHolder(), confirmHolder.getItemSlot());//执行对应指令

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConfirmHolder)) return;
        //打开UI的时候来一个小动画.
        //首先得获取容器
        Inventory ConfirmInv = event.getInventory();
        //使用异步任务来做渲染.先搁置
    }
}
