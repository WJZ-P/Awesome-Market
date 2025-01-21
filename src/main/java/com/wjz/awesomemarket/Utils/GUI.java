package com.wjz.awesomemarket.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GUI {
    public static void openMarket(Player player){
        Inventory globalMktGUI= Bukkit.createInventory(null,27,"§e§l全球市场");
        player.openInventory(globalMktGUI);
    }
}
