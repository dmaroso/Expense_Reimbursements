package com.empire.app.dao;

import com.empire.app.models.Employee;
import com.empire.app.models.Request;

import java.io.IOException;
import java.util.List;

public interface Repository {
    Object authenticate(String email, String pass);
    Object editInfo(Employee e, String entryLabel, String data);
    void editRequest(int reqId, String status, int managerId);
    void submitRequest(Request request);
    List<Request> getRequests(int employeeID);
    //order by last name
    List<Employee> getEmployees();
    void register(Employee employee);

}
