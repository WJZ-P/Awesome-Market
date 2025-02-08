package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.UsefulTools;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class ConfirmHolder implements InventoryHolder {
    private final int marketPage;//
    private final Inventory confirmGUI;//显示确认信息
    private final int CONFIRM_SLOT = 11;
    private final int ITEM_SLOT = 13;
    private final int CANCEL_SLOT = 15;
    private final MarketItem marketItem;
    private final MarketHolder marketHolder;//存储原来的marketHolder
    public static final String ACTION_KEY = "gui_action";


    @Override
    public @NotNull Inventory getInventory() {
        return this.confirmGUI;
    }

    public ConfirmHolder(MarketHolder marketHolder, int slot) {
        this.marketPage = marketHolder.getCurrentPage();
        this.marketHolder = marketHolder;
        this.marketItem = marketHolder.getMarketItem(slot);
        confirmGUI = Bukkit.createInventory(this, 27, Log.getString("confirm-GUI.buy.title"));

        String buyLore = Log.getString("confirm-GUI.buy.confirm-lore")
                .replace("%price%", String.format("%.2f", marketItem.getPrice()))
                .replace("%currency%", marketItem.getPriceTypeName())
                .replace("%player%", marketItem.getSellerName());
        //创建两个按钮物品
        ItemStack confirmBtn = createButton(UsefulTools.getCustomSkull(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0="),
                Log.getString("confirm-GUI.buy.confirm"),
                buyLore, "confirm");

        ItemStack cancelBtn = createButton(UsefulTools.getCustomSkull(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0="),
                Log.getString("confirm-GUI.buy.cancel"),
                Log.getString("confirm-GUI.buy.cancel-lore"), "cancel");

        //设置物品
        confirmGUI.setItem(CONFIRM_SLOT, confirmBtn);
        confirmGUI.setItem(CANCEL_SLOT, cancelBtn);
        confirmGUI.setItem(ITEM_SLOT, marketItem.getItemStack());

    }

    public int getMarketPage() {
        return this.marketPage;
    }

    public MarketHolder getMarketHolder() {
        return this.marketHolder;
    }

    public MarketItem getMarketItem() {
        return this.marketItem;
    }

    private ItemStack createButton(ItemStack itemStack, String name, String lore, String action) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(lore));

        // 添加动作标识
        meta.getPersistentDataContainer().set(
                new NamespacedKey(AwesomeMarket.getInstance(), "gui_action"),
                PersistentDataType.STRING,
                action
        );

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
