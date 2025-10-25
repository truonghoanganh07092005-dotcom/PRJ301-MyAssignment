package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.RequestForLeave;
import model.iam.User;

@WebServlet(urlPatterns = "/request/detail")
public class RequestDetailController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String rid = req.getParameter("rid");
        if (rid == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        RequestForLeave r = db.getByRid(Integer.parseInt(rid)); // bạn thêm method này trong DBContext
        req.setAttribute("req", r);
        req.getRequestDispatcher("/WEB-INF/request/detail.jsp").forward(req, resp);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        processGet(req, resp, user);
    }
}
