package com.wjz.awesomemarket.sql;

import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.entity.StorageItem;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.MarketTools;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.wjz.awesomemarket.utils.MarketTools.deserializeItem;

public class Mysql {
    private static HikariDataSource dataSource;
    private static ConfigurationSection mysqlConfig;

    public static void setConfig(ConfigurationSection config) {
        mysqlConfig = config;
    }

    public static void tryToConnect() {
        //准备连接数据库
        HikariConfig hikariConfig = new HikariConfig();
        String url = "jdbc:mysql://" + mysqlConfig.getString("ip") + ":" + mysqlConfig.getString("port") + "/" + mysqlConfig.getString("database-name");

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(mysqlConfig.getString("user"));
        hikariConfig.setPassword(mysqlConfig.getString("password"));
        // 设置连接池数量
        hikariConfig.setMaximumPoolSize(mysqlConfig.getInt("pool.maximumPoolSize"));
        hikariConfig.setMinimumIdle(mysqlConfig.getInt("pool.minimumIdle"));
        hikariConfig.setIdleTimeout(mysqlConfig.getLong("pool.idleTimeout"));
        hikariConfig.setMaxLifetime(mysqlConfig.getLong("pool.maxLifetime"));
        hikariConfig.setConnectionTimeout(mysqlConfig.getLong("pool.connectionTimeout"));
        hikariConfig.setKeepaliveTime(300_000);
        String driver = "com.mysql.cj.jdbc.Driver";
        //MC不同版本driver不一样，这里先尝试。
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            driver = "com.mysql.jdbc.Driver";
            Log.infoDirectly("Driver class" + driver + "not found, use legacy MySql Driver com.mysql.cj.jdbc.Driver");
        }
        hikariConfig.setDriverClassName(driver);

