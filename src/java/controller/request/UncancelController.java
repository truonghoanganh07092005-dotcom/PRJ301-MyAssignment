package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet("/request/uncancel")
public class UncancelController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String ridStr = req.getParameter("rid");
        HttpSession s = req.getSession();
        String ctx = req.getContextPath();

        try {
            int rid = Integer.parseInt(ridStr);
            boolean ok = new RequestForLeaveDBContext()
                    .uncancelByOwnerIfCancelled(rid, user.getId());
            s.setAttribute("flash", ok ? "Đã khôi phục trạng thái đơn." : "Không thể khôi phục đơn.");
        } catch (Exception e) {
            s.setAttribute("flash", "Thao tác không hợp lệ.");
        }
        resp.sendRedirect(ctx + "/request/my");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { doGet(req, resp, user); }
}
