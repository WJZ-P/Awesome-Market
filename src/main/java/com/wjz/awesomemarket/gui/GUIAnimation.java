package com.wjz.awesomemarket.gui;

import com.wjz.awesomemarket.AwesomeMarket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * 动画类
 */
public class GUIAnimation {
    private static final Map<Inventory, BukkitTask> map = new HashMap<>();

    private final Inventory inventory;
    private final int[] liteCirclePath = //圆圈路径
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 25, 24, 23, 22, 21, 20, 19, 18, 9};
    private final ItemStack liteCircleItem;

    public GUIAnimation(Inventory inventory) {
        this.inventory = inventory;//设定好容器
        ItemStack itemStack = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1);//设定好物品
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("");//设定好物品名字
        itemStack.setItemMeta(meta);
        liteCircleItem = itemStack;
    }

    //执行小圈圈动画
    public void runLiteCircleAnimate(long delay, long interval) {
        if (map.containsKey(inventory)) {
            return; //  如果该 Inventory 已经有动画任务在运行，  则直接返回，  避免重复启动
        }
        int[] currentIndex = {0, 1, 2, 3, 4};

        //  需要传入插件实例 plugin
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(AwesomeMarket.getInstance(), () -> {
            //在这里编写动画更新逻辑
            //首先清除掉原来的动画物品
            cleanItems(liteCirclePath);
            //接下来设置动画
            for (int i = 0; i < currentIndex.length; i++) {
                inventory.setItem(liteCirclePath[currentIndex[i]], liteCircleItem);
                //更新索引
                currentIndex[i] = (currentIndex[i] + 1) % liteCirclePath.length;
            }
        }, delay, interval);
        map.put(inventory, task);
    }

    public static void stop(Inventory inventory) {
        if (map.containsKey(inventory)) {
            map.get(inventory).cancel();
            map.remove(inventory);
        }
    }

    private void cleanItems(int[] list) {
        for (Integer integer : list) {
            inventory.setItem(integer, new ItemStack(Material.AIR));
        }
    }

}
