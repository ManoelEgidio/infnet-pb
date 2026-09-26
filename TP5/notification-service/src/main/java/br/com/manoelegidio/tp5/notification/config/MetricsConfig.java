package br.com.manoelegidio.tp5.notification.config;

import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @WebEndpoint(id = "prometheus")
    public static class PrometheusCustomEndpoint {
        private final PrometheusMeterRegistry registry;

        public PrometheusCustomEndpoint(PrometheusMeterRegistry registry) {
            this.registry = registry;
        }

        @ReadOperation(produces = "text/plain; version=0.0.4; charset=utf-8")
        public String scrape() {
            return registry.scrape();
        }
    }

    @Bean
    public PrometheusCustomEndpoint prometheusEndpoint(PrometheusMeterRegistry registry) {
        return new PrometheusCustomEndpoint(registry);
    }
}
