package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.utils.GUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public enum MarketGUIAction {
    PREV_PAGE {
        @Override
        public void action(Player player,int slot) {
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            if (marketHolder != null && !marketHolder.turnPrevPage()) {//第一页无法上一页
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.5f);
        }

    }, NEXT_PAGE {
        @Override
        public void action(Player player,int slot) {
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            if (marketHolder != null && !marketHolder.turnNextPage()) {//最后一页无法下一页
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.5f);
        }
    },
    COMMODITY{//商品
        @Override
        public void action(Player player,int slot) {
            //调用confirmGUI。
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            //然后获取holder中独特的marketItem传入
            GUI.openConfirm(player,marketHolder,slot);//传入UI中的具体物品。
        }
    },
    STORAGE{
        @Override
        public void action(Player player,int slot){
            //调用storageGUI。

        }
    }
    ;

    public abstract void action(Player player,int slot);

    public static MarketGUIAction getType(String type) {
        return MarketGUIAction.valueOf(type.toUpperCase());
    }
}
