package controller.notify;

import controller.iam.BaseRequiredAuthenticationController;
import dal.NotificationDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.Notification;
import model.iam.User;

@WebServlet("/notify")
public class NotifyController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        NotificationDBContext db = new NotificationDBContext();

        if ("1".equals(req.getParameter("readAll"))) {
            db.markAllRead(user.getId());
            resp.sendRedirect(req.getContextPath()+"/notify");
            return;
        }

        List<Notification> list = db.listByUid(user.getId(), 50);
        int unread = new NotificationDBContext().countUnread(user.getId());

        req.setAttribute("list", list);
        req.setAttribute("unread", unread);
        req.getRequestDispatcher("/WEB-INF/notify/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
