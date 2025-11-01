package controller.division;

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

@WebServlet(urlPatterns = "/agenda")
public class ViewAgendaController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        List<RequestForLeave> mine = Collections.emptyList();
        List<RequestForLeave> subs = Collections.emptyList();

        Integer eid = null;
        try { if (user != null && user.getEmployee() != null) eid = user.getEmployee().getId(); } catch (Exception ignore) {}

        if (eid != null) {
            mine = new RequestForLeaveDBContext().recentOfEmployee(eid, 50);
        }
        if (user != null) {
            subs = new RequestForLeaveDBContext().recentOfSubordinatesByUid(user.getId(), 50);
        }

        req.setAttribute("mine", mine);
        req.setAttribute("subs", subs);
        req.getRequestDispatcher("/WEB-INF/division/agenda.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        doGet(req, resp, user);
    }
}
