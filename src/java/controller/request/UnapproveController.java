package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.NotificationDBContext;
import dal.RequestForLeaveDBContext;
import dal.RequestHistoryDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet("/request/unapprove")
public class UnapproveController extends BaseRequiredAuthorizationController {

    protected boolean isAuthorized(User user, HttpServletRequest req) {
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));
            return new RequestForLeaveDBContext().canUnapprove(rid, user.getId());
        } catch (Exception e) { return false; }
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        HttpSession s = req.getSession();
        String ctx = req.getContextPath();

        try {
            int rid = Integer.parseInt(req.getParameter("rid"));
            boolean ok = new RequestForLeaveDBContext().unapproveWithin24h(rid, user.getId());
            if (ok) {
                new RequestHistoryDBContext().add(rid, user.getId(), "UNAPPROVED", null);
                Integer ownerUid = new RequestForLeaveDBContext().ownerUidByRid(rid);
                if (ownerUid != null) {
                    new NotificationDBContext().push(
                        ownerUid, "Đơn #" + rid + " trở lại chờ duyệt",
                        "Quản lý hủy quyết định trong 24 giờ.", rid
                    );
                }
                s.setAttribute("flash", "Đã HỦY DUYỆT/TỪ CHỐI đơn #" + rid);
            } else {
                s.setAttribute("flash", "Không thể hủy duyệt/từ chối (quá 24h hoặc sai người xử lý).");
            }
        } catch (Exception ex) {
            s.setAttribute("flash", "Tham số không hợp lệ.");
        }
        String back = req.getHeader("Referer");
        resp.sendRedirect(back != null ? back : (ctx + "/home"));
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { processGet(req, resp, user); }
}
