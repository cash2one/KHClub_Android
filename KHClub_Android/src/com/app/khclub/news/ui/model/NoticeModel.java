package com.app.khclub.news.ui.model;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.utils.NewsOperate.LikeCallBack;
import com.app.khclub.news.ui.utils.NewsOperate.OperateCallBack;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.content.Context;

public class NoticeModel {
	//公告ID
	private String id;
	//用户ID
	private String user_id;
	//内容
	private String content_text;
	//评论时间
	private String add_date;
	//评论数
	private String comment_quantity;
	//点赞数量
	private String like_quantity;
	//是否已经点赞
	private String is_like;
	//记录上次操作
	private int lastOperateType = -1;
	//private OperateCallBack callInterface;
	// 点赞回调接口
	private LikeCallBack likeCallInterface;

	public final static int OP_Type_Like = 5;
	public final static int OP_Type_Like_cancel = 6;
	private int lastPostion = 0;
	//private  Context context;
	// 是否正在传输数据
	private boolean isUploadData = false;
	

//	public Context getContext() {
//		return context;
//	}
//
//	public void setContext(Context context) {
//		this.context = context;
//	}

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
	
	
	/**
	 * 点赞回调接口
	 * */
	public interface LikeCallBack {
		public void onLikeStart(boolean isLike);

		public void onLikeFail(boolean isLike);
	}
}
