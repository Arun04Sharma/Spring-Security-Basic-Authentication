package com.epic.websocket;

import com.epic.pojo.WebSocketMessage;
import com.epic.pojo.WebSocketMessageHeader;
import com.epic.pojo.WebSocketMessageHeader.MessageType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Type;
import java.net.URI;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.ConnectionLostException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Log4j2
public class WebSocketConnectClient implements StompSessionHandler {
	private URI url;
	private StompSession currentSession;
	private WebSocketClient client = new StandardWebSocketClient();
	private WebSocketStompClient stompClient;

	public WebSocketConnectClient(final String url) {
		this.stompClient = new WebSocketStompClient(this.client);
		this.url = URI.create(url);
		this.init();
	}

	public boolean sendNotification(final String agentId, final Object payload, final String type) {
		return this.sendMessage(agentId, payload, type);
	}

	private void init() {
		this.client = new StandardWebSocketClient();
		StompHeaders headers = new StompHeaders();
		headers.add("username", "ivisionCore");
		this.stompClient = new WebSocketStompClient(this.client);
		WebSocketHttpHeaders headers2 = new WebSocketHttpHeaders();
		this.stompClient.setMessageConverter(new StringMessageConverter());
		//this.stompClient.connect(this.url, headers2, headers, this);
		this.stompClient.connectAsync(this.url, headers2, headers, this);
		this.stompClient.start();
	}

	public boolean sendMessage(String destination, final Object payload, final String type) {
		log.atInfo().withLocation().log("Sending Data. Destination:{}, Msg:{}", destination, payload);
		if (this.currentSession == null) {
			log.atWarn().withLocation().log("WebSocket session not yet established. Message not sent.");
			return false;
		} else {
			StompHeaders headers = new StompHeaders();
			headers.setDestination("/novelvoxwebsockbroker/" + destination);
			JsonObject messagePayload = new JsonObject();
			if (type.equals("OutboundApi")) {
				messagePayload.addProperty("OutboundApi", payload.toString());
			} else {
				messagePayload.addProperty("HangupApi", payload.toString());
			}

			log.atInfo().withLocation().log("MESSAGE PAYLOAD. Params --> {}", messagePayload);
			String pushMessage = (new Gson())
					.toJson(createMessagePayload(destination, MessageType.CONNECT, messagePayload, "ivisionCore"));
			if (!this.currentSession.isConnected()) {
				this.destroyAndReconnect();
			}

			log.atDebug().withLocation().log("Pushed Msg --> {}", pushMessage);
			this.currentSession.send(headers, pushMessage);
			return true;
		}
	}

	private static WebSocketMessage createMessagePayload(final String destination, final MessageType msgType,
			final Object payLoad, final String clientName) {
		WebSocketMessage newMessage = new WebSocketMessage();
		WebSocketMessageHeader messageHeader = new WebSocketMessageHeader();
		messageHeader.setDestination(destination);
		messageHeader.setMessageType(msgType);
		messageHeader.setSender(clientName);
		newMessage.setHeader(messageHeader);
		newMessage.setMessageData(payLoad.toString());
		return newMessage;
	}

	public Type getPayloadType(final StompHeaders arg0) {
		return String.class;
	}

	public void afterConnected(final StompSession session, final StompHeaders connectedHeaders) {
		this.currentSession = session;
		log.atInfo().withLocation().log("AfterConnected Method ::: isConnected :{} ", session.isConnected());
	}

	public void handleException(final StompSession arg0, final StompCommand arg1, final StompHeaders arg2,
			final byte[] arg3, final Throwable arg4) {
		log.atError().withLocation().withThrowable(arg4).log();
		if (arg4 instanceof ConnectionLostException || arg4 instanceof Exception) {
			this.destroyAndReconnect();
		}

	}

	public void handleTransportError(final StompSession arg0, final Throwable arg1) {
		log.atError().withLocation().withThrowable(arg1).log();
		if (arg1 instanceof ConnectionLostException || arg1 instanceof Exception) {
			this.destroyAndReconnect();
		}

	}

	public void handleFrame(final StompHeaders arg0, final Object arg1) {
		log.atInfo().withLocation().log("Handle Frame: {}", arg1);
	}

	private void destroyAndReconnect() {
		if (this.currentSession != null && this.currentSession.isConnected()) {
			this.currentSession.disconnect();
		}

		this.currentSession = null;

		try {
			Thread.sleep(10000L);
		} catch (Exception var2) {
			log.atError().withLocation().log("Error Reconnecting :{}|{}", this.url.getHost(), var2.getMessage());
		}

		if (this.stompClient != null) {
			this.stompClient.stop();
			this.stompClient = null;
		}

		log.atInfo().withLocation().log("Destroying & Reconnecting the Websocket Session");
		this.init();
	}
}