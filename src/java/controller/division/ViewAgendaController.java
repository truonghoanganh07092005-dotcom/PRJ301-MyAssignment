package controller.division;

import controller.iam.BaseRequiredAuthorizationController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.iam.User;

@WebServlet(urlPatterns = {"/division/agenda", "/agenda"})   // thêm "/agenda"
public class ViewAgendaController extends BaseRequiredAuthorizationController {

    @Override protected void processPost(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException { }

    @Override protected void processGet(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        resp.setContentType("text/plain; charset=UTF-8");
        resp.getWriter().println("Agenda page (stub)");
    }
}
