package com.gt.stick2code.filecopy.common;

import java.io.Serializable;

public class RequestResponseWrapper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5752870540812212353L;
	Ack ack;
	Object object;
	
	public RequestResponseWrapper(Ack ack){
		this.ack = ack;
	}
	
	public Ack getAck() {
		return ack;
	}
	public void setAck(Ack ack) {
		this.ack = ack;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	
	
	
}
