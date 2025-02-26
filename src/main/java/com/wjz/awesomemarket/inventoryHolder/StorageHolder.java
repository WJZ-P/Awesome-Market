package com.wjz.awesomemarket.inventoryHolder;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.constants.SkullType;
import com.wjz.awesomemarket.constants.StorageType;
import com.wjz.awesomemarket.entity.StorageItem;
import com.wjz.awesomemarket.sql.MysqlType;
import com.wjz.awesomemarket.sql.SQLFilter;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.sql.Mysql;
import com.wjz.awesomemarket.utils.UsefulTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class StorageHolder implements InventoryHolder {
    private final MarketHolder marketHolder;
    private final Inventory storageGUI;
    private int currentPage = 1;
    private final Player opener;//这个holder的打开者
    private final OfflinePlayer owner;//这个容器的拥有者
    private int maxPage;
    private List<StorageItem> storageItems;
    public static final String PREV_PAGE_KEY = "prev_page";
    public static final String NEXT_PAGE_KEY = "next_page";
    public static final String MARKET_KEY = "market";
    public static final String WAITING_FOR_CLAIM_KEY = "waiting_for_claim";
    public static final String DELISTED_KEY = "delisted_item";
    private static final int PREV_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int MARKET_SLOT = 49;
    public static final NamespacedKey GUI_ACTION_KEY = new NamespacedKey
            (AwesomeMarket.getPlugin(AwesomeMarket.class), MarketHolder.ACTION_KEY);

    private final AtomicBoolean canTurnPage = new AtomicBoolean(true);

    @Override
    public @NotNull Inventory getInventory() {
        return storageGUI;
    }

    public Player getOpener() {
        return opener;
    }

    public MarketHolder getMarketHolder() {
        return marketHolder;
    }

    public StorageItem getStorageItem(int slot) {
        return storageItems.get(slot);
    }

    public StorageHolder(Player player, MarketHolder marketHolder) {
        this.marketHolder = marketHolder;
        this.opener = player;
        this.owner = player;
        this.maxPage = (int) Math.ceil((double) Mysql.getStorageTotalItemsCount(player.getName()) / 45);

        storageGUI = Bukkit.createInventory(this, 54, Log.getString("storage-gui.title").replace("%player%", player.getName()));

        //以灰色玻璃板作为默认填充
        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = background.getItemMeta();
        bgMeta.setDisplayName(" ");
        background.setItemMeta(bgMeta);
        //填充背景板
        for (int i = 0; i < 54; i++) {
            storageGUI.setItem(i, background);
        }
        //加载功能栏
        this.loadFuncBar();
        //加载物品
        this.loadAndSetStorageItems(owner);
    }

    public StorageHolder(OfflinePlayer owner, Player opener, MarketHolder marketHolder) {
        this.marketHolder = marketHolder;
        this.opener = opener;
        this.owner = owner;
        this.maxPage = (int) Math.ceil((double) Mysql.getStorageTotalItemsCount(owner.getName()) / 45);

        storageGUI = Bukkit.createInventory(this, 54, Log.getString("storage-gui.title").replace("%player%", owner.getName()));

        //以灰色玻璃板作为默认填充
        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = background.getItemMeta();
        bgMeta.setDisplayName(" ");
        background.setItemMeta(bgMeta);
        //填充背景板
        for (int i = 0; i < 54; i++) {
            storageGUI.setItem(i, background);
        }

        //加载功能栏
        this.loadFuncBar();
        //加载物品
        this.loadAndSetStorageItems(owner);
    }

    public boolean turnPrevPage() {
        if (currentPage <= 1 || !canTurnPage.get()) return false;
        currentPage -= 1;
        //重新渲染物品和功能栏,渲染功能栏是为了修改当前页数
        this.loadAndSetStorageItems(owner);
        this.loadFuncBar();
        return true;
    }

    public boolean turnNextPage() {
        if (!canTurnPage.get() || currentPage >= maxPage) return false;//不允许翻页就直接返回false

        currentPage += 1;
        this.loadAndSetStorageItems(owner);
        this.loadFuncBar();
        return true;
    }

    public void loadAndSetStorageItems(OfflinePlayer owner) {
        //加载物品的时候不允许翻页
        this.canTurnPage.set(false);
        //从数据库中加载物品，使用异步
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            List<StorageItem> storageList = Mysql.getStorageItems(owner, currentPage);
            this.storageItems = storageList;
            //然后载入物品
            //获取完毕后，切换回主线程更新UI
            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                int slot = 0;
                for (StorageItem storageItem : storageList) {
                    if (slot >= 45) break;

                    //下面根据不同的storage类型进行操作
                    switch (storageItem.getStorageType()) {
                        case WAITING_FOR_CLAIM: {
                            //等待领取的物品

                            //要给itemstack设置一些属性
                            ItemStack itemStack = storageItem.getItemStack().clone();
                            ItemMeta meta = itemStack.getItemMeta();
                            List<String> oldLore = itemStack.getLore();
                            if (oldLore == null) oldLore = new ArrayList<>();
                            List<String> lore = Log.getStringList("storage-gui.item");
                            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(storageItem.getPurchaseTime()), ZoneId.systemDefault());
                            for (int i = 0; i < lore.size(); i++) {
                                lore.set(i, lore.get(i)
                                        .replace("%buy_time%", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                        .replace("%price%", String.valueOf(storageItem.getPrice()))
                                        .replace("%currency%", storageItem.getPriceType().getName())
                                        .replace("%player%", storageItem.getSeller()));
                            }
                            //商品lore添加完毕后追加到原lore后
                            oldLore.addAll(lore);
                            meta.setLore(oldLore);
                            //添加商品的NBT标签
                            meta.getPersistentDataContainer().set(GUI_ACTION_KEY, PersistentDataType.STRING, WAITING_FOR_CLAIM_KEY);
                            //设置好的meta数据写入到item中
                            itemStack.setItemMeta(meta);
                            storageGUI.setItem(slot, itemStack);
                            slot++;
                            break;
                        }
                        case DELISTED: {
                            //下架的物品
                            ItemStack itemStack = storageItem.getItemStack().clone();
                            ItemMeta meta = itemStack.getItemMeta();
                            List<String> oldLore = itemStack.getLore();
                            if (oldLore == null) oldLore = new ArrayList<>();
                            List<String> lore = Log.getStringList("storage-gui.unlisted-item");
                            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(storageItem.getPurchaseTime()), ZoneId.systemDefault());
                            for (int i = 0; i < lore.size(); i++) {
                                lore.set(i, lore.get(i)
                                        .replace("%time%", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                        .replace("%price%", String.valueOf(storageItem.getPrice()))
                                        .replace("%currency%", storageItem.getPriceType().getName())
                                        .replace("%player%", storageItem.getSeller()));
                            }
                            //商品lore添加完毕后追加到原lore后
                            oldLore.addAll(lore);
                            meta.setLore(oldLore);
                            //添加商品的NBT标签
                            meta.getPersistentDataContainer().set(GUI_ACTION_KEY, PersistentDataType.STRING, DELISTED_KEY);
                            //设置好的meta数据写入到item中
                            itemStack.setItemMeta(meta);
                            storageGUI.setItem(slot, itemStack);
                            slot++;
                            break;
                        }

                        case RECEIPT:
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + storageItem.getStorageType());
                    }


                }
                if (slot < 45) {//物品不足一页时填充
                    //以灰色玻璃板作为默认填充
                    ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                    ItemMeta bgMeta = background.getItemMeta();
                    bgMeta.setDisplayName(" ");
                    background.setItemMeta(bgMeta);
                    for (int i = slot; i < 45; i++) {
                        storageGUI.setItem(i, background);
                    }
                }
                this.canTurnPage.set(true);
            });
        });
    }

    private void loadFuncBar() {
        Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
            //设置页数
            this.maxPage = (int) Math.ceil((double) Mysql.getStorageTotalItemsCount(owner.getName()) / 45);
            //功能栏
            ItemStack prevBtn = UsefulTools.createNavItemStack(new ItemStack(Material.ARROW), PREV_PAGE_KEY, Log.getString("storage-gui.prev-page"),
                    Collections.singletonList(String.format(Log.getString("storage-gui.prev-page-lore"), currentPage, maxPage)), GUI_ACTION_KEY);
            ItemStack nextBtn = UsefulTools.createNavItemStack(new ItemStack(Material.ARROW), NEXT_PAGE_KEY, Log.getString("storage-gui.next-page"),
                    Collections.singletonList(String.format(Log.getString("storage-gui.next-page-lore"), currentPage, maxPage)), GUI_ACTION_KEY);
            ItemStack marketBtn = UsefulTools.createNavItemStack(UsefulTools.getCustomSkull(SkullType.YELLOW_MARKET_DATA), MARKET_KEY, Log.getString("storage-gui.market"), null, GUI_ACTION_KEY);

            Bukkit.getScheduler().runTask(AwesomeMarket.getInstance(), () -> {
                storageGUI.setItem(PREV_PAGE_SLOT, prevBtn);
                storageGUI.setItem(NEXT_PAGE_SLOT, nextBtn);
                storageGUI.setItem(MARKET_SLOT, marketBtn);
            });
        });

    }
}
