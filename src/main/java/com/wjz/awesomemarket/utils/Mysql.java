package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.constants.MysqlType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.text.MessageFormat;

public class Mysql {
    private static HikariDataSource dataSource;
    private static ConfigurationSection mysqlConfig;

    public static void setConfig(ConfigurationSection config) {
        mysqlConfig = config;
    }

    public static void tryToConnect() {
        //准备连接数据库
        HikariConfig hikariConfig = new HikariConfig();
        String url = "jdbc:mysql://" + mysqlConfig.getString("ip") +
                ":" + mysqlConfig.getString("port");//先看能否连接上

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(mysqlConfig.getString("user"));
        hikariConfig.setPassword(mysqlConfig.getString("password"));
        String driver = "com.mysql.cj.jdbc.Driver";
        //MC不同版本driver不一样，这里先尝试。
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            driver = "com.mysql.jdbc.Driver";
            Log.infoDirectly("Driver class" + driver + "not found, use legacy MySql Driver com.mysql.cj.jdbc.Driver");
        }

        try {
            HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
            Log.info("connect_mysql_success");
            Mysql.dataSource = hikariDataSource;
            Connection connection = dataSource.getConnection();
            //建立数据库和表
            createNeeds(mysqlConfig);

        } catch (SQLException e) {
            Log.severe("connect_mysql_fail");
            e.printStackTrace();
        }
    }

    private static boolean isDatabaseExist(String name) {
        //应该用create if not exist语句才对，不需要检查
        String query = "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = '" + name + "'";
        try (Statement stmt = dataSource.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeDataSource() {
        if (dataSource != null)
            dataSource.close();
    }

    /**
     * 创建数据库，表
     *
     * @param sqlConfig sql配置
     */
    private static void createNeeds(ConfigurationSection sqlConfig) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement()//创库不能用预处理语句
                    .execute("CREATE DATABASE IF NOT EXISTS " + sqlConfig.getString("database-name"));
            //选择数据库
            connection.createStatement().execute("USE " + sqlConfig.getString("database-name"));
            //下面建表

            //创建sell表
            Statement stmt = connection.createStatement();
            stmt.execute(String.format(MysqlType.CREATE_ON_SELLING_ITEMS_TABLE,
                    sqlConfig.getString("table-prefix") + MysqlType.ON_SELL_ITEMS_TABLE));

            //创建expire表
            stmt = connection.createStatement();
            stmt.execute(String.format(MysqlType.CREATE_EXPIRE_ITEMS_TABLE,
                    sqlConfig.getString("table-prefix") + MysqlType.EXPIRE_ITEMS_TABLE));

            //创建transaction
            stmt = connection.createStatement();
            stmt.execute(String.format(MysqlType.CREATE_TRANSACTIONS_TABLE,
                    sqlConfig.getString("table-prefix") + MysqlType.TRANSACTIONS_TABLE));

        } catch (SQLException e) {
            Log.severe("create_mysql_fail");
            e.printStackTrace();
        }

    }

    /**
     * 把物品上传到数据库
     */
    public static void InsertItemsToMarket(String itemDetail, String itemType, String seller, String payment, double price, long onSellTime, long expiryTime) {
        String insertSQL = String.format(MysqlType.INSERT_ITEM_TO_MARKET, mysqlConfig.getString("table-prefix") + MysqlType.ON_SELL_ITEMS_TABLE);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, itemDetail);
            pstmt.setString(2, itemType);
            pstmt.setString(3, seller);
            pstmt.setString(4, payment);
            pstmt.setString(5, String.valueOf(price));
            pstmt.setLong(6, onSellTime);
            pstmt.setLong(7, expiryTime);
            //执行sql
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
