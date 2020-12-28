package com.empire.app.util;

import com.empire.app.models.Employee;
import com.empire.app.models.Request;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.*;


public class SessionUtil {

    private SessionFactory sf;
    Properties props = new Properties();

    /**
     * <h3>configSession</h3>
     * <p>
     *     Configures the private session factory via calling the the {@link #configure configure} method and setting
     *     the properties to the the paramters read from the file db.properties file in resources.
     * </p>
     * @return SessionFactory
     */
    public SessionFactory configSession() {
        try {
            props.load(new FileReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource("db.properties")).getFile()));
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            props.setProperty("hibernate.connection.url", url);
            props.setProperty("hibernate.connection.username", username);
            props.setProperty("hibernate.connection.password", password);
            configure(props);
            return this.sf;
        } catch (IOException e) {
            System.out.println("failed");
            return null;
        }
    }

    private void configure(Properties props) {
        Configuration config = null;
        if(props == null) {
            config = new Configuration().configure();
        } else {
            config = new Configuration();
            config.setProperties(props);
            config.addAnnotatedClass(Employee.class);
            config.addAnnotatedClass(Request.class);
        }
        if(config != null) {
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(config.getProperties());
            this.sf = config.buildSessionFactory(builder.build());
        }
    }

}

