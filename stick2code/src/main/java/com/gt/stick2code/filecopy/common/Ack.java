package com.gt.stick2code.filecopy.common;


public enum Ack {
	SUCCESS("success"),FAILURE("failure"),
	READY("ready");
	
	String value;
	
	private Ack(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	
	
}
