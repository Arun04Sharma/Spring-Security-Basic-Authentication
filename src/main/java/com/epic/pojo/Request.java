package com.epic.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Request {
	private String PhoneAgentID;
	private String PhoneNumber;
	private String OriginPhoneExtension;
	private String EpicCallID;

	public String getMsg() {
		return this.PhoneAgentID + "|" + this.PhoneNumber + "|" + this.EpicCallID;
	}
}