package com.app.khclub.personal.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.easeim.activity.ChatActivity;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.contact.ui.activity.ShareContactsActivity;
import com.app.khclub.personal.ui.activity.CardActivity;
import com.app.khclub.personal.ui.activity.MyCircleActivity;
import com.app.khclub.personal.ui.activity.PersonalNewsActivity;
import com.app.khclub.personal.ui.activity.PersonalSettingActivity;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu.BottomClickListener;
import com.easemob.chat.EMMessage;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalFragment extends BaseFragment {

	// 头像
	@ViewInject(R.id.head_image_view)
	private ImageView headImageView;
	// 姓名
	@ViewInject(R.id.name_text_view)
	private TextView nameTextView;
	// 职业
	@ViewInject(R.id.job_text_view)
	private TextView jobTextView;
	// 公司
	@ViewInject(R.id.company_text_view)
	private TextView companyTextView;
	// 电话
	@ViewInject(R.id.phone_number_text_view)
	private TextView phoneTextView;
	// 邮箱
	@ViewInject(R.id.email_text_view)
	private TextView emailTextView;
	// 邮箱
	@ViewInject(R.id.address_text_view)
	private TextView addressTextView;
	//二维码
	@ViewInject(R.id.qr_code_image_view)
	private ImageView qrcodeImageView;
	//名片背景
	@ViewInject(R.id.card_layout)
	private LinearLayout cardLayout;	
	// 用户
	private UserModel userModel;
	// 新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	
	// 页卡内容
//	@ViewInject(R.id.vPager)
//	private ViewPager mPager;
	// 签名
	@ViewInject(R.id.sign_text_view)
	private TextView signTextView;
	// 没有动态提示
	@ViewInject(R.id.no_moment_text_view)
	private TextView noMomentTextView;
	// 图片1
	@ViewInject(R.id.personal_picture_image_view1)
	private ImageView pictureImageView1;
	// 图片2
	@ViewInject(R.id.personal_picture_image_view2)
	private ImageView pictureImageView2;
	// 图片3
	@ViewInject(R.id.personal_picture_image_view3)
	private ImageView pictureImageView3;
	// 我的圈子1
	@ViewInject(R.id.personal_mycircle_image_view1)
	private ImageView myCircleImageView1;
	// 我的圈子2
	@ViewInject(R.id.personal_mycircle_image_view2)
	private ImageView myCircleImageView2;
	// 我的圈子3
	@ViewInject(R.id.personal_mycircle_image_view3)
	private ImageView myCircleImageView3;
	// 操作菜单
	@ViewInject(R.id.btn_more_operate)
	private ImageButton operateButton;
	// 前10张图片数组
	private List<String> newsImageList = new ArrayList<String>();
	//获取我的圈子头像
	private List<String> myCircleImageList = new ArrayList<String>();
	// 控件数组
	private List<ImageView> imageList = new ArrayList<ImageView>();
	//我的圈子数组
	private List<ImageView> myCircleList = new ArrayList<ImageView>();
	// 图片缓存工具
	private DisplayImageOptions imageOptions;
	// 操作菜单
//	private PersonalPopupMenu popupMenu;
	// 分享弹出菜单
	private PersonalBottomPopupMenu shareMenu;

	@OnClick({ R.id.base_tv_back, R.id.image_cover_layout,
		R.id.btn_more_operate, R.id.robot_cover_layout,
		R.id.card_layout,R.id.image_mycircle_layout})
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.base_tv_back:
			Intent intent = new Intent(getActivity(),
					PersonalSettingActivity.class);
			startActivityWithRight(intent);
			break;
		case R.id.image_cover_layout:
			// 跳转至动态列表
			Intent intentToNewsList = new Intent(this.getActivity(),
					PersonalNewsActivity.class);
			intentToNewsList.putExtra(PersonalNewsActivity.INTNET_KEY_UID,
					UserManager.getInstance().getUser().getUid());
			startActivityWithRight(intentToNewsList);
			break;
		case R.id.image_mycircle_layout:
			// 跳转至我的圈子
			Intent myCircleIntent = new Intent(this.getActivity(),
					MyCircleActivity.class);
//			intentToNewsList.putExtra(PersonalNewsActivity.INTNET_KEY_UID,
//					UserManager.getInstance().getUser().getUid());
			startActivityWithRight(myCircleIntent);
			break;
		case R.id.btn_more_operate:
			// 操作菜单
//			popupMenu.showPopupWindow(operateButton);
			// 分享点击
			shareMenu.showPopupWindow(operateButton);
			break;
		case R.id.robot_cover_layout:
			//商务管家
//			startActivityWithRight(new Intent(getActivity(),RobotsActivity.class));
		{
			Intent roIntent = new Intent();
			roIntent.setClass(getActivity(), ChatActivity.class);
			roIntent.putExtra("userId", KHConst.KH_ROBOT);
			startActivity(roIntent);
		}
			break;
		case R.id.card_layout:
			//名片点击 该功能取消
//			Intent cardIntent = new Intent(getActivity(), CardActivity.class);
//			cardIntent.putExtra(CardActivity.INTENT_IS_SELF_KEY, true);
//			startActivityWithRight(cardIntent);
			break;
		default:
			break;
		}

	}

	@Override
	public int setLayoutId() {
		return R.layout.fragment_main_personal;
	}

	@Override
	public void loadLayout(View rootView) {
		imageList.add(pictureImageView1);
		imageList.add(pictureImageView2);
		imageList.add(pictureImageView3);
		myCircleList.add(myCircleImageView1);
		myCircleList.add(myCircleImageView2);
		myCircleList.add(myCircleImageView3);
		imageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default)
				.cacheInMemory(false).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public void setUpViews(View rootView) {
		
		// 显示头像的配置
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		
//		initViewPager();
		// 操作菜单监听
//		popupMenu = new PersonalPopupMenu(getActivity());
		shareMenu = new PersonalBottomPopupMenu(getActivity(), false);
//		popupMenu.setListener(new OperateListener() {
//
//			@Override
//			public void shareClick() {
//				// 分享点击
//				shareMenu.showPopupWindow(operateButton);
//			}
//
//			@Override
//			public void switchClick() {
//				// 切换名片样式
//				int style = ConfigUtils.getIntConfig(ConfigUtils.CARD_CONFIG);
//				if (style == ConfigUtils.CARD_TWO) {
//					 //加载课程成功后更新界面
//					ConfigUtils.saveConfig(ConfigUtils.CARD_CONFIG, ConfigUtils.CARD_ONE);
//				}else {
//					ConfigUtils.saveConfig(ConfigUtils.CARD_CONFIG, ConfigUtils.CARD_TWO);
//				}
//				initViewPager();
//			}
//		});

		// 分享菜单的事件
		shareMenu.setListener(new BottomClickListener() {

			@Override
			public void shareToWeiboClick() {
				// 分享到微博
				ShareParams sp = new ShareParams();
				sp.setTitle(getString(R.string.exchange_card));
				sp.setUrl(KHConst.SHARE_CARD_WEB+"?user_id="+UserManager.getInstance().getUser().getUid()); // 标题的超链接
				sp.setShareType(Platform.SHARE_WEBPAGE);
//				sp.setVenueName("KHClub");
//				sp.setTitleUrl(KHConst.SHARE_CARD_WEB+"?user_id="+UserManager.getInstance().getUser().getUid());
				sp.setText(UserManager.getInstance().getUser().getName());
				if (UserManager.getInstance().getUser().getName() == null || UserManager.getInstance().getUser().getName().length() < 1) {
					sp.setText("KHClub");
				}
				sp.setText(sp.getText()+"|"+UserManager.getInstance().getUser().getJob()+"\n"+UserManager.getInstance().getUser().getCompany_name());
				if (null != UserManager.getInstance().getUser().getHead_sub_image() && UserManager.getInstance().getUser().getHead_sub_image().length()>0) {
					sp.setImageUrl(KHConst.ATTACHMENT_ADDR+UserManager.getInstance().getUser().getHead_sub_image());	
				}else {
					sp.setImageUrl(KHConst.ROOT_IMG);	
				}
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
				sp.setTitle(getString(R.string.exchange_card));
				sp.setUrl(KHConst.SHARE_CARD_WEB+"?user_id="+UserManager.getInstance().getUser().getUid()); // 标题的超链接
				sp.setShareType(Platform.SHARE_WEBPAGE);
				sp.setTitleUrl(KHConst.SHARE_CARD_WEB+"?user_id="+UserManager.getInstance().getUser().getUid()); // 标题的超链接
				sp.setText(UserManager.getInstance().getUser().getName());
				if (UserManager.getInstance().getUser().getName() == null || UserManager.getInstance().getUser().getName().length() < 1) {
					sp.setText("KHClub");
				}
				sp.setText(sp.getText()+"|"+UserManager.getInstance().getUser().getJob()+"\n"+UserManager.getInstance().getUser().getCompany_name());
				if (null != UserManager.getInstance().getUser().getHead_sub_image() && UserManager.getInstance().getUser().getHead_sub_image().length()>0) {
					sp.setImageUrl(KHConst.ATTACHMENT_ADDR+UserManager.getInstance().getUser().getHead_sub_image());	
				}else {
					sp.setImageUrl(KHConst.ROOT_IMG);	
				}
				Platform wexin = ShareSDK.getPlatform(Wechat.NAME);
				wexin.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				wexin.share(sp);
			}

			@Override
			public void shareToQzoneClick() {
				// 分享到朋友圈
				ShareParams sp = new ShareParams();
				sp.setTitle(getString(R.string.exchange_card));
				sp.setTitleUrl(KHConst.SHARE_CARD_WEB+"?user_id="+UserManager.getInstance().getUser().getUid()); // 标题的超链接
				sp.setText(UserManager.getInstance().getUser().getName());
				if (UserManager.getInstance().getUser().getName() == null || UserManager.getInstance().getUser().getName().length() < 1) {
					sp.setText("KHClub");
				}
				sp.setText(sp.getText()+"|"+UserManager.getInstance().getUser().getJob()+"\n"+UserManager.getInstance().getUser().getCompany_name());
				if (null != UserManager.getInstance().getUser().getHead_sub_image() && UserManager.getInstance().getUser().getHead_sub_image().length()>0) {
					sp.setImageUrl(KHConst.ATTACHMENT_ADDR+UserManager.getInstance().getUser().getHead_sub_image());	
				}else {
					sp.setImageUrl(KHConst.ROOT_IMG);	
				}
				Platform qq = ShareSDK.getPlatform(QZone.NAME);
				qq.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				qq.share(sp);
			}

			@Override
			public void shareToQQFriendsClick() {
				// 分享给qq好友
				ShareParams sp = new ShareParams();
				sp.setTitle(getString(R.string.exchange_card));
				sp.setTitleUrl(KHConst.SHARE_CARD_WEB+"?user_id="+UserManager.getInstance().getUser().getUid()); // 标题的超链接
				sp.setText(UserManager.getInstance().getUser().getName());
				if (UserManager.getInstance().getUser().getName() == null || UserManager.getInstance().getUser().getName().length() < 1) {
					sp.setText("KHClub");
				}
				sp.setText(sp.getText()+"|"+UserManager.getInstance().getUser().getJob()+"\n"+UserManager.getInstance().getUser().getCompany_name());
				if (null != UserManager.getInstance().getUser().getHead_sub_image() && UserManager.getInstance().getUser().getHead_sub_image().length()>0) {
					sp.setImageUrl(KHConst.ATTACHMENT_ADDR+UserManager.getInstance().getUser().getHead_sub_image());	
				}else {
					sp.setImageUrl(KHConst.ROOT_IMG);	
				}
				Platform qq = ShareSDK.getPlatform(QQ.NAME);
				qq.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				qq.share(sp);
			}

			@Override
			public void shareToFriendClick() {
				// 分享给好友
				JSONObject object = new JSONObject();
				//单聊
				object.put("type", ""+EMMessage.ChatType.Chat.ordinal());
				object.put("id", KHUtils.selfCommonIMID());
				object.put("title",UserManager.getInstance().getUser().getName());
				object.put("avatar", KHConst.ATTACHMENT_ADDR+UserManager.getInstance().getUser().getHead_sub_image());
				Intent intent = new Intent(getActivity(), ShareContactsActivity.class);
				intent.putExtra(ShareContactsActivity.INTENT_CARD_KEY, object.toJSONString());
				startActivityWithRight(intent);
			}

			@Override
			public void shareToCircleofFriendsClick() {
				// 分享到朋友圈
				ShareParams sp = new ShareParams();
				sp.setTitle(getString(R.string.exchange_card));
				sp.setUrl(KHConst.SHARE_CARD_WEB+"?user_id="+UserManager.getInstance().getUser().getUid()); // 标题的超链接
				sp.setShareType(Platform.SHARE_WEBPAGE);
				sp.setTitleUrl(KHConst.SHARE_CARD_WEB+"?user_id="+UserManager.getInstance().getUser().getUid()); // 标题的超链接
				sp.setText(UserManager.getInstance().getUser().getName());
				if (UserManager.getInstance().getUser().getName() == null || UserManager.getInstance().getUser().getName().length() < 1) {
					sp.setText("KHClub");
				}
				sp.setText(sp.getText()+"|"+UserManager.getInstance().getUser().getJob()+"\n"+UserManager.getInstance().getUser().getCompany_name());
				if (null != UserManager.getInstance().getUser().getHead_sub_image() && UserManager.getInstance().getUser().getHead_sub_image().length()>0) {
					sp.setImageUrl(KHConst.ATTACHMENT_ADDR+UserManager.getInstance().getUser().getHead_sub_image());	
				}else {
					sp.setImageUrl(KHConst.ROOT_IMG);	
				}
				Platform wexin = ShareSDK.getPlatform(WechatMoments.NAME);
				wexin.setPlatformActionListener(platformActionListener); // 设置分享事件回调
				// 执行图文分享
				wexin.share(sp);
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

	@Override
	public void onResume() {
		super.onResume();
		userModel = UserManager.getInstance().getUser();
		// 签名
		if (null != userModel.getSignature() && userModel.getSignature().length() > 0) {
			signTextView.setText(userModel.getSignature());
		} else {
			signTextView.setText(R.string.personal_none);
		}
		// 获取图片
		getNewsImages();
		//获取关注圈子
		setMyCircle();
		//更新个人信息
		setInfo();
		
	}
	
	private void setMyCircle(){

		String path = KHConst.GET_MY_CIRCLE_LIST + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							// 数据处理
							JSONArray array = jsonResponse
									.getJSONArray(KHConst.HTTP_RESULT);
							myCircleImageList.clear();
							for (int i = 0; i < array.size(); i++) {
								JSONObject object = (JSONObject) array.get(i);
								myCircleImageList.add(object.getString("circle_cover_sub_image"));
							}

							// 最多3
							int size = myCircleImageList.size();
							if (size > 3) {
								size = 3;
							}
							// 设置不可见
							for (ImageView imageView : myCircleList) {
								imageView.setVisibility(View.GONE);
							}

							for (int i = 0; i < size; i++) {
								ImageView imageView = myCircleList.get(i);
								String path = myCircleImageList.get(i);
								// 设置图片
								if (null != path && path.length() > 0) {
									ImageLoader.getInstance().displayImage(
											KHConst.ATTACHMENT_ADDR + path,
											imageView, imageOptions);
								} else {
									imageView
											.setImageResource(R.drawable.loading_default);
								}
								imageView.setVisibility(View.VISIBLE);
							}
							
							//提示暂无
							if (size > 0) {
								noMomentTextView.setVisibility(View.GONE);
							}else {
								noMomentTextView.setVisibility(View.VISIBLE);
							}
						}

						if (status == KHConst.STATUS_FAIL) {
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}
				}, null));
	}
	//设置内容
	private void setInfo(){
		userModel = UserManager.getInstance().getUser();
		// 头像不为空
		if (null != userModel.getHead_image()) {
			ImageLoader.getInstance().displayImage(
					KHConst.ATTACHMENT_ADDR + userModel.getHead_image(),
					headImageView, headImageOptions);
		}
		// 姓名
		nameTextView.setText(KHUtils.emptyRetunNone(userModel.getName()));
		if (userModel.getJob() != null && userModel.getJob().length() > 0) {
			nameTextView.setText(nameTextView.getText()+"/");
		}
		// 职业
		jobTextView.setText(userModel.getJob());
		// 公司
		companyTextView.setText(KHUtils.emptyRetunNone(userModel.getCompany_name()));
		// 电话
		phoneTextView.setText(KHUtils.emptyRetunNone(userModel.getPhone_num()));
		// 邮件
		emailTextView.setText(KHUtils.emptyRetunNone(userModel.getE_mail()));
		// 地址
		addressTextView.setText(KHUtils.emptyRetunNone(userModel.getAddress()));
		
		//不存在获取 存在直接设置
		if (userModel.getQr_code() != null && userModel.getQr_code().length() > 0) {
			ImageLoader.getInstance().displayImage(KHConst.ROOT_PATH+userModel.getQr_code(), qrcodeImageView, imageOptions);
		}
		
		//从网上获取一次
		String path = KHConst.GET_USER_QRCODE+"?"+"user_id="+userModel.getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {
					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							String qrpath = jsonResponse.getString(KHConst.HTTP_RESULT);
							//相等不处理
							if (userModel.getQr_code() == null || qrpath.equals(userModel.getQr_code())) {
								return;
							}							
							//本地缓存
							ImageLoader.getInstance().displayImage(KHConst.ROOT_PATH+qrpath, qrcodeImageView, imageOptions);
							UserManager.getInstance().getUser().setQr_code(qrpath);
							UserManager.getInstance().saveAndUpdate();
							
						}
						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(getActivity(), R.string.net_error);
						}
					}
					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}
				}, null));
	}
	
	//分享监听
	PlatformActionListener platformActionListener = new PlatformActionListener() {
		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			ToastUtil.show(getActivity(), R.string.personal_share_fail);
		}
		@Override
		public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//			ToastUtil.show(getActivity(), R.string.personal_share_ok);
		}
		@Override
		public void onCancel(Platform arg0, int arg1) {
		}
	};

	/**
	 * 初始化ViewPager
	 */
