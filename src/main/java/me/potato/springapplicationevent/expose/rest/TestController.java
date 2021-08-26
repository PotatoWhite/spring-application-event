package me.potato.springapplicationevent.expose.rest;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.potato.springapplicationevent.config.Publisher;
import me.potato.springapplicationevent.events.TestEvent;

/**
 * @author PotatoWhite
 */

@Slf4j
@RequiredArgsConstructor
@RestController
public class TestController {

	private final Publisher publisher;

	@PostMapping("/eventType01")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void createEventType01() {
		var message = String.format("messageType01 : %s", LocalDateTime.now().toString());
		publisher.pub(new TestEvent(message));
		log.info("Fin. eventType01");
	}

	@PostMapping("/eventType02")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void createEventType02() {
		var message = String.format("messageType02 : %s", LocalDateTime.now().toString());
		publisher.pub(new TestEvent(message));
		log.info("Fin. eventType01");
	}

	@EventListener
	public void handle(TestEvent event) throws InterruptedException {
		Thread.sleep(1000);
		log.info("handle : {}", event.getMessage());
	}

	@EventListener(condition = "#event.message.startsWith('messageType02')")
	public void handleFilter(TestEvent event) throws InterruptedException {
		Thread.sleep(1000);
		log.info("handle only messageType02 : {}", event.getMessage());
	}

}