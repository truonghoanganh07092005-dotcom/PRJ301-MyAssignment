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
        if (to.isBefore(from)) {
            // đổi chỗ nếu user nhập ngược
            LocalDate tmp = from; from = to; to = tmp;
        }

        // 2) Cột ngày (List<LocalDate>)
        List<LocalDate> days = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) days.add(d);

        // 3) Hàng nhân sự (mọi cấp dưới – kể cả chưa có đơn nghỉ)
        RequestForLeaveDBContext db = new RequestForLeaveDBContext();
        List<RequestForLeaveDBContext.SimpleEmp> subs =
                db.subordinatesOfManaged(user.getId()); // theo UID manager

        // map EID -> mảng cells (0=đi làm, 1=nghỉ)
        Map<Integer, int[]> eidToCells = new LinkedHashMap<>();
        for (RequestForLeaveDBContext.SimpleEmp s : subs) {
            eidToCells.put(s.eid, new int[days.size()]); // default 0 (work)
        }

        // 4) Lấy các khoảng nghỉ đã duyệt và gán 1 vào cells
        List<RequestForLeaveDBContext.AgendaItem> items =
                new RequestForLeaveDBContext().agendaOfSubordinates(
                        user.getId(),
                        java.sql.Date.valueOf(from),
                        java.sql.Date.valueOf(to)
                );

        for (RequestForLeaveDBContext.AgendaItem it : items) {
            int[] cells = eidToCells.get(it.eid);
            if (cells == null) continue; // không phải cấp dưới trực tiếp

            LocalDate lf = it.from.toLocalDate();
            LocalDate lt = it.to.toLocalDate();
            for (int i = 0; i < days.size(); i++) {
                LocalDate d = days.get(i);
                if (!d.isBefore(lf) && !d.isAfter(lt)) {
                    cells[i] = 1; // nghỉ
                }
            }
        }

     // 5) Build rows cho JSP
List<Map<String,Object>> rows = new ArrayList<>();
for (RequestForLeaveDBContext.SimpleEmp s : subs) {
    Map<String,Object> row = new HashMap<>();
    row.put("name", s.ename); // <-- đổi từ s.sname thành s.ename
    row.put("cells", toList(eidToCells.get(s.eid)));
    rows.add(row);
}
        // 6) Push attribute và forward JSP
        req.setAttribute("fromIso", from.toString());
        req.setAttribute("toIso",   to.toString());
        req.setAttribute("cols",    days);   // List<LocalDate>
        req.setAttribute("rows",    rows);   // List<Map<name,cells>>
        req.getRequestDispatcher("/WEB-INF/division/agenda.jsp").forward(req, resp);
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
