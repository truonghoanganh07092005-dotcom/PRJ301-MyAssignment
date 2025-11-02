package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import dal.RequestHistoryDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import model.RequestActionHistory;
import model.iam.User;

@WebServlet("/request/history")
public class HistoryController extends BaseRequiredAuthenticationController {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        int rid;
        try { rid = Integer.parseInt(req.getParameter("rid")); }
        catch (Exception e){ resp.sendError(400,"rid invalid"); return; }

        // quyền xem: tái dùng rule canViewRequest
        Integer viewerEid = (user.getEmployee()==null)? null : user.getEmployee().getId();
        boolean canView = new RequestForLeaveDBContext().canViewRequest(rid, viewerEid);
        if (!canView){ resp.sendError(403); return; }

        ArrayList<RequestActionHistory> list = new RequestHistoryDBContext().listByRid(rid);
        req.setAttribute("rid", rid);
        req.setAttribute("history", list);
        req.getRequestDispatcher("/WEB-INF/request/history.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { doGet(req, resp, user); }
}
