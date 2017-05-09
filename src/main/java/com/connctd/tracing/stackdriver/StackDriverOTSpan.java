package com.connctd.tracing.stackdriver;

import com.google.cloud.trace.core.Labels;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public class StackDriverOTSpan implements Span {
    private StackDriverOTSpanContext context;
    private com.google.cloud.trace.core.EndSpanOptions endSpanOptions;

    public StackDriverOTSpan(StackDriverOTSpanContext context) {
        this.context = context;
        this.endSpanOptions = new com.google.cloud.trace.core.EndSpanOptions();
    }

    @Override
    public SpanContext context() {
        return context;
    }

    @Override
    public void finish() {
        context.getUnderlyingTracer().endSpan(context.getUnderlyingTraceContext());
    }

    @Override
    public void finish(long timestamp) {
        endSpanOptions.setTimestamp(new com.google.cloud.trace.core.Timestamp() {
            @Override
            public long getSeconds() {
                return timestamp / 1000;
            }

            @Override
            public int getNanos() {
                return java.lang.Math.toIntExact(timestamp % 1000) * 1000000;
            }
        });

        context.getUnderlyingTracer().endSpan(context.getUnderlyingTraceContext(), endSpanOptions);
    }

    @Override
    public void close() {
        throw new NotImplementedException();
    }

    @Override
    public Span setTag(String key, String value) {
        com.google.cloud.trace.core.TraceContext traceContext = context.getUnderlyingTraceContext();
        context.getUnderlyingTracer().annotateSpan(traceContext, Labels.builder().add(key,value).build());
        return this;
    }

    @Override
    public Span setTag(String key, boolean value) {
        setTag(key, String.valueOf(value));
        return this;
    }

    @Override
    public Span setTag(String key, Number value) {
        setTag(key, String.valueOf(value));
        return this;
    }

    @Override
    public Span log(Map<String, ?> map) {
        throw new NotImplementedException();
    }

    @Override
    public Span log(long l, Map<String, ?> map) {
        throw new NotImplementedException();
    }

    @Override
    public Span log(String s) {
        throw new NotImplementedException();
    }

    @Override
    public Span log(long l, String s) {
        throw new NotImplementedException();
    }

    @Override
    public Span setBaggageItem(String s, String s1) {
        throw new NotImplementedException();
    }

    @Override
    public String getBaggageItem(String s) {
        throw new NotImplementedException();
    }

    @Override
    public Span setOperationName(String s) {
        throw new NotImplementedException();
    }

    @Override
    public Span log(String s, Object o) {
        throw new NotImplementedException();
    }

    @Override
    public Span log(long l, String s, Object o) {
        throw new NotImplementedException();
    }
}
