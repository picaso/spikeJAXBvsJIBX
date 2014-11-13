package com.thoughtworks.spike.jibx.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer")
public class CustomerJAXB {
    private PersonJAXB personJAXB;
    private String street;
    private String city;
    private String state;
    private Integer zip;
    private String phone;

    @XmlElement(name = "person")
    public PersonJAXB getPersonJAXB() {
        return personJAXB;
    }

    public void setPersonJAXB(PersonJAXB personJAXB) {
        this.personJAXB = personJAXB;
    }
    @XmlElement(name = "street")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
    @XmlElement(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @XmlElement(name = "state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @XmlElement(name = "zip")
    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    @XmlElement(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

