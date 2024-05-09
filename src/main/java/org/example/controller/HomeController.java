package org.example.controller;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.example.entity.Miniuser;
import org.example.entity.Shop;
import org.example.jdbc.DatabaseConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeController extends HttpServlet {

    DatabaseConnection dao = new DatabaseConnection();

    public void selectAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        List<Shop> entities = dao.getEntities(null,"shop", Shop.class,"");
        Gson gson = new Gson();
        String json = gson.toJson(entities);
        response.getWriter().write(json);
    }

    public void selectOne(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        List<Shop> entities = dao.getEntities(id,"shop", Shop.class,"id");
        Gson gson = new Gson();
        String json = gson.toJson(entities);
        response.getWriter().write(json);
    }

    @SneakyThrows
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String data = request.getParameter("action");
        if ("one".equals(data)) {
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
                     dao.postEntities(objects, "miniuser", Miniuser.class);
                }
            }
        }
        response.getWriter().write("Response Data");
    }
}

