package com.zhonghaodi.model;

import java.io.Serializable;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Crop implements Parcelable,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int category;
	private String name;
	private String pname;
	private String image;
	private List<Phase> phases;
	public static final Parcelable.Creator<Crop> CREATOR = new Parcelable.Creator<Crop>(){  
        @Override  
        public Crop createFromParcel(Parcel source) {//从Parcel中读取数据，返回person对象  
            return new Crop(source.readInt(), source.readString(),source.readInt());  
        }  
        @Override  
        public Crop[] newArray(int size) {  
            return new Crop[size];  
        }  
    };  
    
	public Crop() {
	}

	public Crop(int id, String name, int category) {
		this.id = id;
		this.name = name;
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	

	public List<Phase> getPhases() {
		return phases;
	}

	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(category);
	}

	@Override
	public String toString() {
		return name;
	}


}
