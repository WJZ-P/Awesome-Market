package com.wjz.awesomemarket.utils;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;

public class CurrencyManager {
    public static Economy economy = null;
    public static PlayerPointsAPI ppAPI = null;

    public static void setEconomy(Economy eco) {
        economy = eco;
    }

    public static void setPpAPI(PlayerPointsAPI pp) {
        ppAPI = pp;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static PlayerPointsAPI getPpAPI() {
        return ppAPI;
    }
}
