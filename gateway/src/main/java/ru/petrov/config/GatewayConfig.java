package ru.petrov.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final CommonProps commonProps;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("deal", r1 -> r1.path("/deal/**")
                        .uri(commonProps.getDealUrl()))
                .route("statement", r1 -> r1.path("/statement/**")
                        .uri(commonProps.getStatementUrl()))
                .build();
    }

    @Bean
    public GlobalFilter customLogFilter() {
        return (exchange, chain) -> {
            log.info("Pre-filter: request id -> {}", exchange.getRequest().getId());
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() ->
                            log.info("Post-filter: response code -> {}",
                                    exchange.getResponse().getStatusCode())));
        };
    }
}