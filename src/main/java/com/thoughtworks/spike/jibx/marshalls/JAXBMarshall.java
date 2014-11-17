package com.thoughtworks.spike.jibx.marshalls;


import com.google.common.collect.Lists;
import com.thoughtworks.spike.jibx.model.CustomerJAXB;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import org.jibx.runtime.JiBXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class JAXBMarshall {
    private EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    private static final int MY_THREADS = 8;
    private ExecutorService executor = Executors.newFixedThreadPool(MY_THREADS);

    private JAXBContext context;

    public JAXBMarshall() {
        try {
            context = JAXBContext.newInstance(CustomerJAXB.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public CustomerJAXB convertXML(InputStream document) throws JAXBException, IOException {
        Unmarshaller um = context.createUnmarshaller();

        EtmPoint point = etmMonitor.createPoint("JAXBMarshall:convertXML");
        CustomerJAXB customerJAXB = null;
        try {
            customerJAXB = (CustomerJAXB) um.unmarshal(document);
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            document.close();
            point.collect();
        }
        return customerJAXB;
    }

    public List<CustomerJAXB> batchConvertXML(Collection<InputStream> customers) throws JAXBException, JiBXException {
        ArrayList<Future<CustomerJAXB>> futureCustomerObjects = Lists.newArrayListWithCapacity(customers.size());
        ArrayList<CustomerJAXB> customerObjects = Lists.newArrayListWithCapacity(customers.size());
        final JAXBMarshallerPool<CustomerJAXB> pool = new JAXBMarshallerPool<>(8, context);

        EtmPoint point = etmMonitor.createPoint("JAXBMarshall:batchConvertXML");
        try {
            for (final InputStream customer : customers) {
                Future<CustomerJAXB> future = executor.submit(new Callable<CustomerJAXB>() {

                    @Override
                    public CustomerJAXB call() throws Exception {
                        return pool.borrowUnmarshaller(new JAXBMarshallerPool.Borrow() {
                            @Override
                            public CustomerJAXB run(Unmarshaller unmarshaller) throws JAXBException {
                                return (CustomerJAXB) unmarshaller.unmarshal(customer);
                            }
                        });
                    }
                });
                futureCustomerObjects.add(future);
            }
            executor.shutdown();
            executor.awaitTermination(50, TimeUnit.SECONDS);

            for(Future<CustomerJAXB> customer : futureCustomerObjects) {
                customerObjects.add(customer.get());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            point.collect();
        }

        return customerObjects;

    }

    public InputStream convertObject(CustomerJAXB customer) throws JAXBException {
        Marshaller mctx = context.createMarshaller();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            mctx.marshal(customer, out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
