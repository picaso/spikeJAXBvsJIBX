package com.thoughtworks.spike.jibx.marshalls;

import com.google.common.collect.Lists;
import com.thoughtworks.spike.jibx.model.CustomerJIBX;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import org.jibx.runtime.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class JiBXMarshall {
    private static final int MYTHREADS = 100;
    private EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
    IBindingFactory bfact;
    IUnmarshallingContext uctx;
    IMarshallingContext mctx;

    public JiBXMarshall() {
        try {
            bfact = BindingDirectory.getFactory(CustomerJIBX.class);
            uctx = bfact.createUnmarshallingContext();
            mctx = bfact.createMarshallingContext();
        } catch (JiBXException e) {
            e.printStackTrace();
        }
    }

    public CustomerJIBX convertXML(InputStream document) {
        EtmPoint point = etmMonitor.createPoint("JiBXMarshall:convertXML");
        CustomerJIBX customerJIBX = null;
        try {
            customerJIBX = (CustomerJIBX) uctx.unmarshalDocument(document, null);
        } catch (JiBXException e) {
            e.printStackTrace();
        } finally {
            point.collect();
        }

        return customerJIBX;
    }

    public InputStream convertObject(CustomerJIBX customerJIBX) {
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

    public List<CustomerJIBX> batchConvertXML(Collection<InputStream> customers) {
        EtmPoint point = etmMonitor.createPoint("JiBXMarshall:batchConvertXML");
        ArrayList<CustomerJIBX> customerObjects = Lists.newArrayListWithCapacity(customers.size());
        try {
            for (final InputStream customer : customers) {
                Future<CustomerJIBX> future = executor.submit(new Callable<CustomerJIBX>() {
                    @Override
                    public CustomerJIBX call() throws Exception {
                        return (CustomerJIBX) uctx.unmarshalDocument(customer, null);
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
}
