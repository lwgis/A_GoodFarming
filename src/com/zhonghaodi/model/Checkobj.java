package com.zhonghaodi.model;

import java.io.Serializable;

public class Checkobj implements Serializable {

	private String status;
	private String message;
	private boolean result;
	
	public Checkobj(){
		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
		return "Checkobj [status=" + status + ", result=" + result + "]";
	}
	
}
