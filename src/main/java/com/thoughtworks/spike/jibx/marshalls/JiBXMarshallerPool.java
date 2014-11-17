package com.thoughtworks.spike.jibx.marshalls;

import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import java.util.concurrent.ArrayBlockingQueue;

public class JiBXMarshallerPool<T> {
    private final IBindingFactory factory;
    private final java.util.concurrent.BlockingQueue<IUnmarshallingContext> queue;

    public JiBXMarshallerPool(int size, IBindingFactory factory) throws JiBXException {
        this.factory = factory;

        queue = new ArrayBlockingQueue<>(size);
        for(int i = 0; i < size; i++) {
            queue.add(factory.createUnmarshallingContext());
        }
    }

    public T borrowUnmarshaller(Borrow<T> b) throws JiBXException, InterruptedException {
        IUnmarshallingContext context = queue.take();
        T result = b.run(context);
        queue.add(context);
        return result;
    }

    public interface Borrow<T> {
        T run(IUnmarshallingContext context) throws JiBXException;
    }
}
