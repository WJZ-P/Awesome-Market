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

    public static void tryToConnect(FileConfiguration config) {
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
            Connection connection = dataSource.getConnection();
            //看指定数据库是否存在
            if (!isDatabaseExist(MysqlType.DATABASE_NAME)) {//不存在就要新建数据库
                Log.info("try_create_database");
                connection.createStatement().execute(MysqlType.CREATE_DATABASE);
                //同时要完成建表


            } else {//说明数据库已存在
                connection.createStatement().execute("use " + MysqlType.DATABASE_NAME);
            }
            connection.close();//处理完成关闭链接
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

}
