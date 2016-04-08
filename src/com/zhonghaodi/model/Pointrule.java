package com.zhonghaodi.model;

import java.io.Serializable;

public class Pointrule implements Serializable {

	private boolean result;
	private String message;
	
	public Pointrule(){}

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
		return "Pointrule [result=" + result + ", message=" + message + "]";
	}
	
 	
}
