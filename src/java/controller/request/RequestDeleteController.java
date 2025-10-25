// controller/request/RequestDeleteController.java
package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet(urlPatterns = "/request/delete")
public class RequestDeleteController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        String ridStr = req.getParameter("rid");
        if (ridStr == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        int rid = Integer.parseInt(ridStr);

        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        boolean ok = db.deleteByOwnerIfInProgress(rid, user.getId());

        if (ok) {
            req.getSession().setAttribute("flash", "Đã xoá đơn #" + rid);
        } else {
            req.getSession().setAttribute("flash",
                "Không thể xoá đơn (không thuộc bạn hoặc đã xử lý).");
        }
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        processGet(req, resp, user);
    }
}
