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

        // Lấy 5 đơn gần đây của user
        RequestForLeaveDBContext rdb = new RequestForLeaveDBContext();
        List<RequestForLeave> all = rdb.getByEmployeeAndSubodiaries(u.getId());
        List<RequestForLeave> recent = all.size() > 5 ? all.subList(0, 5) : all;
        req.setAttribute("recent", recent);

        req.setAttribute("displayName", u.getDisplayname());
        req.setAttribute("recent", recent);
        req.getRequestDispatcher("/WEB-INF/home.jsp").forward(req, resp);
    }
}
