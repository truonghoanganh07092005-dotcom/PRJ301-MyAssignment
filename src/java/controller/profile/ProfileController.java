package controller.profile;

import controller.iam.BaseRequiredAuthenticationController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import model.Employee;
import model.iam.Role;
import model.iam.User;

@WebServlet("/profile")
public class ProfileController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {

        // ===== Display name (ưu tiên displayname -> fullname -> name -> username)
        String displayName = firstNotBlank(
                safe(user.getDisplayName()),
                safe(user.getFullName()),
                safe(user.getName()),
                safe(user.getUsername())
        );

        // ===== Lấy Employee nếu có
        Employee emp = user.getEmployee();

        // ===== Build các field dưới dạng String (JSP chỉ in ra, không format thêm)
        String username   = safe(user.getUsername());
        String email      = firstNotBlank(safe(user.getEmail()),     fromEmpStr(emp, "getEmail"));
        String phone      = firstNotBlank(safe(user.getPhone()),     fromEmpStr(emp, "getPhone"));
        String empCode    = firstNotBlank(safe(user.getCode()),      fromEmpStr(emp, "getCode"));

        String position   = firstNotBlank(
                safe(user.getTitle()),             // nếu User có convenience getter
                fromEmpStr(emp, "getTitle"),
                fromEmpStr(emp, "getPosition")
        );

        String department = resolveDepartmentName(user, emp);

        // hire date -> String dd/MM/yyyy (nếu null thì "")
        String hireDate   = formatDate(resolveHireDate(user, emp));

        String manager    = resolveManagerName(emp);

        String roles = "";
        if (user.getRoles() != null) {
            roles = user.getRoles().stream()
                    .map(Role::getName)
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.joining(", "));
        }

        // ===== Gán attribute cho view (đều là String)
        req.setAttribute("displayName", displayName);
        req.setAttribute("username",    username);
        req.setAttribute("email",       email);
        req.setAttribute("phone",       phone);
        req.setAttribute("empCode",     empCode);
        req.setAttribute("department",  department);
        req.setAttribute("position",    position);
        req.setAttribute("hireDate",    hireDate);      // <-- String
        req.setAttribute("manager",     manager);
        req.setAttribute("roles",       roles);

        // để navbar dùng tên hiển thị
        req.setAttribute("_displayName", displayName);

        // forward đúng chỗ file JSP của bạn
        req.getRequestDispatcher("/view/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    /* ================= Helpers ================= */

    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private static String firstNotBlank(String... arr) {
        for (String s : arr) if (s != null && !s.trim().isEmpty()) return s.trim();
        return "";
    }

    private static String fromEmpStr(Employee emp, String method) {
        if (emp == null) return "";
        try {
            Method m = emp.getClass().getMethod(method);
            Object v = m.invoke(emp);
            return v == null ? "" : v.toString();
        } catch (Exception ignore) { return ""; }
    }

    private static Object fromEmpObj(Employee emp, String method) {
        if (emp == null) return null;
        try {
            Method m = emp.getClass().getMethod(method);
            return m.invoke(emp);
        } catch (Exception ignore) { return null; }
    }

    private static String resolveDepartmentName(User user, Employee emp) {
        // ưu tiên User.getDepartment().getName() nếu có
        try {
            Method m = user.getClass().getMethod("getDepartment");
            Object dept = m.invoke(user);
            String name = deptNameByReflection(dept);
            if (!name.isBlank()) return name;
        } catch (Exception ignore) {}

        // fallback: Employee.getDepartment()
        Object dept = fromEmpObj(emp, "getDepartment");
        return deptNameByReflection(dept);
    }

    private static String deptNameByReflection(Object dept) {
        if (dept == null) return "";
        for (String g : new String[]{"getName","getDname","getDepartmentName"}) {
            try {
                Method m = dept.getClass().getMethod(g);
                Object v = m.invoke(dept);
                if (v != null && !v.toString().isBlank()) return v.toString();
            } catch (Exception ignore) {}
        }
        return "";
    }

    private static Date resolveHireDate(User user, Employee emp) {
        // ưu tiên User.getHireDate() nếu có
        try {
            Method m = user.getClass().getMethod("getHireDate");
            Object v = m.invoke(user);
            if (v instanceof Date) return (Date) v;
        } catch (Exception ignore) {}

        // fallback: Employee.getHireDate()
        try {
            Method m = emp!=null ? emp.getClass().getMethod("getHireDate") : null;
            Object v = (m==null) ? null : m.invoke(emp);
            if (v instanceof Date) return (Date) v;
        } catch (Exception ignore) {}

        return null;
    }

    private static String formatDate(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(d);
    }

    private static String resolveManagerName(Employee emp) {
        if (emp == null) return "";
        try {
            Method m = emp.getClass().getMethod("getSupervisor");
            Object sup = m.invoke(emp);
            if (sup != null) {
                Method gm = sup.getClass().getMethod("getName");
                Object v = gm.invoke(sup);
                return v == null ? "" : v.toString();
            }
        } catch (Exception ignore) {}
        return "";
    }
}
