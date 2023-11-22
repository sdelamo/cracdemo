package cracdemo;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;

@Singleton
public class StartupEventListener implements ApplicationEventListener<StartupEvent> {
    private final MessageRepository messageRepository;

    public StartupEventListener(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        if (messageRepository.count() == 0) {
            messageRepository.save(new Message(null, "Hello World"));
        }
    }
}
