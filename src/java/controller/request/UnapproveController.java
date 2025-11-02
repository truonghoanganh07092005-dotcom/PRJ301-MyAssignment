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

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        HttpSession s = req.getSession();
        String ctx = req.getContextPath();
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));
            Integer prev = new RequestForLeaveDBContext().getStatus(rid);

            boolean ok = new RequestForLeaveDBContext().unapproveWithin24h(rid, user.getId());
            if (ok) {
                new RequestHistoryDBContext().add(
                    rid, "UNAPPROVE", user.getId(),
                    (user.getEmployee()==null)? null : user.getEmployee().getId(),
                    prev, 0, null
                );
                Integer ownerUid = new RequestForLeaveDBContext().ownerUidByRid(rid);
                if (ownerUid != null) {
                    String url = ctx + "/request/detail?rid=" + rid;
                    new NotificationDBContext().create(ownerUid,
                        "Đơn #"+rid+" đã HỦY DUYỆT/TỪ CHỐI (về In-Progress).", url);
                }
                s.setAttribute("flash","Đã hủy duyệt/từ chối – đơn quay về In-Progress.");
            } else s.setAttribute("flash","Không thể hủy (chỉ trong 24h & đúng người xử lý).");
        } catch (Exception e) { s.setAttribute("flash","Tham số không hợp lệ."); }

        String back = req.getHeader("Referer");
        resp.sendRedirect(back != null ? back : (ctx + "/home"));
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { processGet(req, resp, user); }
}
