package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet(urlPatterns = "/request/reject")
public class RejectController extends BaseRequiredAuthorizationController {
    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String ridStr = req.getParameter("rid");
        HttpSession s = req.getSession();
        String ctx = req.getContextPath();
        try {
            int rid = Integer.parseInt(ridStr);
            boolean ok = new RequestForLeaveDBContext()
                    .rejectByManagerIfInProgress(rid, user.getId());
            s.setAttribute("flash", ok
                    ? ("ĐÃ TỪ CHỐI đơn #" + rid)
                    : ("Không thể từ chối đơn #" + rid + " (chỉ khi In-Progress & đúng cấp quản lý)."));
        } catch (Exception ex) {
            s.setAttribute("flash", "Tham số không hợp lệ.");
        }
        String back = req.getHeader("Referer");
        resp.sendRedirect(back != null ? back : (ctx + "/home"));
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        processGet(req, resp, user);
    }
}
