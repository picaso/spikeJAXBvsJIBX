package com.thoughtworks.spike.jibx.marshalls;

import org.jibx.runtime.JiBXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.concurrent.ArrayBlockingQueue;

public class JAXBMarshallerPool<T> {
    private final JAXBContext context;
    private final java.util.concurrent.BlockingQueue<Unmarshaller> queue;

    public JAXBMarshallerPool(int size, JAXBContext context) throws JiBXException, JAXBException {
        this.context = context;

        queue = new ArrayBlockingQueue<>(size);
        for(int i = 0; i < size; i++) {
            queue.add(context.createUnmarshaller());
        }
    }

    public T borrowUnmarshaller(Borrow<T> b) throws JiBXException, InterruptedException, JAXBException {
        Unmarshaller unmarshaller = queue.take();
        T result = b.run(unmarshaller);
        queue.add(unmarshaller);
        return result;
    }

    public interface Borrow<T> {
        T run(Unmarshaller unmarshaller) throws JAXBException;
    }
}

