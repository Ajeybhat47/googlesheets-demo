package com.googlesheets.demo.model;


import javax.persistence.*;

@Entity
public class Employee {

    @Id
    private Long id;

    @Column
    private String name;
    @Column
    private int age;
    @Column
    private String city;
    @Column
    private double salary;
    @Column
    private String lastModifiedTime;



    // Constructors, getters, setters, and toString() method


    public Employee() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Employee(Long id, String name, int age, String city, double salary, String lastModifiedTime) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.city = city;
        this.salary = salary;
        this.lastModifiedTime = lastModifiedTime;
    }


    // Getters and setters omitted for brevity
}
