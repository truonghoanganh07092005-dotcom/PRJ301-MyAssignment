// src/java/controller/request/ListController.java
package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.RequestForLeave;
import model.iam.User;

@WebServlet(urlPatterns = {"/request/my", "/request/team"})
public class ListController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        String path = req.getServletPath(); // "/request/my" hoặc "/request/team"
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        List<RequestForLeave> data;

        if ("/request/team".equals(path)) {
            data = db.recentOfSubordinates(user.getId(), 50); // hoặc list đầy đủ
            req.setAttribute("title", "Đơn cấp dưới");
        } else {
            data = db.recentOfEmployee(user.getId(), 50);
            req.setAttribute("title", "Đơn của tôi");
        }

        req.setAttribute("list", data);
        req.getRequestDispatcher("/WEB-INF/request/list.jsp").forward(req, resp);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
