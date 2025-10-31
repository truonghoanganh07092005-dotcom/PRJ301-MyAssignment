package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/print")
public class PrintController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        int rid = parseInt(req.getParameter("rid"));
        if (rid <= 0){ resp.sendError(400, "rid invalid"); return; }

        Integer viewerEid = (user.getEmployee()==null)? null : user.getEmployee().getId();
        boolean canView = new RequestForLeaveDBContext().canViewRequest(rid, viewerEid);
        if (!canView) { resp.sendError(403); return; }

        RequestForLeave r = new RequestForLeaveDBContext().get(rid);
        req.setAttribute("requestObj", r);
        req.getRequestDispatcher("/WEB-INF/request/print.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { doGet(req, resp, user); }

    private int parseInt(String s){ try { return Integer.parseInt(s); } catch(Exception e){ return -1; } }
}
