
package com.example.demo2.controller;

import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin
public class MyController {
    private static String URL = "jdbc:hive2://bigdata41.depts.bingosoft.net:22241/";
    private static String DRIVER = "org.apache.hive.jdbc.HiveDriver";
    private static String USERNAME;
    private static String PASSWORD;
    private static Connection CONNECTION = null;
    private static Logger logger = LoggerFactory.getLogger(MyController.class);


    /**
     * 获取所有数据库
     *
     * @return List<String>
     */
    @PostMapping("/databases")
    @ResponseBody
    public static List<String> getDatabases() {
        if (CONNECTION == null) {
            return null;
        } else {
            try {
                Statement statement = CONNECTION.createStatement();
                List<String> databases = new ArrayList<>();
                ResultSet resultSet = statement.executeQuery("show databases");
                while (resultSet.next()) {
                    String databaseName = resultSet.getString(1);
                    databases.add(databaseName);

                }
                return databases;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    /**
     * 获取指定数据库下的所有表
     *
     * @param database 数据库名称
     * @return List<String>
     */
    @PostMapping("/tables")
    @ResponseBody
    public static List<String> getTables(@RequestParam("database") String database) {
        if (CONNECTION == null) {
            return null;
        } else {
            try {

                Statement statement = CONNECTION.createStatement();
                List<String> tables = new ArrayList<>();
                statement.executeQuery("use " + database);
                ResultSet resultSet = statement.executeQuery("show tables");

                while (resultSet.next()) {
                    String tableName = resultSet.getString(1);
                    tables.add(tableName);
                    //输出所有表名

                }
                statement.executeQuery("use default");
                return tables;
            } catch (SQLException e) {
                e.printStackTrace();

                return null;
            }
        }

    }

    /**
     * 查询语句
     *
     * @param sql 查询语句
     * @return List<List < String>>
     */
    @PostMapping("/query")
    @ResponseBody
    public static List query(@RequestParam("sql") String sql) {
        if (CONNECTION == null) {
            logger.warn("no conn");
            List<String> errors = new ArrayList<>();
            errors.add("SQL Connection close. Please log in again.");
            return errors;
        } else {
            List<List<String>> query = new ArrayList<>();
            List<String> row;
            int numCol;
            try {
                logger.info("SQL Script: " + sql);
                Statement statement = CONNECTION.createStatement();
                ResultSet result = statement.executeQuery(sql);
                numCol = result.getMetaData().getColumnCount();
                // 第一个元素默认为表的各个属性名
                row = new ArrayList<>();
                for (int i = 1; i <= numCol; i++) {
                    row.add(result.getMetaData().getColumnName(i));
                }
                query.add(row);
                while (result.next()) {
                    // 表的每一行
                    row = new ArrayList<>();
                    for (int i = 1; i <= numCol; i++) {
                        row.add(result.getString(i));
                    }
                    query.add(row);
                }
                result.close();
            } catch (SQLException e) {
                List<String> errors = new ArrayList<>();
                System.out.println(e.toString());
                errors.add(e.toString());
                return errors;
            }
            return query;
        }
    }

    /**
     * 连接数据库
     *
     * @param url      数据库连接地址
     * @param driver   驱动
     * @param user     用户名
     * @param password 密码
     * @return 1表示连接成功，0表示连接失败
     */
    @PostMapping("/connect")
    public static String connect(@RequestParam(value = "url", required = false, defaultValue = "jdbc:hive2://bigdata29.depts.bingosoft.net:22229/") String url,
                                 @RequestParam(value = "driver", required = false, defaultValue = "org.apache.hive.jdbc.HiveDriver") String driver,
                                 @RequestParam("user") String user,
                                 @RequestParam("password") String password) {
        logger.info("开始创建连接");

        Connection connection = null;
        USERNAME = user;
        PASSWORD = password;
        URL = url;
        DRIVER = driver;
        Properties properties = new Properties();
        properties.setProperty("driverClassName", DRIVER);
        properties.setProperty("user", USERNAME);
        properties.setProperty("password", PASSWORD);
        try {
            if (properties != null) {
                connection = DriverManager.getConnection(url, properties);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (connection != null) {
            CONNECTION = connection;
            return "1";
        } else return "0";
    }
}

