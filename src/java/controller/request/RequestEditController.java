package controller.request;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.RequestForLeave;
import model.iam.User;

@WebServlet(urlPatterns = "/request/edit")
public class RequestEditController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        int rid = parseInt(req.getParameter("rid"));
        if (rid <= 0) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }

        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        RequestForLeave r = db.get(rid);
        if (r == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

        // chỉ chủ đơn + còn In Progress mới được sửa
        if (r.getStatus() != 0 || r.getCreated_by() == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // đổ dữ liệu vào form (dùng lại create.jsp)
        req.setAttribute("edit", true);
        req.setAttribute("rid", String.valueOf(rid));
        req.setAttribute("form_title", r.getTitle());
        req.setAttribute("form_from", r.getFrom() == null ? "" : r.getFrom().toString());
        req.setAttribute("form_to", r.getTo() == null ? "" : r.getTo().toString());
        req.setAttribute("form_reason", r.getReason());

        req.getRequestDispatcher("/WEB-INF/request/create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        int rid = parseInt(req.getParameter("rid"));
        String title  = nvl(req.getParameter("title"));
        String fromS  = nvl(req.getParameter("from"));
        String toS    = nvl(req.getParameter("to"));
        String reason = nvl(req.getParameter("reason"));

        List<String> errors = new ArrayList<>();
        LocalDate from = null, to = null;

        try { from = LocalDate.parse(fromS); } catch (Exception e) { errors.add("Ngày bắt đầu không hợp lệ."); }
        try { to   = LocalDate.parse(toS);   } catch (Exception e) { errors.add("Ngày kết thúc không hợp lệ."); }

        LocalDate today = LocalDate.now();
        if (from != null && from.isBefore(today)) errors.add("Từ ngày phải ≥ hôm nay (" + today + ").");
        if (from != null && to != null && to.isBefore(from)) errors.add("Đến ngày phải ≥ Từ ngày.");
        if (reason.isBlank()) errors.add("Vui lòng nhập lý do.");

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("edit", true);
            req.setAttribute("rid", String.valueOf(rid));
            req.setAttribute("form_title",  title);
            req.setAttribute("form_from",   fromS);
            req.setAttribute("form_to",     toS);
            req.setAttribute("form_reason", reason);
            req.getRequestDispatcher("/WEB-INF/request/create.jsp").forward(req, resp);
            return;
        }

        boolean ok = new RequestForLeaveDBContext()
                .updateByOwnerIfInProgress(
                        rid,
                        user.getId(),
                        title,
                        java.sql.Date.valueOf(from),
                        java.sql.Date.valueOf(to),
                        reason);

        String ctx = req.getContextPath();
        if (ok) resp.sendRedirect(ctx + "/request/detail?rid=" + rid);
        else    resp.sendRedirect(ctx + "/request/my");
    }

    /* helpers */
    private static int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
    private static String nvl(String s) { return s == null ? "" : s.trim(); }
}
