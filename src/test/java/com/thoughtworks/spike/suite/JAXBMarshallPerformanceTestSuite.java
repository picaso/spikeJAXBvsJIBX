package com.thoughtworks.spike.suite;


import com.thoughtworks.spike.jibx.JAXBMarshallPerformanceTest;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        JAXBMarshallPerformanceTest.class,
})
public class JAXBMarshallPerformanceTestSuite {
    private static EtmMonitor etmMonitor;

    @BeforeClass
    public static void setUp() {
        BasicEtmConfigurator.configure();
        etmMonitor = EtmManager.getEtmMonitor();
        etmMonitor.start();
    }

    @AfterClass
    public static void tearDown() {
        etmMonitor.render(new SimpleTextRenderer());
        etmMonitor.stop();
        etmMonitor.reset();
    }

}
