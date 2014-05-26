package com.test.amazon.common.exception;

/**
 * @author Ganaraj
 *
 */
public class ValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5405301628132080897L;
	public String errorCode = null;
	public ValidationException(){
		super();
		this.errorCode = "";
	}
	
	public ValidationException(String errorCode){
		super();
		this.errorCode = errorCode;
	}
	
	public ValidationException(String errorCode,String message){
		super(message);
		this.errorCode = errorCode;
	}
	
	public ValidationException(String errorCode,Throwable th){
		
		super(th);
		this.errorCode = errorCode;
	}
	
	public ValidationException(String errorCode, String message, Throwable th){
		super(message,th);
		this.errorCode = errorCode;
	}
}
