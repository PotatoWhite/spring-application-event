package me.potato.springapplicationevent.events;

import lombok.Getter;
import lombok.ToString;

/**
 * @author PotatoWhite
 */
@Getter
@ToString
public class TestEvent {
	private final String message;

	public TestEvent(String message) {
		this.message = message;
	}
}