package com.wjz.awesomemarket.sql;

import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.constants.StorageType;
import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.entity.StatisticInfo;
import com.wjz.awesomemarket.entity.StorageItem;
import com.wjz.awesomemarket.entity.TransactionItem;
import com.wjz.awesomemarket.utils.Log;
import com.wjz.awesomemarket.utils.MarketTools;
import com.wjz.awesomemarket.utils.UsefulTools;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.time.Instant;
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
            // 创建 transaction 表
            stmt.execute(String.format(MysqlType.CREATE_TRANSACTIONS_TABLE, tablePrefix + MysqlType.TRANSACTIONS_TABLE));
            // 创建 player_storage 表
            stmt.execute(String.format(MysqlType.CREATE_PLAYER_STORAGE_TABLE, tablePrefix + MysqlType.PLAYER_STORAGE_TABLE));
            // 创建statistic 表
            stmt.execute(String.format(MysqlType.CREATE_STATISTIC_TABLE, tablePrefix + MysqlType.STATISTIC_TABLE));

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
    public static int getItemsCountWithFilter(String tableName, SQLFilter sqlFilter) {
        String query = MysqlType.SELECT_ALL_ITEMS_COUNT.replace("%table%", mysqlConfig.getString("table-prefix") + tableName)
                .replace("%condition%", sqlFilter.getCondition());

        try (Connection connection = dataSource.getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery(query);
            if (rs.next()) return rs.getInt("total");
            else {
                Log.severe("查询" + tableName + "表数据总数失败！");
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
        String query = MysqlType.SELECT_MARKET_ITEMS_BY_CONDITION
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
                    long onSellTime = rs.getLong("on_sell_time");
                    MarketItem marketItem = new MarketItem(itemStack, seller, price, priceType, id, onSellTime);
                    marketItems.add(marketItem);
                }
            }
            return marketItems;

        } catch (SQLException e) {
            Log.severeDirectly("根据页数查询物品失败");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.severe("command.general.error.deserialize-fail");
        }

        return marketItems;
    }

    public static List<TransactionItem> getTransactionItems(SQLFilter sqlFilter) {
        List<TransactionItem> transactionItems = new ArrayList<>();
        String query = MysqlType.SELECT_TRANSACTION_BY_CONDITION
                .replace("%table%", mysqlConfig.getString("table-prefix") + MysqlType.TRANSACTIONS_TABLE)
                .replace("%condition%", sqlFilter.getCondition())
                .replace("%sort%", sqlFilter.getLimit());
        Log.infoDirectly(query);
        try (Connection connection = dataSource.getConnection(); PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, sqlFilter.getOffset());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ItemStack itemStack = deserializeItem(rs.getString("item_detail"));
                    //封装成交易记录物品
                    String seller = rs.getString("seller");
                    String buyer = rs.getString("buyer");
                    double price = rs.getDouble("price");
                    PriceType priceType = PriceType.getType(rs.getString("payment"));
                    long id = rs.getLong("id");
                    long tradeTime = rs.getLong("trade_time");
                    int isClaimed = rs.getInt("isClaimed");
                    TransactionItem transactionItem = new TransactionItem(itemStack, id, seller, buyer, tradeTime, price, priceType, isClaimed);
                    transactionItems.add(transactionItem);
                }
            }
            return transactionItems;

        } catch (SQLException e) {
            Log.severeDirectly("Mysql查询交易记录失败!");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.severe("command.general.error.deserialize-fail");
        }
        return transactionItems;
    }

    //交易完成后要增加交易记录
    public static void addTradeTransaction(String itemDetail, String itemType, String seller, String buyer, String payment, double price, int isClaimed) {
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format(MysqlType.INSERT_INTO_TRANSACTION, mysqlConfig.getString("table-prefix") + MysqlType.TRANSACTIONS_TABLE);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, itemDetail);
            preparedStatement.setString(2, itemType);
            preparedStatement.setString(3, seller);
            preparedStatement.setString(4, buyer);
            preparedStatement.setString(5, payment);
            preparedStatement.setDouble(6, price);
            preparedStatement.setLong(7, Instant.now().getEpochSecond());
            preparedStatement.setInt(8, isClaimed);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("创建交易单失败！");
        }
    }

    //根据卖家的玩家名更新单据。同时给卖家钱。必须保证卖家是在线的
    public static boolean claimTransaction(String sellerName) {
        try (Connection connection = dataSource.getConnection()) {
            boolean hasClaimed = false;
            //首先要先查询出所有需要确认的单据
            String query = String.format(MysqlType.SELECT_UNCLAIMED_TRANSACTION_BY_SELLER, mysqlConfig.getString("table-prefix") + MysqlType.TRANSACTIONS_TABLE);
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, sellerName);
            //先把所有未确认的账单确认。给玩家钱
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    //下面准备给钱
                    hasClaimed = true;
                    double price = rs.getDouble("price");
                    PriceType priceType = PriceType.getType(rs.getString("payment"));
                    Player seller = Bukkit.getPlayer(sellerName);
                    priceType.give(seller, price);

                    //给钱之后给卖家增加统计信息


                    //这里准备发送信息
                    ItemStack itemStack = deserializeItem(rs.getString("item_detail"));
                    String receiveTip = Log.getString("tip.receive-money").
                            replace("%money%", String.format("%,.2f", price)).replace("%currency%", priceType.getName());
                    //做版本检查
                    if (UsefulTools.isVersionNewerThan("1.17")) {
                        Component message = Component.text(receiveTip).replaceText(b -> b.matchLiteral("%item%")
                                .replacement(Component.translatable(itemStack.getType().translationKey())
                                        .color(itemStack.displayName().color()).hoverEvent(itemStack.asHoverEvent())));

                        seller.sendMessage(message);
                    } else {
                        seller.sendMessage(receiveTip.replace("%item%", itemStack.getType().name()));
                    }
                }
            }
            //然后更新所有未确认账单为确认
            query = String.format(MysqlType.UPDATE_TRANSACTION_BY_SELLER, mysqlConfig.getString("table-prefix") + MysqlType.TRANSACTIONS_TABLE);
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, sellerName);
            pstmt.execute();
            return hasClaimed;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("单据更新失败！");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.severe("command.general.error.deserialize-fail");
        }
        return false;
    }

    //交易完成后还需要增加统计记录
    public static void upsertStatistic(OfflinePlayer player, double price, PriceType priceType, boolean isBuy) {
        String query = String.format(MysqlType.UPSERT_STATISTIC, mysqlConfig.getString("table-prefix") + MysqlType.STATISTIC_TABLE);
        boolean isMoney = String.valueOf(priceType).equalsIgnoreCase("money");
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(query);

            if (price == 0) {//说明是新建数据
                pstmt.setString(1, player.getUniqueId().toString());
                pstmt.setString(2, player.getName());
                pstmt.setDouble(3, 0);
                pstmt.setDouble(4, 0);
                pstmt.setDouble(5, 0);
                pstmt.setDouble(6, 0);
                pstmt.setInt(7, 0);
                pstmt.setInt(8, 0);
                pstmt.executeUpdate();
                return;
            }

            pstmt.setString(1, player.getUniqueId().toString());//用户的UUID
            pstmt.setString(2, player.getName());//用户名
            pstmt.setDouble(3, !isBuy ? 0 : isMoney ? price : 0);//花费的钱
            pstmt.setDouble(4, !isBuy ? 0 : isMoney ? 0 : price);//花费的点券
            pstmt.setDouble(5, !isBuy ? (isMoney ? price : 0) : 0);//获得的钱
            pstmt.setDouble(6, !isBuy ? (isMoney ? 0 : price) : 0);//获得的点券
            pstmt.setInt(7, isBuy ? 0 : 1);//卖出的数量
            pstmt.setInt(8, isBuy ? 1 : 0);//买入的数量
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("UPSERT统计数据失败！");
        }
    }

    //根据UUID查询统计记录
    public static StatisticInfo searchStatistic(OfflinePlayer player) {
        String query = String.format(MysqlType.SELECT_FROM_STATISTIC, mysqlConfig.getString("table-prefix") + MysqlType.STATISTIC_TABLE);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSet new_rs = rs;
                if (!rs.next()) {
                    //如果没有记录，则创建一条默认的统计记录
                    upsertStatistic(player, 0, PriceType.MONEY, true);
                    new_rs = pstmt.executeQuery();
                    new_rs.next();
                }
                return new StatisticInfo(new_rs.getDouble("cost_money"),
                        new_rs.getDouble("cost_point"),
                        new_rs.getDouble("buy_money"),
                        new_rs.getDouble("buy_point"),
                        new_rs.getInt("sell_count"),
                        new_rs.getInt("buy_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("UPSERT统计数据失败！");
            return null;
        }
    }

    //把物品放到暂存库中
    public static void addItemToTempStorage(String owner, String seller, String itemDetail, String itemType, long storeTime, double price, String priceType, String storageType) {
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format(MysqlType.INSERT_INTO_STORAGE_TABLE, mysqlConfig.getString("table-prefix") + MysqlType.PLAYER_STORAGE_TABLE);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, owner);
            preparedStatement.setString(2, seller);
            preparedStatement.setString(3, itemDetail);
            preparedStatement.setString(4, itemType);
            preparedStatement.setLong(5, storeTime);
            preparedStatement.setDouble(6, price);
            preparedStatement.setString(7, priceType);
            preparedStatement.setString(8, storageType);
            preparedStatement.execute();


        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("创建交易单失败！");
        }
    }

    public static List<StorageItem> getStorageItems(OfflinePlayer player, int page) {
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
                    StorageType storageType = StorageType.getType(rs.getString("storageType"));
                    StorageItem storageItem = new StorageItem(id, MarketTools.deserializeItem(itemDetail), seller, purchaseTime, price, priceType, storageType);
                    storageItemList.add(storageItem);
                }
            }
            return storageItemList;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.severeDirectly("获取暂存库物品失败！");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.severe("command.general.error.deserialize-fail");
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
