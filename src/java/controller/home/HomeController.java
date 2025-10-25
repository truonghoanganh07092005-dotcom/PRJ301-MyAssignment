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

        Integer eid = null;
        try {
            if (user != null && user.getEmployee() != null)
                eid = user.getEmployee().getId();
        } catch (Exception ignore) {}

        List<RequestForLeave> recent = Collections.emptyList();
        List<RequestForLeave> subs   = Collections.emptyList();

        if (eid != null) {
            RequestForLeaveDBContext rdb = new RequestForLeaveDBContext();
            // Top 5 đơn gần đây của chính nhân viên
            recent = rdb.recentOfEmployee(eid, 5);

            // Nếu có quyền duyệt thì lấy thêm đơn cấp dưới (top 5)
            Boolean canReview = (Boolean) req.getSession().getAttribute("canReview");
            if (canReview != null && canReview) {
                subs = rdb.recentOfSubordinates(eid, 5);
            }
        }

        req.setAttribute("recent", recent);
        req.setAttribute("subs", subs);

        // render trang
        req.getRequestDispatcher("/WEB-INF/home.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        // Trang home chỉ GET
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
