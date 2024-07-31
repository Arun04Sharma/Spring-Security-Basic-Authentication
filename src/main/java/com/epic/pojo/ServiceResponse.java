package com.epic.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServiceResponse {
	
	public enum Status {
		ZERO, ONE;
	}
	private String msg;
	private Status status;
	
	private Object data;
	
	public ServiceResponse( Status status,String msg, Object data) {
		super();
		this.msg = msg;
		this.status = status;
		this.data = data;
	}
	public ServiceResponse(Status status, Object data) {
		super();
		this.status = status;
		this.data = data;
	}

	public ServiceResponse(Status status) {
		super();
		this.status = status;
	}

}
