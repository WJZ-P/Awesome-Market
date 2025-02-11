package com.wjz.awesomemarket.sql;

import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.SortType;
import org.bukkit.entity.Player;

/**
 * SQL Filter SQL语句，用于过滤数据
 */
public class SQLFilter {
    private final SortType sortType;//根据筛选类型搜索
    private final PriceType priceType;//根据价格类型搜索
    private final int page;//根据页数来搜索
    private final String seller;//根据玩家搜索
    private final String item_type;//根据商品类型搜索

    public SQLFilter(SortType sortType, PriceType priceType, String seller, String item_type, int page) {
        this.sortType = sortType;
        this.priceType = priceType;
        this.page = page;
        this.seller = seller;
        this.item_type = item_type;
    }

    public String getLimit() {
        return sortType.toSQL();
    }

    public String getCondition() {
        return new StringBuilder().append("WHERE 1=1 ")
                .append(priceType == null ? "" : priceType.toSQL())
                .append(seller == null ? "" : " AND seller = " + seller)
                .append(item_type == null ? "" : " AND item_type=" + item_type)
                .toString();
    }

    public int getOffset() {
        return page - 1;
    }
}
