package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/detail")
public class RequestDetailController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        // rid hợp lệ?
        String ridRaw = req.getParameter("rid");
        int rid;
        try {
            rid = Integer.parseInt(ridRaw);
        } catch (Exception ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // lấy EID người đang đăng nhập
        Integer eid = null;
        try {
            if (user != null && user.getEmployee() != null) {
                eid = user.getEmployee().getId();
            }
        } catch (Exception ignore) {}
        if (eid == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        RequestForLeaveDBContext rdb = new RequestForLeaveDBContext();

        // ✅ Check quyền xem
        if (!rdb.canViewRequest(rid, eid)) {
            req.getRequestDispatcher("/WEB-INF/error/403.jsp").forward(req, resp);
            return;
        }

        // OK -> load chi tiết
        RequestForLeave r = rdb.get(rid);
        if (r == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("request", r);
        req.getRequestDispatcher("/WEB-INF/request/detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
