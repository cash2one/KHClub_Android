package com.app.khclub.personal.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.activity.AlertDialog;
import com.app.khclub.base.easeim.activity.ChatActivity;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
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
import com.app.khclub.personal.ui.fragment.OtherPersonalInfoFragment;
import com.app.khclub.personal.ui.fragment.OtherPersonalQrcodeFragment;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu.BottomClickListener;
import com.easemob.chat.EMContactManager;
import com.lidroid.xutils.exception.HttpException;
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
	// 是否是好友
	private boolean isFriend;
	// 私有dialog
	private ProgressDialog progressDialog;
	// 底部操作弹出菜单
	private PersonalBottomPopupMenu shareMenu;

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

		getPersonalInformation();

		// 是否是好友
		User user = ((KHHXSDKHelper) KHHXSDKHelper.getInstance())
				.getContactList().get(KHConst.KH + uid);
		if (user != null) {
			isFriend = true;
			addSendButton.setText(R.string.personal_send_message);
			shareMenu = new PersonalBottomPopupMenu(this, true);
		} else {
			isFriend = false;
			addSendButton.setText(R.string.personal_add_friend);
			shareMenu = new PersonalBottomPopupMenu(this, false);
		}

		// 添加右边操作按钮
		ImageView rightBtn = addRightImgBtn(R.layout.right_image_button,
				R.id.layout_top_btn_root_view, R.id.img_btn_right_top);
		rightBtn.setImageResource(R.drawable.ic_launcher);
		rightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				shareMenu.showPopupWindow(titleBar);
			}
		});
		initPopupListener();
	}

	/**
	 * 初始化操作菜单事件
	 */
	private void initPopupListener() {
		// 分享菜单的事件
		shareMenu.setListener(new BottomClickListener() {

			@Override
			public void shareToWeiboClick() {
				// 分享到微博

			}

			@Override
			public void shareToWeChatClick() {
				//分享到微信

			}

			@Override
			public void shareToQzoneClick() {
				// 分享到qq空间

			}

			@Override
			public void shareToQQFriendsClick() {
				// 分享给qq好友

			}

			@Override
			public void shareToFriendClick() {
				// 分享给好友

			}

			@Override
			public void shareToCircleofFriendsClick() {
				//分享到朋友圈

			}

			@Override
			public void editRemarkClick() {
				// 设置备注

			}

			@Override
			public void deleteFriendClick() {
				// 删除好友

			}

			@Override
			public void cancelClick() {
				// 取消操作
			}
		});
	}

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

		UserModel otherUserModel = new UserModel();
		otherUserModel.setContentWithJson(jsonObject);
		// 签名
		if (null != otherUserModel.getSignature()
				&& otherUserModel.getSignature().length() > 0) {
			signTextView.setText(otherUserModel.getName());
		} else {
			signTextView.setText(R.string.personal_none);
		}
		otherPersonalInfoFragment.setUIWithModel(otherUserModel, isFriend,
				jsonObject.getIntValue("isCollected"));
		otherPersonalQRFragment.setQRcode(otherUserModel);
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

}
