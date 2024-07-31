package com.epic.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WebSocketMessageHeader {

	public enum MessageType {
		CONNECT("CONNECT", 0), DISCONNECT("DISCONNECT", 1), DATA("DATA", 2);

		private MessageType(final String name, final int ordinal) {
		}
	}

	private String sender;
	private String destination;
	private MessageType messageType;

}
