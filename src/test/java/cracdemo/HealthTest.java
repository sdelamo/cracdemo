package cracdemo;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class HealthTest {

    @Test
    void healthEndpointExposed(@Client("/") HttpClient client) {
        HttpStatus status = assertDoesNotThrow(() -> client.toBlocking().retrieve(HttpRequest.GET("/health"), HttpStatus.class));
        assertEquals(HttpStatus.OK, status);
    }
}