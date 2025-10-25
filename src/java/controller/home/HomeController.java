package controller.home;

import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/home")
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession s = req.getSession(false);
        User u = (s == null) ? null : (User) s.getAttribute("user");
        if (u == null) {
            req.setAttribute("error", "Bạn chưa đăng nhập. Vui lòng đăng nhập trước.");
            req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
            return;
        }

        RequestForLeaveDBContext rdb = new RequestForLeaveDBContext();

// 5 đơn của chính user
List<RequestForLeave> recent = rdb.recentOfEmployee(u.getId(), 5);
req.setAttribute("recent", recent);

// 5 đơn của cấp dưới (nếu có)
RequestForLeaveDBContext rdb2 = new RequestForLeaveDBContext();
List<RequestForLeave> teamRecent = rdb2.recentOfSubordinates(u.getId(), 5);
if (teamRecent != null && !teamRecent.isEmpty()) {
    req.setAttribute("teamRecent", teamRecent);
}


req.setAttribute("displayName", u.getDisplayname());


req.getRequestDispatcher("/WEB-INF/home.jsp").forward(req, resp);
    }
}
