package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.iam.User;

@WebServlet("/request/delete")
public class RequestDeleteController extends BaseRequiredAuthenticationController {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        int rid = -1;
        try { rid = Integer.parseInt(req.getParameter("rid")); } catch (Exception ignore) {}

        boolean ok = false;
        if (rid > 0 && user != null) {
            ok = new RequestForLeaveDBContext().deleteByOwnerIfInProgress(rid, user.getId());
        }
        // có thể set flash message ở session nếu bạn muốn
        resp.sendRedirect(req.getContextPath() + "/request/my");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        // an toàn: chỉ cho xóa bằng POST
        resp.sendRedirect(req.getContextPath() + "/request/my");
    }
}
