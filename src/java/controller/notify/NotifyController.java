package controller.notify;

import controller.iam.BaseRequiredAuthenticationController;
import dal.NotificationDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import model.Notification;
import model.iam.User;

@WebServlet("/notify")
public class NotifyController extends BaseRequiredAuthenticationController {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        ArrayList<Notification> list = new NotificationDBContext().listUnreadByUid(user.getId(), 50);
        req.setAttribute("list", list);
        req.getRequestDispatcher("/WEB-INF/notify/list.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String nid = req.getParameter("nid");
        if (nid != null) {
            try { new NotificationDBContext().markRead(Integer.parseInt(nid)); }
            catch (Exception ignore){}
        }
        resp.sendRedirect(req.getContextPath() + "/notify");
    }
}
