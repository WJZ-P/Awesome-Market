package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.utils.Log;

public enum TradeType {
    BUY {
        @Override
        public String getName() {
            return Log.getString("trade-type.buy");
        }
    }, SELL {
        @Override
        public String getName() {
            return Log.getString("trade-type.sell");
        }
    }, ALL {
        @Override
        public String getName() {
            return Log.getString("trade-type.all");
        }
    };

    public abstract String getName();
}
