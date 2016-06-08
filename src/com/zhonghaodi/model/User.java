package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

import android.R.integer;

public class User implements Serializable {
	private String id;
	private String phone;
	private String alias;
	private String thumbnail;
	private String address;
	private Level level;
	private String password;
	private int point;
	private int currency;
	private int followcount;
	private int fanscount;
	private String description;
	private String tjPhone;
	private String tjCode;
	private List<UserCrop> crops;
	private int levelID;
	private String identifier;
	private boolean teamwork;
	private int tjcoin;
	private int recCount;
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getThumbnail() {
		return thumbnail.trim();
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getCurrency() {
		return currency;
	}

	public void setCurrency(int currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public int getFollowcount() {
		return followcount;
	}

	public void setFollowcount(int followcount) {
		this.followcount = followcount;
	}

	public int getFanscount() {
		return fanscount;
	}

	public void setFanscount(int fanscount) {
		this.fanscount = fanscount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTjPhone() {
		return tjPhone;
	}

	public void setTjPhone(String tjPhone) {
		this.tjPhone = tjPhone;
	}
	
	public String getTjCode() {
		return tjCode;
	}

	public void setTjCode(String tjCode) {
		this.tjCode = tjCode;
	}

	public List<UserCrop> getCrops() {
		return crops;
	}


	public void setCrops(List<UserCrop> crops) {
		this.crops = crops;
	}

	public int getLevelID() {
		return levelID;
	}

	public void setLevelID(int levelID) {
		this.levelID = levelID;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isTeamwork() {
		return teamwork;
	}

	public void setTeamwork(boolean teamwork) {
		this.teamwork = teamwork;
	}

	public int getTjcoin() {
		return tjcoin;
	}

	public void setTjcoin(int tjcoin) {
		this.tjcoin = tjcoin;
	}

	public int getRecCount() {
		return recCount;
	}

	public void setRecCount(int recCount) {
		this.recCount = recCount;
	}
	
}
