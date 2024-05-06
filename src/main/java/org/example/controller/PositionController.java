package org.example.controller;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.example.ConfigurationLoader;
import org.example.entity.Miniuser;
import org.example.entity.Position;
import org.example.jdbc.DatabaseConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/position")
public class PositionController extends HttpServlet{


        DatabaseConnection dao = new DatabaseConnection();
        private static final String PROPERTIES_FILE = "sql.properties";

        @SneakyThrows
        public PositionController() {
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
                ResultSet resultSet = metaData.getColumns(null, null, "posi", null);

                List<String> columnNames = new ArrayList<>();
                while (resultSet.next()) {
                    columnNames.add(resultSet.getString("COLUMN_NAME"));
                }
                // 执行 SQL 查询
                String sql = "SELECT * FROM posi WHERE openid = '"+request.getParameter("openid")+"';";
                try (Statement statement = connection.createStatement();
                     ResultSet queryResult = statement.executeQuery(sql)) {

                    List<Position> entities = new ArrayList<>();
                    while (queryResult.next()) {
                        Position entity = DatabaseConnection.mapResultSetToEntity(queryResult, columnNames, Position.class);
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
            Position person = gson.fromJson(data, Position.class);
            if ("delete".equals(person.getAction())) {
                deleteRecord(request, response);
            } else {
                insertRecord(request, response);
            }

        }

    public void insertRecord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = new String(request.getParameter("data").getBytes("ISO-8859-1"), "UTF-8");
        Gson gson = new Gson();
        Position person = gson.fromJson(data, Position.class);


        // 从配置文件加载数据库连接信息
        String sql = "INSERT  INTO posi(openid,username,userphone,userposi) VALUES('"+person.getOpenid()+
                "','"+person.getUsername()+
                "','"+person.getUserphone()+
                "','"+person.getUserposi()+"')";

        String[] datasql = ConfigurationLoader.a();
        try (Connection connection = DriverManager.getConnection(datasql[0], datasql[1], datasql[2]);
             Statement statement = connection.createStatement()) {
            String sql2 = "SELECT * FROM posi WHERE userposi = '"+request.getParameter("userposi")+"'AND openid = '"+request.getParameter("openid")+"'";
            ResultSet queryResult = statement.executeQuery(sql2);
            if (!queryResult.next()) {
                // 执行更新操作
                int rowsAffected = statement.executeUpdate(sql);
                response.getWriter().write(rowsAffected);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = new String(request.getParameter("data").getBytes("ISO-8859-1"), "UTF-8");
        Gson gson = new Gson();
        Position person = gson.fromJson(data, Position.class);


        // 从配置文件加载数据库连接信息
        String sql = "DELETE  FROM posi WHERE  userposi = '"+person.getUserposi()+"'AND openid = '"+person.getOpenid()+"';";

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
