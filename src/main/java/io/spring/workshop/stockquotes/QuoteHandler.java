package io.spring.workshop.stockquotes;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@Component
public class QuoteHandler {
    private final Flux<Quote> quoteFlux;
    private final Flux<Foo> helloStream = Flux.interval(Duration.ofMillis(500))
            .map(l -> new Foo("hello", l));

    public QuoteHandler(QuoteGenerator quoteGenerator) {
        quoteFlux = quoteGenerator.fetchQuoteStream(Duration.ofMillis(200));
    }

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok().contentType(TEXT_PLAIN).syncBody("hello world!");
    }

    public Mono<ServerResponse> echo(ServerRequest request) {
        return ServerResponse.ok().contentType(TEXT_PLAIN).body(request.bodyToFlux(String.class), String.class);
    }

    public Mono<ServerResponse> quotes(ServerRequest request) {
        return ServerResponse.ok().contentType(APPLICATION_STREAM_JSON).body(quoteFlux, Quote.class);
    }

    public Mono<ServerResponse> initialQuotes(ServerRequest serverRequest) {
        Integer size = getNrOfElemsOrDefault(serverRequest.queryParam("size"));

        return ServerResponse.ok().contentType(APPLICATION_JSON).body(quoteFlux.take(size), Quote.class);
    }

    private Integer getNrOfElemsOrDefault(Optional<String> sizeParam) {
        return sizeParam.flatMap(x -> {
            try {
                return Optional.of(Integer.parseInt(x));
            } catch (Exception e) {
                return Optional.empty();
            }
        }).orElse(10);
    }

    public Mono<ServerResponse> helloStreamInit(ServerRequest serverRequest) {
        return ServerResponse.ok().body(helloStream.take(Duration.ofSeconds(3)), Foo.class);
    }


    private static class Foo {
        private String foo;
        private Long bar;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public Long getBar() {
            return bar;
        }

        public void setBar(Long bar) {
            this.bar = bar;
        }

        public Foo(String foo, Long bar) {
            this.foo = foo;
            this.bar = bar;
        }

    }

    public Mono<ServerResponse> helloStream(ServerRequest request) {
        //this APPLICATION_STREAM_JSON is a key. If you remove it, seems response is not flushed
        return ServerResponse.ok().contentType(APPLICATION_STREAM_JSON)
                .body(helloStream.doOnRequest((l) -> System.out.println("asked for " + l)),
                        Foo.class);
    }
}
