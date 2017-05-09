package com.connctd.tracing.stackdriver;

import com.connctd.tracing.stackdriver.propagation.DatatypePropagator;
import com.connctd.tracing.stackdriver.propagation.TextMapPropagator;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;

import java.util.HashMap;
import java.util.Map;

public class StackDriverOTTracer implements Tracer {
    private com.google.cloud.trace.Tracer tracer;
    private Map<Format, DatatypePropagator> propagators;

    public StackDriverOTTracer(com.google.cloud.trace.Tracer tracer) {
        this.tracer = tracer;
        propagators = new HashMap<>();
        propagators.put(Format.Builtin.TEXT_MAP, new TextMapPropagator());
    }

    public SpanBuilder buildSpan(String spanName) {
        return new StackDriverOTSpanBuilder(spanName, tracer);
    }

    @Override
    public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
        if (!propagators.containsKey(format)) {
            throw new UnsupportedOperationException("Unsupported format");
        } else if (!(spanContext instanceof StackDriverOTSpanContext)) {
            throw new UnsupportedOperationException("SpanContext does not seem to be valid");
        }

        propagators.get(format).inject((StackDriverOTSpanContext)spanContext, carrier);
    }

    @Override
    public <C> SpanContext extract(Format<C> format, C carrier) {
        if (!propagators.containsKey(format)) {
            throw new UnsupportedOperationException("Unsupported format");
        }

        return propagators.get(format).extract(tracer, carrier);
    }

    /**
     * Registers a propagator for given format
     * @param format
     * @param propagator
     * @param <C>
     */
    public <C> void registerPropagator(Format<C> format, DatatypePropagator propagator) {
        if (!propagators.containsKey(format)) {
            propagators.put(format, propagator);
        }
    }

    /**
     * Removes propagator for given format
     * @param format
     */
    public <C> void deregisterPropagator(Format<C> format) {
        if (propagators.containsKey(format)) {
            propagators.remove(format);
        }
    }
}
