package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import dal.RequestHistoryDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet("/request/history")
public class HistoryController extends BaseRequiredAuthenticationController {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, model.iam.User user)
            throws ServletException, IOException {
        int rid = Integer.parseInt(req.getParameter("rid"));
        // quyền xem: chủ đơn hoặc quản lý trực tiếp
        Integer viewerEid = (user.getEmployee()==null)? null : user.getEmployee().getId();
        boolean canView = new RequestForLeaveDBContext().canViewRequest(rid, viewerEid);
        if (!canView) { resp.sendError(403); return; }

        req.setAttribute("rid", rid);
        req.setAttribute("rows", new RequestHistoryDBContext().listByRid(rid));
        req.getRequestDispatcher("/WEB-INF/history.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
