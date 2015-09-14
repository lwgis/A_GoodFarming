package com.zhonghaodi.model;

import java.io.Serializable;

public class GuaResult implements Serializable {
	private boolean success;
	private String message;
	private Gua guagua;
	private int oid;
	public GuaResult(){
		
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Gua getGuagua() {
		return guagua;
	}
	public void setGuagua(Gua guagua) {
		this.guagua = guagua;
	}
	public int getOid() {
		return oid;
	}
	public void setOid(int oid) {
		this.oid = oid;
	}
	
}
