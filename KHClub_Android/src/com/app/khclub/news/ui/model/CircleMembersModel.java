package com.app.khclub.news.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.base.utils.KHConst;

public class CircleMembersModel {
	//private List<CircleMembersModel> imageNewsList = new ArrayList<CircleMembersModel>();
	private String userID;
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	private String Membersimage;

//	public List<CircleMembersModel> getImageNewsList() {
//		return imageNewsList;
//	}
//
//	public void setImageNewsList(List<CircleMembersModel> imageNewsList) {
//		this.imageNewsList = imageNewsList;
//	}


	public String getMembersimage() {
		return Membersimage;
	}

	public void setMembersimage(String members_image) {
		Membersimage = members_image;
	}

	public void setContentWithJson(JSONObject newsObj) {
		// TODO Auto-generated method stub
		if (newsObj.containsKey("id")) {
			setUserID(newsObj.getString("id"));
		}
		if (newsObj.containsKey("head_sub_image")) {
			setMembersimage(KHConst.ATTACHMENT_ADDR + newsObj.getString("head_sub_image"));
		}
	}

}
