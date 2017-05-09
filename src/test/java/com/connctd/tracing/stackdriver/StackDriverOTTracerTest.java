package com.connctd.tracing.stackdriver;

import com.connctd.tracing.stackdriver.propagation.DatatypePropagator;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StackDriverOTTracerTest {
    @Test
    public void testRegisterOwnPropagator() {
        StackDriverOTTracer tracer = new StackDriverOTTracer(com.google.cloud.trace.Trace.getTracer());

        MockPropagator mockPropagator = new MockPropagator();
        tracer.registerPropagator(DummyFormat.INSTANCE, mockPropagator);

        assertFalse(mockPropagator.injectCalled());

        Span span = tracer.buildSpan("test").start();
        tracer.inject(span.context(), DummyFormat.INSTANCE, null);

        assertTrue(mockPropagator.injectCalled());

        assertFalse(mockPropagator.extractCalled());

        tracer.extract(DummyFormat.INSTANCE, null);

        assertTrue(mockPropagator.extractCalled());
    }

    private static class DummyFormat implements Format {
        public final static DummyFormat INSTANCE = new DummyFormat();
    }

    private class MockPropagator extends DatatypePropagator {
        private boolean injectCalled = false;
        private boolean extractCalled = false;

        @Override
        public void inject(StackDriverOTSpanContext spanContext, Object textMap) {
            injectCalled = true;
        }

        @Override
        public SpanContext extract(com.google.cloud.trace.Tracer tracer, Object carrier) {
            extractCalled = true;
            return null;
        }

        public boolean injectCalled() {
            return injectCalled;
        }

        public boolean extractCalled() {
            return extractCalled;
        }
    }
}
