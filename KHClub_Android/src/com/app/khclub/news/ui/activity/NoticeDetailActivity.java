package com.app.khclub.news.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.core.r;
import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.adapter.MultiItemTypeSupport;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.ui.view.CustomListViewDialog;
import com.app.khclub.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.TimeHandle;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.activity.NoticeDetailActivity.ItemViewClick;
import com.app.khclub.news.ui.model.CommentModel;
import com.app.khclub.news.ui.model.LikeModel;
import com.app.khclub.news.ui.model.NewsConstants;
import com.app.khclub.news.ui.model.NewsItemModel;
import com.app.khclub.news.ui.model.NoticeDetailsModel;
import com.app.khclub.news.ui.model.NoticeLikesModel;
import com.app.khclub.news.ui.model.NoticeReplyModel;
import com.app.khclub.news.ui.utils.NewsOperate;
import com.app.khclub.news.ui.utils.NoticeOperate;
import com.app.khclub.news.ui.utils.NoticeOperate.OperateCallBack;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.sharesdk.framework.authorize.e;

public class NoticeDetailActivity extends BaseActivityWithTopBar implements OnClickListener {
	private static final String TARGET_NAME = "Target_name";
	private static final String COMMENT_CONTENT = "comment_content";
	private static final String USER_ID = "userID";
	private static final String JOB = "job";
	private static final String COMMENT_TIME = "commentTime";
	private static final String USER_IMAGE = "userImage";
	private static final String USERNAME = "username";
	private static final String COMMENTID = "commentid";
	private final static String TARGET_ID = "target_id";
	// 记录对动态的操作
	private String actionType = NewsConstants.OPERATE_NO_ACTION;
	private List<NoticeLikesModel> likeList;
	// 是发布评论还是回复
	private boolean isPublishComment = true;
	private boolean isCurrentShowComment = true;
	// 通知评论列表
	@ViewInject(R.id.notice_reply_listView)
	private PullToRefreshListView noticereplyListView;
	@ViewInject(R.id.btn_comment_send)
	Button sendButton;
	private MultiItemTypeSupport multiItemTypeSupport;
	// 符合显示格式的数据
	private List<Map<String, String>> dataList;
	// 加载图片
	private ImageLoader imgLoader;
	// 图片配置
	private DisplayImageOptions options;
	// 公告ID
	private String id;
	// 公告详情信息
	private NoticeDetailsModel detail;
	// 评论列表数据源
	// 评论输入框
	@ViewInject(R.id.edt_comment_input)
	private EditText commentEditText;
	private List<NoticeReplyModel> replydata;
	private HelloHaAdapter<Map<String, String>> listAdapter;
	// 当前操作的位置
	private int currentOperateIndex = -1;
	// 评论的内容
	private String commentContentStr = "";
	// 工具类
	private NoticeOperate noticeOperate;
	// 点击item监听对象
	private ItemViewClick itemViewClickListener;
	private TextView replyCounttv;
	private TextView likeCounttv;

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_notice_detail;
	}

	@Override
	protected void setUpView() {
		// 公告ID
		id = getIntent().getStringExtra(AnnouncementActivity.NOTICEID);
		String Uid = UserManager.getInstance().getUser().getUid() + "";
		// TODO Auto-generated method stub
		setBarText(getString(R.string.notice_details));
		// addRightImgBtn(R.id.layout_base_title,R.id.base_ll_right_btns,
		// R.drawable.share_btn_normal);
		imgLoader = ImageLoader.getInstance();
		// 显示图片的配置
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.color.main_light_white)
				.showImageOnFail(R.drawable.icon).cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		// 发送评论
		sendButton.setOnClickListener(this);
		// 右上角分享按钮
		final ImageView rightBtn = addRightImgBtn(R.layout.right_image_button, R.id.layout_top_btn_root_view,
				R.id.img_btn_right_top);
		rightBtn.setImageResource(R.drawable.personal_more);
		commentEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence str, int start, int before, int count) {
				commentContentStr = str.toString().trim();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		// 符合要求的数据源
		dataList = new ArrayList<Map<String, String>>();
		// 获取去数据
		getNoticeDetailData(Uid, id);
		// 初始化listview
		initListView();
		newsOperateSet();
	}

	/**
	 * 设置键盘状态打开或收起
	 */
	private void setKeyboardStatu(boolean state) {
		if (state) {
			InputMethodManager imm = (InputMethodManager) commentEditText.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		} else {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(NoticeDetailActivity.this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@SuppressWarnings("unchecked")
	private void initListView() {
		itemViewClickListener = new ItemViewClick();
		replydata = new ArrayList<NoticeReplyModel>();
		noticeOperate = new NoticeOperate(NoticeDetailActivity.this);
		listAdapter = new HelloHaAdapter<Map<String, String>>(NoticeDetailActivity.this,
				R.layout.activity_notice_list_item, dataList) {
			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, final Map<String, String> item) {
				// TODO Auto-generated method stub
				// 评论用户头像
				ImageView headImage = helper.getView(R.id.img_mian_notice_user_head);
				imgLoader.displayImage(item.get(USER_IMAGE), headImage, options);
				// Log.i("wx", KHConst.ATTACHMENT_ADDR + item.get(USER_IMAGE));
				if ("".equals(item.get(USERNAME))) {
					helper.setText(R.id.txt_notice_user_name, getString(R.string.personal_none));
				} else {
					helper.setText(R.id.txt_notice_user_name, item.get(USERNAME));
				}
				helper.setText(R.id.txt_main_notice_user_job, item.get(JOB));
				if (isCurrentShowComment) {
					helper.setText(R.id.txt_notice_reply_time,
							TimeHandle.getShowTimeFormat(item.get(COMMENT_TIME), getApplicationContext()));
					helper.setText(R.id.notice_comment_reply, item.get(COMMENT_CONTENT));
					TextView commentView = (TextView) helper.getView(R.id.notice_comment_reply);

					if ("".equals(item.get(TARGET_NAME))) {
						// 有回复的人时
						commentView.setText(item.get(COMMENT_CONTENT));
					} else {
						Log.i("wwww", 11111 + "");
						commentView.setText(getString(R.string.reply));
						SpannableString spStr = new SpannableString(item.get(TARGET_NAME) + " : ");

						spStr.setSpan(new ClickableSpan() {
							@Override
							public void updateDrawState(TextPaint ds) {
								// 设置文件颜色
								ds.setColor(getResources().getColor(R.color.main_light_blue));
								// 设置下划线
								ds.setUnderlineText(false);
							}

							@Override
							public void onClick(View widget) {
								LogUtils.i("TARGET_ID=" + item.get(TARGET_ID));
								// 跳转至用户的主页
								if (!item.get(TARGET_ID).equals("")) {
									// JumpToHomepage(KHUtils.stringToInt(item.get(TARGET_ID)));
								}
							}
						}, 0, item.get(USERNAME).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						// 设置点击后的颜色为透明，否则会一直出现高亮
						commentView.setHighlightColor(Color.TRANSPARENT);
						commentView.append(spStr);
						commentView.append(item.get(COMMENT_CONTENT));
						// 开始响应点击事件
						commentView.setMovementMethod(LinkMovementMethod.getInstance());
						// commentView.setText(item.get(COMMENT_CONTENT));
					}
				} else {
					helper.setVisible(R.id.txt_notice_reply_time, false);
					helper.setVisible(R.id.notice_comment_reply, false);
				}
				final int postion = helper.getPosition();
				OnClickListener listener = new OnClickListener() {

					@Override
					public void onClick(View view) {
						itemViewClickListener.onClick(view, postion, view.getId());
					}
				};
				helper.setOnClickListener(R.id.notice_commemt_root, listener);
				// helper.setOnClickListener(R.id.txt_news_reply_user_name,
				// listener);
				// helper.setOnClickListener(R.id.img_news_reply_user_head,
				// listener);

			}
		};
		noticereplyListView.setAdapter(listAdapter);
	}

	private interface ListItemClickHelp {
		void onClick(View view, int postion, int viewID);
	}

	public class ItemViewClick implements ListItemClickHelp {

		@Override
		public void onClick(View view, int postion, int viewID) {
			currentOperateIndex = postion;

			if (isCurrentShowComment) {
				Log.i("wx", postion + "");
				// 在显示评论的情况下
				if (viewID == R.id.notice_commemt_root) {
					if (replydata.get(currentOperateIndex).getUser_id()
							.equals(String.valueOf(UserManager.getInstance().getUser().getUid()))) {
						// 如果是自己发布的评论，则删除评论
						List<String> menuList = new ArrayList<String>();
						menuList.add(getString(R.string.delete_comment));
						final CustomListViewDialog downDialog = new CustomListViewDialog(NoticeDetailActivity.this,
								menuList);
						downDialog.setClickCallBack(new ClickCallBack() {

							@Override
							public void Onclick(View view, int which) {
								noticeOperate.deleteComment(replydata.get(currentOperateIndex).getId(), id);
								downDialog.cancel();
							}
						});
						downDialog.show();
					} else {
						// 发布回复别人的评论
						commentEditText.requestFocus();
						commentEditText.setText("");
						commentEditText.setHint(
								getString(R.string.reply) + ":" + replydata.get(currentOperateIndex).getName());
						isPublishComment = false;
						// 显示键盘
						setKeyboardStatu(true);
					}
				} else {
					// 跳转至别人主页
					// JumpToHomepage(KHUtils.stringToInt(replydata.get(currentOperateIndex).getUser_id()));
				}
			} else {
				// 显示点赞列表的情况下 跳转至别人主页
				// JumpToHomepage(KHUtils.stringToInt(likeList.get(currentOperateIndex).getUser_id()));
			}
		}
	}

	private void sendComment() {
		if (commentContentStr.length() > 0) {
			String tempContent = commentContentStr;
			// 清空输入内容
			commentEditText.setText("");
			commentEditText.setHint(getString(R.string.news_enter_comment_content));
			// 隐藏输入键盘
			setKeyboardStatu(false);

			// 发布评论
			NoticeReplyModel temMode = new NoticeReplyModel();
			temMode.setComment_content(tempContent);
			temMode.setAdd_date(TimeHandle.getCurrentDataStr());
			if (isPublishComment) {
				// 发布评论
				noticeOperate.publishComment(UserManager.getInstance().getUser(), id, tempContent);
				currentOperateIndex = -1;
			} else {
				// 发布回复
				noticeOperate.publishComment(UserManager.getInstance().getUser(), id, tempContent,
						replydata.get(currentOperateIndex).getUser_id(), replydata.get(currentOperateIndex).getName());
			}
			isPublishComment = true;
		}
	}

	private void listViewHeadInit() {
		View headView = LayoutInflater.from(this).inflate(R.layout.activity_notice_detail_head, null);
		noticereplyListView.getRefreshableView().addHeaderView(headView);
		ImageView likeImage = (ImageView) headView.findViewById(R.id.like_notice);
		TextView contenttv = (TextView) headView.findViewById(R.id.notice_content);
		TextView datetv = (TextView) headView.findViewById(R.id.notice_publish_date);
		likeCounttv = (TextView) headView.findViewById(R.id.btn_notice_like);
		replyCounttv = (TextView) headView.findViewById(R.id.btn_mian_reply);
		if ("0".equals(detail.getIsLike())) {
			likeImage.setVisibility(View.VISIBLE);
			likeImage.setImageResource(R.drawable.like_btn_normal);
		} else {
			likeImage.setVisibility(View.VISIBLE);
			likeImage.setImageResource(R.drawable.like_btn_press);
		}
		contenttv.setText(detail.getNoticeContent());
		datetv.setText(TimeHandle.getShowTimeFormat(detail.getTime(), getApplicationContext()));
		likeCounttv.setText("赞" + detail.getLikeQuantity());
		replyCounttv.setText("评论" + detail.getCommentQuantity());
		likeCounttv.setOnClickListener(this);
		replyCounttv.setOnClickListener(this);
	}

	protected void setReply(HelloHaBaseAdapterHelper helper, Map<String, String> item) {
		// TODO Auto-generated method stub

	}

	/*
	 * protected void setHeaderData(HelloHaBaseAdapterHelper helper) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 */

	@SuppressWarnings("rawtypes")
	private void multiItemTypeSet() {
		multiItemTypeSupport = new MultiItemTypeSupport() {
			// @Override
			// public int getLayoutId1(int position, Object t) {
			// int layoutId = 0;
			// if (position == 0) {
			// layoutId = R.layout.activity_notice_list_item;
			// } else {
			// layoutId = R.layout.activity_notice_detail_head;
			// }
			// return layoutId;
			// }

			@Override
			public int getViewTypeCount() {
				return 2;
			}

			@Override
			public int getLayoutId(int position, Object t) {
				// TODO Auto-generated method stub
				int layoutId = 0;
				if (position == 0) {
					layoutId = R.layout.activity_notice_list_item;
				} else {
					layoutId = R.layout.activity_notice_detail_head;
				}
				return layoutId;
			}

			@Override
			public int getItemViewType(int postion, Object t) {
				// TODO Auto-generated method stub
				if (postion == 0) {
					return 1;
				} else {
					return 0;
				}
			}
		};
	}

	/**
	 * 显示评论数据
	 */
	private void showCommentData() {
		dataList.clear();
		for (int index = 0; index < replydata.size(); index++) {
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put(COMMENTID, replydata.get(index).getId());
			tempMap.put(USERNAME, replydata.get(index).getName());
			tempMap.put(USER_IMAGE, replydata.get(index).getHead_sub_image());
			tempMap.put(COMMENT_TIME, replydata.get(index).getAdd_date());
			tempMap.put(JOB, replydata.get(index).getJob());
			tempMap.put(USER_ID, replydata.get(index).getUser_id());
			tempMap.put(COMMENT_CONTENT, replydata.get(index).getComment_content());
			tempMap.put(TARGET_ID, replydata.get(index).getTarget_id());
			tempMap.put(TARGET_NAME, replydata.get(index).getTarget_name());
			dataList.add(tempMap);
			// isCurrentShowComment = true;
			// Log.i("wx", 1111 + "");
		}
		isCurrentShowComment = true;
		listAdapter.replaceAll(dataList);
	}

	/**
	 * 显示点赞数据
	 */
	private void showLikeData() {
		dataList.clear();
		for (int index = 0; index < likeList.size(); index++) {
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put(USER_IMAGE, likeList.get(index).getHead_sub_image());
			tempMap.put(USERNAME, likeList.get(index).getName());
			tempMap.put(JOB, likeList.get(index).getJob());
			tempMap.put(USER_ID, likeList.get(index).getUser_id());
			dataList.add(tempMap);
			// isCurrentShowComment = false;
			// Log.i("wx", 1 + "");
		}
		isCurrentShowComment = false;
		listAdapter.replaceAll(dataList);
	}

	private void getNoticeDetailData(String uid, String noticeId) {
		String path = KHConst.GET_NOTICE_DETAILS + "?" + "id=" + noticeId + "&user_id=" + uid;
		// Log.i("wx", path);
		HttpManager.get(path, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
				if (status == KHConst.STATUS_SUCCESS) {
					JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					JSONObject noticeObject = jResult.getJSONObject("notice");
					// 公告头部数据
					// Log.i("wx", noticeObject.toJSONString());
					getNotice(noticeObject);
					// 评论数据
					JSONArray commentList = jResult.getJSONArray("commentList");
					// 点赞数据list
					String Likelist = jResult.getString("likes");
					for (int i = 0; i < commentList.size(); i++) {
						NoticeReplyModel cmtTemp = new NoticeReplyModel();
						cmtTemp.setContentWithJson(commentList.getJSONObject(i));
						replydata.add(cmtTemp);
					}
					// replydata = JSON.parseArray(commentList,
					// NoticeReplyModel.class);
					likeList = JSON.parseArray(Likelist, NoticeLikesModel.class);
					// Log.i("wx", likeList.get(0).getName());
					showCommentData();
				}

				if (status == KHConst.STATUS_FAIL) {
					ToastUtil.show(NoticeDetailActivity.this, jsonResponse.getString(KHConst.HTTP_MESSAGE));
					// noticereplyListView.onRefreshComplete();
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
				// noticereplyListView.onRefreshComplete();
				ToastUtil.show(NoticeDetailActivity.this, getString(R.string.network_unavailable));
			}

		}, null));

	}

	/**
	 * 动态操作设置
	 */
	private void newsOperateSet() {
		noticeOperate.setOperateListener(new OperateCallBack() {
			@Override
			public void onStart(int operateType) {
				switch (operateType) {
				case NewsOperate.OP_Type_Delete_News:
					// 删除动态
					break;

				case NewsOperate.OP_Type_Add_Comment:
					showLoading(getResources().getString(R.string.news_publish_commenting), true);
					break;

				case NewsOperate.OP_Type_Delete_Comment:
					break;

				default:
					break;
				}
			}

			@Override
			public void onFinish(int operateType, boolean isSucceed, Object resultValue) {
				actionType = NewsConstants.OPERATE_UPDATE;
				switch (operateType) {
				case NoticeOperate.OP_Type_Delete_News:
					if (isSucceed) {
						ToastUtil.show(NoticeDetailActivity.this,
								getResources().getString(R.string.news_delete_success));
						// 返回上一页
						actionType = NewsConstants.OPERATE_DELETET;
						finishWithRight();
					} else {
						ToastUtil.show(NoticeDetailActivity.this, getResources().getString(R.string.news_delete_fail));
					}
					break;

				case NoticeOperate.OP_Type_Add_Comment:
					if (isSucceed) {
						if (isCurrentShowComment) {
							// 发布成功,并且在显示评论列表的情况下 则更新评论列表
							NoticeReplyModel resultmModel = (NoticeReplyModel) resultValue;
							// 返回来的评论对象
							Map<String, String> tempMap = new HashMap<String, String>();
							tempMap.put(USER_IMAGE, resultmModel.getHead_sub_image());
							tempMap.put(USERNAME, resultmModel.getName());
							tempMap.put(COMMENT_TIME, resultmModel.getAdd_date());
							tempMap.put(JOB, resultmModel.getJob());
							tempMap.put(COMMENT_CONTENT, resultmModel.getComment_content());
							tempMap.put(USER_ID, resultmModel.getUser_id());
							tempMap.put(COMMENTID, resultmModel.getId());
							// 更新数据
							listAdapter.add(tempMap);
							replydata.add(resultmModel);
							// 滚动到底部
							noticereplyListView.getRefreshableView().setSelection(listAdapter.getCount() - 1);
							// 更新数据
							detail.setCommentQuantity(
									String.valueOf(Integer.parseInt(detail.getCommentQuantity()) + 1));
							replyCounttv.setText(getResources().getString(R.string.news_comment) + " "
									+ detail.getCommentQuantity());
						}
					} else {
						ToastUtil.show(NoticeDetailActivity.this, getResources().getString(R.string.news_publish_fail));
					}
					hideLoading();
					break;

				case NoticeOperate.OP_Type_Delete_Comment:
					if (isSucceed) {
						// 删除评论
						listAdapter.remove(currentOperateIndex);
						ToastUtil.show(NoticeDetailActivity.this,
								getResources().getString(R.string.news_delete_success));
						detail.setCommentQuantity(String.valueOf(Integer.parseInt(detail.getCommentQuantity()) - 1));
						replyCounttv.setText(
								getResources().getString(R.string.news_comment) + " " + detail.getCommentQuantity());
					} else {
						ToastUtil.show(NoticeDetailActivity.this, getResources().getString(R.string.news_delete_fail));
					}
					break;

				default:
					break;
				}
			}
		});
	}

	protected void getNotice(JSONObject noticeObject) {
		detail = new NoticeDetailsModel();
		detail.JosntoNoticeDetails(noticeObject);
		listViewHeadInit();

	}

	/**
	 * 跳转至用户的主页
	 */
	private void JumpToHomepage(int userID) {
		Intent intentUsrMain = new Intent(NoticeDetailActivity.this, OtherPersonalActivity.class);
		intentUsrMain.putExtra(OtherPersonalActivity.INTENT_KEY, userID);
		startActivityWithRight(intentUsrMain);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_notice_like:
			showLikeData();
			break;
		case R.id.btn_mian_reply:
			showCommentData();
			break;
		case R.id.btn_comment_send:
			sendComment();
			break;
		default:
			break;
		}

	}
}
