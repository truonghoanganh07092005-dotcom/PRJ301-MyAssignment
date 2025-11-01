package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet(urlPatterns = "/request/unapprove")
public class UnapproveController extends BaseRequiredAuthorizationController {
    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String ctx = req.getContextPath();
        HttpSession s = req.getSession();
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));
            boolean ok = new RequestForLeaveDBContext().unapproveWithin24h(rid, user.getId());
            s.setAttribute("flash", ok
                    ? "Đã hủy duyệt/từ chối, đơn quay về In-Progress."
                    : "Không thể hủy (chỉ trong 24h và đúng người đã xử lý).");
        } catch (Exception e) {
            s.setAttribute("flash", "Tham số không hợp lệ.");
        }
        String back = req.getHeader("Referer");
        resp.sendRedirect(back != null ? back : (ctx + "/home"));
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { processGet(req, resp, user); }
}
