package com.wjz.awesomemarket.Listeners;

import com.wjz.awesomemarket.utils.GUI;
import com.wjz.awesomemarket.utils.MarketHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
}
