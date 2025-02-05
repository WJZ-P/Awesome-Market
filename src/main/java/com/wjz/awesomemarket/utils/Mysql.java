package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.constants.MysqlType;
import com.wjz.awesomemarket.constants.PriceType;
import com.wjz.awesomemarket.entity.MarketItem;
import com.wjz.awesomemarket.inventoryHolder.MarketHolder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
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
        String url = "jdbc:mysql://" + mysqlConfig.getString("ip") +
                ":" + mysqlConfig.getString("port") + "/" + mysqlConfig.getString("database-name");

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(mysqlConfig.getString("user"));
        hikariConfig.setPassword(mysqlConfig.getString("password"));
        // 设置连接池数量
        hikariConfig.setMaximumPoolSize(mysqlConfig.getInt("pool.maximumPoolSize"));
        hikariConfig.setMinimumIdle(mysqlConfig.getInt("pool.minimumIdle"));
        hikariConfig.setIdleTimeout(mysqlConfig.getLong("pool.idleTimeout"));
        hikariConfig.setMaxLifetime(mysqlConfig.getLong("pool.maxLifetime"));
        hikariConfig.setConnectionTimeout(mysqlConfig.getLong("pool.connectionTimeout"));
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
    private static void createNeeds(ConfigurationSection sqlConfig) {
        String databaseName = sqlConfig.getString("database-name");
        String tablePrefix = sqlConfig.getString("table-prefix");

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            //创库不能用预处理语句，现在改成
//            stmt.execute("CREATE DATABASE IF NOT EXISTS " + sqlConfig.getString("database-name"));
//            // 选择数据库
//            stmt.execute("USE " + databaseName);

            // 创建 sell 表
            stmt.execute(String.format(MysqlType.CREATE_ON_SELLING_ITEMS_TABLE,
                    tablePrefix + MysqlType.ON_SELL_ITEMS_TABLE));

            // 创建 expire 表
            stmt.execute(String.format(MysqlType.CREATE_EXPIRE_ITEMS_TABLE,
                    tablePrefix + MysqlType.EXPIRE_ITEMS_TABLE));

            // 创建 transaction 表
            stmt.execute(String.format(MysqlType.CREATE_TRANSACTIONS_TABLE,
                    tablePrefix + MysqlType.TRANSACTIONS_TABLE));

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

    public static List<ItemStack> getAndSetItemsByPage(int page, List<MarketItem> marketItemList) {
        List<ItemStack> items = new ArrayList<>();
        String query = String.format(MysqlType.SHOW_ITEMS_BY_PAGE,
                mysqlConfig.getString("table-prefix") + MysqlType.ON_SELL_ITEMS_TABLE);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            int offset = (page - 1) * 45;
            pstmt.setInt(1, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ItemStack itemStack = deserializeItem(rs.getString("item_detail"));
                    ItemMeta meta = itemStack.getItemMeta();
                    List<String> oldLore = itemStack.getLore();
                    if (oldLore == null) oldLore = new ArrayList<>();
                    //要给物品上描述信息
                    List<String> commodityLore = Log.langConfig.getStringList("market-GUI.name.commodity");
                    //添加lore
                    //price,currency,player,on_sell_time

                    //这里格式化时间
                    long timeStamp = rs.getLong("on_sell_time");
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), ZoneId.systemDefault());

                    //下面获取一些信息
                    String seller = rs.getString("seller");
                    double price = rs.getDouble("price");
                    PriceType priceType = PriceType.getType(rs.getString("payment"));
                    long id= rs.getLong("id");

                    //封装好item，放入list内
                    marketItemList.add(new MarketItem(itemStack,seller,price,priceType,id));

                    //修改要展示到UI上的物品描述
                    for (int i = 0; i < commodityLore.size(); i++) {
                        String modifiedLore = commodityLore.get(i).replace("%player%", seller)
                                .replace("%price%", String.format("%.2f", price))
                                .replace("%currency%", priceType.getName())
                                .replace("%on_sell_time%", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        commodityLore.set(i, modifiedLore);
                    }
                    //商品lore添加完毕后追加到原lore后
                    oldLore.addAll(commodityLore);
                    meta.setLore(oldLore);
                    //添加商品的NBT标签
                    meta.getPersistentDataContainer().set(MarketHolder.GUI_ACTION_KEY, PersistentDataType.STRING, MarketHolder.COMMODITY_KEY);
                    //设置好的meta数据写入到item中
                    itemStack.setItemMeta(meta);

                    items.add(itemStack);
                }
            }
            return items;

        } catch (SQLException e) {
            Log.severeDirectly("根据页数查询物品失败");
            e.printStackTrace();
        }

        return items;
    }
}
