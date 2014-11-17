package com.thoughtworks.spike.jibx;

import com.thoughtworks.spike.jibx.marshalls.JiBXMarshall;
import com.thoughtworks.spike.jibx.model.CustomerJIBX;
import com.thoughtworks.spike.jibx.model.PersonJIBX;
import org.jibx.runtime.JiBXException;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JiBXMarshallPerformanceTest {
    private final int MAX = 1000*1000;

    @Test
    public void shouldBindXMLWithClassUsingJiBX() throws Exception {
        //Given
        JiBXMarshall jiBXMarshall = new JiBXMarshall();
        CustomerJIBX customerJIBX = null;

        //When
        for (int i = 0; i < MAX; i++) {
            customerJIBX = jiBXMarshall.convertXML(this.getClass().getClassLoader().getResourceAsStream("customer.xml"));
        }

        //Then
        PersonJIBX personJIBX = customerJIBX.getPersonJIBX();

        assertThat(customerJIBX.getCity(), is("Plunk"));
        assertThat(customerJIBX.getState(), is("WA"));
        assertThat(customerJIBX.getZip(), is(98059));
        assertThat(customerJIBX.getPhone(), is("888.555.1234"));

        assertThat(personJIBX.getCustomerNumber(), is(123456789));
        assertThat(personJIBX.getFirstName(), is("John"));
        assertThat(personJIBX.getLastName(), is("Smith"));

    }

    @Test
    public void shouldBatchBindXMLWithClassUsingJiBX() throws Exception {
        //Given
        JiBXMarshall jiBXMarshall = new JiBXMarshall();
        int counter = 0;

        //When
        List<CustomerJIBX> customers = jiBXMarshall.batchConvertXML(generateJiBXCustomers(MAX, jiBXMarshall));

        //Then
        for (CustomerJIBX customer : customers) {
            assertThat(customer.getZip(), is(counter));
            counter++;
        }

    }

    private Collection<InputStream> generateJiBXCustomers(int size, JiBXMarshall jiBXMarshall) throws JiBXException {
        Collection<InputStream> customers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CustomerJIBX customer = new CustomerJIBX();
            customer.setCity("city");
            customer.setPhone("123");
            customer.setState("state");
            customer.setStreet("street");
            customer.setZip(i);
            PersonJIBX person = new PersonJIBX();
            person.setCustomerNumber(i);
            person.setFirstName("fname");
            person.setLastName("lname");
            customer.setPersonJIBX(person);
            customers.add(jiBXMarshall.convertObject(customer));
        }
        return customers;
    }
}
