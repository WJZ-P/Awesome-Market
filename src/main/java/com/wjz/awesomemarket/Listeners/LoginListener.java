package com.wjz.awesomemarket.Listeners;

import com.wjz.awesomemarket.sql.Mysql;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        //玩家上线时，处理确认订单
        Player player = event.getPlayer();
        Mysql.claimTransaction(player.getName());
    }
}
