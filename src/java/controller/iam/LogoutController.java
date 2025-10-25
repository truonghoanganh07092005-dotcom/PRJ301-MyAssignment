package controller.iam;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Hủy session nếu có
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();

        // (tuỳ chọn) xoá cookie JSESSIONID để trình duyệt tạo phiên mới
        Cookie jsid = new Cookie("JSESSIONID", "");
        jsid.setPath(req.getContextPath());
        jsid.setMaxAge(0);
        resp.addCookie(jsid);

        // Chuyển về trang login
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
