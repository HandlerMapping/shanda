package org.example.controller;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.example.entity.Miniuser;
import org.example.entity.MiniuserPO;
import org.example.jdbc.DatabaseConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;


@WebServlet("/miniuser")
public class MiniUserController extends HttpServlet {

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
        String parameterValue = new String(request.getParameter("parameterName").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        System.out.println(parameterValue);
        Gson gson = new Gson();
        MiniuserPO person = gson.fromJson(parameterValue, MiniuserPO.class);
        if (person.getAction().equals("delete")){
            deleteRecord(request,response);
        }else {
            if (person.getOpenid() != null && !person.getOpenid().equals("")) {
                if (dao.getEntities(person.getOpenid(), "miniuser", Miniuser.class, "openId").size() <= 0) {
                    Object[] objects = new Object[1];
                    Miniuser miniuser = new Miniuser();
                    miniuser.setUseravatar(person.getUseravatar());
                    miniuser.setOpenid(person.getOpenid());
                    miniuser.setUsername(person.getUsername());
                    miniuser.setUserphone(person.getUserphone());
                    objects[0] = miniuser;
                    dao.postEntities(objects, "miniuser", Miniuser.class);
                }
            }
        }
        response.getWriter().write("Response Data");
    }

    public void deleteRecord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = new String(request.getParameter("parameterName").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Miniuser person = gson.fromJson(data, Miniuser.class);
        // 从配置文件加载数据库连接信息
        String sql = "DELETE  FROM miniuser WHERE  openid = '"+person.getOpenid()+"';";
        response.getWriter().write(DatabaseConnection.sql(sql));

    }
}
