package com.connctd.tracing.stackdriver.propagation;

import com.connctd.tracing.stackdriver.StackDriverOTSpanContext;
import com.connctd.tracing.stackdriver.StackDriverOTTracer;
import com.google.cloud.trace.Trace;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.propagation.TextMapInjectAdapter;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TextMapPropagatorTest {
    @Test
    public void testInject() {
        Tracer tracer = new StackDriverOTTracer(Trace.getTracer());

        Span span = tracer.buildSpan("Dummy").start();
        com.google.cloud.trace.core.SpanContext contextToInject = ((StackDriverOTSpanContext)span.context()).getUnderlyingSpanContext();

        Map<String, String> resultingMap = new HashMap<>();
        tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new TextMapInjectAdapter(resultingMap));

        assertEquals(contextToInject.getTraceId().getApiString(), resultingMap.get(TextMapPropagator.FIELD_TRACE_ID));
        assertEquals(contextToInject.getSpanId().getApiString(), resultingMap.get(TextMapPropagator.FIELD_SPAN_ID));
    }

    @Test
    public void testExtract() {
        Tracer tracer = new StackDriverOTTracer(Trace.getTracer());

        Map<String, String> fakeHeaders = new HashMap<>();
        fakeHeaders.put(TextMapPropagator.FIELD_TRACE_ID, "ea3e3b18e8ce393683a2b1c533c5cb2c");
        fakeHeaders.put(TextMapPropagator.FIELD_SPAN_ID, "4994894571082534213");

        SpanContext extractedContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(fakeHeaders));
        com.google.cloud.trace.core.SpanContext context = ((StackDriverOTSpanContext)extractedContext).getUnderlyingSpanContext();

        assertEquals(fakeHeaders.get(TextMapPropagator.FIELD_TRACE_ID), context.getTraceId().getApiString());
        assertEquals(fakeHeaders.get(TextMapPropagator.FIELD_SPAN_ID), context.getSpanId().getApiString());
    }
}