        try {
            HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
            Log.info("connect_mysql_success");
            Mysql.dataSource = hikariDataSource;
            //建立数据库和表
            createNeeds(mysqlConfig);

        } catch (Exception e) {
            Log.severe("connect_mysql_fail");
            e.printStackTrace();
        }
    }

    private static boolean isDatabaseExist(String name) {
        //应该用create if not exist语句才对，不需要检查
        String query = "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = '" + name + "'";
        try (Statement stmt = dataSource.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeDataSource() {
        if (dataSource != null) dataSource.close();
    }

    /**
     * 创建数据库，表
     *
     * @param sqlConfig sql配置
     */
    private static void createNeeds(ConfigurationSection sqlConfig) {
        String tablePrefix = sqlConfig.getString("table-prefix");

        try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {
            //创库不能用预处理语句，现在改成
//            stmt.execute("CREATE DATABASE IF NOT EXISTS " + sqlConfig.getString("database-name"));
//            // 选择数据库
//            stmt.execute("USE " + databaseName);

            // 创建 sell 表
            stmt.execute(String.format(MysqlType.CREATE_ON_SELLING_ITEMS_TABLE, tablePrefix + MysqlType.ON_SELL_ITEMS_TABLE));

            // 创建 expire 表
            stmt.execute(String.format(MysqlType.CREATE_EXPIRE_ITEMS_TABLE, tablePrefix + MysqlType.EXPIRE_ITEMS_TABLE));

            // 创建 transaction 表
            stmt.execute(String.format(MysqlType.CREATE_TRANSACTIONS_TABLE, tablePrefix + MysqlType.TRANSACTIONS_TABLE));
            // 创建 player_storage 表
            stmt.execute(String.format(MysqlType.CREATE_PLAYER_STORAGE_TABLE, tablePrefix + MysqlType.PLAYER_STORAGE_TABLE));

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
        try (Connection connection = dataSource.getConnection(); PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, itemDetail);
            pstmt.setString(2, itemType);
            pstmt.setString(3, seller);
            pstmt.setString(4, payment);
            pstmt.setDouble(5, price);
            pstmt.setLong(6, onSellTime);
            pstmt.setLong(7, expiryTime);
            //执行sql
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取商场中的所有商品.在表on_selling中
     *
     * @return
     */
    public static int getTotalItemsCount() {
        String query = String.format(MysqlType.SELECT_ALL_ITEMS_COUNT, mysqlConfig.getString("table-prefix") + MysqlType.ON_SELL_ITEMS_TABLE);
        try (Connection connection = dataSource.getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery(query);
            if (rs.next()) return rs.getInt("total");
            else {
                Log.severe("查询数据库on_selling商品总数失败！");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getStorageTotalItemsCount(String playerName) {
        String query = String.format(MysqlType.SELECT_ALL_STORAGE_ITEMS_COUNT, mysqlConfig.getString("table-prefix") + MysqlType.PLAYER_STORAGE_TABLE);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerName);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) return rs.getInt("total");
            else {
                Log.severe("查询玩家暂存库的商品总数失败！");
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean deleteMarketItem(long id) {
        String deleteQuery = String.format(MysqlType.DELETE_ITEM_FROM_MARKET, mysqlConfig.getString("table-prefix") + MysqlType.ON_SELL_ITEMS_TABLE);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setLong(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<MarketItem> getMarketItems(SQLFilter sqlFilter) {
        List<MarketItem> marketItems = new ArrayList<>();
        String query = MysqlType.SHOW_ITEMS_BY_PAGE
                .replace("%table%", mysqlConfig.getString("table-prefix") + MysqlType.ON_SELL_ITEMS_TABLE)
                .replace("%condition%", sqlFilter.getCondition())
                .replace("%sort%", sqlFilter.getLimit());

        try (Connection connection = dataSource.getConnection(); PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, sqlFilter.getOffset());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ItemStack itemStack = deserializeItem(rs.getString("item_detail"));
                    //封装成marketItem
                    String seller = rs.getString("seller");
                    double price = rs.getDouble("price");
                    PriceType priceType = PriceType.getType(rs.getString("payment"));
                    long id = rs.getLong("id");
                    long onSellTime=rs.getLong("on_sell_time");
                    MarketItem marketItem=new MarketItem(itemStack,seller,price,priceType,id,onSellTime);
                    marketItems.add(marketItem);
                }
            }
            return marketItems;

        } catch (SQLException e) {
            Log.severeDirectly("根据页数查询物品失败");
            e.printStackTrace();
        }

        return marketItems;
    }

    //交易完成后要增加交易记录
    public static void addTradeTransaction(String itemDetail, String itemType, String seller, String buyer, String payment, double price) {
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format(MysqlType.INSERT_INTO_TRANSACTION, mysqlConfig.getString("table-prefix") + MysqlType.TRANSACTIONS_TABLE);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, itemDetail);
            preparedStatement.setString(2, itemType);
            preparedStatement.setString(3, seller);
            preparedStatement.setString(4, buyer);
            preparedStatement.setString(5, payment);
            preparedStatement.setDouble(6, price);
            preparedStatement.setLong(7, System.currentTimeMillis());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("创建交易单失败！");
        }
    }

    //把物品放到暂存库中
    public static void addItemToTempStorage(long id, String owner, String seller, String itemDetail, String itemType, long storeTime, double price, String priceType) {
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format(MysqlType.INSERT_INTO_STORAGE_TABLE, mysqlConfig.getString("table-prefix") + MysqlType.PLAYER_STORAGE_TABLE);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, owner);
            preparedStatement.setString(3, seller);
            preparedStatement.setString(4, itemDetail);
            preparedStatement.setString(5, itemType);
            preparedStatement.setLong(6, storeTime);
            preparedStatement.setDouble(7, price);
            preparedStatement.setString(8, priceType);
            preparedStatement.execute();


        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("创建交易单失败！");
        }
    }

    public static List<StorageItem> getStorageItems(Player player, int page) {
        List<StorageItem> storageItemList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format(MysqlType.SELECT_ITEM_FROM_STORAGE_TABLE, mysqlConfig.getString("table-prefix") + MysqlType.PLAYER_STORAGE_TABLE);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, player.getName());
            preparedStatement.setInt(2, page - 1);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    //先创建一个marketItem
                    String itemDetail = rs.getString("item_detail");
                    String seller = rs.getString("seller");
                    double price = rs.getDouble("price");
                    PriceType priceType = PriceType.getType(rs.getString("priceType"));
                    long purchaseTime = rs.getLong("store_time");
                    long id = rs.getLong("id");
                    StorageItem storageItem = new StorageItem(id, MarketTools.deserializeItem(itemDetail), seller, purchaseTime, price, priceType);
                    storageItemList.add(storageItem);
                }
            }
            return storageItemList;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("获取暂存库物品失败！");
        }
        return storageItemList;
    }

    //从数据库删除暂存库的某个物品
    public static boolean deleteStorageItem(long id) {
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format(MysqlType.DELETE_ITEM_FROM_STORAGE_TABLE, mysqlConfig.getString("table-prefix") + MysqlType.PLAYER_STORAGE_TABLE);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            int rowaffected = preparedStatement.executeUpdate();
            return rowaffected != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("暂存库物品删除失败！");
            return false;
        }
    }
}
