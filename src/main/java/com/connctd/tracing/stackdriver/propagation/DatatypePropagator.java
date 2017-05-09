package com.connctd.tracing.stackdriver.propagation;

import com.connctd.tracing.stackdriver.StackDriverOTSpanContext;
import com.google.cloud.trace.Trace;
import io.opentracing.SpanContext;

public abstract class DatatypePropagator<T> {
    public static final String PREFIX_TRACER_STATE = "ot-tracer-";

	public static final String FIELD_TRACE_ID = PREFIX_TRACER_STATE + "traceid";
	public static final String FIELD_SPAN_ID = PREFIX_TRACER_STATE + "spanid";

    public abstract void inject(StackDriverOTSpanContext spanContext, T textMap);

    public abstract SpanContext extract(com.google.cloud.trace.Tracer tracer, T textMap);

    protected SpanContext buildSpanContext(com.google.cloud.trace.Tracer tracer, String traceId, String spanId) {
        if (traceId == null) {
            throw new IllegalArgumentException("Unable to build span due to missing traceId");
        } else if (spanId == null) {
            throw new IllegalArgumentException("Unable to build span due to missing spanId");
        }

        // we are going to spawn a new context - it can only be used for reference
        com.google.cloud.trace.core.SpanContext spanContext = Trace.getSpanContextFactory().fromHeader(traceId+"/"+spanId+";");
        StackDriverOTSpanContext span = new StackDriverOTSpanContext(tracer, null, spanContext);

        return span;
    }
}
