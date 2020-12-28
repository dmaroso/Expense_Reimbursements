package com.empire.app.controllers;

import com.empire.app.dao.EmployeeRepo;
import com.empire.app.models.Employee;
import com.empire.app.models.Request;
import com.empire.app.models.Response;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * <h2>EmployeeController</h2>
 * <p>
 *     Controller class which implements several Handler methods to process get and post requests related to employee
 *     actions.
 * </p>
 * @see #handleHome(Context)
 * @see #handleInfoEdit(Context)
 * @see #handleRequestView(Context)
 * @see #handleSubmit(Context)
 */
public class EmployeeController {
    private static final Logger ecLog = LogManager.getLogger(EmployeeController.class);

    /**
     * <h3>handleHome</h3>
     * <p>
     *     Sends {@link Response Response} object as json from get request setting the Response body to the <code>Employee</code>
     *     object assigned to the context session attribute 'currentUser' assigned at login
     * </p>
     * @param ctx
     */
    public static void handleHome(Context ctx) {
        ecLog.debug("Successfully loaded Employee information");
        Response resp = new Response();
        resp.setBody(ctx.sessionAttribute("currentUser"));
        ctx.json(resp);
    };

    /**
     * <h3>handleSubmit</h3>
     * <p>
     *     Creates new {@link Request Request} object setting its properties based on the current user and form parameters
     *     'amount' and 'description'. The <code>Request</code> object is then passed into the {@link EmployeeRepo#submitRequest(Request) submitRequest}
     *     method, returning a json message and type='success' to the request or type='error' if the query fails.
     * </p>
     * @param ctx
     */
    public static void handleSubmit(Context ctx) {
        ecLog.debug("Received Submission Post Request");
        Request req = new Request();
        EmployeeRepo er = new EmployeeRepo();
        Response resp = new Response();
        try {
            Employee e = ctx.sessionAttribute("currentUser");
            assert e != null;
            req.setEmployeeId(e.getId());
            req.setAmount(Double.parseDouble(Objects.requireNonNull(ctx.formParam("amount"))));
            req.setDescription(ctx.formParam("description"));
            req.setStatus("PENDING");
            er.submitRequest(req);
            ecLog.debug("Submission Successful");
            ctx.status(201);
            resp.setMessage("Submission Successful");
            resp.setType("success");
            ctx.json(resp);
        } catch (Exception e) {
            ecLog.debug("Submission Failed");
            resp.setType("error");
            resp.setMessage("Request Submission Failed");
            ctx.status(500);
            ctx.json(resp);
        }
    };

    /**
     * <h3>handleRequestView</h3>
     * <p>
     *     Generates list of requests for current user based on their ID from the session attribute 'currentUser' which
     *     is passed into the {@link EmployeeRepo#getRequests(int) getRequests} method. The returned list is sent to the
     *     get request as a json {@link Response Response} object assigned to the 'body' parameter with a type='data'.
     *     If null, the <code>Response</code> has type='error' with complimentary message.
     * </p>
     * @param ctx
     */
    public static void handleRequestView(Context ctx) {
        ecLog.debug("Get Request for Request View Received");
        Response resp = new Response();
        EmployeeRepo er = new EmployeeRepo();
        try {
            Employee e = ctx.sessionAttribute("currentUser");
            assert e != null;
            resp.setBody(er.getRequests(e.getId()));
            if (resp.getBody() != null) {
                ecLog.debug("Request Query Successful, Response Populated");
                resp.setType("data");
                ctx.status(201);
                ctx.json(resp);
            } else {
                ecLog.debug("Request Query Failed, Response Empty");
                resp.setType("error");
                resp.setMessage("No Current Requests Found");
                ctx.status(400);
            }
            ctx.json(resp);
        } catch (Exception e) {
            ecLog.debug("Request View Failed");
            resp.setType("error");
            resp.setMessage("Failed to Fetch Requests");
            ctx.status(500);
            ctx.json(resp);
        }
    };

    /**
     * <h3>handleInfoEdit</h3>
     * <p>
     *     Takes in the form parameters 'info' and 'data' which are passed into the {@link EmployeeRepo#editInfo(Employee, String, String) editInfo}
     *     method along with the employee object assigned to the session attribute 'currentUser'. The session attribute
     *     is reassigned to the returned <code>Employee</code> object. A {@link Response Response} is sent to the post request with type
     *     success with message or type error if query failed or invalid input.
     * </p>
     * @param ctx
     */
    public static void handleInfoEdit(Context ctx) {
        ecLog.debug("Post Request for Information Update Received");
        Employee e = ctx.sessionAttribute("currentUser");
      EmployeeRepo er = new EmployeeRepo();
      Response resp = new Response();
      try {
          e = er.editInfo(e, Objects.requireNonNull(ctx.formParam("info")), ctx.formParam("data"));
          ecLog.debug("Information Update Successful");
          ctx.sessionAttribute("currentUser", e);
          ctx.status(201);
          resp.setMessage("Profile Information Successfully Updated");
          resp.setType("success");
          ctx.json(resp);
      } catch (Exception exception) {
          ecLog.debug("Information Update Failed");
          ctx.status(500);
          resp.setMessage("Information Update Failed");
          resp.setType("error");
          ctx.json(resp);
      }
    };
}
