package org.example.controller;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.example.entity.Miniuser;
import org.example.jdbc.DatabaseConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet("/shop")
public class shopController extends HttpServlet {

    DatabaseConnection dao = new DatabaseConnection();

    @SneakyThrows
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String openId = request.getParameter("openId");

        List<Miniuser> entities = dao.getEntities(openId,"miniuser", Miniuser.class,"openId");
        Gson gson = new Gson();
        String json = gson.toJson(entities);
        response.getWriter().write(json);
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
