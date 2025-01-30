package com.wjz.awesomemarket.cache;

import com.wjz.awesomemarket.AwesomeMarket;
import com.wjz.awesomemarket.utils.Mysql;
import org.bukkit.Bukkit;

public class MarketCache {
    private static int totalPages=1;
    private static long lastUpdate=0;

    public static int getTotalPages(boolean forceRefresh){
        //五分钟刷新一次缓存
        if(forceRefresh||System.currentTimeMillis()-lastUpdate>300_000){
            //创建异步任务去更新
            Bukkit.getScheduler().runTaskAsynchronously(AwesomeMarket.getInstance(),()->{
               int count= Mysql.getTotalItemsCount();
            });
        }
    }
}
