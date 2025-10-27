package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/my")
public class ListController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        Integer eid = null;
        try { if (user != null && user.getEmployee() != null) eid = user.getEmployee().getId(); } catch (Exception ignore){}

        List<RequestForLeave> list = Collections.emptyList();
        if (eid != null) {
            RequestForLeaveDBContext db = new RequestForLeaveDBContext();
            String q = req.getParameter("q");
            if (q != null && !q.trim().isEmpty()) {
                list = db.searchOfEmployee(eid, q.trim(), 200);
            } else {
                list = db.recentOfEmployee(eid, 200);
            }
        }

        req.setAttribute("list", list);
        req.getRequestDispatcher("/WEB-INF/request/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        doGet(req, resp, user);
    }
}
