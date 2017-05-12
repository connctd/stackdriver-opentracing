# stackdriver-opentracing

OpenTracing Tracer implementation for GCloud StackDriver based on Java. **Caution** The tracer is highly experimental. Feel free to contribute!
 
## Limitations
- opentracing's boolean, number and string tags are mapped to stackdriver string labels
- no baggage item support
- no log support
- so far there only exists a primitive text map propagator but you can easily write and hook in your own  
- SpanBuilders 'withTag' not implemented

## Setup
```
<dependency>
    <groupId>com.connctd.tracing.stackdriver</groupId>
    <artifactId>stackdriver-opentracing</artifactId>
    <version>1.0</version>
</dependency>
```

## Sample usage
```
// gcloud configuration
TraceService traceService = TraceGrpcApiService.builder()
        .setProjectId("Your project id")
        .setCredentials(GoogleCredentials.fromStream(new FileInputStream("credentials.json")))
        .setScheduledDelay(1)
        .build();

Trace.init(traceService);

// create new tracer
Tracer tracer = new StackDriverOTTracer(Trace.getTracer());

// spawn new spans
Span parentSpan = tracer.buildSpan("/api/v1/test").start();
parentSpan.setTag("description","just a test");

Span childSpan = tracer.buildSpan("authorize").asChildOf(parentSpan).start();

// ...

childSpan.finish();

parentSpan.finish();
```

## How to build
```
mvn clean install
```

## Further examples

### Extracting trace information from map
```
HashMap<String, String> fakeHeaders = new HashMap<>();
fakeHeaders.put(TextMapPropagator.FIELD_TRACE_ID, "ea3e3b18e8ce393683a2b1c533c5cb2c");
fakeHeaders.put(TextMapPropagator.FIELD_SPAN_ID, "4994894571082534213");

// restore span context by extracting text map
SpanContext extractedContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(fakeHeaders));

// attach restored span context to context
Trace.getSpanContextHandler().attach(((StackDriverOTSpanContext)extractedContext).getUnderlyingSpanContext());

// attached span will now be parent of following span
Span myChildSpan = tracer.buildSpan("Hello world!").withStartTimestamp(Calendar.getInstance().getTimeInMillis()).start();
...
```

### Registering own propagator implementation (e.g for transmitting trace information via HTTPHeaders)
```
tracer.registerPropagator(yourFormat, yourPropagator);
```

...where yourPropagator needs to implement DatatypePropagator

