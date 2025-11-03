package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import dal.RequestHistoryDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.RequestHistory;
import model.iam.User;

@WebServlet("/request/history")
public class HistoryController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String ctx = req.getContextPath();
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));

            // chỉ cho xem nếu là chủ đơn hoặc quản lý trực tiếp
            Integer viewerEid = (user.getEmployee()==null)? null : user.getEmployee().getId();
            boolean can = new RequestForLeaveDBContext().canViewRequest(rid, viewerEid);
            if (!can) { resp.sendRedirect(ctx + "/home"); return; }

            List<RequestHistory> list = new RequestHistoryDBContext().listByRid(rid, 100);
            req.setAttribute("rid", rid);
            req.setAttribute("list", list);
            req.getRequestDispatcher("/WEB-INF/request/history.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(ctx + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
