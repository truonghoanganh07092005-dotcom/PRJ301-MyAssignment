// src/java/controller/request/ApproveController.java
package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet(urlPatterns = "/request/approve")
public class ApproveController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String ridStr = req.getParameter("rid");
        HttpSession s = req.getSession();
        String ctx = req.getContextPath();
        try {
            int rid = Integer.parseInt(ridStr);
            boolean ok = new RequestForLeaveDBContext()
                    .approveByManagerIfInProgress(rid, user.getId());
            s.setAttribute("flash", ok ? ("Đã DUYỆT đơn #" + rid) :
                                         ("Không thể duyệt đơn #" + rid + " (chỉ duyệt khi In Progress & là quản lý trực tiếp)."));
        } catch (Exception ex) {
            s.setAttribute("flash", "Tham số không hợp lệ.");
        }
        // quay về trang trước nếu có, mặc định /home
        String back = req.getHeader("Referer");
        resp.sendRedirect(back != null ? back : (ctx + "/home"));
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        processGet(req, resp, user);
    }
}
