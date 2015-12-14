package com.app.khclub.news.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R.string;
import com.app.khclub.base.model.ImageModel;
import com.app.khclub.base.utils.KHConst;

public class CirclePageModel {
	private String circleId;
	// 圈子ID
	private String userID;
	// 用户ID
	private String circleName;
	// 圈子名称
	private String circleDetail;
	// 圈子详情
	private String circleCoverImage;
	// 圈子头像
	private String circleCoverSubImage;
	// 圈子子头像
	private String PhoneNum;
	// 联系电话
	private String Adress;
	// 圈子地址
	// private List<ImageModel> imageNewsList = new ArrayList<ImageModel>();
	// 发布的位置
	private String WxNum;
	// 微信号
	private String WxqrCode;
	// 微信二维码
	private String userName;
	// 用户名
	private String followQuantity;
	// 关注量
	private String circleUrl;
	// 圈子网址
	private String isFollow;

	// 用户是否点赞
	// 内容注入
	@SuppressWarnings("unchecked")
	public void setContentWithJson(JSONObject object) {

		if (object.containsKey("circleId")) {
			setCircleId(object.getString("id"));
		}
		if (object.containsKey("name")) {
			setUserName(object.getString("name"));
		}
		if (object.containsKey("user_id")) {
			setUserID(object.getString("user_id"));
		}
		if (object.containsKey("circle_name")) {
			setCircleName(object.getString("circle_name"));
		}
		if (object.containsKey("circle_detail")) {
			setCircleDetail(object.getString("circle_detail"));
		}
		if (object.containsKey("circle_cover_image")) {
			setCircleCoverImage(KHConst.ATTACHMENT_ADDR + object.getString("circle_cover_image"));
		}
		if (object.containsKey("circle_cover_sub_image")) {
			setCircleCoverSubImage(KHConst.ATTACHMENT_ADDR + object.getString("circle_cover_sub_image"));
		}
		if (object.containsKey("phone_num")) {
			setPhoneNum(object.getString("phone_num"));
		}
		if (object.containsKey("address")) {
			setAdress(object.getString("address"));
		}
		if (object.containsKey("wx_num")) {
			setWxNum(object.getString("wx_num"));
		}
		if (object.containsKey("wx_qrcode")) {
			setWxqrCode(KHConst.ATTACHMENT_ADDR + object.getString("wx_qrcode"));
		}
		if (object.containsKey("follow_quantity")) {
			setFollowQuantity(object.getString("follow_quantity"));
		}
		if (object.containsKey("circle_web")) {
			setCircleUrl(object.getString("circle_web"));
		}
		if (object.containsKey("is_follow")) {
			setIsFollow(object.getString("is_follow"));
		}
	}

	public String getCircleId() {
		return circleId;
	}

	public void setCircleId(String circleId) {
		this.circleId = circleId;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getCircleName() {
		return circleName;
	}

	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}

	public String getCircleDetail() {
		return circleDetail;
	}

	public void setCircleDetail(String circleDetail) {
		this.circleDetail = circleDetail;
	}

	public String getCircleCoverImage() {
		return circleCoverImage;
	}

	public void setCircleCoverImage(String circleCoverImage) {
		this.circleCoverImage = circleCoverImage;
	}

	public String getCircleCoverSubImage() {
		return circleCoverSubImage;
	}

	public void setCircleCoverSubImage(String circleCoverSubImage) {
		this.circleCoverSubImage = circleCoverSubImage;
	}

	public String getPhoneNum() {
		return PhoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		PhoneNum = phoneNum;
	}

	public String getAdress() {
		return Adress;
	}

	public void setAdress(String adress) {
		Adress = adress;
	}

	public String getWxNum() {
		return WxNum;
	}

	public void setWxNum(String wxNum) {
		WxNum = wxNum;
	}

	public String getWxqrCode() {
		return WxqrCode;
	}

	public void setWxqrCode(String wxqrCode) {
		WxqrCode = wxqrCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFollowQuantity() {
		return followQuantity;
	}

	public void setFollowQuantity(String followQuantity) {
		this.followQuantity = followQuantity;
	}

	public String getCircleUrl() {
		return circleUrl;
	}

	public void setCircleUrl(String circleUrl) {
		this.circleUrl = circleUrl;
	}

	public String getIsFollow() {
		return isFollow;
	}

	public void setIsFollow(String isFollow) {
		this.isFollow = isFollow;
	}

}
