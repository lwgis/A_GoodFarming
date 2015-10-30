package com.zhonghaodi.model;

import java.io.Serializable;

import com.zhonghaodi.networking.GFDate;

import android.R.integer;

public class RComment implements Serializable {

	private int id;
	private int rid;
	private int sid;
	private User writer;
	private String content;
	private String time;
	
	public RComment(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public User getWriter() {
		return writer;
	}

	public void setWriter(User writer) {
		this.writer = writer;
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

	@Override
	public String toString() {
		return "RComment [id=" + id + ", rid=" + rid + ", writer=" + writer
				+ ", content=" + content + ", time=" + time + "]";
	}
	
}
