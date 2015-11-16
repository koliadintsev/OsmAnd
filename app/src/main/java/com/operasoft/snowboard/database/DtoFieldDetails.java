package com.operasoft.snowboard.database;

import java.lang.reflect.Method;

public class DtoFieldDetails {
	public String name;
	public String column;
	public Method getter;
	public Method setter;
	
	public DtoFieldDetails(String name, String column, Method getter, Method setter) {
		this.name = name;
		this.column = column;
		this.getter = getter;
		this.setter = setter;
	}

	
}
