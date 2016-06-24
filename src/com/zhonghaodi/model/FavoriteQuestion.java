package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.Date;

public class FavoriteQuestion implements Serializable {
	private int id;
	private Date time;
	private Question myquestion;
	private int status;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Question getMyquestion() {
		return myquestion;
	}
	public void setMyquestion(Question myquestion) {
		this.myquestion = myquestion;
	}
	
}
