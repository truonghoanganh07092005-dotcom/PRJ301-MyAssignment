package controller.profile;

import controller.iam.BaseRequiredAuthenticationController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.Employee;
import model.iam.User;

@WebServlet("/profile")
public class ProfileController extends BaseRequiredAuthenticationController {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        Employee e = user.getEmployee();

        req.setAttribute("displayName", user.getDisplayName());
        req.setAttribute("username", user.getUsername());
        req.setAttribute("email",   e!=null ? e.getEmail() : null);
        req.setAttribute("phone",   e!=null ? e.getPhone() : null);
        req.setAttribute("empCode", e!=null ? e.getEmpCode() : null);
        req.setAttribute("department", (e!=null && e.getDept()!=null) ? e.getDept().getName() : null);
        req.setAttribute("position", e!=null ? e.getTitle() : null);
        req.setAttribute("hireDate", e!=null ? (e.getHireDate()==null? null : e.getHireDate().toString()) : null);
        req.setAttribute("manager",  (e!=null && e.getSupervisor()!=null) ? e.getSupervisor().getName() : null);

        String roles = null;
        if (user.getRoles()!=null && !user.getRoles().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (var r : user.getRoles()) {
                if (sb.length()>0) sb.append(", ");
                sb.append(r.getName());
            }
            roles = sb.toString();
        }
        req.setAttribute("roles", roles);

        req.getRequestDispatcher("/view/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { doGet(req, resp, user); }
}
