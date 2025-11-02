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

@WebServlet(urlPatterns = "/request/reject")
public class RejectController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        HttpSession s = req.getSession();
        String ctx = req.getContextPath();
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));

            Integer prev = new RequestForLeaveDBContext().getStatus(rid);
            boolean ok = new RequestForLeaveDBContext()
                    .rejectByManagerIfInProgress(rid, user.getId());

            if (ok) {
                new RequestHistoryDBContext().add(
                    rid, "REJECT", user.getId(),
                    (user.getEmployee()==null)? null : user.getEmployee().getId(),
                    prev, 2, null
                );
                Integer ownerUid = new RequestForLeaveDBContext().ownerUidByRid(rid);
                if (ownerUid != null) {
                    String url = ctx + "/request/detail?rid=" + rid;
                    new NotificationDBContext().create(ownerUid,
                        "Đơn #"+rid+" đã bị TỪ CHỐI.", url);
                }
                s.setAttribute("flash","ĐÃ TỪ CHỐI đơn #"+rid);
            } else s.setAttribute("flash","Không thể từ chối (chỉ khi In-Progress & đúng cấp).");
        } catch (Exception ex) { s.setAttribute("flash","Tham số không hợp lệ."); }

        String back = req.getHeader("Referer");
        resp.sendRedirect(back != null ? back : (ctx + "/home"));
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { processGet(req, resp, user); }
}
