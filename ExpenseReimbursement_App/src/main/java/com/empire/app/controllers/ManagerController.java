package com.empire.app.controllers;

import com.empire.app.dao.EmployeeRepo;
import com.empire.app.models.Employee;
import com.empire.app.models.Response;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * <h2>ManagerController</h2>
 * <p>
 *     Controller class that implements several handler methods used to process get and post requests corresponding to
 *     manager activity in the application.
 * </p>
 * @see #handleEmployeeView(Context)
 * @see #handleRequestEdit(Context)
 * @see #handleRequestView(Context)
 * @see #handleRegister(Context)
 */
public class ManagerController {
    private static final Logger mcLog = LogManager.getLogger(ManagerController.class);

    /**
     * <h3>handleEmployeeView</h3>
     * <p>
     *     Retrieves list of {@link Employee Employee} objects via the {@link EmployeeRepo#getEmployees() getEmployees}
     *     method assigning the returned collection to a {@link Response Response} object body property. The <code>Response</code>
     *     is then sent as json to the get request with type='data' or type='error' if returned collection of <code>Employee</code>
     *     is null with complementary error message.
     * </p>
     * @param ctx
     */
    public static void handleEmployeeView (Context ctx) {
        mcLog.debug("Get Request for List of Employees Received");
        Response resp = new Response();
        EmployeeRepo er = new EmployeeRepo();
        try {
            resp.setBody(er.getEmployees());
            mcLog.debug("Employee List Generation Successful");
            resp.setType("data");
            ctx.status(201);
            ctx.json(resp);
        } catch (Exception e) {
            mcLog.debug("Employee List Generation Failed");
            resp.setType("error");
            resp.setMessage("Employee List Retrieval Failed");
            ctx.status(500);
            ctx.json(resp);
        }
    };
    /**
     * <h3>handleRequestEdit</h3>
     * <p>
     *     Passes in the from parameters 'id' and 'status' along with the current user's manager ID into the {@link EmployeeRepo#editRequest(int, String, int) editRequest}
     *     method. If successful, a {@link Response Response} object with type='success' or type='error' if the query fails,
     *     is sent to the post request as json.
     * </p>
     * @param ctx
     */
    public static void handleRequestEdit(Context ctx) {
        mcLog.debug("Received Post Request for Request Status Update");
        EmployeeRepo er = new EmployeeRepo();
        Response resp = new Response();
        try {
            Employee manager = ctx.sessionAttribute("currentUser");
            assert manager != null;
            er.editRequest(Integer.parseInt(Objects.requireNonNull(ctx.formParam("id"))), ctx.formParam("status"), manager.getId());
            mcLog.debug("Request Status Update Successful");
            resp.setType("success");
            resp.setMessage("Status Update Successful");
            ctx.status(201);
            ctx.json(resp);
        } catch (Exception e) {
            mcLog.debug("Request Status Update Failed");
            resp.setType("error");
            resp.setMessage("Status Update Failed");
            ctx.status(500);
            ctx.json(resp);
        }
        ctx.json(resp);
    };

    /**
     * <h3>handleRequestView</h3>
     * <p>
     *     Retrieves list of all requests via the {@link EmployeeRepo#getRequests(int,) getRequests} method and assigns
     *     the Collection to a {@link Response Response} object body with type='data' or type='error' if the query fails.
     *     The <code>Response</code> is sent to the get request as json.
     * </p>
     * @param ctx
     */
    public static void handleRequestView(Context ctx) {
        mcLog.debug("Get Request for Request List Received");
        Response resp = new Response();
        EmployeeRepo er = new EmployeeRepo();
        try {
            resp.setBody(er.getRequests(0));
            mcLog.debug("Request Retrieval Successful");
            resp.setType("data");
            ctx.status(201);
        } catch (Exception e) {
            mcLog.debug("Request Retrieval Failed");
            resp.setMessage("Request Retrieval Failed");
            resp.setType("data");
            ctx.status(500);
        }
        ctx.json(resp);
    };

    /**
     * <h3>handleRegister</h3>
     * <p>
     *     Creates new {@link Employee Employee} object and sets each of its properties to the relative form parameters
     *     received from the post request. The <code>Employee</code> object is then passed into the {@link EmployeeRepo#register(Employee) register}
     *     method and a new {@link Response Response} object is sent as json to the post request with type='success' or
     *     type='error' if the update query fails.
     * </p>
     * @param ctx
     */
    public static void handleRegister(Context ctx) {
        mcLog.debug("Post Request for Employee Registration Received");
        EmployeeRepo er = new EmployeeRepo();
        Employee e = new Employee();
        Response resp = new Response();
        try {
            e.setFirstName(ctx.formParam("firstName"));
            e.setLastName(ctx.formParam("lastName"));
            e.setEmail(ctx.formParam("email"));
            e.setPassword(ctx.formParam("password"));
            e.setAddress(ctx.formParam("address"));
            e.setRole(ctx.formParam("role"));
            er.register(e);
            mcLog.debug("Employee Registration Successful");
            ctx.status(201);
            resp.setType("success");
            resp.setMessage("Employee Registration Successful");
        } catch (Exception exception) {
            mcLog.debug("Employee Registration Failed");
            ctx.status(500);
            resp.setType("error");
            resp.setMessage("Employee Registration Failed");
        }
        ctx.json(resp);
    };
}
