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
        NotificationDBContext db = new NotificationDBContext();
        ArrayList<Notification> unread = db.unread(user.getId());
        ArrayList<Notification> recent = new NotificationDBContext().recent(user.getId(), 20);
        req.setAttribute("unread", unread);
        req.setAttribute("recent", recent);
        req.getRequestDispatcher("/WEB-INF/notify.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        if ("all".equals(req.getParameter("mark"))) {
            new NotificationDBContext().readAll(user.getId());
        }
        resp.sendRedirect(req.getContextPath() + "/notify");
    }
}
