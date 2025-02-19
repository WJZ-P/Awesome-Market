package com.wjz.awesomemarket.sql;

import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.SortType;
import com.wjz.awesomemarket.constants.TradeType;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

/**
 * SQL Filter SQL语句，用于过滤数据
 */
public class SQLFilter {
    private final SortType sortType;//根据筛选类型搜索
    private final PriceType priceType;//根据价格类型搜索
    private final int page;//根据页数来搜索
    private final String seller;//根据卖家搜索
    private String item_type;//根据商品类型搜索
    private TradeType tradeType;//根据交易类型搜索，仅交易可用。
    private String buyer;//根据买家搜索

    public SQLFilter(SortType sortType, PriceType priceType, String seller, String item_type, int page) {
        this.sortType = sortType;
        this.priceType = priceType;
        this.page = page;
        this.seller = seller;
        this.item_type = item_type;
    }

    public SQLFilter(String seller, String buyer, SortType sortType, PriceType priceType, TradeType tradeType, int page) {
        this.sortType = sortType;
        this.priceType = priceType;
        this.page = page;
        this.seller = seller;
        this.buyer = buyer;
        this.tradeType = tradeType;
    }

    public String getLimit() {
        return sortType.toSQL();
    }

    public String getCondition() {
        if (tradeType != null) {//说明查询的是交易记录表
            String condition = null;
            switch (tradeType) {
                case ALL -> condition = "AND (seller = '%owner%' OR buyer = '%owner%') ";
                case SELL -> condition = "AND seller = '%seller%' ";
                case BUY -> condition = "AND buyer = '%buyer%' ";
            }
            return new StringBuilder().append("WHERE 1=1 ")
                    .append(sortType == null ? "" : sortType.toSQL())
                    .append(priceType == null ? "" : priceType.toSQL())
                    .append(condition.replace("%owner%", seller))
                    .toString();
        } else {
            //说明查询的是商店物品
            return new StringBuilder().append("WHERE 1=1 ")
                    .append(priceType == null ? "" : priceType.toSQL())
                    .append(seller == null ? "" : " AND seller = '" + seller + "'")
                    .append(item_type == null ? "" : " AND item_type ='" + item_type + "'")
                    .toString();
        }
    }

    public int getOffset() {
        return 45 * (page - 1);
    }

    @Override
    public String toString() {
        return new StringJoiner("|")
                .add(sortType != null ? sortType.name() : "null")
                .add(priceType != null ? priceType.name() : "null")
                .add("page=" + page)
                .add(seller != null ? seller : "null")
                .add(buyer != null ? seller : "null")
                .add(item_type != null ? item_type : "null")
                .toString();
    }
}
