package com.zhonghaodi.model;

import java.io.Serializable;

import android.R.integer;

public class Prescription implements Serializable {

	private int id;
	private String uid;
	private String title;
	private String content;
	private String time;
	private int status;
	
	public Prescription(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Prescription [id=" + id + ", uid=" + uid + ", title=" + title
				+ ", content=" + content + ", time=" + time + ", status="
				+ status + "]";
	}
	
}
