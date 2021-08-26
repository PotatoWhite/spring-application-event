package me.potato.springapplicationevent.handler;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.potato.springapplicationevent.events.TestEvent;

/**
 * @author PotatoWhite
 */
@Slf4j
@Component
public class TestEventHandler {

	@Async
	@EventListener
	public void handle(TestEvent event) throws InterruptedException {
		Thread.sleep(1000);
		log.info("handle : {}", event.getMessage());
	}

	@Async
	@EventListener(condition = "#event.message.startsWith('messageType02')")
	public void handleFilter(TestEvent event) throws InterruptedException {
		Thread.sleep(1000);
		log.info("handle only messageType02 : {}", event.getMessage());
	}

	@EventListener
	public void handle(Object event) {
		log.info("ANY EVENTS, {}", event.toString());
	}
}
