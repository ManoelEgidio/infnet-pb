package br.com.manoelegidio.tp5.taskmanager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActuatorObservabilityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Actuator Root deve listar os endpoints expostos")
    void shouldListActuatorEndpoints() throws Exception {
        mockMvc.perform(get("/actuator"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Actuator Health deve retornar status UP para monitoramento")
    void shouldReturnHealthStatusUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Kubernetes Liveness Probe deve responder UP no endpoint /actuator/health/liveness")
    void shouldReturnLivenessProbeUp() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Kubernetes Readiness Probe deve responder UP no endpoint /actuator/health/readiness")
    void shouldReturnReadinessProbeUp() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Endpoint Prometheus deve expor métricas no formato padrão de scraping")
    void shouldExposePrometheusMetrics() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk());
    }
}
