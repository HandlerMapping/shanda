package org.example.controller;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.example.ConfigurationLoader;
import org.example.entity.Dingdan;
import org.example.entity.DingdanPO;
import org.example.entity.Miniuser;
import org.example.entity.Position;
import org.example.jdbc.DatabaseConnection;
import org.example.jdbc.LoadP;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@WebServlet("/dingdan")
public class dingdanController extends HttpServlet {

    DatabaseConnection dao = new DatabaseConnection();
    private static final String PROPERTIES_FILE = "sql.properties";

   @SneakyThrows
   public dingdanController() {
       Class.forName("com.mysql.cj.jdbc.Driver");
   }
    @SneakyThrows
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 从配置文件加载数据库连接信息
        String[] datasql = ConfigurationLoader.a();
        try (Connection connection = DriverManager.getConnection(datasql[0], datasql[1], datasql[2])) {
            // 获取数据库元数据
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, "dingdan", null);

            List<String> columnNames = new ArrayList<>();
            while (resultSet.next()) {
                columnNames.add(resultSet.getString("COLUMN_NAME"));
            }

            // 执行 SQL 查询
            String sql = "SELECT * FROM dingdan WHERE created_by = '"+request.getParameter("createdBy")+"';";
            try (Statement statement = connection.createStatement();
                 ResultSet queryResult = statement.executeQuery(sql)) {

                List<Dingdan> entities = new ArrayList<>();
                while (queryResult.next()) {
                    Dingdan entity = DatabaseConnection.mapResultSetToEntity(queryResult, columnNames, Dingdan.class);
                    entities.add(entity);
                }



                // 将实体列表转换为 JSON 字符串并写入响应
                Gson gson = new Gson();
                String json = gson.toJson(entities);
                response.getWriter().write(json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error retrieving data from the database");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String data = request.getParameter("data");
        Gson gson = new Gson();
        Dingdan person = gson.fromJson(data, Dingdan.class);
        if ("update".equals(person.getAction())) {
            updateRecord(request, response);
        } else {
            insertRecord(request, response);
        }

    }

    public void insertRecord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String parameterValue = new String(request.getParameter("data").getBytes("ISO-8859-1"), "UTF-8");
        if (parameterValue != null) {
            Gson gson = new Gson();
            String[] datasql = ConfigurationLoader.a();
            DingdanPO person = gson.fromJson(parameterValue, DingdanPO.class);
            try (Connection connection = DriverManager.getConnection(datasql[0], datasql[1], datasql[2])) {
                String sql = "INSERT INTO dingdan (zt, caidan, price, created_by, created_at, updated_at, acl, position,phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                    // Convert ArrayList to JSON string
                    String caidanJson = gson.toJson(person.getCaidan());
                    preparedStatement.setString(1, person.getZt());
                    preparedStatement.setString(2, caidanJson);
                    preparedStatement.setBigDecimal(3, person.getPrice());
                    preparedStatement.setString(4, person.getCreatedBy());
                    preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    preparedStatement.setTimestamp(6, null);
                    preparedStatement.setString(7, person.getAcl());
                    preparedStatement.setString(8, person.getPosition());
                    preparedStatement.setString(9, person.getPhone());
                    // 执行SQL插入操作
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        response.getWriter().write("Record inserted successfully");
                    } else {
                        response.getWriter().write("Failed to insert record");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error inserting record into the database");
            }
        } else {
            response.getWriter().write("No data received");
        }
    }


    public void updateRecord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String zt = request.getParameter("zt");
        String id = request.getParameter("id");
        // 从配置文件加载数据库连接信息
        String sql = "UPDATE dingdan SET zt = "+zt+" WHERE id = "+id;

        String[] datasql = ConfigurationLoader.a();
        try (Connection connection = DriverManager.getConnection(datasql[0], datasql[1], datasql[2]);
             Statement statement = connection.createStatement()) {

            // 执行更新操作
            int rowsAffected = statement.executeUpdate(sql);
            response.getWriter().write(rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
