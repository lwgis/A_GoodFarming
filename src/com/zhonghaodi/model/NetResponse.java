package com.zhonghaodi.model;

import java.io.Serializable;

import android.R.integer;

public class NetResponse implements Serializable {

	private int status;
	private String message;
	private String result;
	private String auth;
	
	public NetResponse(){
		
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}
	
}
