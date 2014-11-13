package com.thoughtworks.spike.jibx.marshalls;

import com.thoughtworks.spike.jibx.model.CustomerJIBX;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import java.io.InputStream;

public class JiBXMarshall {
    private EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    IBindingFactory bfact;

    public JiBXMarshall() {
        try {
            bfact =
                    BindingDirectory.getFactory(CustomerJIBX.class);
        } catch (JiBXException e) {
            e.printStackTrace();
        }
    }

    public CustomerJIBX convertXML(InputStream document) {
        EtmPoint point = etmMonitor.createPoint("JiBXMarshall:convertXML");
        CustomerJIBX customerJIBX = null;

        try {
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            customerJIBX = (CustomerJIBX) uctx.unmarshalDocument
                    (document, null);

        } catch (JiBXException e) {
            e.printStackTrace();
        } finally {
            point.collect();
        }

        return customerJIBX;
    }
}
