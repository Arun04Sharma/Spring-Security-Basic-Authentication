package com.epic.websocket;

import lombok.extern.log4j.Log4j2;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.epic.pojo.ServiceResponse;
import com.epic.pojo.ServiceResponse.Status;

@Log4j2
@Component
public class WebSocketManager {

	@Value("${PrimaryWS}")
	private String primaryServerUrl;

	@Value("${SecondaryWS}")
	private String secondaryServerUrl;

	private WebSocketConnectClient primaryWebSocketClient;
	private WebSocketConnectClient secondaryWebSocketClient;

	@PostConstruct
	public void initialize() {

		if (this.primaryWebSocketClient == null) {
			this.initializePrimaryWebSocketClient();
		}

		if (this.secondaryWebSocketClient == null) {
			this.initializeSecondaryWebSocketClient();
		}

	}

	public ServiceResponse sendNotification(String agentId, Object message, String type) {
		ServiceResponse response = null;
		log.atInfo().withLocation().log("INPUT| {}, {} ", agentId, message);

		this.initializePrimaryWebSocketClient();
		log.atDebug().withLocation().log("Primary WS --> {}", this.primaryWebSocketClient);

		boolean status = this.sendToWebSocketClient(this.primaryWebSocketClient, agentId, message, type);

		response = (status) ? new ServiceResponse(Status.ZERO, "Message Sent to Primary WS |", (Object) null)
				: new ServiceResponse(Status.ZERO, "Error on Primary WS |", (Object) null);

		this.initializeSecondaryWebSocketClient();
		log.atDebug().withLocation().log("Secondary WS --> {}", this.secondaryWebSocketClient);

		status = this.sendToWebSocketClient(this.secondaryWebSocketClient, agentId, message, type);
		if (status) {
			response.setMsg(response.getMsg().concat(" Msg Sent to Secodary WS |"));
		} else {
			response.setMsg(response.getMsg().concat(" Error Occurred Sending Msg to Secodary WS"));
		}

		return response;
	}

	private boolean sendToWebSocketClient(WebSocketConnectClient client, String agentId, Object message, String type) {
		if (client != null) {
			synchronized (this) {
				return client.sendNotification(agentId, message, type);
			}
		} else {
			log.atInfo().withLocation().log("WebSocket client is null");
			return false;
		}
	}

	private void initializePrimaryWebSocketClient() {
		log.atInfo().withLocation().log("Initializing the Primary WebSocket Client");
		if (this.primaryWebSocketClient == null) {
			if (this.primaryServerUrl != null) {
				try {
					log.atInfo().withLocation().log("Primary Websocket --> {}", this.primaryServerUrl);
					this.primaryWebSocketClient = new WebSocketConnectClient(this.primaryServerUrl);
				} catch (Exception var2) {
					log.atError().withLocation().log("Error Occurred. {}, {} --> ", var2.getMessage(), var2);
				}
			} else {
				log.atWarn().withLocation().log("Primary Server URL is Null");
			}
		}

	}

	private void initializeSecondaryWebSocketClient() {
		log.atInfo().withLocation().log("Initializing the Secondary WebSocket Client");
		if (this.secondaryWebSocketClient == null) {
			if (this.secondaryServerUrl != null) {
				try {
					log.atInfo().withLocation().log("Secondary Websocket --> {}", this.secondaryServerUrl);
					this.secondaryWebSocketClient = new WebSocketConnectClient(this.secondaryServerUrl);
				} catch (Exception var2) {
					log.atError().withLocation().log("Error Occurred. {}, {} --> ", var2.getMessage(), var2);
				}
			} else {
				log.atWarn().withLocation().log("Secondary Server URL is Null");
			}
		}

	}

}