package com.wjz.awesomemarket.entity;

import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.MarketTools;
import com.wjz.awesomemarket.sql.Mysql;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;

public class MarketItem {
    private final ItemStack itemStack;
    private final String seller;//出售者
    private final double price;//价格
    private final PriceType priceType;//价格类型
    private final long id;//id是数据库中的id。根据这个索引物品。
    private final long onSellTime;


    public MarketItem(ItemStack itemStack, String seller, double price, PriceType priceType, long id, long time) {
        this.itemStack = itemStack;
        this.id = id;
        this.price = price;
        this.seller = seller;
        this.priceType = priceType;
        this.onSellTime = time;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public double getPrice() {
        return this.price;
    }

    public long getId() {
        return this.id;
    }

    public long getOnSellTime() {return onSellTime;}

    public PriceType getPriceType() {return this.priceType;}

    public String getSellerName() {
        return this.seller;
    }

    /**
     * 购买商品，成功返回true失败返回false。
     */
    public boolean purchase(Player player) {
        //要先判断玩家是否满足购买需求
        double playerEconomy = priceType.look(player);//查询玩家当前货币
        if (playerEconomy < price) {
            String buyFail = Log.getString("buy_fail.lack-of-eco")
                    .replace("%money%", String.format("%.2f", price))
                    .replace("%currency%", priceType.getName());

            player.sendMessage(buyFail);
            return false;
        }

        //下面开始走付款流程。
        if (Mysql.deleteMarketItem(this.id)) {
            priceType.take(player, price);
            //成功删除了物品，并且成功扣款

            //这里直接使用翻译键，后面再做适配
            String buySuccess = Log.getString("buy_success")
                    .replace("%money%", String.format("%.2f", price))
                    .replace("%currency%", priceType.getName())
                    .replace("%seller%", seller);//%item%留给翻译键做处理
            Component message = Component.text(buySuccess).replaceText(b -> b.matchLiteral("%item%")
                            .replacement(Component.translatable(itemStack.getType().translationKey())))
                    .color(itemStack.displayName().color()).hoverEvent(itemStack.asHoverEvent());
            player.sendMessage(message);

            //扣款之后把物品给玩家
            Inventory playerInv = player.getInventory();
            if (playerInv.firstEmpty() != -1) {
                //说明玩家的背包是没满的
                playerInv.addItem(itemStack);
            } else {
                //说明玩家背包满了
                //那就需要把物品放到暂存库里
                Mysql.addItemToTempStorage(id, player.getName(), seller, MarketTools.serializeItem(itemStack), String.valueOf(itemStack.getType()),
                        Instant.now().getEpochSecond(), price, String.valueOf(priceType));
                player.sendMessage(Log.getString("add_item_to_storage"));
            }
            //下面创建交易单数据。
            Mysql.addTradeTransaction(MarketTools.serializeItem(itemStack),
                    String.valueOf(itemStack.getType()), seller, player.getName(), String.valueOf(priceType), price);
            return true;

        } else {
            //删除物品失败了。说明物品已经被买走了
            player.sendMessage(Log.getString("buy_fail.has-been-bought"));
            return false;
        }
    }
}
