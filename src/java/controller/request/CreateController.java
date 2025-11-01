package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
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
        // để JSP set min="today"
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

        // parse ngày
        java.sql.Date from = null, to = null;
        try { from = java.sql.Date.valueOf(fromS); } catch (Exception e) { errors.add("Ngày bắt đầu không hợp lệ."); }
        try { to   = java.sql.Date.valueOf(toS);   } catch (Exception e) { errors.add("Ngày kết thúc không hợp lệ."); }

        // ---- Business rules ----
        if (isBlank(reason)) errors.add("Vui lòng nhập lý do.");

        // hôm nay (không kèm thời gian)
        java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());

        if (from == null) {
            errors.add("Vui lòng chọn Từ ngày.");
        } else {
            // KHÔNG cho xin nghỉ từ quá khứ
            if (from.before(today)) {
                errors.add("Từ ngày phải từ hôm nay trở đi (" + today + ").");
            }
        }

        if (to == null) {
            errors.add("Vui lòng chọn Đến ngày.");
        } else if (from != null && to.before(from)) {
            errors.add("Đến ngày phải ≥ Từ ngày.");
        }

        if (!errors.isEmpty()) {
            // trả form + lỗi
            req.setAttribute("errors", errors);
            req.setAttribute("form_title",  title);
            req.setAttribute("form_from",   fromS);
            req.setAttribute("form_to",     toS);
            req.setAttribute("form_reason", reason);
            req.setAttribute("todayIso", today.toString());
            req.getRequestDispatcher("/WEB-INF/request/create.jsp").forward(req, resp);
            return;
        }

        // insert
        RequestForLeave r = new RequestForLeave();
        r.setCreated_by(user.getEmployee()); // created_by = EID
        r.setFrom(from);
        r.setTo(to);
        r.setReason(reason);
        r.setStatus(0);
        r.setTitle(title);

        int rid = new RequestForLeaveDBContext().insertReturningId(r);

        // flash highlight ở /request/my
        HttpSession s = req.getSession(false);
        if (s != null) s.setAttribute("flash", "Tạo đơn thành công (#" + rid + ").");

        resp.sendRedirect(req.getContextPath() + "/request/my?createdRid=" + rid);
    }

    private String trim(String s){ return s==null? null : s.trim(); }
    private boolean isBlank(String s){ return s==null || s.trim().isEmpty(); }
}
