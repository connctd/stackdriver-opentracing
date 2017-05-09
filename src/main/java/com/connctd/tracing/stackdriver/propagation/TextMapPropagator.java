package com.connctd.tracing.stackdriver.propagation;

import com.connctd.tracing.stackdriver.StackDriverOTSpanContext;
import io.opentracing.SpanContext;
import io.opentracing.propagation.TextMap;

import java.util.Iterator;
import java.util.Map;

/**
 * Simple propagator that is capabale of extracting/injection span information from/into text map
 */
public class TextMapPropagator extends DatatypePropagator<TextMap> {
    public void inject(StackDriverOTSpanContext spanContext, TextMap textMap) {
        com.google.cloud.trace.core.SpanContext context = spanContext.getUnderlyingSpanContext();
        textMap.put(FIELD_TRACE_ID, context.getTraceId().getApiString());
        textMap.put(FIELD_SPAN_ID, context.getSpanId().getApiString());
    }

    public SpanContext extract(com.google.cloud.trace.Tracer tracer, TextMap textMap) {
        String traceId = null;
        String spanId = null;

        Iterator<Map.Entry<String, String>> iter = textMap.iterator();
        while(iter.hasNext()) {
            Map.Entry<String, String> currEntry = iter.next();

            if (currEntry.getKey().compareToIgnoreCase(FIELD_TRACE_ID) == 0) {
                traceId = currEntry.getValue();
            } else if (currEntry.getKey().compareToIgnoreCase(FIELD_SPAN_ID) == 0) {
                spanId = currEntry.getValue();
            }
        }

        return buildSpanContext(tracer, traceId, spanId);
    }
}