//	private void initViewPager() {
//		MessageFragmentPagerAdapter adapter = new MessageFragmentPagerAdapter(getActivity().getSupportFragmentManager());
//		mPager.setAdapter(adapter);
//		mPager.setCurrentItem(0);
//	}

//	private class MessageFragmentPagerAdapter extends
//			android.support.v4.app.FragmentPagerAdapter {
//
//		public MessageFragmentPagerAdapter(FragmentManager fm) {
//			super(fm);
//		}
//		
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {
////			return super.instantiateItem(container, position);
//			//得到缓存的fragment
//		    Fragment fragment = (Fragment)super.instantiateItem(container,position);
//		    
//		    int style = ConfigUtils.getIntConfig(ConfigUtils.CARD_CONFIG);
//			if (fragment instanceof PersonalInfoFragment) {
//				if (style == ConfigUtils.CARD_TWO) {
//					//得到tag
//				    String fragmentTag = fragment.getTag();
//				    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//					//移除旧的fragment
//					ft.remove(fragment);
//					//换成新的fragment
//					fragment = new PersonalInfoTwoFragment();
//					//添加新fragment时必须用前面获得的tag ❶
//					ft.add(container.getId(), fragment, fragmentTag);
//					ft.attach(fragment);
//					ft.commit();
//				}
//			}else if (fragment instanceof PersonalInfoTwoFragment) {
//				if (style == ConfigUtils.CARD_ONE) {
//					//得到tag
//				    String fragmentTag = fragment.getTag();
//				    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//					//移除旧的fragment
//					ft.remove(fragment);
//					//换成新的fragment
//					fragment = new PersonalInfoFragment();
//					//添加新fragment时必须用前面获得的tag ❶
//					ft.add(container.getId(), fragment, fragmentTag);
//					ft.attach(fragment);
//					ft.commit();
//				}				
//			}			
//			
//		    return fragment;
//		}
//
//		@Override
//		public Fragment getItem(int i) {
//			Fragment fragment = null;
//			switch (i) {
//			case 0:
//				int style = ConfigUtils.getIntConfig(ConfigUtils.CARD_CONFIG);
//			    if (style == ConfigUtils.CARD_TWO) {
//			    	fragment = new PersonalInfoTwoFragment();
//			    }else {
//			    	fragment = new PersonalInfoFragment();	
//				}
//				
//				break;
//			case 1:
//				fragment = new PersonalQrcodeFragment();
//				break;
//			}
//			return fragment;
//		}
//
//		@Override
//		public int getCount() {
//			return 2;
//		}
//	}

	// 获取当前最近的十张状态图片
	private void getNewsImages() {

		String path = KHConst.GET_NEWS_COVER_LIST + "?" + "uid="
				+ UserManager.getInstance().getUser().getUid();

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
							// 数据处理
							JSONArray array = jResult
									.getJSONArray(KHConst.HTTP_LIST);
							newsImageList.clear();
							for (int i = 0; i < array.size(); i++) {
								JSONObject object = (JSONObject) array.get(i);
								newsImageList.add(object.getString("sub_url"));
							}

							// 最多3
							int size = newsImageList.size();
							if (size > 3) {
								size = 3;
							}
							// 设置不可见
							for (ImageView imageView : imageList) {
								imageView.setVisibility(View.GONE);
							}

							for (int i = 0; i < size; i++) {
								ImageView imageView = imageList.get(i);
								String path = newsImageList.get(i);
								// 设置图片
								if (null != path && path.length() > 0) {
									ImageLoader.getInstance().displayImage(
											KHConst.ATTACHMENT_ADDR + path,
											imageView, imageOptions);
								} else {
									imageView
											.setImageResource(R.drawable.loading_default);
								}
								imageView.setVisibility(View.VISIBLE);
							}
							
							//提示暂无
							if (size > 0) {
								noMomentTextView.setVisibility(View.GONE);
							}else {
								noMomentTextView.setVisibility(View.VISIBLE);
							}
						}

						if (status == KHConst.STATUS_FAIL) {
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}
				}, null));
	}
}
