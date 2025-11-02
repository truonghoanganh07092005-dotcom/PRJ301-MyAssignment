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

@WebServlet(urlPatterns = "/request/approve")
public class ApproveController extends BaseRequiredAuthorizationController {

    protected boolean isAuthorized(User user, HttpServletRequest req) {
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));
            // chỉ kiểm tra có thể duyệt theo nghiệp vụ (In-Progress & đúng manager)
            // ủy quyền chi tiết nằm ở DB update, nên ở đây cho phép vào trang
            return true;
        } catch (Exception e) { return false; }
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        HttpSession s = req.getSession();
        String ctx = req.getContextPath();
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));

            Integer prev = new RequestForLeaveDBContext().getStatus(rid);
            boolean ok = new RequestForLeaveDBContext()
                    .approveByManagerIfInProgress(rid, user.getId());

            if (ok) {
                new RequestHistoryDBContext().add(
                    rid, "APPROVE", user.getId(),
                    (user.getEmployee()==null)? null : user.getEmployee().getId(),
                    prev, 1, null
                );
                Integer ownerUid = new RequestForLeaveDBContext().ownerUidByRid(rid);
                if (ownerUid != null) {
                    String url = ctx + "/request/detail?rid=" + rid;
                    new NotificationDBContext().create(ownerUid,
                        "Đơn #" + rid + " đã được DUYỆT.", url);
                }
                s.setAttribute("flash", "Đã DUYỆT đơn #" + rid);
            } else {
                s.setAttribute("flash","Không thể duyệt đơn (chỉ khi In-Progress & đúng cấp).");
            }
        } catch (Exception ex) { s.setAttribute("flash","Tham số không hợp lệ."); }

        String back = req.getHeader("Referer");
        resp.sendRedirect(back != null ? back : (ctx + "/home"));
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { processGet(req, resp, user); }
}
