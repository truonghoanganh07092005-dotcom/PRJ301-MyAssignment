package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.RequestForLeave;
import model.iam.User;

@WebServlet(urlPatterns = "/request/my")
public class ListController extends BaseRequiredAuthorizationController {

    @Override
protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
        throws ServletException, IOException {
    String q = req.getParameter("q");
    RequestForLeaveDBContext db = new RequestForLeaveDBContext();
    List<RequestForLeave> list;

    if (q != null && !q.trim().isEmpty()) {
        list = db.searchOfEmployee(user.getEmployee().getId(), q, 200);
        req.setAttribute("searchQuery", q.trim());
    } else {
        // danh sách bình thường (ví dụ của bạn)
        list = db.recentOfEmployee(user.getEmployee().getId(), 200);
    }
    req.setAttribute("requests", list);
    req.getRequestDispatcher("/WEB-INF/request/list.jsp").forward(req, resp);
}

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        processGet(req, resp, user);
    }
}
