/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.iam;

import dal.RoleDBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.iam.Feature;
import model.iam.Role;
import model.iam.User;

/**
 *
 * @author sonnt
 */
public abstract class BaseRequiredAuthorizationController extends BaseRequiredAuthenticationController {

    private boolean isAuthorized(HttpServletRequest req, User user) {
        if (user.getRoles().isEmpty())//check if not yet fetch roles from db to user
        {
            RoleDBContext db = new RoleDBContext();
            user.setRoles(db.getByUserId(user.getId()));
            req.getSession().setAttribute("auth", user);
        }
        String url = req.getServletPath();
        for (Role role : user.getRoles()) {
            for (Feature feature : role.getFeatures()) {
                if(feature.getUrl().equals(url))
                    return true;
            }
        }
        return false;
    }

    protected abstract void processPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException;
    protected abstract void processGet(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException ;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        if(isAuthorized(req, user))
            processPost(req, resp, user);
        else
            resp.getWriter().println("access denied!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp, User user) throws ServletException, IOException {
        if(isAuthorized(req, user))
            processGet(req, resp, user);
        else
            resp.getWriter().println("access denied!");
    }

}
