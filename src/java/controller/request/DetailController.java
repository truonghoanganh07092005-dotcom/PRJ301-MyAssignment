package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/detail")
public class DetailController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        int rid = parseInt(req.getParameter("rid"));
        if (rid <= 0){ resp.sendError(400, "rid invalid"); return; }

        Integer viewerEid = (user.getEmployee()==null)? null : user.getEmployee().getId();
        boolean canView = new RequestForLeaveDBContext().canViewRequest(rid, viewerEid);
        if (!canView) {
            req.setAttribute("forbidden_message", "Bạn không có quyền xem đơn #" + rid);
            req.setAttribute("backUrl", req.getContextPath() + "/home");
            req.getRequestDispatcher("/WEB-INF/error/forbidden.jsp").forward(req, resp);
            return;
        }

        RequestForLeave r = new RequestForLeaveDBContext().get(rid);
        req.setAttribute("requestObj", r);
        req.getRequestDispatcher("/WEB-INF/request/detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { doGet(req, resp, user); }

    private int parseInt(String s){ try { return Integer.parseInt(s); } catch(Exception e){ return -1; } }
}
