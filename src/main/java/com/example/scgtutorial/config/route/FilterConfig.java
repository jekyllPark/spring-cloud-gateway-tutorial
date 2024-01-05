package com.example.scgtutorial.config.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("google", r -> r.path("/google/{param}")
                        .filters(f -> f.rewritePath("/google(?<segment>/?.*)", "/search")
                                .addRequestParameter("q", "{param}"))
                        .uri("http://google.com"))
                .route("naver", r -> r.path("/naver/{param}")
                        .filters(f -> f.rewritePath("/naver(?<segment>/?.*)", "/search.naver")
                                .addRequestParameter("query", "{param}"))
                        .uri("http://search.naver.com"))
                .build();
    }
}
