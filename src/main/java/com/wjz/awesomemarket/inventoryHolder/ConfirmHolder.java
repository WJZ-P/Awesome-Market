package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.constants.ConfirmGUIAction;
import com.wjz.awesomemarket.utils.Log;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class ConfirmHolder implements InventoryHolder {
    private Inventory confirmGUI;//显示确认信息
    private int ITEM_SLOT=13;
    private int CONFIRM_SLOT =11;
    private int CANCEL_SLOT =15;
    private static final FileConfiguration langConfig = Log.langConfig;

    @Override
    public @NotNull Inventory getInventory() {
        return this.confirmGUI;
    }

    public ConfirmHolder(ItemStack itemStack){
        confirmGUI = Bukkit.createInventory(this, 27, langConfig.getString("confirm-GUI.buy.title"));

        //创建两个按钮物品
        ItemStack confirmBtn=createButton(Material.GREEN_STAINED_GLASS_PANE,langConfig.getString("confirm-GUI.buy.yes"),
                langConfig.getString("confirm-GUI.buy.yes-lore"),"buy");

        ItemStack cancelBtn=createButton(Material.RED_STAINED_GLASS_PANE,langConfig.getString("confirm-GUI.buy.no"),
                langConfig.getString("confirm-GUI.buy.no-lore"),"cancel");

        //设置物品
        confirmGUI.setItem(CONFIRM_SLOT,confirmBtn);
        confirmGUI.setItem(CANCEL_SLOT,cancelBtn);
        confirmGUI.setItem(ITEM_SLOT,itemStack);

    }

    private ItemStack createButton(Material material, String name, String lore, String action) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(lore));

        // 添加动作标识
        meta.getPersistentDataContainer().set(
                new NamespacedKey(AwesomeMarket.getInstance(), "gui_action"),
                PersistentDataType.STRING,
                action
        );

        button.setItemMeta(meta);
        return button;
    }
}
