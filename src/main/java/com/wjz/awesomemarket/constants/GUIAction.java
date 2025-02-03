package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.cache.MarketCache;
import com.wjz.awesomemarket.utils.GUI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum GUIAction {
    PREV_PAGE {
        @Override
        public void action(Player player) {
            int currentPage = GUI.getPlayerPageMap(player);
            if (currentPage <= 1) {//第一页无法上一页
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            //Log.infoDirectly("当前玩家商店页数设置为" + (currentPage - 1));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,1.0f,0.5f);
            GUI.setPlayerPageMap(player, currentPage - 1);
            GUI.setPlayerPage(player, currentPage - 1);
        }

    }, NEXT_PAGE {
        @Override
        public void action(Player player) {
            int currentPage = GUI.getPlayerPageMap(player);
            int maxPage = MarketCache.getTotalPages(false);
            if (currentPage >= maxPage) {//当前是最后一页就不能换了
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            //Log.infoDirectly("当前玩家商店页数设置为" + (currentPage + 1));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,1.0f,0.5f);
            GUI.setPlayerPageMap(player, currentPage + 1);
            GUI.setPlayerPage(player, currentPage + 1);
        }
    },;

    public abstract void action(Player player);

    public static GUIAction getType(String type){
        return GUIAction.valueOf(type.toUpperCase());
    }
}
