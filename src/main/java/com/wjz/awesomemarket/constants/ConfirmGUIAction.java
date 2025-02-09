package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.inventoryHolder.ConfirmHolder;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.utils.Log;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public enum ConfirmGUIAction {
    CONFIRM {
        @Override
        public void action(Player player, MarketItem marketItem) {
            //确认购买
            if (marketItem.purchase(player)) {
                //Log输出等已经在购买方法内部完成
                //负责播放声音和返回商店界面即可。

                //在玩家头上放烟花
                Location loc = player.getLocation();
                Firework fw = (Firework) player.getLocation().add(0, 2, 0).getWorld()
                        .spawnEntity(loc, EntityType.FIREWORK_ROCKET);
                FireworkMeta fwm = fw.getFireworkMeta();
                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(Color.WHITE)
                        .withFade(Color.BLUE)
                        .with(FireworkEffect.Type.BURST) //绽放效果
                        .withFlicker() //闪闪发光
                        .build();
                fwm.addEffect(effect);
                fwm.setPower(1);
                fw.setFireworkMeta(fwm);
                player.spawnParticle(Particle.BUBBLE_COLUMN_UP, loc, 30, 0.5, 0.5, 0.5, 0.1);
                player.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0F, 1.0F);
                player.playSound(player.getLocation(),Sound.ENTITY_VILLAGER_YES,1.0F,1.0F);
                player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1.5F,0.8F);
                //接下来就可以让玩家返回商店继续购买了
                player.openInventory(new MarketHolder(((ConfirmHolder) player.getOpenInventory().getTopInventory().
                        getHolder()).getMarketPage()).getInventory());
            }else{
                //购买失败了，说明东西已经被买走了
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                player.openInventory(new MarketHolder(((ConfirmHolder) player.getOpenInventory().getTopInventory().
                        getHolder()).getMarketPage()).getInventory());
            }
        }

    }, CANCEL {
        @Override
        public void action(Player player, MarketItem marketItem) {
            //取消购买
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F);
            player.openInventory(new MarketHolder(((ConfirmHolder) player.getOpenInventory().getTopInventory().
                    getHolder()).getMarketPage()).getInventory());
        }
    };

    public abstract void action(Player player, MarketItem marketItem);

    public static ConfirmGUIAction getType(String type) {
        return ConfirmGUIAction.valueOf(type.toUpperCase());//返回实例
    }

    private static final FileConfiguration langConfig = Log.langConfig;
}
