package com.app.khclub.news.ui.model;

import com.alibaba.fastjson.JSONObject;

import android.util.Log;

public class NoticeDetailsModel {
	// 公告内容
	private String noticeContent;
	// 是否点赞
	private String isLike;
	// 发布时间
	private String time;
	// 公告ID
	private String id;
	// 圈子ID
	private String circleId;
	// 评论数量
	private String commentQuantity;
	// 点赞数量
	private String likeQuantity;
	// 关注数量
	private String browseQuantity;
	// 更新时间
	private String updateTime;
	// 用户ID
	private String userId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCircleId() {
		return circleId;
	}

	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}

	public String getCommentQuantity() {
		return commentQuantity;
	}

	public void setCommentQuantity(String commentQuantity) {
		this.commentQuantity = commentQuantity;
	}

	public String getLikeQuantity() {
		return likeQuantity;
	}

	public void setLikeQuantity(String likeQuantity) {
		this.likeQuantity = likeQuantity;
	}

	public String getBrowseQuantity() {
		return browseQuantity;
	}

	public void setBrowseQuantity(String browseQuantity) {
		this.browseQuantity = browseQuantity;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public String getIsLike() {
		return isLike;
	}

	public void setIsLike(String isLike) {
		this.isLike = isLike;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void JosntoNoticeDetails(JSONObject noticeObject) {
		if (noticeObject.containsKey("id")) {
			setId(noticeObject.getString("id"));
		}
		if (noticeObject.containsKey("add_date")) {
			setTime(noticeObject.getString("add_date"));
		}
		if (noticeObject.containsKey("is_like")) {
			setIsLike(noticeObject.getString("is_like"));
		}
		if (noticeObject.containsKey("content_text")) {
			setNoticeContent(noticeObject.getString("content_text"));
		}
		if (noticeObject.containsKey("circle_id")) {
			setCircleId(noticeObject.getString("circle_id"));
		}
		if (noticeObject.containsKey("comment_quantity")) {
			setCommentQuantity(noticeObject.getString("comment_quantity"));
		}
		if (noticeObject.containsKey("like_quantity")) {
			setLikeQuantity(noticeObject.getString("like_quantity"));
		}
		if (noticeObject.containsKey("update_date")) {
			setUpdateTime(noticeObject.getString("update_date"));
		}
		if (noticeObject.containsKey("browse_quantity")) {
			setBrowseQuantity(noticeObject.getString("browse_quantity"));
		}
		if (noticeObject.containsKey("user_id")) {
			setUserId(noticeObject.getString("user_id"));
		}
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
