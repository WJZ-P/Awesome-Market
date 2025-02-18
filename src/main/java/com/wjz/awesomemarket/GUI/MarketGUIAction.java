package com.wjz.awesomemarket.GUI;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.SortType;
import com.wjz.awesomemarket.constants.StorageType;
import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.sql.Mysql;
import com.wjz.awesomemarket.utils.GUI;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.MarketTools;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.time.Instant;

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
        public void action(Player player, int slot, InventoryClickEvent event) {
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
            ClickType clickType = event.getClick();
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();

            if (clickType.isLeftClick()) {//点击鼠标左键
                if (!clickType.isShiftClick()) {
                    //不可以买自己的商品
                    if (marketHolder.getMarketItem(slot).getSellerName().equals(player.getName())) {
                        player.sendMessage(Log.getString("tip.can-not-buy-self"));
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        return;
                    }
                    //调用confirmGUI。
                    GUI.openConfirm(player, marketHolder, slot);//传入UI中的具体物品。
                } else {
                    //shift+左键，下架物品
                    if (!marketHolder.getMarketItem(slot).getSellerName().equals(player.getName())) {
                        //只有自己可以下架自己的物品
                        return;
                    }

                    //下面准备下架物品
                    MarketItem marketItem = marketHolder.getMarketItem(slot);
                    if (Mysql.deleteMarketItem(marketItem.getId())) {
                        //如果玩家背包没满的话就可以放到背包里
                        if (player.getInventory().firstEmpty() != -1) {
                            player.getInventory().addItem(marketItem.getItemStack());
                            player.sendMessage(Log.getString("tip.unlisted-success"));
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
                                //物品放入暂存库内,存储类型设置为下架。可以用异步处理
                                Mysql.addItemToTempStorage(player.getName(), player.getName(), MarketTools.serializeItem(marketItem.getItemStack()),
                                        String.valueOf(marketItem.getItemStack().getType()), Instant.now().getEpochSecond(),
                                        marketItem.getPrice(), String.valueOf(marketItem.getPriceType()), String.valueOf(StorageType.DELISTED));
                            });
                            player.sendMessage(Log.getString("tip.unlisted-storage"));
                        }
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
                        //下架完成后要重新打开市场UI
                        GUI.openMarket(player, marketHolder.getCurrentPage());

                    } else {
                        //删除失败
                        player.sendMessage(Log.getString("tip.unlisted-fail"));
                        return;
                    }
                }
            } else if (clickType.isRightClick()) {//点击鼠标右键
                if (!clickType.isShiftClick()) {
                    //鼠标右键就只显示该类物品
                    marketHolder.setItemType(String.valueOf(marketHolder.getMarketItem(slot).getItemStack().getType()));
                    marketHolder.reload();
                } else {
                    //shift+右键 显示卖家所有商品
                    marketHolder.setSellerName(marketHolder.getMarketItem(slot).getSellerName());
                    marketHolder.reload();
                }
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5f, 1.5f);
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
            //点击恢复默认排序
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            marketHolder.setItemType(null);
            marketHolder.setSellerName(null);
            marketHolder.setSortType(SortType.TIME_DESC);
            marketHolder.setPriceType(PriceType.ALL);
            marketHolder.reload();

            //播放一点声音
            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
        }
    },
    STATISTIC{
        public void action(Player player, int slot, InventoryClickEvent event) {

        }
    },
    SORT_TYPE {
        public void action(Player player, int slot, InventoryClickEvent event) {
            MarketHolder marketHolder = (MarketHolder) player.getOpenInventory().getTopInventory().getHolder();
            SortType sortType = marketHolder.getSortType();
            marketHolder.setSortType(sortType.next());//切换到下一个排序类型
            marketHolder.reload();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, // 清脆的"叮"声
                    1.0f, (float) (0.8 + 0.1 * sortType.ordinal())); // 根据排序类型改变音高
        }
    }, PRICE_TYPE {
        public void action(Player player, int slot, InventoryClickEvent event) {
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
