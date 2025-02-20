package com.wjz.awesomemarket.sql;

public class MysqlType {

    public static String ON_SELL_ITEMS_TABLE = "on_selling_items";
    public static String TRANSACTIONS_TABLE = "transactions";
    public static String PLAYER_STORAGE_TABLE = "player_storage";
    public static String STATISTIC_TABLE = "statistic";
    public static String CREATE_PLAYER_STORAGE_TABLE = "CREATE TABLE IF NOT EXISTS `player_storage` (\n" +
            "\t`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
            "\t`owner` VARCHAR(50) NULL DEFAULT NULL COMMENT '物品拥有者' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`seller` VARCHAR(50) NOT NULL COMMENT '卖家' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`item_detail` LONGTEXT NOT NULL COMMENT '序列化后的物品数据' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`item_type` VARCHAR(50) NOT NULL COMMENT '物品类型' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`store_time` BIGINT(20) NOT NULL COMMENT '存入时间戳',\n" +
            "\t`price` DOUBLE NULL DEFAULT NULL,\n" +
            "\t`priceType` VARCHAR(20) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\t`storageType` VARCHAR(20) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\tPRIMARY KEY (`id`) USING BTREE,\n" +
            "\tINDEX `owner` (`owner`) USING BTREE\n" +
            ")\n" +
            "COMMENT='暂存箱，购买后因背包已满，不能直接给玩家的物品会放在此处。\\r\\ntemp store chest. Items that are not able to give to player by no empty slot  will store here.'\n" +
            "COLLATE='utf8mb4_general_ci'\n" +
            "ENGINE=InnoDB\n" +
            ";";
    public static String CREATE_ON_SELLING_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS `%s` (\n" +
            "\t`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
            "\t`item_detail` LONGTEXT NOT NULL COMMENT 'Serialized item' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`item_type` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`seller` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\t`payment` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\t`price` DOUBLE UNSIGNED NULL DEFAULT NULL,\n" +
            "\t`on_sell_time` BIGINT(20) NULL DEFAULT NULL,\n" +
            "\t`expiry_time` BIGINT(20) NULL DEFAULT NULL,\n" +
            "\tPRIMARY KEY (`id`) USING BTREE,\n" +
            "\tINDEX `on_sell_time` (`on_sell_time`) USING BTREE,\n" +
            "\tINDEX `price` (`price`) USING BTREE,\n" +
            "\tINDEX `item_type` (`item_type`) USING BTREE\n" +
            ")\n" +
            "COMMENT='正在出售的物品\\r\\nitems that are on selling.'\n" +
            "COLLATE='utf8mb4_general_ci'\n" +
            "ENGINE=InnoDB\n" +
            ";\n";

    public static String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS `%s` (\n" +
            "\t`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
            "\t`item_detail` LONGTEXT NOT NULL COMMENT 'Serialized item' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`item_type` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`seller` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\t`buyer` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\t`payment` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\t`price` DOUBLE UNSIGNED NULL DEFAULT NULL,\n" +
            "\t`trade_time` BIGINT(20) NULL DEFAULT NULL,\n" +
            "\t`isClaimed` TINYINT(3) UNSIGNED ZEROFILL NOT NULL," +
            "\tPRIMARY KEY (`id`) USING BTREE\n" +
            ")\n" +
            "COMMENT='记录玩家之间的交易记录\\r\\nRecord trades between players.'\n" +
            "COLLATE='utf8mb4_general_ci'\n" +
            "ENGINE=InnoDB\n" +
            ";\n";
    public static String CREATE_STATISTIC_TABLE = "CREATE TABLE IF NOT EXISTS `%s` (\n" +
            "\t`player_uuid` CHAR(36) NOT NULL COMMENT '玩家UUID' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`player_name` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci'," +
            "\t`cost_money` DOUBLE UNSIGNED ZEROFILL NOT NULL,\n" +
            "\t`cost_point` DOUBLE UNSIGNED ZEROFILL NOT NULL,\n" +
            "\t`buy_money` DOUBLE UNSIGNED ZEROFILL NOT NULL DEFAULT '0000000000000000000000',\n" +
            "\t`buy_point` DOUBLE UNSIGNED ZEROFILL NOT NULL," +
            "\t`sell_count` INT(10) UNSIGNED ZEROFILL NOT NULL,\n" +
            "\t`buy_count` INT(10) UNSIGNED ZEROFILL NOT NULL,\n" +
            "\tPRIMARY KEY (`player_uuid`) USING BTREE\n" +
            ")\n" +
            "COLLATE='utf8mb4_general_ci'\n" +
            "ENGINE=InnoDB\n" +
            ";";
    //选择某个表的全部数据数量
    public static String SELECT_ALL_ITEMS_COUNT = "SELECT COUNT(*) AS total FROM `%table%` %condition%";
    public static String SELECT_ALL_STORAGE_ITEMS_COUNT = "SELECT COUNT(*) AS total FROM `%s` where owner = ?";
    //往全球市场插入物品
    public static String INSERT_ITEM_TO_MARKET = "INSERT INTO `%s` " +
            "(`item_detail`, `item_type`, `seller`, `payment`, `price`, `on_sell_time`, `expiry_time`) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    //从全球市场查询物品,第二个%s应该用查询条件,第三个%s类似on_sell_time DESC
    public static String SELECT_MARKET_ITEMS_BY_CONDITION = "SELECT * FROM `%table%` %condition% " +
            "ORDER BY %sort% LIMIT 45 OFFSET ?;";
    //从交易表中根据所给情况进行查询。
    public static String SELECT_TRANSACTION_BY_CONDITION = "SELECT * FROM `%table%` %condition% " +
            "ORDER BY %sort% LIMIT 45 OFFSET ?;";

    //从市场里删除物品
    public static String DELETE_ITEM_FROM_MARKET = "DELETE FROM `%s` WHERE ID = ?";
    //插入交易记录
    public static String INSERT_INTO_TRANSACTION = "INSERT INTO `%s` " +
            "(item_detail, item_type, seller, buyer, payment, price, trade_time, isClaimed) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    //根据seller名查询交易记录
    public static String SELECT_UNCLAIMED_TRANSACTION_BY_SELLER = "SELECT * FROM `%s` WHERE seller = ? AND isClaimed = 0;";
    //根据玩家名更新交易记录，设置成已确认
    public static String UPDATE_TRANSACTION_BY_SELLER = "UPDATE `%s` SET isClaimed = 1 WHERE seller = ? AND isClaimed = 0;";

    //插入到暂存库中
    public static String INSERT_INTO_STORAGE_TABLE = "INSERT INTO `%s` " +
            "(owner, seller, item_detail, item_type, store_time, price, priceType, storageType) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    //从暂存库根据玩家名选择对应物品
    public static String SELECT_ITEM_FROM_STORAGE_TABLE = "SELECT * FROM `%s` WHERE owner = ? ORDER BY store_time DESC LIMIT 45 OFFSET ?;";
    //从暂存库里面删除物品
    public static String DELETE_ITEM_FROM_STORAGE_TABLE = "DELETE FROM `%s` WHERE id = ?";
    //往统计记录里面插入或更新数据
    public static String UPSERT_STATISTIC = "INSERT INTO `%s` (player_uuid, player_name, cost_money, cost_point, buy_money, buy_point, sell_count, buy_count) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "player_name = VALUES(player_name), " +
            "cost_money = cost_money + VALUES(cost_money), " +
            "cost_point = cost_point + VALUES(cost_point), " +
            "buy_money = buy_money + VALUES(buy_money), " +
            "buy_point = buy_point + VALUES(buy_point)," +
            "sell_count = sell_count + VALUES(sell_count), " +
            "buy_count = buy_count + VALUES(buy_count)";
    //根据uuid查询统计记录
    public static String SELECT_FROM_STATISTIC = "SELECT * FROM `%s` WHERE player_uuid = ?";
}