package com.epic.service;

import com.epic.pojo.Request;
import com.epic.pojo.ServiceResponse;

public interface OutboundService {
	
	ServiceResponse sendWebSocketMessage(Request body, String type);

}
