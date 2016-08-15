package com.zhonghaodi.model;

import java.io.Serializable;

public class ResponseDto implements Serializable {

	private String content;
	private User writer;
	
	public ResponseDto() {
		// TODO Auto-generated constructor stub
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getWriter() {
		return writer;
	}

	public void setWriter(User writer) {
		this.writer = writer;
	}

	@Override
	public String toString() {
		return "ResponseDto [content=" + content + ", writer=" + writer + "]";
	}
	
	
}
