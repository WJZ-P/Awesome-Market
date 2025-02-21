package com.wjz.awesomemarket.GUI;

import com.wjz.awesomemarket.AwesomeMarket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动画类
 */
public class GUIAnimation {
    private final Map<Inventory, BukkitTask> map = new HashMap<>();

    private final Inventory inventory;
    private final List<Integer> liteCirclePath = Arrays.asList(//圆圈路径
            0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 25, 24, 23, 22, 21, 20, 19, 18, 9
    );
    private final ItemStack liteCircleItem;

    GUIAnimation(Inventory inventory) {
        this.inventory = inventory;//设定好容器
        this.liteCircleItem = new ItemStack(Material.PINK_STAINED_GLASS_PANE, 1);//设定好物品
        ItemMeta meta= liteCircleItem.getItemMeta();
        meta.setDisplayName("");//设定好物品名字
        liteCircleItem.setItemMeta(meta);
    }

    //执行小圈圈动画
    public void runLiteCircleAnimate() {
        if (map.containsKey(inventory)) {
            return; //  如果该 Inventory 已经有动画任务在运行，  则直接返回，  避免重复启动
        }
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(AwesomeMarket.getInstance(), new BukkitRunnable() { //  需要传入插件实例 plugin
            public void run() {
                //在这里编写动画更新逻辑

            }
        }, 0L, 4L); // 0 delay,每 2 ticks 执行一次
    }
}
