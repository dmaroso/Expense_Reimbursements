package com.empire.app.dao;

import com.empire.app.models.Employee;
import com.empire.app.models.Request;
import com.empire.app.util.SessionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class EmployeeRepo implements Repository {

    protected SessionFactory sf;

    private Session createSession() {
        SessionUtil su =  new SessionUtil();
        this.sf = su.configSession();
        return this.sf.openSession();
    }

    /**
     * <h2>authenticate</h2>
     * <p>
     *     queries from Employees table for record matching the passed in 'email' and 'password' and returns said
     *     record as an <code>Employee</code> object or null if no match.
     * </p>
     * @param email Login credential entered by user
     * @param pass Login credential entered by user
     * @return <code>Employee</code> object
     */
    @Override
    public Employee authenticate(String email, String pass) {
        try {
            Session s = createSession();
            String hql = "from Employee where email = :email and password = :pass";
            Query query = s.createQuery(hql);
            query.setString("email", email);
            query.setString("pass", pass);
            Employee user = (Employee) query.list().get(0);
            s.close();
            sf.close();
            return user;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * <h2>editInfo</h2>
     * <p>
     *     executes query update changing the specified information entry to the specified data value of the passed in
     *     {@link Employee Employee} object. The method execution is determined by a switch statement and returns the
     *     updated <code>Employee</code> object.
     * </p>
     * @param e current employee
     * @param entryLabel type of information to update
     * @param data updated value
     * @return updated Employee object
     */
    @Override
    public Employee editInfo(Employee e, String entryLabel, String data) {
        Session s = createSession();
        String hql = null;
        Query query = null;
        switch (entryLabel) {
            case "email":
                e.setEmail(data);
                hql = "update Employee e set e.email= :email where e.id = :id";
                query = s.createQuery(hql);
                query.setString("email", data);
                break;
            case "password":
                e.setPassword(data);
                hql = "update Employee e set e.password= :pass where e.id = :id";
                query = s.createQuery(hql);
                query.setString("pass", data);
                break;
            case "address":
                e.setAddress(data);
                hql = "update Employee e set e.address= :address where e.id = :id";
                query = s.createQuery(hql);
                query.setString("address", data);
                break;
        }
        query.setInteger("id", e.getId());
        Transaction tx = s.beginTransaction();
        query.executeUpdate();
        tx.commit();
        s.close();
        sf.close();
        return e;
    }

    /**
     * <h3>editRequest</h3>
     * <p>
     *     executes update query for specified request ID to the specified status change and records the current active
     *     manager's ID who made the update.
     * </p>
     * @param reqId target request ID
     * @param status status update
     * @param managerId current active manager ID
     */
    @Override
    public void editRequest(int reqId, String status, int managerId) {
        Session s = createSession();
        String hql = "update Request r set r.status= :status, r.managerId= :mid where r.id = :id";
        Query query = s.createQuery(hql);
        query.setString("status", status);
        query.setInteger("id", reqId);
        query.setInteger("mid", managerId);
        Transaction tx = s.beginTransaction();
        query.executeUpdate();
        tx.commit();
        s.close();
        sf.close();
    }

    @Override
    public void submitRequest(Request request) {
        Session s = createSession();
        Transaction tx = s.beginTransaction();
        s.save(request);
        tx.commit();
        s.close();
        sf.close();
    }

    @Override
    public List<Request> getRequests(int employeeID) {
        Session s = createSession();
        Query query;
        if (employeeID == 0) {
            String hql = "from Request";
            query = s.createQuery(hql);
        } else {
            String hql = "from Request r where r.employeeId = :id";
            query = s.createQuery(hql);
            query.setInteger("id", employeeID);
        }
        List req = query.list();
        s.close();
        sf.close();
        if (req.isEmpty()) {
            return null;
        } else {
            return req;
        }
    }

    @Override
    public List<Employee> getEmployees() {
        Session s = createSession();
        String hql = "from Employee";
        Query query = s.createQuery(hql);
        List user = query.list();
        s.close();
        sf.close();
        return user;
    }

    @Override
    public void register(Employee employee) {
        Session s = createSession();
        Transaction tx = s.beginTransaction();
        s.save(employee);
        tx.commit();
        s.close();
        sf.close();
    }
}
