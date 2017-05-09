package com.connctd.tracing.stackdriver;

import io.opentracing.SpanContext;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public class StackDriverOTSpanContext implements SpanContext {
    private com.google.cloud.trace.core.TraceContext traceContext;
    private com.google.cloud.trace.core.SpanContext currentContext;
    private com.google.cloud.trace.Tracer tracer;

    public StackDriverOTSpanContext(com.google.cloud.trace.Tracer tracer, com.google.cloud.trace.core.TraceContext traceContext, com.google.cloud.trace.core.SpanContext currentContext) {
        this.tracer = tracer;
        this.traceContext = traceContext;
        this.currentContext = currentContext;
    }

    public com.google.cloud.trace.core.SpanContext getUnderlyingSpanContext() {
        return currentContext;
    }

    public com.google.cloud.trace.core.TraceContext getUnderlyingTraceContext() {
        return traceContext;
    }

    public com.google.cloud.trace.Tracer getUnderlyingTracer() {
        return tracer;
    }

    @Override
    public Iterable<Map.Entry<String, String>> baggageItems() {
        throw new NotImplementedException();
    }
}
