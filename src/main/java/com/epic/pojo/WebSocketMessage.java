package com.epic.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WebSocketMessage {
	private WebSocketMessageHeader header;
	private String messageData;

}

