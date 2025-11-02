package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.NotificationDBContext;
import dal.RequestForLeaveDBContext;
import dal.RequestHistoryDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.RequestForLeave;
import model.iam.User;

@WebServlet("/request/create")
public class CreateController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        req.setAttribute("todayIso", java.time.LocalDate.now().toString());
        req.getRequestDispatcher("/WEB-INF/request/create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String title  = trim(req.getParameter("title"));
        String fromS  = trim(req.getParameter("from"));
        String toS    = trim(req.getParameter("to"));
        String reason = trim(req.getParameter("reason"));

        java.util.List<String> errors = new java.util.ArrayList<>();
        java.sql.Date from=null,to=null,today=java.sql.Date.valueOf(java.time.LocalDate.now());
        try { from = java.sql.Date.valueOf(fromS); } catch(Exception e){ errors.add("Ngày bắt đầu không hợp lệ."); }
        try { to   = java.sql.Date.valueOf(toS);   } catch(Exception e){ errors.add("Ngày kết thúc không hợp lệ."); }
        if (reason==null || reason.isBlank()) errors.add("Vui lòng nhập lý do.");
        if (from!=null && from.before(today)) errors.add("Từ ngày phải từ hôm nay trở đi ("+today+").");
        if (from!=null && to!=null && to.before(from)) errors.add("Đến ngày phải ≥ Từ ngày.");

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form_title", title);
            req.setAttribute("form_from", fromS);
            req.setAttribute("form_to", toS);
            req.setAttribute("form_reason", reason);
            req.setAttribute("todayIso", today.toString());
            req.getRequestDispatcher("/WEB-INF/request/create.jsp").forward(req, resp);
            return;
        }

        RequestForLeave r = new RequestForLeave();
        r.setCreated_by(user.getEmployee());
        r.setFrom(from); r.setTo(to);
        r.setReason(reason); r.setStatus(0);
        r.setTitle(title);
        int rid = new RequestForLeaveDBContext().insertReturningId(r);

        // history
        new RequestHistoryDBContext().add(
            rid, "CREATE", user.getId(),
            (user.getEmployee()==null)? null : user.getEmployee().getId(),
            null, 0, null
        );

        // notify manager
        Integer managerUid = new RequestForLeaveDBContext().managerUidOfOwnerByRid(rid);
        if (managerUid != null) {
            String url = req.getContextPath()+"/request/detail?rid="+rid;
            new NotificationDBContext().create(managerUid,
                "Có đơn nghỉ mới #"+rid+" cần xử lý.", url);
        }

        HttpSession s = req.getSession(false);
        if (s != null) s.setAttribute("flash", "Tạo đơn thành công (#" + rid + ").");
        resp.sendRedirect(req.getContextPath() + "/request/my?createdRid=" + rid);
    }

    private String trim(String s){ return s==null? null : s.trim(); }
}
