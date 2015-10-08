package com.app.khclub.news.ui.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.base.utils.KHConst;

public class LikeModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 263009424634441987L;
	// 点赞者的id
	private String userID;
	// 点赞占的头像全图
	private String headImage;
	// 点赞占的头像缩略图
	private String headSubImage;
	// 点赞者的名字
	private String name;
	//点赞者的职位
	private String userJob;

	// 内容注入
	public void setContentWithJson(JSONObject object) {
		setUserID(object.getString("user_id"));
		setHeadImage(KHConst.ATTACHMENT_ADDR + object.getString("head_image"));
		setHeadSubImage(KHConst.ATTACHMENT_ADDR
				+ object.getString("head_sub_image"));
		if (object.containsKey("name")) {
			setName(object.getString("name"));
		}
		if (object.containsKey("job")) {
			setUserJob(object.getString("job"));
		}
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserJob() {
		return userJob;
	}

	public void setUserJob(String job) {
		this.userJob = job;
	}
}
