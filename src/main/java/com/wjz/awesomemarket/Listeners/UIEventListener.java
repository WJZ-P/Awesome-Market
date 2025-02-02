package com.wjz.awesomemarket.Listeners;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.cache.MarketCache;
import com.wjz.awesomemarket.utils.*;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class UIEventListener implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();

        // 检查是否是市场 GUI
        if (event.getInventory().getHolder() instanceof MarketHolder) {
            GUI.getPlayerPageMap().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GUI.getPlayerPageMap().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMarketClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof MarketHolder)) return;//不是全球市场就返回
        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0f, 0.8f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;//点击的是否是玩家
        if (!(event.getInventory().getHolder() instanceof MarketHolder)) return;//点击的容器是否是全球市场
        if (event.getCurrentItem() == null) return;//如果点击的是空的地方
        Player player = (Player) event.getWhoClicked();
        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();

        //获取物品的标识
        String action = itemMeta.getPersistentDataContainer().get(
                new NamespacedKey(AwesomeMarket.getInstance(), GUI.ACTION_KEY),
                PersistentDataType.STRING
        );

        event.setCancelled(true);//取消点击事件防止玩家移动物品
        if (action != null) {
            switch (action) {
                case GUI.PREV_PAGE_KEY: {
                    prevPageHandler(player);
                }
                case GUI.NEXT_PAGE_KEY: {
                    nextPageHandler(player);
                }
            }
        }
    }

    /**
     * 处理上一页
     */
    private void prevPageHandler(Player player) {
        int currentPage = GUI.getPlayerPageMap(player);
        if (currentPage <= 1) {//第一页无法上一页
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        GUI.setPlayerPageMap(player, currentPage - 1);
        GUI.setPlayerPage(player, currentPage - 1);
    }

    /**
     * 处理下一页
     */
    private void nextPageHandler(Player player) {
        int currentPage = GUI.getPlayerPageMap(player);
        int maxPage = MarketCache.getTotalPages(true);
        if (currentPage >= maxPage) {//当前是最后一页就不能换了
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        GUI.setPlayerPageMap(player, currentPage + 1);
        GUI.setPlayerPage(player, currentPage + 1);
    }
}
