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

@WebServlet("/request/uncancel")
public class UncancelController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String ctx = req.getContextPath();
        HttpSession s = req.getSession();
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));
            Integer prev = new RequestForLeaveDBContext().getStatus(rid);
            boolean ok = new RequestForLeaveDBContext().uncancelByOwnerIfCancelled(rid, user.getId());
            if (ok) {
                new RequestHistoryDBContext().add(
                    rid, "UN_CANCEL", user.getId(),
                    (user.getEmployee()==null)? null : user.getEmployee().getId(),
                    prev, 0, null
                );
                Integer managerUid = new RequestForLeaveDBContext().managerUidOfOwnerByRid(rid);
                if (managerUid != null) {
                    String url = ctx + "/request/detail?rid=" + rid;
                    new NotificationDBContext().create(managerUid,
                        "Nhân viên KHÔI PHỤC đơn #"+rid+" để tiếp tục xử lý.", url);
                }
                s.setAttribute("flash","Đã khôi phục trạng thái đơn.");
            } else s.setAttribute("flash","Không thể khôi phục đơn.");
        } catch (Exception e) { s.setAttribute("flash","Thao tác không hợp lệ."); }
        resp.sendRedirect(ctx + "/request/my");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { doGet(req, resp, user); }
}
