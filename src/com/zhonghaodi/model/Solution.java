package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

import com.zhonghaodi.networking.GFDate;

import android.R.integer;

public class Solution implements Serializable {

	private int id;
	private int did;
	private User writer;
	private String content;
	private String time;
	private int zan;
	private int commentCount;
	private List<RComment> comments;
	private boolean liked;
	
	public Solution(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDid() {
		return did;
	}

	public void setDid(int did) {
		this.did = did;
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

	public int getZan() {
		return zan;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}
	
	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public List<RComment> getComments() {
		return comments;
	}

	public void setComments(List<RComment> comments) {
		this.comments = comments;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	@Override
	public String toString() {
		return "Solution [id=" + id + ", did=" + did + ", writer=" + writer
				+ ", content=" + content + ", time=" + time + ", zan=" + zan
				+ "]";
	}
	
}
