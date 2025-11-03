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

@WebServlet("/request/cancel")
public class CancelController extends BaseRequiredAuthorizationController {

    protected boolean isAuthorized(User user, HttpServletRequest req) {
        // cho phép vào, ràng buộc nằm ở update (chỉ owner & status=0 mới hủy)
        try { Integer.parseInt(req.getParameter("rid")); return true; }
        catch (Exception e) { return false; }
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        HttpSession s = req.getSession();
        String ctx = req.getContextPath();

        try {
            int rid = Integer.parseInt(req.getParameter("rid"));
            boolean ok = new RequestForLeaveDBContext()
                    .cancelByOwnerIfInProgress(rid, user.getId());
            if (ok) {
                new RequestHistoryDBContext().add(rid, user.getId(), "CANCELLED", null);
                Integer managerUid = new RequestForLeaveDBContext().managerUidOfOwnerByRid(rid);
                if (managerUid != null) {
                    new NotificationDBContext().push(
                        managerUid, "Đơn #" + rid + " đã hủy",
                        "Chủ đơn hủy khi đang chờ duyệt.", rid
                    );
                }
                s.setAttribute("flash", "Đã HỦY đơn #" + rid);
            } else {
                s.setAttribute("flash", "Chỉ chủ đơn & trạng thái In-Progress mới hủy được.");
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
