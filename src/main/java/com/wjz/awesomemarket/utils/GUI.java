package com.wjz.awesomemarket.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GUI {
    public static void openMarket(Player player){
        Inventory globalMktGUI= Bukkit.createInventory(null,54,Log.getString("market_name"));

        player.openInventory(globalMktGUI);
    }
}
