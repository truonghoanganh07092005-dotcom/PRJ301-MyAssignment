package controller.iam;

import dal.UserDBContext;
import dal.RoleDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Optional;
import model.iam.User;
import model.iam.Role;
import model.iam.Feature;

/**
 * /login: xử lý đăng nhập, đặt session:
 *  - "auth"  : User    (để tương thích BaseRequiredAuthenticationController)
 *  - "user"  : User    (nếu bạn dùng tên này ở chỗ khác)
 *  - "displayName": String (tên chào hỏi)
 *  - "canReview"  : Boolean (có quyền duyệt đơn hay không)
 */
@WebServlet("/login")
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("auth") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String username = Optional.ofNullable(req.getParameter("username")).orElse("").trim();
        String password = Optional.ofNullable(req.getParameter("password")).orElse("").trim();

        if (username.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập đủ Username và Password.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
            return;
        }

        try {
            UserDBContext udb = new UserDBContext();
            User u = udb.get(username, password);

            if (u == null) {
                req.setAttribute("error", "Sai username hoặc password.");
                req.setAttribute("username", username);
                req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
                return;
            }

            // regenerate session
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();

            HttpSession s = req.getSession(true);
            // để tương thích mọi nơi:
            s.setAttribute("auth", u);
            s.setAttribute("user", u);

            // Lấy roles + features (nếu bạn đã có RoleDBContext)
            try {
                RoleDBContext rdb = new RoleDBContext();
                u.setRoles(rdb.getByUserId(u.getId()));
            } catch (Exception ignore) { /* không bắt buộc */ }

            // Tên hiển thị
            s.setAttribute("displayName", resolveDisplayName(u));

            // Có quyền duyệt hay không? => có feature /request/review hoặc /request/approve
            boolean canReview = hasReviewRight(u);
            s.setAttribute("canReview", canReview);

            // phiên
            s.setMaxInactiveInterval(30 * 60);
            resp.sendRedirect(req.getContextPath() + "/home");

        } catch (Exception ex) {
            ex.printStackTrace();
            req.setAttribute("error", "Có lỗi hệ thống khi đăng nhập.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("view/auth/login.jsp").forward(req, resp);
        }
    }

    /* ---------- Helpers ---------- */

    private String resolveDisplayName(Object userObj) {
    // thử lần lượt: user.displayName → user.fullName → user.name
    // → user.employee.name → user.username → "User"
    String v = getIfExists(userObj, "getDisplayName");
    if (isBlank(v)) v = getIfExists(userObj, "getFullName");
    if (isBlank(v)) v = getIfExists(userObj, "getName");

    if (isBlank(v)) {
        Object emp = getObject(userObj, "getEmployee");
        if (emp != null) {
            v = getIfExists(emp, "getName");
        }
    }
    if (isBlank(v)) v = getIfExists(userObj, "getUsername");

    return isBlank(v) ? "User" : v.trim();
}

private String getIfExists(Object obj, String method) {
    try {
        var m = obj.getClass().getMethod(method);
        Object r = m.invoke(obj);
        return (r == null) ? null : r.toString();
    } catch (Exception ignore) {
        return null;
    }
}

private Object getObject(Object obj, String method) {
    try {
        var m = obj.getClass().getMethod(method);
        return m.invoke(obj);
    } catch (Exception ignore) {
        return null;
    }
}

private boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
}

  private boolean hasReviewRight(User u) {
    try {
        if (u.getRoles() == null) return false;
        for (Role r : u.getRoles()) {
            if (r.getFeatures() == null) continue;
            for (Feature f : r.getFeatures()) {
                String url = f.getUrl();
                if (url == null) continue;
                url = url.trim().toLowerCase();
                if (url.startsWith("/request/review")
                    || url.startsWith("/request/approve")
                    || url.startsWith("/request/reject")
                    || url.equals("/request/")               // ✅ thêm dòng này
                    || url.startsWith("/request/unapprove")) // để hiện menu hủy duyệt
                    return true;
            }
        }
    } catch (Exception ignore) {}
    return false;
}

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
