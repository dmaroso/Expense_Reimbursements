package com.empire.app;

import com.empire.app.controllers.EmployeeController;
import com.empire.app.controllers.LoginController;
import com.empire.app.controllers.ManagerController;
import io.javalin.Javalin;

public class Application {

    public static void main(String[] args) {
        Javalin app = Javalin.create( config -> {
            config.addStaticFiles("/public");
            config.contextPath = "/ERS";
        }).start(8080);

        app.before("/home/*", LoginController.forceBeforeLogin);
        app.post("/login", LoginController::handleLogin);
        app.get("/logout", LoginController::handleLogout);
        app.get("/home/employee", EmployeeController::handleHome);
        app.post("/home/employee/submit", EmployeeController::handleSubmit);
        app.get("/home/employee/requests", EmployeeController::handleRequestView);
        app.post("/home/employee/editInfo", EmployeeController::handleInfoEdit);
        app.get("/home/manager/employees", ManagerController::handleEmployeeView);
        app.post("/home/manager/editRequest", ManagerController::handleRequestEdit);
        app.get("/home/manager/requests", ManagerController::handleRequestView);
        app.post("/home/manager/register", ManagerController::handleRegister);
    }
}
