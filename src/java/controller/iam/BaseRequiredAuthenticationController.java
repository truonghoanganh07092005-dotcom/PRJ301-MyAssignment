package controller.iam;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

public abstract class BaseRequiredAuthenticationController extends HttpServlet {

    private User getLoginUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return null;
        Object o = s.getAttribute("auth");
        if (o == null) o = s.getAttribute("user");   // bắt luôn trường hợp bạn lưu là "user"
        return (o instanceof User) ? (User) o : null;
    }

    // các hàm con cần override
    protected abstract void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException;

    protected abstract void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException;

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User u = getLoginUser(req);
        if (u != null) {
            doPost(req, resp, u);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login?timeout=1");
        }
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User u = getLoginUser(req);
        if (u != null) {
            doGet(req, resp, u);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login?timeout=1");
        }
    }
}
