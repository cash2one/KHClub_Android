package com.app.khclub.news.ui.model;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R.id;
import com.app.khclub.base.utils.KHConst;

public class NoticeReplyModel {
	// 评论ID
	private String id;
	// 评论人ID
	private String user_id;
	// 评论人名字
	private String name;	
	// 评论人职务
	private String job;
	// 评论人头像
	private String head_sub_image;
	// 评论内容
	private String comment_content;
	// 评论时间
	private String add_date;
    //针对评论者ID
	private String target_id;
	//针对评论者名字
	private String target_name; 
	
	public String getTarget_name() {
		return target_name;
	}

	public void setTarget_name(String target_name) {
		this.target_name = target_name;
	}

	public String getTarget_id() {
		return target_id;
	}

	public void setTarget_id(String target_id) {
		this.target_id = target_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getHead_sub_image() {
		return head_sub_image;
	}

	public void setHead_sub_image(String head_sub_image) {
		this.head_sub_image = head_sub_image;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public String getAdd_date() {
		return add_date;
	}

	public void setAdd_date(String add_date) {
		this.add_date = add_date;
	}
	public void setContentWithJson(JSONObject object) {
		if (object.containsKey("id")) {
			setId(object.getString("id"));
		}
		if (object.containsKey("name")) {
			setName(object.getString("name"));
		}
		if (object.containsKey("head_sub_image")) {
			setHead_sub_image(KHConst.ATTACHMENT_ADDR+object.getString("head_sub_image"));
		}
		
		if (object.containsKey("add_date")) {
			setAdd_date(object.getString("add_date"));
		}
		if (object.containsKey("user_id")) {
			setUser_id(object.getString("user_id"));
		}
		if (object.containsKey("comment_content")) {
			setComment_content(object.getString("comment_content"));
		}
		if (object.containsKey("job")) {
			setJob(object.getString("job"));
		}
		if (object.containsKey("target_id")) {
			setTarget_id(object.getString("target_id"));
		}
		if (object.containsKey("target_name")) {
			setTarget_name(object.getString("target_name"));
		}
	}


}
