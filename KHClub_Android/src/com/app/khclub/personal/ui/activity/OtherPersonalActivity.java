package com.app.khclub.personal.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.core.v;
import com.app.khclub.R;
import com.app.khclub.base.app.KHApplication;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.activity.AlertDialog;
import com.app.khclub.base.easeim.activity.ChatActivity;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
import com.app.khclub.base.easeim.db.InviteMessgeDao;
import com.app.khclub.base.easeim.db.UserDao;
import com.app.khclub.base.easeim.domain.User;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.contact.ui.activity.ShareContactsActivity;
import com.app.khclub.personal.ui.fragment.OtherPersonalInfoFragment;
import com.app.khclub.personal.ui.fragment.OtherPersonalQrcodeFragment;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu.BottomClickListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMMessage;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OtherPersonalActivity extends BaseActivityWithTopBar {

	public final static String INTENT_KEY = "uid";

	// 标题栏
	@ViewInject(R.id.title_bar)
	private RelativeLayout titleBar;
	// 页卡内容
	@ViewInject(R.id.vPager)
	private ViewPager mPager;
	// 签名
	@ViewInject(R.id.sign_text_view)
	private TextView signTextView;
	// 图片1
	@ViewInject(R.id.personal_picture_image_view1)
	private ImageView pictureImageView1;
	// 图片2
	@ViewInject(R.id.personal_picture_image_view2)
	private ImageView pictureImageView2;
	// 图片3
	@ViewInject(R.id.personal_picture_image_view3)
	private ImageView pictureImageView3;
	// 加好友或者发送消息按钮
	@ViewInject(R.id.add_send_btn)
	private Button addSendButton;
	// 前10张图片数组
	private List<String> newsImageList = new ArrayList<String>();
	// 控件数组
	private List<ImageView> imageList = new ArrayList<ImageView>();
	// 图片缓存工具
	private DisplayImageOptions imageOptions;
	// 个人信息fragment
	private OtherPersonalInfoFragment otherPersonalInfoFragment;
	// 二维码fragment
	private OtherPersonalQrcodeFragment otherPersonalQRFragment;
	// 用户ID
	private int uid;
	// 查看者的模型
	private UserModel otherUserModel;
	// 如果是好友的话备注
	private String remark = "";
	// 是否是好友
	private boolean isFriend;
	// 是否收藏
	private int isCollected = 0;
	// 私有dialog
	private ProgressDialog progressDialog;
	// 底部操作弹出菜单
	private PersonalBottomPopupMenu shareMenu;
	// imUser
	private User imUser;

	@OnClick({ R.id.image_cover_layout, R.id.add_send_btn })
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.image_cover_layout:
			// 跳转至动态列表
			Intent intentToNewsList = new Intent(OtherPersonalActivity.this,
					PersonalNewsActivity.class);
			intentToNewsList.putExtra(PersonalNewsActivity.INTNET_KEY_UID, uid);
			startActivityWithRight(intentToNewsList);
			break;
		case R.id.add_send_btn:
			if (isFriend) {
				// 进入聊天页面
				Intent intent = new Intent(this, ChatActivity.class).putExtra(
						"userId", KHConst.KH + uid);
				startActivityWithRight(intent);
			} else {
				addContact();
			}
			break;
		default:
			break;
		}

	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_other_personal;
	}

	@Override
	protected void setUpView() {
		imageList.add(pictureImageView1);
		imageList.add(pictureImageView2);
		imageList.add(pictureImageView3);
		imageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default)
				.cacheInMemory(false).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		initViewPager();
		Intent intent = getIntent();
		uid = intent.getIntExtra(INTENT_KEY, 0);
		if (uid == 0) {
			ToastUtil.show(this, R.string.personal_no_person);
			return;
		}

		// 是否是好友
		imUser = ((KHHXSDKHelper) KHHXSDKHelper.getInstance()).getContactList()
				.get(KHConst.KH + uid);
		if (imUser != null) {
			isFriend = true;
			addSendButton.setText(R.string.personal_send_message);
			shareMenu = new PersonalBottomPopupMenu(this, true);
		} else {
			isFriend = false;
			addSendButton.setText(R.string.personal_add_friend);
			shareMenu = new PersonalBottomPopupMenu(this, false);
		}

		//如果是本人，则隐藏添加按钮
		if (uid == UserManager.getInstance().getUser().getUid()) {
			addSendButton.setVisibility(View.GONE);
		}
		initPopupListener();

		getPersonalInformation();
	}

	/**
	 * 初始化操作菜单事件
	 */
	private void initPopupListener() {

		// 添加右边操作按钮
		ImageView rightBtn = addRightImgBtn(R.layout.right_image_button,
				R.id.layout_top_btn_root_view, R.id.img_btn_right_top);
		rightBtn.setImageResource(R.drawable.personal_more);
		rightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				shareMenu.showPopupWindow(titleBar);
			}
		});

		// 分享菜单的事件
		shareMenu.setListener(new BottomClickListener() {

			@Override
			public void shareToWeiboClick() {
				// 分享到微博
				ShareParams sp = new ShareParams();
				sp.setText(otherUserModel.getName());
				sp.setImageUrl(KHConst.ATTACHMENT_ADDR
						+ otherUserModel.getHead_sub_image());
				Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
				weibo.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				weibo.SSOSetting(true);
				// 执行图文分享
				weibo.share(sp);
			}

			@Override
			public void shareToWeChatClick() {
				// 分享到微信
				ShareParams sp = new ShareParams();
				sp.setText(otherUserModel.getName());
				sp.setImageUrl(KHConst.ATTACHMENT_ADDR
						+ otherUserModel.getHead_sub_image());
				Platform wexin = ShareSDK.getPlatform(Wechat.NAME);
				wexin.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				wexin.share(sp);
			}

			@Override
			public void shareToQzoneClick() {
				// 分享到qq空间
				ShareParams sp = new ShareParams();
				sp.setText(otherUserModel.getName());
				sp.setImageUrl(KHConst.ATTACHMENT_ADDR
						+ otherUserModel.getHead_sub_image());
				Platform wexin = ShareSDK.getPlatform(WechatMoments.NAME);
				wexin.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				wexin.share(sp);
			}

			@Override
			public void shareToQQFriendsClick() {
				// 分享给qq好友
				ShareParams sp = new ShareParams();
				sp.setTitle("KHClub");
				sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
				sp.setText(otherUserModel.getName());
				sp.setImageUrl(KHConst.ATTACHMENT_ADDR
						+ otherUserModel.getHead_sub_image());
				Platform qq = ShareSDK.getPlatform(QQ.NAME);
				qq.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				qq.share(sp);
			}

			@Override
			public void shareToFriendClick() {
				if (otherUserModel != null) {
					// 分享给好友
					JSONObject object = new JSONObject();
					// 单聊
					object.put("type", "" + EMMessage.ChatType.Chat.ordinal());
					object.put("id", KHConst.KH + otherUserModel.getUid());
					object.put("title", otherUserModel.getName());
					object.put("avatar", KHConst.ATTACHMENT_ADDR
							+ otherUserModel.getHead_sub_image());
					Intent intent = new Intent(OtherPersonalActivity.this,
							ShareContactsActivity.class);
					intent.putExtra(ShareContactsActivity.INTENT_CARD_KEY,
							object.toJSONString());
					startActivityWithRight(intent);
				}
			}

			@Override
			public void shareToCircleofFriendsClick() {
				// 分享到朋友圈
				ShareParams sp = new ShareParams();
				sp.setTitle("KHClub");
				sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
				sp.setText(otherUserModel.getName());
				sp.setImageUrl(KHConst.ATTACHMENT_ADDR
						+ otherUserModel.getHead_sub_image());
				Platform qq = ShareSDK.getPlatform(QZone.NAME);
				qq.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				qq.share(sp);
			}

			@Override
			public void editRemarkClick() {
				if (isFriend) {
					if (otherUserModel != null) {
						// 设置备注
						remarkUpdate();
					}
				}
			}

			@Override
			public void deleteFriendClick() {
				if (imUser != null) {
					// 删除好友
					deleteContact(imUser);
				}
			}

			@Override
			public void cancelClick() {
				// 取消操作
			}
		});
	}

	// 分享监听
	PlatformActionListener platformActionListener = new PlatformActionListener() {
		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			ToastUtil.show(OtherPersonalActivity.this,
					R.string.personal_share_fail);
		}

		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			// ToastUtil.show(getActivity(), R.string.personal_share_ok);
		}

		@Override
		public void onCancel(Platform arg0, int arg1) {
		}
	};

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mPager.setAdapter(new MessageFragmentPagerAdapter(
				getSupportFragmentManager()));
		mPager.setCurrentItem(0);
	}

	private class MessageFragmentPagerAdapter extends
			android.support.v4.app.FragmentPagerAdapter {

		public MessageFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = null;
			switch (i) {
			case 0:
				otherPersonalInfoFragment = new OtherPersonalInfoFragment();
				fragment = otherPersonalInfoFragment;
				break;
			case 1:
				otherPersonalQRFragment = new OtherPersonalQrcodeFragment();
				fragment = otherPersonalQRFragment;
				break;

			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

	// //////////////////private method////////////////////////
	// 获取用户信息
	private void getPersonalInformation() {

		String path = KHConst.PERSONAL_INFO + "?" + "uid=" + uid
				+ "&current_id=" + UserManager.getInstance().getUser().getUid();
		LogUtils.i(path, 0);
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							handleData(jResult);
						}

						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(OtherPersonalActivity.this,
									R.string.downloading_fail);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(OtherPersonalActivity.this,
								R.string.net_error);
					}

				}, null));
	}

	// 处理数据
	private void handleData(JSONObject jsonObject) {

		otherUserModel = new UserModel();
		otherUserModel.setContentWithJson(jsonObject);
		// 签名
		if (null != otherUserModel.getSignature()
				&& otherUserModel.getSignature().length() > 0) {
			signTextView.setText(otherUserModel.getName());
		} else {
			signTextView.setText(R.string.personal_none);
		}
		// 标题
		setBarText(otherUserModel.getName());

		newsImageList.clear();
		// 他的动态
		JSONArray imagesArray = jsonObject.getJSONArray("image_list");
		for (int i = 0; i < imagesArray.size(); i++) {
			JSONObject object = (JSONObject) imagesArray.get(i);
			newsImageList.add(object.getString("sub_url"));
		}
		// 最多3
		int size = newsImageList.size();
		if (size > 3) {
			size = 3;
		}
		// 设置不可见
		for (ImageView imageView : imageList) {
			imageView.setVisibility(View.INVISIBLE);
		}

		for (int i = 0; i < size; i++) {
			ImageView imageView = imageList.get(i);
			String path = newsImageList.get(i);
			// 设置图片
			if (null != path && path.length() > 0) {
				ImageLoader.getInstance()
						.displayImage(KHConst.ATTACHMENT_ADDR + path,
								imageView, imageOptions);
			} else {
				imageView.setImageResource(R.drawable.loading_default);
			}
			imageView.setVisibility(View.VISIBLE);
		}

		// 备注
		if (jsonObject.containsKey("remark")) {
			remark = jsonObject.getString("remark");
		}
		if (jsonObject.containsKey("isCollected")) {
			isCollected = jsonObject.getIntValue("isCollected");
		}

		if (otherPersonalInfoFragment != null) {
			otherPersonalInfoFragment.setUIWithModel(otherUserModel, isFriend,
					isCollected, remark);
			otherPersonalQRFragment.setQRcode(otherUserModel);
		} else {
		}

	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact() {

		final String targetIMID = KHConst.KH + uid;
		if (KHUtils.selfCommonIMID().equals(targetIMID)) {
			String str = getString(R.string.not_add_myself);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
					str));
			return;
		}
		if (((KHHXSDKHelper) HXSDKHelper.getInstance()).getContactList()
				.containsKey(targetIMID)) {
			// 提示已在好友列表中，无需添加
			if (EMContactManager.getInstance().getBlackListUsernames()
					.contains(targetIMID)) {
				startActivity(new Intent(this, AlertDialog.class).putExtra(
						"msg", R.string.im_in_black_list));
				return;
			}
			String strin = getString(R.string.This_user_is_already_your_friend);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
					strin));
			return;
		}

		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {

				try {
					// demo写死了个reason，实际应该让用户手动填入
					// 先就用DEMO这个死的
					String s = getResources().getString(R.string.Add_a_friend);
					EMContactManager.getInstance().addContact(targetIMID, s);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(
									R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, 1)
									.show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(
									R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(),
									s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 删除联系人
	 * 
	 * @param toDeleteUser
	 */
	public void deleteContact(final User tobeDeleteUser) {

		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("target_id", tobeDeleteUser.getUsername()
				.replace(KHConst.KH, ""));

		HttpManager.post(KHConst.DELETE_FRIEND, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							new Thread(new Runnable() {
								public void run() {
									try {
										EMContactManager.getInstance()
												.deleteContact(
														tobeDeleteUser
																.getUsername());
										// 删除db和内存中此用户的数据
										UserDao dao = new UserDao(
												OtherPersonalActivity.this);
										dao.deleteContact(tobeDeleteUser
												.getUsername());
										((KHHXSDKHelper) HXSDKHelper
												.getInstance())
												.getContactList().remove(
														tobeDeleteUser
																.getUsername());
										runOnUiThread(new Runnable() {
											public void run() {
												pd.dismiss();
												// UI修改
												shareMenu = new PersonalBottomPopupMenu(
														OtherPersonalActivity.this,
														false);
												initPopupListener();
												isFriend = false;
												addSendButton
														.setText(R.string.personal_add_friend);
											}
										});
										// 删除相关的邀请消息
										InviteMessgeDao msgDao = new InviteMessgeDao(
												OtherPersonalActivity.this);
										msgDao.deleteMessage(tobeDeleteUser
												.getUsername());

									} catch (final Exception e) {
										runOnUiThread(new Runnable() {
											public void run() {
												pd.dismiss();
												Toast.makeText(
														OtherPersonalActivity.this,
														st2 + e.getMessage(), 1)
														.show();
											}
										});
									}
								}
							}).start();
						} else {
							ToastUtil.show(OtherPersonalActivity.this,
									R.string.net_error);
							pd.dismiss();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(OtherPersonalActivity.this,
								R.string.net_error);
						pd.dismiss();
					}
				}, null));
	}

	// 备注点击
	private void remarkUpdate() {

		// dialog
		Builder nameAlertDialog = new Builder(this);
		LinearLayout textViewLayout = (LinearLayout) View.inflate(this,
				R.layout.dialog_text_view, null);
		nameAlertDialog.setView(textViewLayout);
		final EditText et_search = (EditText) textViewLayout
				.findViewById(R.id.name_edit_text);
		et_search.setHint(R.string.personal_enter_remark);
		TextView etTextView = (TextView) textViewLayout
				.findViewById(R.id.name_text_view);
		etTextView.setText(R.string.personal_remark);
		// 备注
		et_search.setText(remark);
		et_search.setSelection(et_search.getText().length());

		final Dialog dialog = nameAlertDialog.show();
		TextView cancelTextView = (TextView) textViewLayout
				.findViewById(R.id.tv_custom_alert_dialog_cancel);
		cancelTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		TextView confirmTextView = (TextView) textViewLayout
				.findViewById(R.id.tv_custom_alert_dialog_confirm);
		confirmTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String remark = et_search.getText().toString().trim();
				if (remark.length() > 64) {
					ToastUtil.show(OtherPersonalActivity.this,
							R.string.personal_remark_too_long);
					return;
				}
				addRemark(remark);
				dialog.dismiss();
			}
		});

	}

	// 上传信息
	private void addRemark(final String remark) {

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("user_id", UserManager.getInstance().getUser()
				.getUid()
				+ "");
		params.addBodyParameter("target_id", uid + "");
		params.addBodyParameter("friend_remark", remark);

		showLoading(getString(R.string.uploading), false);
		HttpManager.post(KHConst.ADD_REMARK, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {

						hideLoading();
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {

							OtherPersonalActivity.this.remark = remark;

							String secondName = "";
							if (remark.length() > 0) {
								// 设置名
								imUser.setNick(remark);
								secondName = otherUserModel.getName();
							} else {
								// 为空恢复
								imUser.setNick(otherUserModel.getName());
							}
							// 缓存
							UserDao userDao = new UserDao(KHApplication
									.getInstance());
							userDao.saveContact(imUser);
							// 更新UI
							otherPersonalInfoFragment.setNameTextView(
									imUser.getNick(), secondName);

						}
						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(OtherPersonalActivity.this,
									R.string.net_error);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(OtherPersonalActivity.this,
								R.string.net_error);
					}
				}, null));
	}

}
