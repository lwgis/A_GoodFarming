package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;


public class Second implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String title;
	private String content;
	private String image;
	private User nzd;
	private Integer verify;
	private Integer status;
	private String starttime;
	private String endtime;
	private Integer count;
	private Double nprice;
	private Double oprice;
	private Integer acount;
	private Integer maxbuy;
	private String time;
	private Integer type;
	private Integer zone;
	private int haocount;
	private int zhongcount;
	private int chacount;
	private int sumcount;
	private double distance;
	
	public Second() {
		// TODO Auto-generated constructor stub
	}
	public Integer getVerify() {
		return verify;
	}

	public void setVerify(Integer verify) {
		this.verify = verify;
	}

	public Integer getAcount() {
		return acount;
	}

	public void setAcount(Integer acount) {
		this.acount = acount;
	}

	public Integer getMaxbuy() {
		return maxbuy;
	}

	public void setMaxbuy(Integer maxbuy) {
		this.maxbuy = maxbuy;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getZone() {
		return zone;
	}

	public void setZone(Integer zone) {
		this.zone = zone;
	}

	public int getHaocount() {
		return haocount;
	}

	public void setHaocount(int haocount) {
		this.haocount = haocount;
	}

	public int getZhongcount() {
		return zhongcount;
	}

	public void setZhongcount(int zhongcount) {
		this.zhongcount = zhongcount;
	}

	public int getChacount() {
		return chacount;
	}

	public void setChacount(int chacount) {
		this.chacount = chacount;
	}

	public int getSumcount() {
		return sumcount;
	}

	public void setSumcount(int sumcount) {
		this.sumcount = sumcount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public User getNzd() {
		return nzd;
	}

	public void setNzd(User nzd) {
		this.nzd = nzd;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Double getNprice() {
		return nprice;
	}

	public void setNprice(Double nprice) {
		this.nprice = nprice;
	}

	public Double getOprice() {
		return oprice;
	}

	public void setOprice(Double oprice) {
		this.oprice = oprice;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Second [id=" + id + ", title=" + title + ", content=" + content
				+ ", image=" + image + ", nzd=" + nzd + ", status=" + status
				+ ", starttime=" + starttime + ", endtime=" + endtime
				+ ", count=" + count + ", nprice=" + nprice + ", oprice="
				+ oprice + "]";
	}
	
}
