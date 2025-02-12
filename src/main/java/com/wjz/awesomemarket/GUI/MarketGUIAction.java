package com.wjz.awesomemarket.GUI;

import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.SortType;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.utils.GUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public enum MarketGUIAction {
    PREV_PAGE {
        @Override
        public void action(Player player, int slot, InventoryClickEvent event) {
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            if (marketHolder != null && !marketHolder.turnPrevPage()) {//第一页无法上一页
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.5f);
        }

    }, NEXT_PAGE {
        @Override
        public void action(Player player, int slot,InventoryClickEvent event) {
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            if (marketHolder != null && !marketHolder.turnNextPage()) {//最后一页无法下一页
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.5f);
        }
    },
    COMMODITY {//商品

        @Override
        public void action(Player player, int slot, InventoryClickEvent event) {
            ClickType clickType=event.getClick();
            InventoryAction action=event.getAction();
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            if (clickType.isLeftClick()) {
                //调用confirmGUI。
                GUI.openConfirm(player, marketHolder, slot);//传入UI中的具体物品。
            } else if (clickType.isRightClick() && !clickType.isShiftClick()) {
                //鼠标右键就筛选这名玩家的所有物品。
                marketHolder.setSellerName(marketHolder.getMarketItem(slot).getSellerName());
                marketHolder.reload();
                player.openInventory(marketHolder.getInventory());
            }else if (clickType.isShiftClick() && clickType.isRightClick()){
                //shift+右键
                marketHolder.setItemType(String.valueOf(marketHolder.getMarketItem(slot).getItemStack().getType()));
                marketHolder.reload();
                player.openInventory(marketHolder.getInventory());
            }

        }
    },
    STORAGE {
        @Override
        public void action(Player player, int slot, InventoryClickEvent event) {
            GUI.openStorage(player);
        }
    },
    HELP_BOOK {
        public void action(Player player, int slot, InventoryClickEvent event) {
            //播放一点声音
            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
        }
    },
    SORT_TYPE {
        public void action(Player player, int slot,InventoryClickEvent event) {
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            SortType sortType = marketHolder.getSortType();
            marketHolder.setSortType(sortType.next());//切换到下一个排序类型
            marketHolder.reload();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, // 清脆的"叮"声
                    1.0f, (float) (0.8 + 0.1 * sortType.ordinal())); // 根据排序类型改变音高
        }
    }, PRICE_TYPE {
        public void action(Player player, int slot,InventoryClickEvent event) {
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            marketHolder.setPriceType(marketHolder.getPriceType().next());
            marketHolder.reload();
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

        }
    };

    public abstract void action(Player player, int slot, InventoryClickEvent event);

    public static MarketGUIAction getType(String type) {
        return MarketGUIAction.valueOf(type.toUpperCase());
    }
}
