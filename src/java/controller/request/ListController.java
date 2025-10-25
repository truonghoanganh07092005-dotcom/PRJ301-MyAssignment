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

        List<RequestForLeave> list = new RequestForLeaveDBContext().listByEmployee(user.getId());
        req.setAttribute("list", list);

        String flash = (String) req.getSession().getAttribute("flash");
        if (flash != null) {
            req.setAttribute("flash", flash);
            req.getSession().removeAttribute("flash");
        }

        req.setAttribute("createdRid", req.getParameter("created")); // highlight nếu muốn
        req.getRequestDispatcher("/WEB-INF/request/list.jsp").forward(req, resp);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        processGet(req, resp, user);
    }
}
