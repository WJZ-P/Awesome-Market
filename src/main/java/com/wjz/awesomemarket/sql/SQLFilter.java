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
    private String viewer;//交易单据里面需要用到的对方。也就是查看owner与viewer之间的交易记录用

    public SQLFilter(SortType sortType, PriceType priceType, String seller, String item_type, int page) {
        this.sortType = sortType;
        this.priceType = priceType;
        this.page = page;
        this.seller = seller;
        this.item_type = item_type;
    }

    public SQLFilter(String owner, String viewer, SortType sortType, PriceType priceType, TradeType tradeType, int page) {
        this.sortType = sortType;
        this.priceType = priceType;
        this.page = page;
        this.seller = owner;
        this.buyer = owner;
        this.tradeType = tradeType;
        this.viewer = viewer;
    }

    public String getLimit() {
        return sortType.toSQL(tradeType != null);
    }

    public String getCondition() {
        if (tradeType != null) {//说明查询的是交易记录表
            String condition = null;
            switch (tradeType) {
                case ALL:
                    condition = viewer == null ? " AND (seller = '%owner%' OR buyer = '%owner%') " :
                            " AND ( (seller = '%owner%' AND buyer = '%viewer%') OR (seller = '%viewer%' AND buyer= '%owner%' ) ) ";
                    break;
                case SELL:
                    condition = viewer == null ? " AND seller = '%owner%' " : "AND (seller = '%owner%' AND buyer = '%viewer%') ";
                    break;
                case BUY:
                    condition = viewer == null ? " AND buyer = '%owner%' " : "AND (buyer = '%owner%' AND seller= '%viewer%' ) ";
                    break;
            }
            return new StringBuilder().append("WHERE 1=1 ")
                    .append(priceType == null ? "" : priceType.toSQL())
                    .append(condition.replace("%owner%", seller).replace("%viewer%", viewer == null ? "" : viewer))
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
