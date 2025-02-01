package com.wjz.awesomemarket.constants;

public class MysqlType {

    public static String ON_SELL_ITEMS_TABLE = "on_selling_items";
    public static String EXPIRE_ITEMS_TABLE = "expire_items";
    public static String TRANSACTIONS_TABLE = "transactions";
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
            "\t`amount` INT(11) UNSIGNED NOT NULL DEFAULT '0',\n" +
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
    public static String SHOW_ITEMS_BY_PAGE="SELECT * FROM `%s` " +
            "ORDER BY on_sell_time ASC " +
            "LIMIT 45 OFFSET ?;";

}
