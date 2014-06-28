package com.gt.stick2code.filecopy.common;

import java.io.Serializable;

public class RequestDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3071228782196305237L;
	
	String encPwd;
	RequestTypeEnum requestType;
	public String getEncPwd() {
		return encPwd;
	}
	public void setEncPwd(String encPwd) {
		this.encPwd = encPwd;
	}
	public RequestTypeEnum getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestTypeEnum requestType) {
		this.requestType = requestType;
	}
	

}
