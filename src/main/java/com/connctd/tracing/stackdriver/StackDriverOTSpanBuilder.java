package com.connctd.tracing.stackdriver;

import com.google.cloud.trace.Trace;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public class StackDriverOTSpanBuilder implements Tracer.SpanBuilder {
    private String spanName;
    private com.google.cloud.trace.Tracer tracer;
    private com.google.cloud.trace.core.SpanContext parentContext;
    private com.google.cloud.trace.core.StartSpanOptions startSpanOptions;

    public StackDriverOTSpanBuilder(String spanName, com.google.cloud.trace.Tracer tracer) {
        this.spanName = spanName;
        this.tracer = tracer;
        this.startSpanOptions = new com.google.cloud.trace.core.StartSpanOptions();
    }

    @Override
    public Tracer.SpanBuilder asChildOf(SpanContext spanContext) {
        parentContext = ((StackDriverOTSpanContext) spanContext).getUnderlyingSpanContext();
        return this;
    }

    @Override
    public Tracer.SpanBuilder asChildOf(Span span) {
        parentContext = ((StackDriverOTSpanContext) span.context()).getUnderlyingSpanContext();
        return this;
    }

    @Override
    public Tracer.SpanBuilder addReference(String s, SpanContext spanContext) {
        throw new NotImplementedException();
    }

    @Override
    public Tracer.SpanBuilder withTag(String s, String s1) {
        throw new NotImplementedException();
    }

    @Override
    public Tracer.SpanBuilder withTag(String s, boolean b) {
        throw new NotImplementedException();
    }

    @Override
    public Tracer.SpanBuilder withTag(String s, Number number) { throw new NotImplementedException(); }

    /**
     * @param timestamp - ms since unix epoch
     * @return SpanBuilder instance
     */
    @Override
    public Tracer.SpanBuilder withStartTimestamp(long timestamp) {
        startSpanOptions.setTimestamp(new com.google.cloud.trace.core.Timestamp() {
            @Override
            public long getSeconds() {
                return timestamp / 1000;
            }

            @Override
            public int getNanos() {
                return java.lang.Math.toIntExact(timestamp % 1000) * 1000000;
            }
        });

        return this;
    }

    @Override
    public Span start() {
        // only attach parent if asChildOf was called and parent was not set before within current thread
        if (parentContext != null && Trace.getSpanContextHandler().current() != parentContext) {
            Trace.getSpanContextHandler().attach(parentContext);
        }

        com.google.cloud.trace.core.TraceContext traceContext = tracer.startSpan(spanName, startSpanOptions);
        com.google.cloud.trace.core.SpanContext currentContext = traceContext.getHandle().getCurrentSpanContext();

        StackDriverOTSpanContext context = new StackDriverOTSpanContext(tracer, traceContext, currentContext);
        StackDriverOTSpan span = new StackDriverOTSpan(context);

        return span;
    }

    @Override
    public Iterable<Map.Entry<String, String>> baggageItems() {
        throw new NotImplementedException();
    }
}
