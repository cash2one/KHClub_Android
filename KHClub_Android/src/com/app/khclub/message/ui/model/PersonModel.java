package com.app.khclub.message.ui.model;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.base.utils.KHConst;

public class PersonModel {

	// 用户的id
	private String userId;
	// 用户名
	private String userName;
	// 用户的头像
	private String headImage;
	// 头像的缩略图
	private String headSubImage;
	// 是否为朋友
	private String isFriend = "0";
	// 电话号码
	private String phoneNumber;

	public void setContentWithJson(JSONObject object) {

		if (object.containsKey("user_id")) {
			setUerId(object.getString("user_id"));
		}
		if (object.containsKey("name")) {
			setUserName(object.getString("name"));
		}
		if (object.containsKey("head_image")) {
			setHeadImage(KHConst.ATTACHMENT_ADDR
					+ object.getString("head_image"));
		}
		if (object.containsKey("head_sub_image")) {
			setHeadSubImage(KHConst.ATTACHMENT_ADDR
					+ object.getString("head_sub_image"));
		}

		if (object.containsKey("is_friend")) {
			setIsFriend(object.getString("is_friend"));
		}
		if (object.containsKey("phone_num")) {
			setPhoneNumber(object.getString("phone_num"));
		}
	}

	// 重写hashCode方法
	@Override
	public int hashCode() {
		return this.userId.hashCode();
	}

	// 重写equals方法
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (null != obj && obj instanceof PersonModel) {
			PersonModel p = (PersonModel) obj;
			if (userId.equals(p.userId) && userName.equals(p.userName)) {
				return true;
			}
		}
		return false;
	}

	public String getUerId() {
		return userId;
	}

	public void setUerId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getHeadSubImage() {
		return headSubImage;
	}

	public void setHeadSubImage(String headSubImage) {
		this.headSubImage = headSubImage;
	}

	public String getIsFriend() {
		return isFriend;
	}

	public void setIsFriend(String isFriend) {
		this.isFriend = isFriend;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
