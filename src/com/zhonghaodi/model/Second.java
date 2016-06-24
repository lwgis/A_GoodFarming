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
	private Integer status;
	private String starttime;
	private String endtime;
	private Integer count;
	private Double nprice;
	private Double oprice;
	private List<NetImage>attachments;
	private int coupon;
	
	public Second() {
		// TODO Auto-generated constructor stub
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

	public List<NetImage> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<NetImage> attachments) {
		this.attachments = attachments;
	}

	public int getCoupon() {
		return coupon;
	}

	public void setCoupon(int coupon) {
		this.coupon = coupon;
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
