package com.app.khclub.news.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.app.khclub.base.model.ImageModel;
import com.app.khclub.base.utils.LogUtils;

/**
 * 将每条动态拆分成不同的几个模块达到滑动优化的效果
 * */
public class NewsItemModel {

	// 动态item的种类数
	public static final int NEWS_ITEM_TYPE_COUNT = 3;
	// 表示动态各item类型
	public static final int NEWS_TITLE = 0;
	public static final int NEWS_BODY = 1;
	public static final int NEWS_OPERATE = 2;

	// 动态的id
	private String newsID = "";
	// 当前的item类型
	private int itemType;

	public int getItemType() {
		return itemType;
	}

	/**
	 * 设置item的类型
	 * */
	public void setItemType(int type) {
		switch (type) {
		case 0:
		case 1:
		case 2:
			itemType = type;
			break;

		default:
			LogUtils.e("news items type error");
			break;
		}
	}

	public String getNewsID() {
		return newsID;
	}

	public void setNewsID(String newsID) {
		this.newsID = newsID;
	}

	/**
	 * 动态的头部
	 * */
	public static class TitleItem extends NewsItemModel {

		// 用户的id
		private String userID;
		// 动态发布者的头像缩略图
		private String headSubImage;
		// 动态发布者的头像
		private String headImage;
		// 动态发布者的名字
		private String userName;
		// 公司
		private String userCompany;
		// 职位
		private String userJob;

		public String getHeadSubImage() {
			return headSubImage;
		}

		public void setHeadSubImage(String headSubImage) {
			this.headSubImage = headSubImage;
		}

		public String getHeadImage() {
			return headImage;
		}

		public void setHeadImage(String userHeadImage) {
			this.headImage = userHeadImage;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userNameStr) {
			this.userName = userNameStr;
		}

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public String getUserCompany() {
			return userCompany;
		}

		public void setUserCompany(String userCompany) {
			this.userCompany = userCompany;
		}

		public String getUserJob() {
			return userJob;
		}

		public void setUserJob(String userJob) {
			this.userJob = userJob;
		}
	}

	/**
	 * 动态的内容主体
	 * */
	public static class BodyItem extends NewsItemModel {

		// 动态的文字内容
		private String newsContent;
		// 动态的图片列表
		private List<ImageModel> newsImageList = new ArrayList<ImageModel>();
		// 发布的位置
		private String location;
		// 发布的圈子
		private String circles;		

		public String getNewsContent() {
			return newsContent;
		}

		public void setNewsContent(String newsContent) {
			this.newsContent = newsContent;
		}

		public List<ImageModel> getNewsImageListList() {
			return newsImageList;
		}

		public void setImageNewsList(List<ImageModel> imageList) {
			this.newsImageList = imageList;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getCircles() {
			return circles;
		}

		public void setCircles(String circles) {
			this.circles = circles;
		}
		
	}

	/**
	 * 动态的操作部分
	 * */
	public static class OperateItem extends NewsItemModel {

		// 是否已赞
		private boolean isLike = false;
		// 点赞数
		private int likeCount;
		// 评论数
		private int commentCount;
		// 发布的时间
		private String sendTime;

		public boolean getIsLike() {
			return isLike;
		}

		public int getLikeCount() {
			return likeCount;
		}

		public void setLikeCount(int likeCount) {
			this.likeCount = likeCount;
		}

		public void setIsLike(Boolean isLike) {
			this.isLike = isLike;
		}

		public String getSendTime() {
			return sendTime;
		}

		public void setSendTime(String sendTime) {
			this.sendTime = sendTime;
		}

		public int getCommentCount() {
			return commentCount;
		}

		public void setCommentCount(String commentCount) {
			try {
				this.commentCount = Integer.parseInt(commentCount);
			} catch (Exception e) {
				LogUtils.e("评论数据格式错误.");
			}
		}
	}
}
