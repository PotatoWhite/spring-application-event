package me.potato.springapplicationevent.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.potato.springapplicationevent.events.TestEvent;

/**
 * @author PotatoWhite
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class Publisher {
	private final ApplicationEventPublisher syncPublisher;

	@Bean(name = "applicationEventMulticaster")
	public ApplicationEventMulticaster asyncEventPublisher() {
		var asyncPublisher = new SimpleApplicationEventMulticaster();
		asyncPublisher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return asyncPublisher;
	}

	public void pub(TestEvent event) {
		log.info("Publishing {}", event.toString());
		syncPublisher.publishEvent(event);
	}
}