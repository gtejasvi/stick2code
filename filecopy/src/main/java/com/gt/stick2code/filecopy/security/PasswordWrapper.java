package com.gt.stick2code.filecopy.security;

import java.io.Serializable;
import java.util.Date;

/**
 * Holds the password and the time of Encryption of the password. The time is
 * always stored in UTC.
 * 
 * @author Ganaraj
 * 
 */
public class PasswordWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8660644219406037686L;

	String password;
	long time;
	
	public PasswordWrapper(String password){
		super();
		this.password = password;
		Date date = new Date();
		this.time = date.getTime();
	}

	public String getPassword() {
		return password;
	}

	void setPassword(String password) {
		this.password = password;
	}

	public long getTime() {
		return time;
	}

	void setTime(long time) {
		this.time = time;
	}

}
