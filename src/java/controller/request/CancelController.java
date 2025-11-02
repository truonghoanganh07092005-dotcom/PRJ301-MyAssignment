package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.NotificationDBContext;
import dal.RequestForLeaveDBContext;
import dal.RequestHistoryDBContext;
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
        HttpSession s = req.getSession(false);
        String ctx = req.getContextPath();
        int rid = -1;
        try {
            rid = Integer.parseInt(req.getParameter("rid"));
            Integer prev = new RequestForLeaveDBContext().getStatus(rid);
            boolean ok = new RequestForLeaveDBContext().cancelByOwnerIfInProgress(rid, user.getId());
            if (ok) {
                new RequestHistoryDBContext().add(
                    rid, "CANCEL", user.getId(),
                    (user.getEmployee()==null)? null : user.getEmployee().getId(),
                    prev, 3, null
                );
                Integer managerUid = new RequestForLeaveDBContext().managerUidOfOwnerByRid(rid);
                if (managerUid != null) {
                    String url = ctx + "/request/detail?rid=" + rid;
                    new NotificationDBContext().create(managerUid,
                        "Nhân viên đã HỦY đơn #"+rid+".", url);
                }
                if (s!=null) s.setAttribute("flash","Đã HỦY đơn #"+rid);
            } else if (s!=null) s.setAttribute("flash","Không thể hủy đơn #"+rid);
        } catch (Exception ignore) { if (s!=null) s.setAttribute("flash","Tham số không hợp lệ."); }

        resp.sendRedirect(ctx + "/request/my");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { doGet(req, resp, user); }
}
