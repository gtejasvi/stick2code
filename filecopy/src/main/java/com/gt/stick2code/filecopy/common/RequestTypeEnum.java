package com.gt.stick2code.filecopy.common;

public enum RequestTypeEnum {
	GETFILELIST("getfilelist"),
	GETFILES("getfiles"),
	PUTFILTERFILESLIST("putfilterfileslist"),
	PUTFILES("putfiles"),
	PUTFILEVALIDATE("putfilevalidate"),
	PUTFILEMERGE("putfilemerge");
	
	private String value;
	
	private RequestTypeEnum(String value){
		this.value = value.toLowerCase();
	}
	
	public String getValue(){
		return value;
	}
	

	public static RequestTypeEnum getEnumByValue(String value){
		for(RequestTypeEnum requestEnum : RequestTypeEnum.values()){
			if(requestEnum.getValue().equalsIgnoreCase(value)){
				return requestEnum;
			}
		}
		return valueOf(value);
		
	}
}
