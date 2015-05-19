package com.zhonghaodi.model;

import java.util.ArrayList;

import com.zhonghaodi.networking.GFDate;

public class Response {
	private int id;
	private User writer;
	private String content ;
	private int zan;
	private String time;
	private ArrayList<Zan> items;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getZan() {
		return zan;
	}
	public void setZan(int zan) {
		this.zan = zan;
	}
	public String getTime() {
		return GFDate.getStandardDate(time);
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ArrayList<Zan> getItems() {
		return items;
	}
	public void setItems(ArrayList<Zan> items) {
		this.items = items;
	}
	
	/**是否有用户点过赞
	 * @param uid 用户id
	 * @return
	 */
	public boolean isHasUser(String uid) {
		if (items==null||items.size()==0) {
			return false;
		}
		for (Zan zan : items) {
			if (zan.getUid().equals(uid)) {
				return true;
			}
		}
		return false;
	}
	
	public void zan(String uid) {
		if (items==null) {	
			items=new ArrayList<Zan>();
		}
		Zan zan=new Zan();
		zan.setRid(id);
		zan.setUid(uid);
		items.add(zan);
	}
	public void cancelZan(String uid) {
		if (items==null||items.size()==0) {
			return;
		}
		for (Zan aZan : items) {
			if (aZan.getUid().equals(uid)) {
				items.remove(aZan);
				return;
			}
		}
	}
	
}
