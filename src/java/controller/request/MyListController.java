package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/my")
public class MyListController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        // Bắt buộc phải có EID để lấy "đơn của tôi"
        Integer eid = (user.getEmployee() != null) ? user.getEmployee().getId() : null;
        if (eid == null) {
            resp.sendRedirect(req.getContextPath() + "/login?timeout=1");
            return;
        }

        String q = req.getParameter("q");
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        List<RequestForLeave> list;

        // Chỉ search theo TITLE
        if (q != null && !q.trim().isEmpty()) {
            list = db.listMineByTitle(eid, q.trim(), 50);   // <-- đúng chữ ký: (Integer, String, int)
        } else {
            list = db.recentOfEmployee(eid, 50);
        }

        // flash từ session
        HttpSession s = req.getSession(false);
        if (s != null) {
            Object f = s.getAttribute("flash");
            if (f != null) {
                req.setAttribute("flash", f.toString());
                s.removeAttribute("flash");
            }
        }

        req.setAttribute("list", list);
        req.setAttribute("createdRid", req.getParameter("createdRid"));
        req.getRequestDispatcher("/WEB-INF/request/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        doGet(req, resp, user);
    }
}
