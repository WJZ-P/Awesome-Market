package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.constants.MysqlType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.logging.Logger;

public class Mysql {
    private static HikariDataSource dataSource;

    public static void tryToConnect(FileConfiguration config, Logger logger) {
        //准备连接数据库
        HikariConfig hikariConfig = new HikariConfig();
        ConfigurationSection mysqlConfig = config.getConfigurationSection("mysql-data-base");
        String url = "jdbc:mysql://" + mysqlConfig.getString("ip") +
                ":" + mysqlConfig.getString("port");//先看能否连接上

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(mysqlConfig.getString("user"));
        hikariConfig.setPassword(mysqlConfig.getString("password"));

        try {
            HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
            Log.info("connect_mysql_success");
            Mysql.dataSource = hikariDataSource;
            //看指定数据库是否存在
            if (!isDatabaseExist(MysqlType.DATABASE_NAME)) {
                //不存在就要新建数据库
                try (Connection connection = dataSource.getConnection()) {
                    connection.createStatement().execute(MysqlType.CREATE_DATABASE);
                    Log.severe("connect_mysql_fail");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                //说明数据库已存在
                try (Connection connection = dataSource.getConnection()) {
                    connection.createStatement().execute("use " + MysqlType.DATABASE_NAME);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isDatabaseExist(String name) {
        String query = "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = '" + name + "'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() {
        try {
            Mysql.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Mysql.connection = null;
    }

}
