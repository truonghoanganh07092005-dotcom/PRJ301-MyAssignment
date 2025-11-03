package controller.division;

import controller.iam.BaseRequiredAuthenticationController;
import dal.RequestForLeaveDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import model.iam.User;

@WebServlet("/agenda")
public class ViewAgendaController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        // 1) Khoảng ngày
        LocalDate today = LocalDate.now();
        LocalDate from = parseOr(req.getParameter("from"), today.minusDays(20));
        LocalDate to   = parseOr(req.getParameter("to"),   today.plusDays(10));
        if (to.isBefore(from)) { LocalDate tmp = from; from = to; to = tmp; }

        // 2) Cột ngày
        List<LocalDate> days = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) days.add(d);

        // 3) Quyền: chỉ mra mới được xem tất cả
        boolean canViewAll = isSuperViewer(user);                 // <- chỉ TRUE với mra
        boolean viewAll = canViewAll && "1".equals(req.getParameter("all"));  // <- chặn ép URL

        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        List<RequestForLeaveDBContext.SimpleEmp> people =
                viewAll ? db.listAllEmployees()
                        : db.subordinatesOfManaged(user.getId()); // theo UID manager

        // 4) Map EID -> dãy cells (0=đi làm, 1=nghỉ)
        Map<Integer, int[]> eidToCells = new LinkedHashMap<>();
        for (RequestForLeaveDBContext.SimpleEmp s : people) {
            eidToCells.put(s.eid, new int[days.size()]);
        }

        // 5) Lấy các khoảng NGHỈ đã duyệt
        List<RequestForLeaveDBContext.AgendaItem> items =
                viewAll ? new RequestForLeaveDBContext().agendaOfAll(
                            java.sql.Date.valueOf(from),
                            java.sql.Date.valueOf(to))
                        : new RequestForLeaveDBContext().agendaOfSubordinates(
                            user.getId(),
                            java.sql.Date.valueOf(from),
                            java.sql.Date.valueOf(to));

        for (RequestForLeaveDBContext.AgendaItem it : items) {
            int[] cells = eidToCells.get(it.eid);
            if (cells == null) continue;
            LocalDate lf = it.from.toLocalDate();
            LocalDate lt = it.to.toLocalDate();
            for (int i = 0; i < days.size(); i++) {
                LocalDate d = days.get(i);
                if (!d.isBefore(lf) && !d.isAfter(lt)) cells[i] = 1;
            }
        }

        // 6) Build rows
        List<Map<String,Object>> rows = new ArrayList<>();
        for (RequestForLeaveDBContext.SimpleEmp s : people) {
            Map<String,Object> row = new HashMap<>();
            row.put("name", s.ename);
            row.put("cells", toList(eidToCells.get(s.eid)));
            rows.add(row);
        }

        // 7) Gửi xuống view
        req.setAttribute("fromIso", from.toString());
        req.setAttribute("toIso",   to.toString());
        req.setAttribute("cols",    days);
        req.setAttribute("rows",    rows);
        req.setAttribute("viewAll", viewAll);
        req.setAttribute("canViewAll", canViewAll);   // <- để JSP ẩn/hiện checkbox

        req.getRequestDispatcher("/WEB-INF/division/agenda.jsp").forward(req, resp);
    }

    /** Chỉ định nghĩa “super viewer” là user có username = mra (không phân biệt hoa thường).
     *  Nếu hệ thống của bạn dùng trường khác, đổi ở đây cho phù hợp. */
    private static boolean isSuperViewer(User u) {
        try {
            if (u == null) return false;
            // Ưu tiên getUsername(); fallback sang getDisplayName() nếu cần
            String un = null;
            try { un = u.getUsername(); } catch (Throwable ignore) {}
            if (un == null || un.isBlank()) {
                try { un = u.getDisplayName(); } catch (Throwable ignore) {}
            }
            return un != null && un.equalsIgnoreCase("mra");
        } catch (Throwable t) {
            return false;
        }
    }

    private static LocalDate parseOr(String s, LocalDate def) {
        try { return (s == null || s.isBlank()) ? def : LocalDate.parse(s); }
        catch (Exception e) { return def; }
    }

    private static List<Integer> toList(int[] arr) {
        List<Integer> l = new ArrayList<>(arr.length);
        for (int v : arr) l.add(v);
        return l;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/agenda");
    }
}
