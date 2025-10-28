package controller.request;

import controller.iam.BaseRequiredAuthorizationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import model.Employee;
import model.RequestForLeave;
import model.iam.User;

@WebServlet(urlPatterns = "/request/create")
public class CreateController extends BaseRequiredAuthorizationController {

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/request/create.jsp").forward(req, resp);
    }

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String type   = req.getParameter("type");     // NEW
        String title  = req.getParameter("title");
        String fromS  = req.getParameter("from");
        String toS    = req.getParameter("to");
        String reason = req.getParameter("reason");

        List<String> errors = new ArrayList<>();
        LocalDate from = null, to = null;

        try { from = LocalDate.parse(fromS); } catch (Exception e) { errors.add("Ngày bắt đầu không hợp lệ."); }
        try { to   = LocalDate.parse(toS);   } catch (Exception e) { errors.add("Ngày kết thúc không hợp lệ."); }

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        if (from != null && from.isBefore(today)) {
            errors.add("Ngày bắt đầu phải ≥ hôm nay (" + today + ").");
        }
        if (from != null && to != null && to.isBefore(from)) {
            errors.add("Ngày kết thúc phải ≥ ngày bắt đầu.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            errors.add("Vui lòng nhập lý do.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("form_type",   type);
            req.setAttribute("form_title",  title);
            req.setAttribute("form_from",   fromS);
            req.setAttribute("form_to",     toS);
            req.setAttribute("form_reason", reason);
            req.getRequestDispatcher("/WEB-INF/request/create.jsp").forward(req, resp);
            return;
        }

        // Build model
        RequestForLeave r = new RequestForLeave();
        // created_by là EID (đúng theo DBContext bạn đang dùng)
        Employee creator = new Employee(); 
        creator.setId(user.getEmployee().getId());
        r.setCreated_by(creator);
        r.setFrom(java.sql.Date.valueOf(from));
        r.setTo(java.sql.Date.valueOf(to));
        r.setReason(reason);
        r.setStatus(0); // In Progress

        // Nếu không nhập title → mặc định theo loại đơn + khoảng ngày
        if (title != null && !title.trim().isEmpty()) {
            r.setTitle(title.trim());
        } else {
            String t = (type == null || type.isBlank()) ? "Đơn xin nghỉ" : type.trim();
            r.setTitle(t);
        }

        int rid = new RequestForLeaveDBContext().insertReturningId(r);
        req.getSession().setAttribute("flash", "Đã tạo đơn thành công!");

        // Nếu không có quyền xem /request/my → quay về /home
        @SuppressWarnings("unchecked")
        Set<String> perms = (Set<String>) req.getSession().getAttribute("perms");
        boolean canViewMy = perms != null && perms.contains("/request/my");

        String ctx = req.getContextPath();
        String next = canViewMy ? (ctx + "/request/my?created=" + rid)
                                : (ctx + "/home");
        resp.sendRedirect(next);
    }
}
