package com.wjz.awesomemarket.listener;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.gui.ConfirmGUIAction;
import com.wjz.awesomemarket.gui.GUIAnimation;
import com.wjz.awesomemarket.inventoryHolder.ConfirmHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ConfirmListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !(clickedInventory.getHolder() instanceof ConfirmHolder)) return;//点击的容器是否是确认界面
        event.setCancelled(true);//取消点击事件防止玩家移动物品
        if (event.getCurrentItem() == null) return;//如果点击的是空的地方
        Player player = (Player) event.getWhoClicked();
        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
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
        //使用异步任务来做渲染
        GUIAnimation guiAnimation = new GUIAnimation(ConfirmInv);
        guiAnimation.runLiteCircleAnimate(0, 2);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConfirmHolder)) return;
        ConfirmHolder confirmHolder = (ConfirmHolder) event.getInventory().getHolder();
        //停止确认界面的动画
        GUIAnimation.stop(confirmHolder.getInventory());
    }
}
