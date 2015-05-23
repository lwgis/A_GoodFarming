package com.zhonghaodi.model;

public class GFMessage {
	private String title;
	private String content;
	private long time;
	private User user;
	private int count;
	private Integer exid;
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
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Integer getExid() {
		return exid;
	}
	public void setExid(Integer exid) {
		this.exid = exid;
	}
	

}
