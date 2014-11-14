package com.thoughtworks.spike.jibx;

import com.thoughtworks.spike.jibx.marshalls.JAXBMarshall;
import com.thoughtworks.spike.jibx.marshalls.JiBXMarshall;
import com.thoughtworks.spike.jibx.model.CustomerJAXB;
import com.thoughtworks.spike.jibx.model.CustomerJIBX;
import com.thoughtworks.spike.jibx.model.PersonJAXB;
import com.thoughtworks.spike.jibx.model.PersonJIBX;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MarshallPerformanceTest {
    private final int MAX = 100;
    private EtmMonitor etmMonitor;


    @Before
    public void setUp() {
        BasicEtmConfigurator.configure();
        etmMonitor = EtmManager.getEtmMonitor();
        etmMonitor.start();
    }

    @After
    public void tearDown() {
        etmMonitor.render(new SimpleTextRenderer());
        etmMonitor.stop();
        etmMonitor.reset();
    }

    @Test
    public void shouldBindXMLWithClassUsingJiBX() throws Exception {
        //Given
        JiBXMarshall jiBXMarshall = new JiBXMarshall();
        CustomerJIBX customerJIBX = null;

        //When
        for (int i = 0; i < MAX; i++) {
            customerJIBX = jiBXMarshall.convertXML(Thread.currentThread().getContextClassLoader().getResourceAsStream("customer.xml"));
        }

        PersonJIBX personJIBX = customerJIBX.getPersonJIBX();

        //Then
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
        List<CustomerJIBX> customers = jiBXMarshall.batchConvertXML(generateCustomers(100, jiBXMarshall));

        //Then
        for (CustomerJIBX customer : customers) {
            assertThat(customer.getZip(), is(counter));
            counter++;
        }

    }


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

    private Collection<InputStream> generateCustomers(int size, JiBXMarshall jiBXMarshall) {
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