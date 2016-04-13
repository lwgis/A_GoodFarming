package com.zhonghaodi.model;

import java.io.Serializable;

public class PostResponse implements Serializable {
	
	private boolean result;
	private String message;
	
	public PostResponse(){
		
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ReportResponse [result=" + result + ", message=" + message
				+ "]";
	}
	

}
