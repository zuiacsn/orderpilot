package com.orderpilot;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootstrapHttpIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void systemInfoReturnsBootstrapMetadata() {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity("/api/system/info", JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.path("name").asText()).isEqualTo("OrderPilot");
        assertThat(body.path("version").asText()).isEqualTo("0.1.0-SNAPSHOT");
        assertThat(body.path("stage").asText()).isEqualTo("bootstrap");
    }

    @Test
    void healthIsUpWithoutExternalServices() {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity("/actuator/health", JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.path("status").asText()).isEqualTo("UP");
    }

    @Test
    void unknownRouteReturnsNotFoundWithoutExceptionDetails() {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity("/api/does-not-exist", JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        JsonNode body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.path("status").asInt()).isEqualTo(404);
        assertThat(body.findValues("exception")).isEmpty();
        assertThat(body.findValues("trace")).isEmpty();
        assertThat(body.findValues("stackTrace")).isEmpty();
        assertThat(body.findValues("stacktrace")).isEmpty();
    }
}
