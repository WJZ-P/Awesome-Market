package com.wjz.awesomemarket.cache;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.sql.Mysql;
import com.wjz.awesomemarket.sql.MysqlType;
import com.wjz.awesomemarket.sql.SQLFilter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class MarketCache {
    private static final int ITEMS_PER_PAGE = 45;
    private static final Map<String, Integer> cacheMap = new HashMap<>();//页数表
    private static final Map<String, Long> timeMap = new HashMap<>();//时间表

    public static int getTotalPages(SQLFilter sqlFilter, boolean forceRefresh) {
        //五分钟刷新一次缓存
        if (forceRefresh || System.currentTimeMillis() - timeMap.getOrDefault(sqlFilter.toString(), 0L) > 300_000) {
            //创建异步任务去更新
            Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(), () -> {
                //这里统计数据需要根据filter的条件来统计
                int count = Mysql.getItemsCountWithFilter(MysqlType.ON_SELL_ITEMS_TABLE, sqlFilter);
                cacheMap.put(sqlFilter.toString(), (int) Math.ceil((double) count / ITEMS_PER_PAGE));
                timeMap.put(sqlFilter.toString(), System.currentTimeMillis());
            });
        }
        if (cacheMap.get(sqlFilter.toString()) == null) {
            //说明是初次加载
            int count=Mysql.getItemsCountWithFilter(MysqlType.ON_SELL_ITEMS_TABLE, sqlFilter);
            int page=(int) Math.ceil((double) count / ITEMS_PER_PAGE);
            cacheMap.put(sqlFilter.toString(), page);
            return page;
        }
        return cacheMap.get(sqlFilter.toString());
    }
}
