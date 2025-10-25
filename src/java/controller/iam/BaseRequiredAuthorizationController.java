// src/java/controller/iam/BaseRequiredAuthorizationController.java
package controller.iam;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.Feature;
import model.iam.Role;
import model.iam.User;
import dal.RoleDBContext;

public abstract class BaseRequiredAuthorizationController
        extends BaseRequiredAuthenticationController {

    private boolean isAuthorized(HttpServletRequest req, User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            RoleDBContext db = new RoleDBContext();
            user.setRoles(db.getByUserId(user.getId()));
            req.getSession().setAttribute("auth", user);
        }
        String url = req.getServletPath(); // ví dụ: /request/create
        for (Role r : user.getRoles()) {
            for (Feature f : r.getFeatures()) {
                if (url.equals(f.getUrl())) return true;
            }
        }
        return false;
    }

    protected abstract void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException;

    protected abstract void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException;

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        if (isAuthorized(req, user)) {
            processPost(req, resp, user);
        } else {
            showForbidden(req, resp);
        }
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        if (isAuthorized(req, user)) {
            processGet(req, resp, user);
        } else {
            showForbidden(req, resp);
        }
    }

    private void showForbidden(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Có thể set thêm thông tin để hiển thị
        req.setAttribute("forbidden_message", "Bạn không có quyền truy cập chức năng này.");
        req.setAttribute("backUrl", req.getContextPath() + "/home");
        // forward tới trang báo quyền
        req.getRequestDispatcher("/WEB-INF/error/forbidden.jsp").forward(req, resp);
    }
}
