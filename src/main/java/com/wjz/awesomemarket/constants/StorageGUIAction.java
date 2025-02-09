package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.inventoryHolder.StorageHolder;
import org.bukkit.Sound;

public enum StorageGUIAction {
    PREV_PAGE {
        @Override
        public void action(StorageHolder storageHolder) {
            //返回上一页
            if(storageHolder.turnPrevPage()){
                storageHolder.getPlayer().playSound(storageHolder.getPlayer().getLocation(),
                        Sound.UI_BUTTON_CLICK,1.0f,1.0f);
            }
            else{
                storageHolder.getPlayer().playSound(storageHolder.getPlayer().getLocation(),
                        Sound.ENTITY_VILLAGER_NO,1.0f,1.0f);
            }
        }
    }, NEXT_PAGE {
        @Override
        public void action(StorageHolder storageHolder) {
            //翻到下一页
            if(storageHolder.turnNextPage()){
                storageHolder.getPlayer().playSound(storageHolder.getPlayer().getLocation(),
                        Sound.UI_BUTTON_CLICK,1.0f,1.0f);
            }
            else{
                storageHolder.getPlayer().playSound(storageHolder.getPlayer().getLocation(),
                        Sound.ENTITY_VILLAGER_NO,1.0f,1.0f);
            }
        }
    }, MARKET {
        @Override
        public void action(StorageHolder storageHolder) {
            //打开市场
            storageHolder.getPlayer().openInventory(new MarketHolder(storageHolder.getMarketPage()).getInventory());
        }
    };

    public static StorageGUIAction getType(String actionString) {
        return StorageGUIAction.valueOf(actionString);
    }

    public abstract void action(StorageHolder storageHolder);
}
