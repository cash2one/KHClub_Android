package com.app.khclub.news.ui.model;

public class NoticeModel {
	private String id;
	private String user_id;
	private String content_text;
	private String add_date;
	private String comment_quantity;
	private String like_quantity;
	private String is_like;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getContent_text() {
		return content_text;
	}

	public void setContent_text(String content_text) {
		this.content_text = content_text;
	}

	public String getAdd_date() {
		return add_date;
	}

	public void setAdd_date(String add_date) {
		this.add_date = add_date;
	}

	public String getComment_quantity() {
		return comment_quantity;
	}

	public void setComment_quantity(String comment_quantity) {
		this.comment_quantity = comment_quantity;
	}

	public String getLike_quantity() {
		return like_quantity;
	}

	public void setLike_quantity(String like_quantity) {
		this.like_quantity = like_quantity;
	}

	public String getIs_like() {
		return is_like;
	}

	public void setIs_like(String is_like) {
		this.is_like = is_like;
	}

}
