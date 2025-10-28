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

@WebServlet(urlPatterns = {"/request/detail"})
public class RequestDetailController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        String ridRaw = req.getParameter("rid");
        if (ridRaw == null || ridRaw.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int rid;
        try {
            rid = Integer.parseInt(ridRaw);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Integer eid = (user != null && user.getEmployee() != null)
                ? user.getEmployee().getId() : null;

        RequestForLeaveDBContext rdb = new RequestForLeaveDBContext();

        // chỉ cho xem nếu là chính chủ hoặc quản lý trực tiếp
        if (!rdb.canViewRequest(rid, eid)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        RequestForLeave r = rdb.get(rid);
        if (r == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("requestObj", r);
        // CHÚ Ý: trong project của bạn file detail.jsp nằm ở /view/detail.jsp
        req.getRequestDispatcher("/view/detail.jsp").forward(req, resp);
    }

    // để class không còn abstract
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
