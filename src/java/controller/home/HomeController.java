package controller.home;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/home")
public class HomeController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        List<RequestForLeave> recent = Collections.emptyList();
        List<RequestForLeave> subs   = Collections.emptyList();

        // recent (đơn của tôi) lấy theo EID
        Integer eid = null;
        try {
            if (user != null && user.getEmployee() != null) {
                eid = user.getEmployee().getId();
            }
        } catch (Exception ignore) {}

        if (eid != null) {
            // dùng instance MỚI cho mỗi query vì DBContext tự đóng connection sau mỗi lần chạy
            recent = new RequestForLeaveDBContext().recentOfEmployee(eid, 5);
        }

        // subs (đơn cấp dưới) lấy theo UID quản lý → map sang EID bên trong DBContext
        if (user != null) {
            subs = new RequestForLeaveDBContext().recentOfSubordinatesByUid(user.getId(), 5);
        }

        req.setAttribute("recent", recent);
        req.setAttribute("subs", subs);
        req.getRequestDispatcher("/WEB-INF/home.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
