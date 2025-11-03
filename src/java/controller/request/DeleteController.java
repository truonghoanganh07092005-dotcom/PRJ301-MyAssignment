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

@WebServlet("/request/delete")
public class DeleteController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        String ctx = req.getContextPath();

        try {
            int rid = Integer.parseInt(req.getParameter("rid"));

            boolean ok = new RequestForLeaveDBContext()
                    .deleteByOwnerIfInProgress(rid, user.getId());

            if (ok) {
                // Lịch sử
                new RequestHistoryDBContext().add(rid, user.getId(), "DELETED", null);

                // Notify quản lý
                Integer managerUid = new RequestForLeaveDBContext().managerUidOfOwnerByRid(rid);
                if (managerUid != null) {
                    new NotificationDBContext().push(
                        managerUid,
                        "Nhân viên đã XÓA đơn #" + rid,
                        "Đơn đã bị xóa bởi chủ đơn.",
                        rid
                    );
                }
                if (s != null) s.setAttribute("flash", "Đã XÓA đơn #" + rid);
            } else {
                if (s != null) s.setAttribute("flash", "Không thể xóa đơn #" + rid + " (chỉ khi In-Progress & là chủ đơn).");
            }
        } catch (Exception e) {
            if (s != null) s.setAttribute("flash", "Tham số không hợp lệ.");
        }

        resp.sendRedirect(ctx + "/request/my");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        doGet(req, resp, user);
    }
}
