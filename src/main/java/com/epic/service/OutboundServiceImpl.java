package com.epic.service;

import org.springframework.stereotype.Service;

import com.epic.pojo.Request;
import com.epic.pojo.ServiceResponse;
import com.epic.websocket.WebSocketManager;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class OutboundServiceImpl implements OutboundService {
	private final WebSocketManager webSocketManager;

	public OutboundServiceImpl(WebSocketManager webSocketManager) {
		this.webSocketManager = webSocketManager;
	}

	@Override
	public ServiceResponse sendWebSocketMessage(Request body, String type) {
		log.atInfo().withLocation().log("Inside Service Layer. Params --> {}, {}", body.toString(), type);

		ServiceResponse response = webSocketManager.sendNotification(body.getPhoneAgentID(), body.getMsg(), type);

		log.atInfo().withLocation().log("Exit from Service Layer.");
		
		return response;
	}
}
