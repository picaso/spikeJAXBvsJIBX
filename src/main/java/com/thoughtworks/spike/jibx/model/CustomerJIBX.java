package com.thoughtworks.spike.jibx.model;

public class CustomerJIBX {
    private PersonJIBX personJIBX;
    private String street;
    private String city;
    private String state;
    private Integer zip;
    private String phone;

    public PersonJIBX getPersonJIBX() {
        return personJIBX;
    }

    public void setPersonJIBX(PersonJIBX personJIBX) {
        this.personJIBX = personJIBX;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

