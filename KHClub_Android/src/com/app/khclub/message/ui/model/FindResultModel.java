package com.app.khclub.message.ui.model;

//搜索结果模型
public class FindResultModel {

	//用户id
	private int uid;
	//用户名
	private String name;
	//头像缩略图
	private String head_image;
	//头像缩略图
	private String head_sub_image;
	//是否是好友
	private int is_friend;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHead_sub_image() {
		return head_sub_image;
	}
	public void setHead_sub_image(String head_sub_image) {
		this.head_sub_image = head_sub_image;
	}
	public String getHead_image() {
		return head_image;
	}
	public void setHead_image(String head_image) {
		this.head_image = head_image;
	}
	public int getIs_friend() {
		return is_friend;
	}
	public void setIs_friend(int is_friend) {
		this.is_friend = is_friend;
	}
	
}
