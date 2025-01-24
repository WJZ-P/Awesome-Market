package com.wjz.awesomemarket.utils;

import com.wjz.awesomemarket.constants.MysqlType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.logging.Logger;

public class Mysql {
    private static Connection connection;

    public static void tryToConnect(FileConfiguration config, Logger logger) {
        //准备连接数据库
        ConfigurationSection mysqlConfig = config.getConfigurationSection("mysql-data-base");
        String url = "jdbc:mysql://" + mysqlConfig.getString("ip") +
                ":" + mysqlConfig.getString("port");//先看能否连接上

        try {
            Connection connection = DriverManager.getConnection(url, mysqlConfig.getString("user"),
                    mysqlConfig.getString("password"));
            Log.info("connect_mysql_success");

            Mysql.connection = connection;//保存好变量

            //看指定数据库是否存在
            if (!isDatabaseExist(MysqlType.DATABASE_NAME)) {
                //不存在就要新建数据库
                connection.createStatement().execute(MysqlType.CREATE_DATABASE);
                Log.severe("connect_mysql_fail");
            }

        } catch (SQLException e) {
            logger.severe("数据库连接失败: " + e.getMessage());
            throw new RuntimeException(e);
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

    public static void closeConnection()  {
        try {
            Mysql.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Mysql.connection=null;
    }

}
