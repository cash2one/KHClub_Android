package com.app.khclub.news.ui.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class CircleItemModel{
	//圈子分类
	private String category_name;
	//圈子ID
	private String id;
	//圈子名称
	private String circle_name;
	//圈子图片
	private String circle_cover_sub_image;
	//关注人数
	private String follow_quantity;
	//圈子未读消息
	private String news_newsnum;
	//是否关注
	private String is_follow;
	
	public String getIs_follow() {
		return is_follow;
	}
	public void setIs_follow(String is_follow) {
		this.is_follow = is_follow;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public String getNews_newsnum() {
		return news_newsnum;
	}
	public void setNews_newsnum(String news_newsnum) {
		this.news_newsnum = news_newsnum;
	}
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
	public void setContentWithJson(JSONObject object) {
         
		if (object.containsKey("id")) {
			setId(object.getString("id"));
		}
		if (object.containsKey("circle_name")) {
			setCircle_name(object.getString("circle_name"));
		}
		if (object.containsKey("circle_cover_sub_image")) {
			setCircle_cover_sub_image(object.getString("circle_cover_sub_image"));
		}
		if (object.containsKey("follow_quantity")) {
			setFollow_quantity(object.getString("follow_quantity"));
		}
		if (object.containsKey("new_newsnum")) {
			setNews_newsnum(object.getString("new_newsnum"));
		}
		if (object.containsKey("category_name")) {
			setCategory_name(object.getString("category_name"));
		}
		if (object.containsKey("is_follow")) {
			setIs_follow(object.getString("is_follow"));
		}
	}
}
