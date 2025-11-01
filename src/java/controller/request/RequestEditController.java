package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/edit")
public class RequestEditController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));
            RequestForLeave r = new RequestForLeaveDBContext().get(rid);
            if (r == null) { resp.sendError(404); return; }

            req.setAttribute("edit", true);
            req.setAttribute("rid", String.valueOf(r.getRid()));
            req.setAttribute("form_title",  r.getTitle());
            req.setAttribute("form_from",   r.getFrom()==null? "" : r.getFrom().toString());
            req.setAttribute("form_to",     r.getTo()==null? ""   : r.getTo().toString());
            req.setAttribute("form_reason", r.getReason());
            req.getRequestDispatcher("/WEB-INF/request/edit.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/request/my");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String ctx = req.getContextPath();
        HttpSession s = req.getSession();
        try {
            int rid = Integer.parseInt(req.getParameter("rid"));
            String title  = req.getParameter("title");
            Date from     = Date.valueOf(req.getParameter("from"));
            Date to       = Date.valueOf(req.getParameter("to"));
            String reason = req.getParameter("reason");

            boolean ok = new RequestForLeaveDBContext()
                    .updateByOwnerIfInProgress(rid, user.getId(), title, from, to, reason);

            s.setAttribute("flash", ok ? "Đã lưu thay đổi đơn." : "Không thể sửa (chỉ khi In-Progress).");
        } catch (Exception ex) {
            s.setAttribute("flash", "Dữ liệu không hợp lệ.");
        }
        resp.sendRedirect(ctx + "/request/my");
    }
}
