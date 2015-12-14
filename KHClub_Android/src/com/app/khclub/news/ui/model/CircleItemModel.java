package com.app.khclub.news.ui.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class CircleItemModel{
	
	
	//圈子ID
	private String id;
	//圈子名称
	private String circle_name;
	//圈子图片
	private String circle_cover_sub_image;
	//关注人数
	private String follow_quantity;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCircle_name() {
		return circle_name;
	}
	public void setCircle_name(String circle_name) {
		this.circle_name = circle_name;
	}
	public String getCircle_cover_sub_image() {
		return circle_cover_sub_image;
	}
	public void setCircle_cover_sub_image(String circle_cover_sub_image) {
		this.circle_cover_sub_image = circle_cover_sub_image;
	}
	public String getFollow_quantity() {
		return follow_quantity;
	}
	public void setFollow_quantity(String follow_quantity) {
		this.follow_quantity = follow_quantity;
	}
	
}
