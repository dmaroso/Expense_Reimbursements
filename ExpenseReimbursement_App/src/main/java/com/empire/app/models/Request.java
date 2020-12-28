package com.empire.app.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name="Requests")
public class Request {

    @Id
    @Column(name="REQUEST_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name="EMPLOYEE_ID")
    private int employeeId;

    @Column(name="AMOUNT")
    private double amount;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="STATUS")
    private String status;

    @Column(name="MANAGER_ID")
    private int managerId;

    public Request(){};
}
