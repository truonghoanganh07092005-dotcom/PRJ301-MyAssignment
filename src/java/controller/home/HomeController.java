package controller.home;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet("/home")
public class HomeController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession s = req.getSession(false);
        User u = (s == null) ? null : (User) s.getAttribute("user");
        if (u == null) {
            // chưa đăng nhập → quay lại login và hiện lỗi ngay trên trang login
            req.setAttribute("error", "Bạn chưa đăng nhập. Vui lòng đăng nhập trước.");
            req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
            return;
        }
        req.setAttribute("displayName", u.getDisplayname());
     req.getRequestDispatcher("/WEB-INF/home.jsp").forward(req, resp);
    }
}
