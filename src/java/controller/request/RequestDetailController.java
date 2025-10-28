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

        String ridRaw = req.getParameter("rid");
        int rid;
        try {
            rid = Integer.parseInt(ridRaw);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "rid không hợp lệ");
            return;
        }

        Integer viewerEid = null;
        try { if (user != null && user.getEmployee() != null) viewerEid = user.getEmployee().getId(); }
        catch (Exception ignore){}

        RequestForLeaveDBContext rdb = new RequestForLeaveDBContext();

        // Quyền xem: chính chủ hoặc quản lý trực tiếp
        if (viewerEid == null || !rdb.canViewRequest(rid, viewerEid)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        RequestForLeave r = rdb.get(rid);
        if (r == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("r", r);
        req.getRequestDispatcher("/WEB-INF/request/detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        doGet(req, resp, user);
    }
}
