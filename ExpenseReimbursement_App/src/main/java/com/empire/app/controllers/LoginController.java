package com.empire.app.controllers;

import com.empire.app.dao.EmployeeRepo;
import com.empire.app.models.Employee;
import com.empire.app.models.Response;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <h2>LoginController</h2>
 * <p>
 *     A Controller class with Handler implementation to process get and post requests related to the application's
 *     Login and Logout functions.
 * </p>
 * @see #handleLogin
 * @see #handleLogout
 */
public class LoginController {
    private static final Logger lcLog = LogManager.getLogger(LoginController.class);
    /**
     * <h3>forceBeforeLogin</h3>
     * <p>
     *     Context handler which checks for session attribute 'currentUser' and if null, redirects to login screen
     * </p>
     */
    public static Handler forceBeforeLogin = ctx -> {
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.redirect("/ERS");
        }
    };
    /**
     * <h3>handleLogin</h3>
     * <p>
     *     Login Handler which passes in the form parameters 'email' and 'pass' into the {@link EmployeeRepo#authenticate(String, String) authenticate}
     *     method. The returned {@link Employee Employee} object is used to check the 'role' property, sending a {@link Response Response}
     *     object where the type is set to 'employee' or 'manager' and the body is set to the <code>Employee</code> object.
     *     The 'currentUser' session attribute of the context is set to the <code>Employee</code> object. If null the
     *     returned <code>Response</code> has type='error' with an accompanied error message and status code.
     * </p>
     * @param ctx
     */
    public static void handleLogin(Context ctx) {
        lcLog.debug("Login Post Request Received");
        Response resp = new Response();
        EmployeeRepo er = new EmployeeRepo();
        Employee user = er.authenticate(ctx.formParam("email"),ctx.formParam("pass"));
        if(user != null) {
            user.setPassword(null);
            ctx.sessionAttribute("currentUser", user);
            resp.setMessage("Login Success");
            ctx.status(201);
            if (user.getRole().equalsIgnoreCase("manager")) {
                lcLog.debug("Login Successful for Manager: " + user.getId());
                resp.setType("manager");
                ctx.json(resp);
            } else {
                lcLog.debug("Login Successful for Employee: " + user.getId());
                resp.setType("employee");
                ctx.json(resp);
            }
       } else {
            lcLog.warn("Invalid Email and Password Detected");
            resp.setMessage("Invalid Email and/or Password");
            resp.setType("error");
            ctx.status(400);
            ctx.json(resp);
        }
    };

    /**
     * <h3>handleLogout</h3>
     * <p>
     *     sets session attribute 'currentUser' to null upon get request and redirects to login screen
     * </p>
     * @param ctx
     */
    public static void handleLogout(Context ctx){
      lcLog.debug("Received Logout Request");
      ctx.sessionAttribute("currentUser",null);
      ctx.redirect("/ERS");
    };

}
