package tests;

import com.empire.app.controllers.EmployeeController;
import com.empire.app.controllers.LoginController;
import com.empire.app.controllers.ManagerController;
import com.empire.app.dao.EmployeeRepo;
import com.empire.app.models.Employee;
import com.empire.app.models.Request;
import com.empire.app.models.Response;
import com.empire.app.util.SessionUtil;
import io.javalin.http.Context;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.*;
import org.junit.runners.MethodSorters;
import static org.mockito.Mockito.*;


import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UnitTests {
    private EmployeeRepo er = new EmployeeRepo();
    public static Employee employee = new Employee();
    public static Request request = new Request();
    public static int reqId;
    public static int empId;
    private Context ctx = mock(Context.class);

    public static int getReqId() {
        return reqId;
    }

    public static void setReqId(int reqId) {
        UnitTests.reqId = reqId;
    }

    public static int getEmpId() {
        return empId;
    }

    public static void setEmpId(int empId) {
        UnitTests.empId = empId;
    }

// DAO Method Unit Tests
    @Test
    public void a_canConnect() {
        SessionUtil su = new SessionUtil();
        SessionFactory sf = su.configSession();
        Session s = sf.openSession();
        s.close();
        sf.close();
    }
    @Test
    public void b_canRegister() {
        employee.setFirstName("test");
        employee.setLastName("test");
        employee.setEmail("test");
        employee.setPassword("test");
        employee.setAddress("test");
        employee.setRole("EMPLOYEE");
        er.register(employee);
    }
    @Test
    public void c_canAuthenticate() {
        Employee e = er.authenticate("test","test");
        setEmpId(e.getId());
        employee.setId(e.getId());
        Assert.assertEquals("Authentication Failed", employee.toString(), e.toString());
    }
    @Test
    public void d_canEditInfo() {
        Employee e = er.editInfo(employee, "address", "editTest");
        employee.setAddress("editTest");
        Assert.assertEquals("Information Edit Failed", employee.toString(), e.toString());
    }
    @Test
    public void e_canGetEmployees() {
        List<Employee> employees = er.getEmployees();
        Assert.assertNotNull(employees);
    }
    @Test
    public void f_canSubmitRequest() {
        request.setEmployeeId(getEmpId());
        request.setAmount(0);
        request.setDescription("test");
        request.setStatus("test");
        request.setManagerId(0);
        er.submitRequest(request);
    }
    @Test
    public void g_canViewRequest() {
        List<Request> reqs = er.getRequests(getEmpId());
        Request req = reqs.get(0);
        Assert.assertEquals("Request Retrieval Failed", req.toString(), request.toString());
        setReqId(req.getId());
    }
    @Test
    public void h_canEditRequest() {
        er.editRequest(getReqId(),"testEdit",0);
        request.setStatus("testEdit");
    }
// Handler Unit Tests
    @Test
    public void i_loginPostEmployee() {
        when(ctx.formParam("email")).thenReturn("test");
        when(ctx.formParam("pass")).thenReturn("test");
        LoginController.handleLogin(ctx);
        verify(ctx).status(201);
}
    @Test
    public void j_logoutGet() {
        LoginController.handleLogout(ctx);
        verify(ctx).sessionAttribute("currentUser", null);
        verify(ctx).redirect("/ERS");
    }
    @Test
    public void k_requestViewPost() {
        ManagerController.handleRequestView(ctx);
        verify(ctx).status(201);
    }
    @Test
    public void l_employeeViewGet() {
        ManagerController.handleEmployeeView(ctx);
        verify(ctx).status(201);
    }
    @Test
    public void m_requestViewEmployeeGet () {
        when(ctx.sessionAttribute("currentUser")).thenReturn(employee);
        EmployeeController.handleRequestView(ctx);
        verify(ctx).status(201);
    }
    @Test
    public void n_infoEditPost() {
        when(ctx.formParam("info")).thenReturn("address");
        when(ctx.formParam("data")).thenReturn("test");
        when(ctx.sessionAttribute("currentUser")).thenReturn(employee);
        EmployeeController.handleInfoEdit(ctx);
        verify(ctx).status(201);
    }
    @Test
    public void o_requestEditPost() {
        when(ctx.sessionAttribute("currentUser")).thenReturn(employee);
        when(ctx.formParam("id")).thenReturn(Integer.toString(getReqId()));
        when(ctx.formParam("status")).thenReturn("test");
        ManagerController.handleRequestEdit(ctx);
        verify(ctx).status(201);
    }

// After class cleanup of test cases
    @AfterClass
    public static void deleteTestEntries() {
        SessionUtil su = new SessionUtil();
        SessionFactory sf = su.configSession();
        Session s = sf.openSession();
        String hql1 = "delete from Employee e where e.id = :id";
        String hql2 = "delete from Request r where r.id = :id";
        Query query1 = s.createQuery(hql1);
        Query query2 = s.createQuery(hql2);
        query1.setInteger("id", getEmpId());
        query2.setInteger("id", getReqId());
        Transaction tx = s.beginTransaction();
        query2.executeUpdate();
        tx.commit();
        Transaction tx2 = s.beginTransaction();
        query1.executeUpdate();
        tx2.commit();
        s.close();
        sf.close();
    };

}
