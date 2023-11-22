package cracdemo;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.Collections;
import java.util.Map;

@Controller
class HelloWorldController {

    private final MessageRepository messageRepository;

    HelloWorldController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Get
    Map<String, String> index() {
        return messageRepository.findAll()
                .stream().map(entity ->
                        Collections.singletonMap("message", entity.message()))
                .findFirst()
                .orElseGet(Collections::emptyMap);
    }
}
