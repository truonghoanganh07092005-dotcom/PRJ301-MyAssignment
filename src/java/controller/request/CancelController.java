package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet("/request/cancel")
public class CancelController extends BaseRequiredAuthenticationController {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        int rid = parseInt(req.getParameter("rid"));
        boolean ok = new RequestForLeaveDBContext().cancelByOwnerIfInProgress(rid, user.getId());
        HttpSession s = req.getSession(false);
        if (s != null) s.setAttribute("flash", ok ? "Đã HỦY đơn #" + rid : "Không thể hủy đơn #" + rid);
        resp.sendRedirect(req.getContextPath() + "/request/my");
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { doGet(req, resp, user); }
    private int parseInt(String s){ try { return Integer.parseInt(s); } catch(Exception e){ return -1; } }
}
