package com.wjz.awesomemarket.constants;

public class MysqlType {

    public static String ON_SELL_ITEMS_TABLE = "on_selling_items";
    public static String EXPIRE_ITEMS_TABLE = "expire_items";
    public static String TRANSACTIONS_TABLE = "transactions";
    public static String PLAYER_STORAGE_TABLE="player_storage";
    public static String CREATE_PLAYER_STORAGE_TABLE="CREATE TABLE `player_storage` (\n" +
            "\t`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
            "\t`owner` VARCHAR(50) NULL DEFAULT NULL COMMENT '物品拥有者' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`seller` VARCHAR(50) NOT NULL COMMENT '卖家' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`item_detail` LONGTEXT NOT NULL COMMENT '序列化后的物品数据' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`item_type` VARCHAR(50) NOT NULL COMMENT '物品类型' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`store_time` BIGINT(20) NOT NULL COMMENT '存入时间戳',\n" +
            "\t`price` DOUBLE NULL DEFAULT NULL,\n" +
            "\t`priceType` VARCHAR(20) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
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
    public static String CREATE_EXPIRE_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS `%s` (\n" +
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
            "\tINDEX `item_type` (`item_type`) USING BTREE,\n" +
            "\tINDEX `seller` (`seller`) USING BTREE\n" +
            ")\n" +
            "COMMENT='因过期而下架的物品\\r\\nitems that are expired.'\n" +
            "COLLATE='utf8mb4_general_ci'\n" +
            "ENGINE=InnoDB;" +
            ";";

    public static String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS `%s` (\n" +
            "\t`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
            "\t`item_detail` LONGTEXT NOT NULL COMMENT 'Serialized item' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`item_type` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb4_general_ci',\n" +
            "\t`seller` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\t`buyer` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\t`payment` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_general_ci',\n" +
            "\t`price` DOUBLE UNSIGNED NULL DEFAULT NULL,\n" +
            "\t`trade_time` BIGINT(20) NULL DEFAULT NULL,\n" +
            "\tPRIMARY KEY (`id`) USING BTREE\n" +
            ")\n" +
            "COMMENT='记录玩家之间的交易记录\\r\\nRecord trades between players.'\n" +
            "COLLATE='utf8mb4_general_ci'\n" +
            "ENGINE=InnoDB\n" +
            ";\n";

    public static final String SELECT_ALL_ITEMS_COUNT = "SELECT COUNT(*) AS total FROM `%s`";

    public static String INSERT_ITEM_TO_MARKET = "INSERT INTO `%s` " +
            "(`item_detail`, `item_type`, `seller`, `payment`, `price`, `on_sell_time`, `expiry_time`) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static String SHOW_ITEMS_BY_PAGE = "SELECT * FROM `%s` " +
            "ORDER BY on_sell_time ASC " +
            "LIMIT 45 OFFSET ?;";

    public static String DELETE_ITEM_FROM_MARKET = "DELETE FROM `%s` WHERE ID = ?";

    public static String INSERT_INTO_TRANSACTION = "INSERT INTO `%s` " +
            "(item_detail, item_type, seller, buyer, payment, price, trade_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static String INSERT_INTO_STORAGE_TABLE="INSERT INTO `%s` "+
            "(id, owner, seller, item_detail, item_type, store_time, price, priceType) "+
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static String SELECT_ITEM_FROM_STORAGE_TABLE="SELECT * FROM `%s` WHERE owner = `%s`";

}
