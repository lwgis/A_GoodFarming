package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.Date;


public class RecipeEvaluate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private Integer urid;
	
	private User user;
	
	private Integer rid;
	
	private ScoringDic scoring;
	
	private String evaluate;
	
	private Integer status;
	
	private String time;
	
	public RecipeEvaluate(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ScoringDic getScoring() {
		return scoring;
	}

	public void setScoring(ScoringDic scoring) {
		this.scoring = scoring;
	}

	public String getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(String evaluate) {
		this.evaluate = evaluate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getUrid() {
		return urid;
	}

	public void setUrid(Integer urid) {
		this.urid = urid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getRid() {
		return rid;
	}

	public void setRid(Integer rid) {
		this.rid = rid;
	}

	@Override
	public String toString() {
		return "RecipeEvaluate [id=" + id + ", urid=" + urid + ", user=" + user
				+ ", rid=" + rid + ", scoring=" + scoring + ", evaluate="
				+ evaluate + ", status=" + status + ", time=" + time + "]";
	}
	
}
