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
            if (user != null && user.getEmployee() != null) {
                eid = user.getEmployee().getId();
            }
        } catch (Exception ignore) {}

        List<RequestForLeave> recent = java.util.Collections.emptyList();
        List<RequestForLeave> subs   = java.util.Collections.emptyList();

        if (eid != null) {
            // Mỗi call dùng một DBContext khác nhau vì mỗi hàm có closeConnection()
            recent = new RequestForLeaveDBContext().recentOfEmployee(eid, 5);
            subs   = new RequestForLeaveDBContext().recentOfSubordinates(eid, 5);
        }

        Boolean canReview = (Boolean) req.getSession().getAttribute("canReview");
        if (canReview == null) canReview = false;

        req.setAttribute("recent", recent);
        req.setAttribute("subs", subs);
        req.setAttribute("canReview", canReview);

        req.getRequestDispatcher("/WEB-INF/home.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
