package com.wjz.awesomemarket.entity;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.StorageType;
import com.wjz.awesomemarket.sql.Mysql;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.MarketTools;
import com.wjz.awesomemarket.utils.UsefulTools;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;

public class MarketItem {
    private final ItemStack itemStack;
    private final String sellerName;//出售者
    private final double price;//价格
    private final PriceType priceType;//价格类型
    private final long id;//id是数据库中的id。根据这个索引物品。
    private final long onSellTime;


    public MarketItem(ItemStack itemStack, String seller, double price, PriceType priceType, long id, long time) {
        this.itemStack = itemStack;
        this.id = id;
        this.price = price;
        this.sellerName = seller;
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

    public long getOnSellTime() {
        return onSellTime;
    }

    public PriceType getPriceType() {
        return this.priceType;
    }

    public String getSellerName() {
        return this.sellerName;
    }

    /**
     * 购买商品，成功返回true失败返回false。
     */
    public boolean purchase(Player customer) {
        //要先判断玩家是否满足购买需求
        double playerEconomy = priceType.look(customer);//查询玩家当前货币
        if (playerEconomy < price) {
            String buyFail = Log.getString("buy_fail.lack-of-eco")
                    .replace("%money%", String.format("%.2f", price))
                    .replace("%currency%", priceType.getName());

            customer.sendMessage(buyFail);
            return false;
        }

        //下面开始走付款流程。
        if (Mysql.deleteMarketItem(this.id)) {
            priceType.take(customer, price);
            //成功删除了物品
            //这里要判断版本，低版本没有翻译键
            String buySuccess = Log.getString("buy_success")
                    .replace("%money%", String.format("%.2f", price))
                    .replace("%currency%", priceType.getName())
                    .replace("%seller%", sellerName);//%item%留给翻译键做处理
            if (UsefulTools.isVersionNewerThan("1.17")) {
                Component message = Component.text(buySuccess).replaceText(b -> b.matchLiteral("%item%")
                        .replacement(Component.translatable(itemStack.getType().translationKey())
                                .color(itemStack.displayName().color()).hoverEvent(itemStack.asHoverEvent())));
                customer.sendMessage(message);
            } else {
                customer.sendMessage(buySuccess.replace("%item%", itemStack.getType().name()));
            }
            //扣款之后把物品给玩家
            Inventory playerInv = customer.getInventory();
            boolean needStorage;//物品是否需要放入暂存箱
            if (playerInv.firstEmpty() != -1) {
                needStorage = false;
                //说明玩家的背包是没满的
                playerInv.addItem(itemStack);
            } else {
                //说明玩家背包满了
                //那就需要把物品放到暂存库里，可以使用异步操作来加入
                needStorage = true;
                customer.sendMessage(Log.getString("add_item_to_storage"));
            }

            //下面的操作都可以异步进行
            Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
                //把物品放入暂存箱
                if (needStorage)
                    Mysql.addItemToTempStorage(customer.getName(), sellerName, MarketTools.serializeItem(itemStack), String.valueOf(itemStack.getType()),
                            Instant.now().getEpochSecond(), price, String.valueOf(priceType), String.valueOf(StorageType.WAITING_FOR_CLAIM));

                //处理购物后事项，包括交易单据、统计数据
                handlePostPurchase(customer);
            });
            return true;

        } else {
            //删除物品失败了。说明物品已经被买走了
            customer.sendMessage(Log.getString("buy_fail.has-been-bought"));
            return false;
        }
    }

    //处理购物后事项，可异步处理。
    private void handlePostPurchase(Player customer) {
        //下面创建交易单数据。
        Mysql.addTradeTransaction(MarketTools.serializeItem(itemStack),
                String.valueOf(itemStack.getType()), sellerName, customer.getName(), String.valueOf(priceType), price, 0);
        //然后更新统计数据
        Mysql.upsertStatistic(customer, price, priceType, true);//这个是给买家更新数据。
        Mysql.upsertStatistic(Bukkit.getOfflinePlayer(sellerName), price, priceType, false);//这个是给卖家更新数据。
        //注意这里放入暂存箱是放入卖家的暂存箱。

        //然后需要给卖家这边发送消息，如果在线的话
        Player seller = Bukkit.getPlayer(sellerName);
        if (seller == null) return;

        //下面处理卖家在线的情况

        //告诉卖家物品已经被购买
        String msg = Log.getString("tip.item-is-sold").replace("%buyer%", customer.getName());
        //同样要做版本判断
        if(UsefulTools.isVersionNewerThan("1.17")){
        Component message = Component.text(msg).replaceText(b -> b.matchLiteral("%item%")
                .replacement(Component.translatable(itemStack.getType().translationKey())
                        .color(itemStack.displayName().color()).hoverEvent(itemStack.asHoverEvent())));
        seller.sendMessage(message);
        }
        else{
            seller.sendMessage(msg.replace("%item%", itemStack.getType().name()));
        }
        seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        seller.playSound(seller.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);

        Mysql.claimTransaction(sellerName);

    }
}
