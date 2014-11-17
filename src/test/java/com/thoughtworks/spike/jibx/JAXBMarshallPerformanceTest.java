package com.thoughtworks.spike.jibx;

import com.thoughtworks.spike.jibx.marshalls.JAXBMarshall;
import com.thoughtworks.spike.jibx.model.CustomerJAXB;
import com.thoughtworks.spike.jibx.model.PersonJAXB;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JAXBMarshallPerformanceTest {
    private final int MAX = 1000*1000;

    @Test
    public void shouldBindXMLWithClassUsingJAXB() throws Exception {
        //Given
        JAXBMarshall jaxbMarshall = new JAXBMarshall();
        CustomerJAXB customerJAXB = null;

        //When

        for (int i = 0; i < MAX; i++) {
            customerJAXB = jaxbMarshall.convertXML(Thread.currentThread().getContextClassLoader().getResourceAsStream("customer.xml"));
        }
        PersonJAXB personJAXB = customerJAXB.getPersonJAXB();

        //Then
        assertThat(customerJAXB.getCity(), is("Plunk"));
        assertThat(customerJAXB.getState(), is("WA"));
        assertThat(customerJAXB.getZip(), is(98059));
        assertThat(customerJAXB.getPhone(), is("888.555.1234"));

        assertThat(personJAXB.getCustomerNumber(), is(123456789));
        assertThat(personJAXB.getFirstName(), is("John"));
        assertThat(personJAXB.getLastName(), is("Smith"));

    }

    @Test
    public void shouldBatchBindXMLWithClassUsingJAXB() throws Exception {
        //Given
        JAXBMarshall jaxbMarshall = new JAXBMarshall();
        int counter = 0;

        //When
        List<CustomerJAXB> customers = jaxbMarshall.batchConvertXML(generateJAXBCustomers(MAX, jaxbMarshall));

        //Then
        for (CustomerJAXB customer : customers) {
            assertThat(customer.getZip(), is(counter));
            counter++;
        }

    }

    private Collection<InputStream> generateJAXBCustomers(int size, JAXBMarshall jaxbMarshall) throws JAXBException {
        Collection<InputStream> customers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CustomerJAXB customer = new CustomerJAXB();
            customer.setCity("city");
            customer.setPhone("123");
            customer.setState("state");
            customer.setStreet("street");
            customer.setZip(i);
            PersonJAXB person = new PersonJAXB();
            person.setCustomerNumber(i);
            person.setFirstName("fname");
            person.setLastName("lname");
            customer.setPersonJAXB(person);
            customers.add(jaxbMarshall.convertObject(customer));
        }
        return customers;
    }
}
