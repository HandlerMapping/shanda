package org.example.controller;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.example.entity.Comment;
import org.example.entity.Miniuser;
import org.example.entity.Shop;
import org.example.jdbc.DatabaseConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/comment")
public class CommentController extends HttpServlet {

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

    }

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
        response.setCharacterEncoding("UTF-8");
        String parameterValue = request.getParameter("parameterName");
        if (parameterValue != null) {
            Gson gson = new Gson();
            Comment person = gson.fromJson(parameterValue, Comment.class);
            Object[] objects = new Object[1];
            objects[0] = person;
            List<Comment> entities = dao.postEntities(objects, "comment", Comment.class);
        }
        response.getWriter().write("Response Data");
    }
}

