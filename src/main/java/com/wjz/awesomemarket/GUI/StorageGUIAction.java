package com.wjz.awesomemarket.GUI;

import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.inventoryHolder.StorageHolder;
import com.wjz.awesomemarket.sql.Mysql;
import com.wjz.awesomemarket.utils.Log;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum StorageGUIAction {
    PREV_PAGE {
        @Override
        public void action(StorageHolder storageHolder, int slot) {
            //返回上一页
            if (storageHolder.turnPrevPage()) {
                storageHolder.getOpener().playSound(storageHolder.getOpener().getLocation(),
                        Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            } else {
                storageHolder.getOpener().playSound(storageHolder.getOpener().getLocation(),
                        Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    },
    NEXT_PAGE {
        @Override
        public void action(StorageHolder storageHolder, int slot) {
            //翻到下一页
            if (storageHolder.turnNextPage()) {
                storageHolder.getOpener().playSound(storageHolder.getOpener().getLocation(),
                        Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            } else {
                storageHolder.getOpener().playSound(storageHolder.getOpener().getLocation(),
                        Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    },
    WAITING_FOR_CLAIM {
        @Override
        public void action(StorageHolder storageHolder, int slot) {
            takeItemToInv(storageHolder, slot);
        }
    },
    DELISTED_ITEM {
        @Override
        public void action(StorageHolder storageHolder, int slot) {
            takeItemToInv(storageHolder, slot);
        }
    },
    MARKET {
        @Override
        public void action(StorageHolder storageHolder, int slot) {
            //打开市场
            Player opener=storageHolder.getOpener();
            storageHolder.getMarketHolder().reload();
            opener.openInventory(storageHolder.getMarketHolder().getInventory());
            opener.playSound(storageHolder.getOpener().getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        }
    };

    public static StorageGUIAction getType(String actionString) {
        return StorageGUIAction.valueOf(actionString.toUpperCase());
    }

    private static void takeItemToInv(StorageHolder storageHolder, int slot){
        //物品入库
        Player player = storageHolder.getOpener();
        //先检查玩家背包是否已满
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Log.getString("get_storage_item_fail"));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        //处理入库操作
        if (Mysql.deleteStorageItem(storageHolder.getStorageItem(slot).getId())) {
            //说明物品删除成功，那就把物品给玩家
            player.getInventory().addItem(storageHolder.getStorageItem(slot).getItemStack());
            player.sendMessage(Log.getString("take_item_from_storage"));
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
            //然后我们还需要把暂存库刷新
            storageHolder.loadAndSetStorageItems(player);
        } else {
            player.sendMessage("[全球市场] 出现错误，请联系管理员");
        }
    }

    public abstract void action(StorageHolder storageHolder, int slot);
}
