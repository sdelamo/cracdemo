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
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldControllerTest {

    @Test
    void emulateCheckpoint() {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, Environment.TEST)) {
            CheckpointSimulator checkpointSimulator =
                    server.getApplicationContext().getBean(CheckpointSimulator.class);
            testApp(server, this::testHelloWorld);

            Object time = testApp(server, this::testTime);
            assertEquals(time, testApp(server, this::testTime));

            checkpointSimulator.runBeforeCheckpoint();
            server.stop();
            checkpointSimulator.runAfterRestore();
            server.start();
            testApp(server, this::testHelloWorld);
            assertNotEquals(time, testApp(server, this::testTime));
        }
    }

    private Object testApp(EmbeddedServer embeddedServer, Function<BlockingHttpClient, Object> clientConsumer) {
        try (HttpClient httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL())) {
            BlockingHttpClient client = httpClient.toBlocking();
            return clientConsumer.apply(client);
        }
    }

    private Void testHelloWorld(BlockingHttpClient client) {
        HttpResponse<Map<String, String>> response = client.exchange(HttpRequest.GET("/"), Argument.mapOf(String.class, String.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        Optional<Map<String, String>> bodyOptional = response.getBody();
        assertTrue(bodyOptional.isPresent());
        assertEquals(Collections.singletonMap("message", "Hello World"), bodyOptional.get());
        return null;
    }

    private String testTime(BlockingHttpClient client) {
        HttpResponse<Map<String, String>> response = client.exchange(HttpRequest.GET("/time"), Argument.mapOf(String.class, String.class));
        assertEquals(HttpStatus.OK, response.getStatus());
        Optional<Map<String, String>> bodyOptional = response.getBody();
        assertTrue(bodyOptional.isPresent());
        Map<String, String> body = bodyOptional.get();
        assertEquals(1, body.keySet().size() );
        assertTrue(body.containsKey("time"));
        return body.get("time");
    }
}