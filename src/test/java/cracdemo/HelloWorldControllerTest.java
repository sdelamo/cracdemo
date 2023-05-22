package cracdemo;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.core.type.Argument;
import io.micronaut.crac.test.CheckpointSimulator;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldControllerTest {

    @Test
    void emulateCheckpoint() {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Environment.TEST)) {
            CheckpointSimulator checkpointSimulator =
                    server.getApplicationContext().getBean(CheckpointSimulator.class);
            testApp(server);
            checkpointSimulator.runBeforeCheckpoint();
            server.stop();
            checkpointSimulator.runAfterRestore();
            server.start();
            testApp(server);
        }
    }

    private void testApp(EmbeddedServer embeddedServer) {
        try (HttpClient httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            HttpResponse<Map<String, String>> response = client.exchange(HttpRequest.GET("/"), Argument.mapOf(String.class, String.class));
            assertEquals(HttpStatus.OK, response.getStatus());
            Optional<Map<String, String>> bodyOptional = response.getBody();
            assertTrue(bodyOptional.isPresent());
            assertEquals(Collections.singletonMap("message", "Hello World"), bodyOptional.get());
        }
    }
}