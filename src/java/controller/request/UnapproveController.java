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
        String ridStr = req.getParameter("rid");
        HttpSession s = req.getSession();
        String ctx = req.getContextPath();
        try {
            int rid = Integer.parseInt(ridStr);
            boolean ok = new RequestForLeaveDBContext()
                    .unapproveByManagerWithin24h(rid, user.getId());
            s.setAttribute("flash",
                ok ? ("Đã HỦY DUYỆT đơn #" + rid + " (trong 24h).")
                   : ("Không thể hủy duyệt đơn #" + rid + 
                      " (chỉ cho trạng thái Approved trong vòng 24h & là quản lý trực tiếp)."));
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
