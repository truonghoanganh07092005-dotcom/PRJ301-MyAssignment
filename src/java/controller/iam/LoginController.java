package controller.iam;

import dal.UserDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // nếu đã đăng nhập rồi thì vào home
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String username = (req.getParameter("username")==null?"":req.getParameter("username").trim());
        String password = (req.getParameter("password")==null?"":req.getParameter("password").trim());

        if (username.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đủ Username và Password.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
            return;
        }

        try {
            UserDBContext db = new UserDBContext();
            User u = db.get(username, password);
            if (u != null) {
                // regenerate session
                HttpSession old = req.getSession(false);
                if (old != null) old.invalidate();
                HttpSession s = req.getSession(true);
                s.setAttribute("user", u);
                s.setMaxInactiveInterval(30*60);
                resp.sendRedirect(req.getContextPath() + "/home");
            } else {
                req.setAttribute("error", "Sai username hoặc password.");
                req.setAttribute("username", username);
                req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi hệ thống khi đăng nhập.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
        }
    }
}
