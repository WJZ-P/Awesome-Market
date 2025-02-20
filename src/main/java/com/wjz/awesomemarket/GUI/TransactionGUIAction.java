package com.wjz.awesomemarket.GUI;

import com.wjz.awesomemarket.constants.SortType;
import com.wjz.awesomemarket.inventoryHolder.TransactionHolder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum TransactionGUIAction {
    MARKET {
        @Override
        public void action(TransactionHolder transactionHolder, int slot) {
            //打开市场
            Player opener = transactionHolder.getOpener();//交易打开者
            opener.openInventory(transactionHolder.getMarketHolder().getInventory());
            opener.playSound(opener.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        }
    },
    PREV_PAGE {
        @Override
        public void action(TransactionHolder transactionHolder, int slot) {
            //返回上一页
            if (transactionHolder.turnPrevPage()) {
                transactionHolder.getOpener().playSound(transactionHolder.getOpener().getLocation(),
                        Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            } else {
                transactionHolder.getOpener().playSound(transactionHolder.getOpener().getLocation(),
                        Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    },
    NEXT_PAGE {
        @Override
        public void action(TransactionHolder transactionHolder, int slot) {
            //翻到下一页
            if (transactionHolder.turnNextPage()) {
                transactionHolder.getOpener().playSound(transactionHolder.getOpener().getLocation(),
                        Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            } else {
                transactionHolder.getOpener().playSound(transactionHolder.getOpener().getLocation(),
                        Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    },
    SORT_TYPE {
        public void action(TransactionHolder transactionHolder, int slot) {
            SortType sortType = transactionHolder.getSortType();
            transactionHolder.setSortType(sortType.next());//切换到下一个排序类型
            transactionHolder.setCurrentPage(1);
            transactionHolder.reload();
            transactionHolder.getOpener().playSound(transactionHolder.getOpener().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, // 清脆的"叮"声
                    1.0f, (float) (0.8 + 0.1 * sortType.ordinal())); // 根据排序类型改变音高
        }
    },
    PRICE_TYPE {
        public void action(TransactionHolder transactionHolder, int slot) {
            transactionHolder.setPriceType(transactionHolder.getPriceType().next());
            transactionHolder.setCurrentPage(1);
            transactionHolder.reload();
            transactionHolder.getOpener().playSound(transactionHolder.getOpener(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
        }
    },
    TRADE_TYPE{
        public void action(TransactionHolder transactionHolder, int slot) {
            transactionHolder.setTradeType(transactionHolder.getTradeType().next());
            transactionHolder.setCurrentPage(1);
            transactionHolder.reload();
            transactionHolder.getOpener().playSound(transactionHolder.getOpener(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
        }
    },
    TRANSACTION{
        public void action(TransactionHolder transactionHolder, int slot) {
            Player player= transactionHolder.getOpener();
            player.playSound(player.getLocation(),Sound.UI_LOOM_TAKE_RESULT, 1.0F, 1.0F);
        }
    };

    public static TransactionGUIAction getType(String actionString) {
        return TransactionGUIAction.valueOf(actionString.toUpperCase());
    }

    public abstract void action(TransactionHolder transactionHolder, int slot);
}
