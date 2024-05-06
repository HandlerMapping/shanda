package org.example.jdbc;

import org.example.ConfigurationLoader;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseConnection {
    String[] datasql = ConfigurationLoader.a();

    public <T> List<T> getEntities(String where,String tableName, Class<T> clazz,String name) {


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<T> entities = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(datasql[0], datasql[1], datasql[2])) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);

            List<String> columnNames = new ArrayList<>();
            while (resultSet.next()) {
                columnNames.add(resultSet.getString("COLUMN_NAME"));
            }
            StringBuffer sql = new StringBuffer("SELECT * FROM " + tableName);
            if (where != null){
                sql.append(" WHERE openId = '"+where+"';");
            }
            try (Statement statement = connection.createStatement();
                 ResultSet queryResult = statement.executeQuery(String.valueOf(sql))) {

                while (queryResult.next()) {
                    T entity = mapResultSetToEntity(queryResult, columnNames, clazz);
                    entities.add(entity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL query", e);
        }
        return entities;
    }

    public static  <T> T mapResultSetToEntity(ResultSet resultSet, List<String> columnNames, Class<T> clazz) throws SQLException {
        try {
            T entity = clazz.getDeclaredConstructor().newInstance();
            for (String columnName : columnNames) {
                try {
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    Object value = resultSet.getObject(columnName);
                    field.set(entity, value);
                } catch (NoSuchFieldException ignored) {
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map ResultSet to entity", e);
        }
    }

    public static Object getFieldValue(Object object, String fieldName) {
        try {
            // 获取对象的类类型
            Class<?> clazz = object.getClass();
            // 获取指定属性名的 Field 对象
            Field field = clazz.getDeclaredField(fieldName);
            // 设置 Field 对象可访问，因为可能是私有属性
            field.setAccessible(true);
            // 使用 Field 对象的 get 方法获取属性值
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> List<T> postEntities(Object[] columnNames, String tableName, Class<T> clazz) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<T> entities = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(datasql[0], datasql[1], datasql[2])) {
            // 将对象数组转换为字符串数组
            String[] columnNamesArray = Arrays.stream(columnNames)
                    .map(Object::toString)
                    .toArray(String[]::new);

            // 构建 SQL 插入语句
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("INSERT INTO ").append(tableName).append(" (");
            // 构建字段名部分
            Field[] fields = columnNames[0].getClass().getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                sqlBuilder.append(fieldName).append(", ");
            }
            // 删除最后一个逗号和空格
            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
            sqlBuilder.append(") VALUES (");

            // 构建占位符部分
            for (int i = 0; i < fields.length; i++) {
               sqlBuilder.append("'"+getFieldValue(columnNames[0],fields[i].getName())+"'"+" , ");

            }
            // 删除最后一个逗号和空格
            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
            sqlBuilder.append(")");

            String sql = sqlBuilder.toString();

            // 使用 PreparedStatement 执行插入操作
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // 执行插入操作
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    // 获取生成的主键值
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        // 构建实体对象并添加到列表中
                        T entity = mapResultSetToEntity(generatedKeys, Arrays.asList(columnNamesArray), clazz);
                        entities.add(entity);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL query", e);
        }
        return entities;
    }


}


