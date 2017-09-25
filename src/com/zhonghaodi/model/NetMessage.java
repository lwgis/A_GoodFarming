package com.zhonghaodi.model;

import java.io.Serializable;

public class NetMessage implements Serializable {
	
	private String message;
	private boolean result;
	
	public NetMessage() {
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
	
	

}
