package org.example.controller;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.example.entity.Miniuser;
import org.example.entity.Position;
import org.example.entity.Shop;
import org.example.jdbc.DatabaseConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/home")
public class HomeController {

    DatabaseConnection dao = new DatabaseConnection();

    public void selectOne(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        List<Shop> entities = dao.getEntities(null,"shop", Shop.class,null);
        Gson gson = new Gson();
        String json = gson.toJson(entities);
        response.getWriter().write(json);
    }

    public void selectAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        List<Shop> entities = dao.getEntities(id,"shop", Shop.class,id);
        Gson gson = new Gson();
        String json = gson.toJson(entities);
        response.getWriter().write(json);
    }

//    public <T> List<T> getEntities(String where,String tableName, Class<T> clazz,String name) {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        List<T> entities = new ArrayList<>();
//        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
//            DatabaseMetaData metaData = connection.getMetaData();
//            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
//
//            List<String> columnNames = new ArrayList<>();
//            while (resultSet.next()) {
//                columnNames.add(resultSet.getString("COLUMN_NAME"));
//            }
//            StringBuffer sql = new StringBuffer("SELECT * FROM " + tableName);
//            if (where != null){
//                sql.append(" WHERE openId = '"+where+"';");
//            }
//            try (Statement statement = connection.createStatement();
//                 ResultSet queryResult = statement.executeQuery(String.valueOf(sql))) {
//
//                while (queryResult.next()) {
//                    T entity = mapResultSetToEntity(queryResult, columnNames, clazz);
//                    entities.add(entity);
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to execute SQL query", e);
//        }
//        return entities;
//    }

    @SneakyThrows
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String data = request.getParameter("data");
        Gson gson = new Gson();
        Position person = gson.fromJson(data, Position.class);
        if ("one".equals(person.getAction())) {
            selectOne(request, response);
        } else {
            selectAll(request, response);
        }

    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String parameterValue = request.getParameter("parameterName");
        System.out.println(parameterValue);
        String openId = request.getParameter("openId");

        if (parameterValue != null) {
            Gson gson = new Gson();
            Miniuser person = gson.fromJson(parameterValue, Miniuser.class);
            if (person.getOpenid() != null && person.getOpenid() != ""){
                if (dao.getEntities(person.getOpenid(),"miniuser", Miniuser.class,"openId").size()<=0) {
                    Object[] objects = new Object[1];
                    objects[0] = person;
                    List<Miniuser> entities = dao.postEntities(objects, "miniuser", Miniuser.class);
                }
            }
        }
        response.getWriter().write("Response Data");
    }
}
