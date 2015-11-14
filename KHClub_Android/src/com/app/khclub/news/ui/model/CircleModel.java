package com.app.khclub.news.ui.model;

import java.io.Serializable;

public class CircleModel implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//ID
	private int id;
	//标题
	private String title;
	//描述
	private String intro;
	//图片
	private String image;
	//管理员姓名
	private String manager_name;
	//联系方式
	private String phone_num;
	//地址
	private String address;
	//微信号
	private String wx_num;
	private int delete_flag;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getManager_name() {
		return manager_name;
	}
	public void setManager_name(String manager_name) {
		this.manager_name = manager_name;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getWx_num() {
		return wx_num;
	}
	public void setWx_num(String wx_num) {
		this.wx_num = wx_num;
	}
	public int getDelete_flag() {
		return delete_flag;
	}
	public void setDelete_flag(int delete_flag) {
		this.delete_flag = delete_flag;
	}
	
	
	
}
