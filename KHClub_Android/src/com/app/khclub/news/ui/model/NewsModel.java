package com.app.khclub.news.ui.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.base.model.ImageModel;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;

import android.util.Log;

public class NewsModel implements Serializable {

	/**
	 * 序列号（activity之间传递的时候使用）
	 */
	private static final long serialVersionUID = 1994327813985826490L;

	// 用户id
	private String uid;
	// 用户的名字
	private String userName;
	// 用户的公司
	private String userCompany;
	// 用户的职位
	private String userJob;
	// 用户的头像
	private String userHeadImage;
	// 用户的头像缩略图
	private String userHeadSubImage;
	// 动态的ID
	private String newsID;
	// 动态的文字内容
	private String newsContent;
	// 添加的图片列表
	private List<ImageModel> imageNewsList = new ArrayList<ImageModel>();
	// 发布的位置
	private String location;
	// 发布动态的时间戳
	private String timesTamp;
	// 发布的时间
	private String sendTime;
	// 动态的评论量
	private int commentQuantity;
	// 动态的点赞量
	private int likeQuantity;
	// 用户是否点赞
	private boolean isLike;

	// 内容注入
	@SuppressWarnings("unchecked")
	public void setContentWithJson(JSONObject object) {
		if (object.containsKey("user_id")) {
			setUid(object.getString("user_id"));
		}
		if (object.containsKey("name")) {
			setUserName(object.getString("name"));
		}
		if (object.containsKey("company_name")) {
			setUserCompany(object.getString("company_name"));
		}
		if (object.containsKey("job")) {
			setUserJob(object.getString("job"));
		}
		if (object.containsKey("head_image")) {
			setUserHeadImage(KHConst.ATTACHMENT_ADDR
					+ object.getString("head_image"));
		}
		if (object.containsKey("head_sub_image")) {
			setUserHeadSubImage(KHConst.ATTACHMENT_ADDR
					+ object.getString("head_sub_image"));
		}
		if (object.containsKey("id")) {
			setNewsID(object.getString("id"));
		}
		if (object.containsKey("content_text")) {
			setNewsContent(object.getString("content_text"));
		}
		if (object.containsKey("location")) {
			setLocation(object.getString("location"));
		}
		if (object.containsKey("comment_quantity")) {
			setCommentQuantity(object.getString("comment_quantity"));
		}
		if (object.containsKey("like_quantity")) {
			setLikeQuantity(object.getString("like_quantity"));
		}
		if (object.containsKey("add_date")) {
			setSendTime(object.getString("add_date"));
		}
		if (object.containsKey("is_like")) {
			setIsLike(object.getString("is_like"));
		}
		Log.i("wwww", "1111");
		// 图片的转换
		if (object.containsKey("images")) {
			List<JSONObject> JImageObj = (List<JSONObject>) object
					.get("images");
			List<ImageModel> imgList = new ArrayList<ImageModel>();
			for (JSONObject imgObject : JImageObj) {
				ImageModel imgTemp = new ImageModel();
				imgTemp.setContentWithJson(imgObject);
				imgList.add(imgTemp);
			}
			setImageNewsList(imgList);
		}

		if (object.containsKey("add_time")) {
			setTimesTamp(object.getString("add_time"));
		}
		
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserHeadImage() {
		return userHeadImage;
	}

	public void setUserHeadImage(String userHeadImage) {
		this.userHeadImage = userHeadImage;
	}

	public String getUserHeadSubImage() {
		return userHeadSubImage;
	}

	public void setUserHeadSubImage(String userHeadSubImage) {
		this.userHeadSubImage = userHeadSubImage;
	}

	public String getNewsID() {
		return newsID;
	}

	public void setNewsID(String newsID) {
		this.newsID = newsID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNewsContent() {
		return newsContent;
	}

	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}

	public int getCommentQuantity() {
		return commentQuantity;
	}

	public void setCommentQuantity(String commentQuantity) {
		this.commentQuantity = KHUtils.stringToInt(commentQuantity);
	}

	public int getLikeQuantity() {
		return likeQuantity;
	}

	public void setLikeQuantity(String likeQuantity) {
		this.likeQuantity = KHUtils.stringToInt(likeQuantity);
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public List<ImageModel> getImageNewsList() {
		return imageNewsList;
	}

	public void setImageNewsList(List<ImageModel> imageNewsList) {
		this.imageNewsList = imageNewsList;
	}

	public boolean getIsLike() {
		return isLike;
	}

	public void setIsLike(String isLike) {
		if (isLike.equals("1")) {
			this.isLike = true;
		} else if (isLike.equals("0")) {
			this.isLike = false;
		} else {
			LogUtils.e("点赞数据错误.");
		}
	}

	public String getTimesTamp() {
		return timesTamp;
	}

	public void setTimesTamp(String timesTamp) {
		this.timesTamp = timesTamp;
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
