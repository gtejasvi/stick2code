package com.test.amazon.common.exception;

/**
 * @author Ganaraj
 *
 */
public class EntityNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5405301628132080897L;
	public String errorCode = null;
	public EntityNotFoundException(){
		super();
		this.errorCode = "";
	}
	
	public EntityNotFoundException(String errorCode){
		super();
		this.errorCode = errorCode;
	}
	
	public EntityNotFoundException(String errorCode,String message){
		super(message);
		this.errorCode = errorCode;
	}
	
	public EntityNotFoundException(String errorCode,Throwable th){
		
		super(th);
		this.errorCode = errorCode;
	}
	
	public EntityNotFoundException(String errorCode, String message, Throwable th){
		super(message,th);
		this.errorCode = errorCode;
	}
}
