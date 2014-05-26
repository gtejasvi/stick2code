package com.test.amazon.common.exception;

/**
 * @author Ganaraj
 *
 */
public class CriticalException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5405301628132080897L;
	public String errorCode = null;
	public CriticalException(){
		super();
		this.errorCode = "";
	}
	
	public CriticalException(String errorCode){
		super();
		this.errorCode = errorCode;
	}
	
	public CriticalException(String errorCode,String message){
		super(message);
		this.errorCode = errorCode;
	}
	
	public CriticalException(String errorCode,Throwable th){
		
		super(th);
		this.errorCode = errorCode;
	}
	
	public CriticalException(String errorCode, String message, Throwable th){
		super(message,th);
		this.errorCode = errorCode;
	}
}
