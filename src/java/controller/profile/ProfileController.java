package controller.profile;

import controller.iam.BaseRequiredAuthenticationController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
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

        // Display name (ưu tiên displayname -> fullname -> name -> username)
        String displayName = firstNotBlank(
                safe(user.getDisplayname()),   // User của bạn là displayname (lowercase)
                safe(fromUserStr(user, "getFullName")),
                safe(fromUserStr(user, "getName")),
                safe(user.getUsername())
        );

        Employee emp = user.getEmployee();

        String username   = safe(user.getUsername());
        String email      = firstNotBlank(fromEmpStr(emp, "getEmail"));
        String phone      = firstNotBlank(fromEmpStr(emp, "getPhone"));
        String empCode    = firstNotBlank(fromEmpStr(emp, "getEmpCode"));
        String position   = firstNotBlank(fromEmpStr(emp, "getTitle"));
        String department = resolveDepartmentName(user, emp);
        String hireDate   = formatDate(resolveHireDate(user, emp));
        String manager    = resolveManagerName(emp);

        String roles = "";
        if (user.getRoles() != null) {
            roles = user.getRoles().stream()
                    .map(Role::getName)
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.joining(", "));
        }

        // push to view
        req.setAttribute("displayName", displayName);
        req.setAttribute("username",    username);
        req.setAttribute("email",       email);
        req.setAttribute("phone",       phone);
        req.setAttribute("empCode",     empCode);
        req.setAttribute("department",  department);
        req.setAttribute("position",    position);
        req.setAttribute("hireDate",    hireDate);
        req.setAttribute("manager",     manager);
        req.setAttribute("roles",       roles);

        // navbar
        req.setAttribute("_displayName", displayName);

        req.getRequestDispatcher("/view/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    // ===== helpers =====
    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String firstNotBlank(String... arr) {
        for (String s : arr) if (s != null && !s.trim().isEmpty()) return s.trim();
        return "";
    }
    private static String fromUserStr(User u, String method) {
        if (u == null) return "";
        try { var m = u.getClass().getMethod(method); var v = m.invoke(u); return v==null?"":v.toString(); }
        catch (Exception ignore) { return ""; }
    }
    private static String fromEmpStr(Employee e, String method) {
        if (e == null) return "";
        try { var m = e.getClass().getMethod(method); var v = m.invoke(e); return v==null?"":v.toString(); }
        catch (Exception ignore) { return ""; }
    }
    private static Object fromEmpObj(Employee e, String method) {
        if (e == null) return null;
        try { var m = e.getClass().getMethod(method); return m.invoke(e); }
        catch (Exception ignore) { return null; }
    }

    private static String resolveDepartmentName(User user, Employee emp) {
        // ưu tiên User.getDepartment()?.getName()
        try {
            var m = user.getClass().getMethod("getDepartment");
            Object dept = m.invoke(user);
            String name = deptNameByReflection(dept);
            if (!name.isBlank()) return name;
        } catch (Exception ignore) {}

        Object dept = null;
        try {
            var m = emp!=null ? emp.getClass().getMethod("getDepartment") : null;
            if (m != null) dept = m.invoke(emp);
        } catch (Exception ignore) {}
        if (dept == null && emp != null) {
            try {
                var m = emp.getClass().getMethod("getDept");
                dept = m.invoke(emp);
            } catch (Exception ignore) {}
        }
        return deptNameByReflection(dept);
    }

    private static String deptNameByReflection(Object dept) {
        if (dept == null) return "";
        for (String g : new String[]{"getName","getDname","getDepartmentName"}) {
            try { var m = dept.getClass().getMethod(g); var v = m.invoke(dept);
                  if (v != null && !v.toString().isBlank()) return v.toString();
            } catch (Exception ignore) {}
        }
        return "";
    }

    private static Date resolveHireDate(User user, Employee emp) {
        try { var m = user.getClass().getMethod("getHireDate"); Object v = m.invoke(user);
              if (v instanceof Date) return (Date) v; } catch (Exception ignore) {}
        try { var m = emp!=null ? emp.getClass().getMethod("getHireDate") : null; Object v = m==null?null:m.invoke(emp);
              if (v instanceof Date) return (Date) v; } catch (Exception ignore) {}
        return null;
    }

    private static String formatDate(Date d) {
        if (d == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(d);
    }

    private static String resolveManagerName(Employee emp) {
        if (emp == null) return "";
        try {
            var m = emp.getClass().getMethod("getSupervisor");
            Object sup = m.invoke(emp);
            if (sup != null) {
                var gm = sup.getClass().getMethod("getName");
                Object v = gm.invoke(sup);
                return v == null ? "" : v.toString();
            }
        } catch (Exception ignore) {}
        return "";
    }
}
