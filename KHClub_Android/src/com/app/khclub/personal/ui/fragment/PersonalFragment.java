package com.app.khclub.personal.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.personal.ui.activity.CollectCardActivity;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.app.khclub.personal.ui.activity.PersonalNewsActivity;
import com.app.khclub.personal.ui.activity.PersonalSettingActivity;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu;
import com.app.khclub.personal.ui.view.PersonalBottomPopupMenu.BottomClickListener;
import com.app.khclub.personal.ui.view.PersonalPopupMenu;
import com.app.khclub.personal.ui.view.PersonalPopupMenu.OperateListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalFragment extends BaseFragment {

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
	// 操作菜单
	@ViewInject(R.id.btn_more_operate)
	private ImageButton operateButton;
	// 前10张图片数组
	private List<String> newsImageList = new ArrayList<String>();
	// 控件数组
	private List<ImageView> imageList = new ArrayList<ImageView>();
	// 图片缓存工具
	private DisplayImageOptions imageOptions;
	// 操作菜单
	private PersonalPopupMenu popupMenu;
	// 分享弹出菜单
	private PersonalBottomPopupMenu shareMenu;

	@OnClick({ R.id.base_tv_back, R.id.image_cover_layout,
			R.id.button_collect_card, R.id.btn_more_operate })
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

		case R.id.button_collect_card:
			// 收藏的名片
			Intent intentToCardList = new Intent(this.getActivity(),
					CollectCardActivity.class);
			startActivityWithRight(intentToCardList);
			break;

		case R.id.btn_more_operate:
			// 操作菜单
			popupMenu.showPopupWindow(operateButton);
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
		initViewPager();
		imageList.add(pictureImageView1);
		imageList.add(pictureImageView2);
		imageList.add(pictureImageView3);
		imageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default)
				.cacheInMemory(false).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public void setUpViews(View rootView) {
		// 操作菜单监听
		popupMenu = new PersonalPopupMenu(getActivity());
		shareMenu = new PersonalBottomPopupMenu(getActivity(), false);
		popupMenu.setListener(new OperateListener() {

			@Override
			public void shareClick() {
				// 分享点击
				shareMenu.showPopupWindow(operateButton);
			}

			@Override
			public void switchClick() {
				// 切换名片样式
			}
		});

		// 分享菜单的事件
		shareMenu.setListener(new BottomClickListener() {

			@Override
			public void shareToWeiboClick() {
				// 分享到微博

			}

			@Override
			public void shareToWeChatClick() {
				// 分享到微信

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
				// 分享到朋友圈

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
		UserModel userModel = UserManager.getInstance().getUser();
		// 签名
		if (null != userModel.getSignature()
				&& userModel.getSignature().length() > 0) {
			signTextView.setText(userModel.getName());
		} else {
			signTextView.setText(R.string.personal_none);
		}
		// 获取图片
		getNewsImages();
	}

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mPager.setAdapter(new MessageFragmentPagerAdapter(getActivity()
				.getSupportFragmentManager()));
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
				fragment = new PersonalInfoFragment();
				break;
			case 1:
				fragment = new PersonalQrcodeFragment();
				break;

			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

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
								imageView.setVisibility(View.INVISIBLE);
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
