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

    private void goBack(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String r = req.getHeader("Referer");
        if (r != null && !r.isBlank()) resp.sendRedirect(r);
        else resp.sendRedirect(req.getContextPath() + "/request/my");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        int rid = -1;
        try { rid = Integer.parseInt(req.getParameter("rid")); } catch (Exception ignore) {}

        if (rid > 0 && user != null) {
            new RequestForLeaveDBContext().deleteByOwnerIfInProgress(rid, user.getId());
        }
        goBack(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        doGet(req, resp, user);
    }
}
