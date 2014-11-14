package com.thoughtworks.spike.jibx.marshalls;


import com.thoughtworks.spike.jibx.model.CustomerJAXB;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class JAXBMarshall {
    private EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
    JAXBContext context;
    Unmarshaller um;

    public JAXBMarshall() {
        try {
            context = JAXBContext.newInstance(CustomerJAXB.class);
            um = context.createUnmarshaller();
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
}
