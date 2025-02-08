package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.utils.Log;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class StorageHolder implements InventoryHolder {
    private Inventory storageGUI;
    
    @Override
    public @NotNull Inventory getInventory() {
        return storageGUI;
    }
    
    StorageHolder(Player player){
        storageGUI = Bukkit.createInventory(this, 54, Log.getString("storage-GUI.title"));

        //以灰色玻璃板作为默认填充
        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = background.getItemMeta();
        bgMeta.setDisplayName(" ");
        background.setItemMeta(bgMeta);
        //填充背景板
        for (int i = 0; i < 54; i++) {
            storageGUI.setItem(i, background);
        }

        //加载功能栏
        this.loadFuncBar();
        //加载物品
        this.loadMarketItems();
    }

    private void loadMarketItems() {
    }

    private void loadFuncBar() {
    }
}
