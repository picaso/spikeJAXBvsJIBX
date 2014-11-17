package com.thoughtworks.spike.jibx.marshalls;

import com.google.common.collect.Lists;
import com.thoughtworks.spike.jibx.model.CustomerJIBX;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import org.jibx.runtime.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class JiBXMarshall {
    private static final int MY_THREADS = 8;
    private EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    ExecutorService executor = Executors.newFixedThreadPool(MY_THREADS);
    IBindingFactory bfact;
    IMarshallingContext mctx;
    IUnmarshallingContext uctx;

    public JiBXMarshall() {
        try {
            bfact = BindingDirectory.getFactory(CustomerJIBX.class);
            uctx = bfact.createUnmarshallingContext();
            mctx = bfact.createMarshallingContext();
        } catch (JiBXException e) {
            e.printStackTrace();
        }
    }

    public CustomerJIBX convertXML(InputStream document) throws JiBXException, IOException {
        EtmPoint point = etmMonitor.createPoint("JiBXMarshall:convertXML");
        CustomerJIBX customerJIBX = null;
        if (document == null) {
            throw new RuntimeException("document is null");
        }

        try {
            customerJIBX = (CustomerJIBX) uctx.unmarshalDocument(document, null);
        } catch (JiBXException e) {
            e.printStackTrace();
        } finally {
            document.close();
            point.collect();
        }

        return customerJIBX;
    }

    public InputStream convertObject(CustomerJIBX customerJIBX) throws JiBXException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            mctx.setIndent(2);
            mctx.setOutput(out, null);
            mctx.marshalDocument(customerJIBX);
        } catch (JiBXException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public List<CustomerJIBX> batchConvertXML(Collection<InputStream> customers) throws JiBXException {
        ArrayList<Future<CustomerJIBX>> futureCustomerObjects = Lists.newArrayListWithCapacity(customers.size());
        ArrayList<CustomerJIBX> customerObjects = Lists.newArrayListWithCapacity(customers.size());
        EtmPoint point = etmMonitor.createPoint("JiBXMarshall:batchConvertXML");
        try {
            for (final InputStream customer : customers) {
                Future<CustomerJIBX> future = executor.submit(new Callable<CustomerJIBX>() {
                    IUnmarshallingContext uctx = bfact.createUnmarshallingContext();

                    @Override
                    public CustomerJIBX call() throws Exception {
                        return (CustomerJIBX) uctx.unmarshalDocument(customer, null);
                    }
                });
                futureCustomerObjects.add(future);
            }
            executor.shutdown();
            executor.awaitTermination(50, TimeUnit.SECONDS);

            for (Future<CustomerJIBX> futureCustomerObject : futureCustomerObjects) {
                customerObjects.add(futureCustomerObject.get());
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
}
