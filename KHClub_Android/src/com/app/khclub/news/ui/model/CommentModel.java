package com.app.khclub.news.ui.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.base.utils.KHConst;

/**
 * 评论对象
 * */
public class CommentModel implements Serializable {

	private static final long serialVersionUID = -1450887079414216891L;
	// 评论的id
	private String commentID;
	// 评论者的名字
	private String publishName;
	// 评论者的id
	private String userId;
	// 评论者的头像全图
	private String headImage;
	// 评论者的头像缩略图
	private String headSubImage;
	// 发布的时间
	private String addDate;
	// 评论的内容
	private String commentContent;
	// 用户的职位
	private String userJob;
	// 评论针对的对象ID
	private String targetUserId;
	// 评论针对的对象的名字
	private String targetUserName;

	// 内容注入
	public void setContentWithJson(JSONObject object) {
		if (object.containsKey("id")) {
			setCommentID(object.getString("id"));
		}
		if (object.containsKey("name")) {
			setPublishName(object.getString("name"));
		}
		if (object.containsKey("head_image")) {
			setHeadImage(KHConst.ATTACHMENT_ADDR
					+ object.getString("head_image"));
		}
		if (object.containsKey("head_sub_image")) {
			setHeadSubImage(KHConst.ATTACHMENT_ADDR
					+ object.getString("head_sub_image"));
		}
		if (object.containsKey("add_date")) {
			setAddDate(object.getString("add_date"));
		}
		if (object.containsKey("user_id")) {
			setUserId(object.getString("user_id"));
		}
		if (object.containsKey("comment_content")) {
			setCommentContent(object.getString("comment_content"));
		}
		if (object.containsKey("job")) {
			setUserJob(object.getString("job"));
		}
		if (object.containsKey("target_id")) {
			setTargetUserId(object.getString("target_id"));
		}
		if (object.containsKey("target_name")) {
			setTargetUserName(object.getString("target_name"));
		}
	}

	public String getCommentID() {
		return commentID;
	}

	public void setCommentID(String commentID) {
		this.commentID = commentID;
	}

	public String getPublishName() {
		return publishName;
	}

	public void setPublishName(String publishName) {
		this.publishName = publishName;
	}

	public String getHeadImage() {
		return headImage;
	}

	public void setHeadImage(String headImage) {
		this.headImage = headImage;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public String getHeadSubImage() {
		return headSubImage;
	}

	public void setHeadSubImage(String headSubImage) {
		this.headSubImage = headSubImage;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getTargetUserId() {
		return targetUserId;
	}

	public void setTargetUserId(String userId) {
		this.targetUserId = userId;
	}

	public String getTargetUserName() {
		return targetUserName;
	}

	public void setTargetUserName(String userName) {
		this.targetUserName = userName;
	}

	public String getUserJob() {
		return userJob;
	}

	public void setUserJob(String job) {
		this.userJob = job;
	}
}
