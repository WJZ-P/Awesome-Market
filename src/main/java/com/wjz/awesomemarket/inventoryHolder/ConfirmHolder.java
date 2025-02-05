package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.entity.MarketItem;
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
    private final Inventory confirmGUI;//显示确认信息
    private final int CONFIRM_SLOT = 11;
    private final int CANCEL_SLOT = 15;

    private MarketHolder marketHolder;//存储当前的marketHolder，方便用户继续浏览全球市场。
    private static final FileConfiguration langConfig = Log.langConfig;

    @Override
    public @NotNull Inventory getInventory() {
        return this.confirmGUI;
    }

    public ConfirmHolder(MarketItem marketItem,MarketHolder marketHolder) {
        this.marketHolder=marketHolder;
        ItemStack itemStack = marketItem.getItemStack();
        confirmGUI = Bukkit.createInventory(this, 27, langConfig.getString("confirm-GUI.buy.title"));

        String buyLore = langConfig.getString("confirm-GUI.buy.yes-lore")
                .replace("%price%",String.format("%.2f",marketItem.getPrice()))
                .replace("%currency%",marketItem.getPriceTypeName())
                .replace("%player%",marketItem.getSellerName());

        //创建两个按钮物品
        ItemStack confirmBtn = createButton(Material.GREEN_STAINED_GLASS_PANE, langConfig.getString("confirm-GUI.buy.yes"),
                buyLore, "buy");

        ItemStack cancelBtn = createButton(Material.RED_STAINED_GLASS_PANE, langConfig.getString("confirm-GUI.buy.no"),
                langConfig.getString("confirm-GUI.buy.no-lore"), "cancel");

        //设置物品
        confirmGUI.setItem(CONFIRM_SLOT, confirmBtn);
        confirmGUI.setItem(CANCEL_SLOT, cancelBtn);
        int ITEM_SLOT = 13;
        confirmGUI.setItem(ITEM_SLOT, itemStack);

    }

    public MarketHolder getMarketHolder(){
        return this.marketHolder;
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
