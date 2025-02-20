package com.wjz.awesomemarket.constants;

import com.wjz.awesomemarket.utils.Log;

public enum SortType {
    TIME_DESC,
    TIME_ASC,
    PRICE_DESC,
    PRICE_ASC;

    private static final SortType[] VALUES = values();

    //获取排序方式的本地化翻译
    public String getString() {
        return Log.getString("sort." + String.valueOf(this).toLowerCase());
    }

    public String toSQL(boolean isTradeType) {
        return switch (this) {
            case PRICE_ASC -> "price ASC";
            case PRICE_DESC -> "price DESC";
            case TIME_ASC -> isTradeType ? "trade_time ASC" : "on_sell_time ASC";
            case TIME_DESC -> isTradeType ? "trade_time DESC" : "on_sell_time DESC";
            default -> throw new IllegalStateException();
        };
    }

    public SortType next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

}
