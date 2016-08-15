package com.zhonghaodi.model;

import java.io.Serializable;

import com.zhonghaodi.networking.GFDate;

public class CaiComment implements Serializable {

	private int id;
	private String content;
	private String time;
	private User writer;
	private boolean win;
	private int status;
	
	public CaiComment(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return GFDate.getStandardDate(time);
	}

	public void setTime(String time) {
		this.time = time;
	}

	public User getWriter() {
		return writer;
	}

	public void setWriter(User writer) {
		this.writer = writer;
	}

	public boolean isWin() {
		return win;
	}

	public void setWin(boolean win) {
		this.win = win;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CaiComment [id=" + id + ", content=" + content + ", time=" + time + ", writer=" + writer + ", win="
				+ win + ", status=" + status + "]";
	}
	
	
}
