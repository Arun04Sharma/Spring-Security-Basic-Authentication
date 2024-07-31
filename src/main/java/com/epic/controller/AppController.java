package com.epic.controller;

import com.epic.pojo.Request;
import com.epic.pojo.ServiceResponse;
import com.epic.pojo.ServiceResponse.Status;
import com.epic.service.OutboundService;
import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class AppController {

	private static final Gson gson = (new Gson()).newBuilder().create();

	@Autowired
	private OutboundService serviceInterface;

	@GetMapping("/test")
	public ResponseEntity<ServiceResponse> test() {
		log.atInfo().withLocation().log("Successfully hit the test endpoint");
		return ResponseEntity.ok(new ServiceResponse(Status.ZERO, "Hello There !!! How are you?"));
	}

	@PostMapping("/outbound")
	public ResponseEntity<ServiceResponse> outboundAPI(@RequestBody String payload) {
		log.atInfo().withLocation().log("Params --> {}", payload.toString());
		Request body = (Request) gson.fromJson(payload, Request.class);
		if (body.getPhoneAgentID() != null && body.getPhoneNumber() != null && body.getEpicCallID() != null
				&& !body.getPhoneAgentID().isEmpty() && !body.getPhoneNumber().isEmpty()
				&& !body.getEpicCallID().isEmpty()) {
			ServiceResponse service = this.serviceInterface.sendWebSocketMessage(body, "OutboundApi");
			log.atInfo().withLocation().log("Exit from Controller. Output --> {}", service);
			return ResponseEntity.ok(service);
		} else {
			log.atError().withLocation().log("Bad Request: Invalid request body - {}", body);
			return ResponseEntity.badRequest().body(new ServiceResponse(Status.ONE, (Object) null));
		}
	}

	@PostMapping("/hangup")
	public ResponseEntity<ServiceResponse> hangupAPI(@RequestBody String payload) {
		log.atInfo().withLocation().log("Params --> {}", payload);
		Request body = (Request) gson.fromJson(payload, Request.class);
		if (body.getPhoneAgentID() != null && body.getEpicCallID() != null && !body.getPhoneAgentID().isEmpty()
				&& !body.getEpicCallID().isEmpty()) {
			ServiceResponse service = this.serviceInterface.sendWebSocketMessage(body, "HangupApi");
			log.atInfo().withLocation().log("Exit from Controller. Output --> {}", service);
			return ResponseEntity.ok(service);
		} else {
			return ResponseEntity.badRequest().body(new ServiceResponse(Status.ONE, (Object) null));
		}
	}
}