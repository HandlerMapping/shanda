package org.example.controller;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.example.ConfigurationLoader;
import org.example.entity.Comment;
import org.example.entity.Dingdan;
import org.example.jdbc.DatabaseConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/comment")
public class CommentController extends HttpServlet {

    DatabaseConnection dao = new DatabaseConnection();

    @SneakyThrows
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        List<Comment> entities = dao.getEntities(id,"comment", Comment.class,"id");
        Gson gson = new Gson();
        String json = gson.toJson(entities);
        response.getWriter().write(json);

    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String parameterValue = new String(request.getParameter("parameterName").getBytes(StandardCharsets.ISO_8859_1), "UTF-8");
        Gson gson = new Gson();
        Comment person = gson.fromJson(parameterValue, Comment.class);
        Object[] objects = new Object[1];
        objects[0] = person;
        dao.postEntities(objects, "comment", Comment.class);

        // 从配置文件加载数据库连接信息
        Dingdan dingdan = new Dingdan();
        String[] datasql = ConfigurationLoader.a();
        try (Connection connection = DriverManager.getConnection(datasql[0], datasql[1], datasql[2]);
             Statement statement = connection.createStatement()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, "dingdan", null);
            List<String> columnNames = new ArrayList<>();
            while (resultSet.next()) {
                columnNames.add(resultSet.getString("COLUMN_NAME"));
            }
            String query = "SELECT * FROM dingdan where id = "+person.getCid();
            ResultSet zt = statement.executeQuery(query);
            while (zt.next()) {
                dingdan = DatabaseConnection.mapResultSetToEntity(zt, columnNames, Dingdan.class);
            }
            // 执行更新操作
            String sql = "UPDATE dingdan SET zt = "+(dingdan.getZt()+3)%4+" WHERE id = "+person.getCid();
            int rowsAffected = statement.executeUpdate(sql);
            response.getWriter().write(rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.getWriter().write("Response Data");
    }
}

