package com.thoughtworks.spike.jibx.marshalls;


import com.google.common.collect.Lists;
import com.thoughtworks.spike.jibx.model.CustomerJAXB;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class JAXBMarshall {
    private EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    private static final int MYTHREADS = 100;
    private ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);

    private JAXBContext context;
    private Unmarshaller um;
    private Marshaller mctx;

    public JAXBMarshall() {
        try {
            context = JAXBContext.newInstance(CustomerJAXB.class);
            um = context.createUnmarshaller();
            mctx = context.createMarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public CustomerJAXB convertXML(InputStream document) {
        EtmPoint point = etmMonitor.createPoint("JAXBMarshall:convertXML");
        CustomerJAXB customerJAXB = null;
        try {
            customerJAXB = (CustomerJAXB) um.unmarshal(document);
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            point.collect();
        }
        return customerJAXB;
    }

    public List<CustomerJAXB> batchConvertXML(Collection<InputStream> customers) {
        EtmPoint point = etmMonitor.createPoint("JAXBMarshall:batchConvertXML");
        ArrayList<CustomerJAXB> customerObjects = Lists.newArrayListWithCapacity(customers.size());
        try {
            for (final InputStream customer : customers) {
                Future<CustomerJAXB> future = executor.submit(new Callable<CustomerJAXB>() {
                    @Override
                    public CustomerJAXB call() throws Exception {
                        return (CustomerJAXB) um.unmarshal(customer);
                    }
                });
                customerObjects.add(future.get());
            }
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            point.collect();

        }
        return customerObjects;

    }

    public InputStream convertObject(CustomerJAXB customer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            mctx.marshal(customer, out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
