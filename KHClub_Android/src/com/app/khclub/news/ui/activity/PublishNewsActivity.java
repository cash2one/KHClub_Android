package com.app.khclub.news.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.ui.view.CustomSelectPhotoDialog;
import com.app.khclub.base.ui.view.gallery.imageloader.GalleyActivity;
import com.app.khclub.base.utils.FileUtil;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.model.NewsConstants;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class PublishNewsActivity extends BaseActivityWithTopBar {

	public static final int TAKE_PHOTO = 1;// 拍照
	public static final int ALBUM_SELECT = 2;// 相册选取
	public static final int PHOTO_ZOOM = 3; // 缩放
	public static final int PHOTO_RESOULT = 4;// 结果
	public static final int PHOTO_DELETE = 5;// 删除

	public static final int LOCATION_SELECT = 100;// 地理位置返回
	public static final String IMAGE_UNSPECIFIED = "image/*";

	// 添加附件的layout
	@ViewInject(R.id.addImageLayout)
	private RelativeLayout addImageLayout;
	// 添加附件的imageView
	@ViewInject(R.id.addImageView)
	private ImageView addImageView;
	@ViewInject(R.id.contentEt)
	private EditText contentEditText;
	// 选地点layout
	@ViewInject(R.id.choice_location_layout)
	private LinearLayout choiceLocationLayout;
	// 选地点textview
	@ViewInject(R.id.choice_location_text_view)
	private TextView choiceLocationTextView;
	// 发布按钮
	@ViewInject(R.id.base_ll_right_btns)
	private LinearLayout rightLayout;

	// 起始左边间距
	private int oriMarginLeft;
	// 设置间隔为5
	private int space = 5;
	// 点击加号的image弹窗
	// private AlertDialog imageDialog;
	private CustomSelectPhotoDialog selectDialog;
	// 临时文件名
	private String tmpImageName;
	// 地点
	private String locationString;
	// 图片加载配置
	DisplayImageOptions headImageOptions;

	@OnClick(value = { R.id.addImageView, R.id.choice_location_layout,
			R.id.base_ll_right_btns, R.id.publish_news_layout })
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 添加图片点击
		case R.id.addImageView:
			showChoiceImageAlert();
			break;
		// 选择地理位置点击
		case R.id.choice_location_layout:
			Intent intent = new Intent(this, ChoiceLocationActivity.class);
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		// 发布
		case R.id.base_ll_right_btns:
			publishNews();
		case R.id.publish_news_layout:
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), 0);
			break;
		default:
			break;
		}
	}

	private void showChoiceImageAlert() {

		// 设置为头像
		if (selectDialog == null) {
			selectDialog = new CustomSelectPhotoDialog(this);
			selectDialog
					.setClicklistener(new CustomSelectPhotoDialog.ClickListenerInterface() {

						@Override
						public void onSelectGallery() {
							// 相册
							Intent intentAlbum = new Intent(
									PublishNewsActivity.this,
									GalleyActivity.class);
							int imageCount = addImageLayout.getChildCount() - 1;
							if (imageCount < 0) {
								imageCount = 0;
							}
							intentAlbum.putExtra(
									GalleyActivity.INTENT_KEY_SELECTED_COUNT,
									imageCount);
							startActivityForResult(intentAlbum, ALBUM_SELECT);
							rightLayout.setEnabled(false);
							selectDialog.dismiss();
						}

						@Override
						public void onSelectCamera() {
							// 拍照
							Intent intentCamera = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							tmpImageName = KHUtils.getPhotoFileName() + "";
							LogUtils.i(tmpImageName, 1);
							File tmpFile = new File(FileUtil.TEMP_PATH
									+ tmpImageName);
							intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(tmpFile));
							startActivityForResult(intentCamera, TAKE_PHOTO);
							selectDialog.dismiss();
						}

					});
		}

		selectDialog.show();

	}

	private void addNewsImageView(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			Toast.makeText(this, "Exception!", Toast.LENGTH_SHORT).show();
			return;
		}

		View imageViewBack = View.inflate(this, R.layout.attrament_image, null);
		LinearLayout layout = (LinearLayout) imageViewBack
				.findViewById(R.id.attrament_image_layout);
		ImageView imageView = (ImageView) imageViewBack
				.findViewById(R.id.image_attrament);
		layout.removeAllViews();
		int imageCount = addImageLayout.getChildCount();
		// 移动位置
		moveImageView(imageView, imageCount);
		// 添加
		addImageLayout.addView(imageView);
		// 设置tag
		imageView.setTag(filePath);
		// 设置照片
		// bitmapUtils.display(imageView, filePath);
		ImageLoader.getInstance().displayImage("file://" + filePath, imageView,
				headImageOptions);

		// 设置点击查看大图事件
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final String tmpFilePath = (String) v.getTag();

				Intent intent = new Intent(PublishNewsActivity.this,
						PublisPhotoHandelActivity.class);
				intent.putExtra(PublisPhotoHandelActivity.INTENT_KEY,
						tmpFilePath);
				startActivityForResult(intent, PHOTO_DELETE);

			}
		});

	}

	// 删除状态图片
	private void deleteNewsImageView(String tag) {
		// 删除
		int subviewsCount = addImageLayout.getChildCount();
		for (int i = 0; i < subviewsCount; i++) {
			View view = addImageLayout.getChildAt(i);
			if (null != view.getTag() && view.getTag().equals(tag)) {
				addImageLayout.removeViewAt(i);
				break;
			}
		}

		// 添加按钮位置重置
		MarginLayoutParams addlp = (MarginLayoutParams) addImageView
				.getLayoutParams();
		addlp.setMargins(oriMarginLeft, 0, 0, 0);
		// 删除之后重新排序
		subviewsCount = addImageLayout.getChildCount();
		for (int i = 1; i < subviewsCount; i++) {
			View view = addImageLayout.getChildAt(i);
			moveImageView((ImageView) view, i);
		}

	}

	// 移动位置
	private void moveImageView(ImageView imageView, int imageCount) {

		int columnNum = imageCount % 4;
		int lineNum = imageCount / 4;
		// 添加按钮位置
		MarginLayoutParams addlp = (MarginLayoutParams) addImageView
				.getLayoutParams();

		int width = addlp.width;
		int height = addlp.height;
		// 布局位置
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,
				height);
		lp.setMargins(addlp.leftMargin, addlp.topMargin, 0, 0);
		imageView.setLayoutParams(lp);
		addlp.setMargins(oriMarginLeft + (width + space) * columnNum,
				(height + 10) * lineNum, 0, 0);
		if (imageCount > 8) {
			addImageView.setVisibility(View.GONE);
		} else {
			addImageView.setVisibility(View.VISIBLE);
		}

	}

	// 发布动态
	private void publishNews() {

		if ("".equals(contentEditText.getText().toString().trim())
				&& addImageLayout.getChildCount() == 1) {
			ToastUtil.show(this, R.string.news_content_image_empty);
			return;
		}

		if (contentEditText.getText().toString().length() > 140) {
			ToastUtil.show(this, R.string.news_content_too_long);
			return;
		}

		final UserModel userModel = UserManager.getInstance().getUser();
		showLoading(getResources().getString(R.string.uploading), false);
		RequestParams params = new RequestParams();
		// 用户id
		params.addBodyParameter("uid", userModel.getUid() + "");
		// 内容
		params.addBodyParameter("content_text", contentEditText.getText()
				.toString());
		// location
		params.addBodyParameter("location", locationString);

		// 图片
		for (int i = 0; i < addImageLayout.getChildCount(); i++) {
			View view = addImageLayout.getChildAt(i);
			// 如果不是添加按钮
			if (view != addImageView) {
				// 图片
				File file = new File((String) view.getTag());
				if (file.exists()) {
					params.addBodyParameter("image" + i, file);
				}
			}
		}

		// 发布
		HttpManager.post(KHConst.PUBLISH_NEWS, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse.getIntValue("status");
						switch (status) {
						case KHConst.STATUS_SUCCESS:
							// toast
							ToastUtil.show(PublishNewsActivity.this,R.string.news_publish_success);
							hideLoading();
							finishWithRight();
							publishFinishBroadcast();
							break;
						case KHConst.STATUS_FAIL:
							hideLoading();
							Toast.makeText(
									PublishNewsActivity.this,
									R.string.news_publish_fail,
									Toast.LENGTH_SHORT).show();
							break;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						Toast.makeText(
								PublishNewsActivity.this,
								R.string.net_error,
								Toast.LENGTH_SHORT).show();
					}
				}, null));
	}

	// /////////////////////////////////////Override//////////////////////////////////////////
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_publish_news;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void setUpView() {
		//标题
		setBarText(getResources().getString(R.string.news_publish));
		
		// 设置初始间隔
		MarginLayoutParams oriLp = (MarginLayoutParams) addImageView
				.getLayoutParams();
		oriMarginLeft = oriLp.leftMargin;
		// 设置图片大小
		RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) addImageView
				.getLayoutParams();
		rlParams.height = rlParams.width = (getWindowManager()
				.getDefaultDisplay().getWidth() - space * 3 - oriMarginLeft * 2) / 4;
		addImageView.setLayoutParams(rlParams);
		// 添加完成按钮
		addRightBtn(getResources().getString(R.string.alert_finish));
		locationString = "";
		// bitmap初始化
		headImageOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(false).cacheOnDisk(false)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	// 销毁的时候清空缓存
	protected void onDestroy() {
		super.onDestroy();

		// 清除缓存
		for (int i = 0; i < addImageLayout.getChildCount(); i++) {
			View view = addImageLayout.getChildAt(i);
			// 如果不是添加按钮
			if (view != addImageView) {
				// 图片
				File file = new File((String) view.getTag());
				if (file.exists()) {
					file.delete();
				}
			}
		}
	}

	// /防止内存不够用
	@Override
	protected void onSaveInstanceState(Bundle outState) {

		// 内容
		outState.putString("content", contentEditText.getText().toString());
		// 地点
		outState.putString("location", locationString);
		// 图片
		ArrayList<String> imageList = new ArrayList<String>();
		int subviewsCount = addImageLayout.getChildCount();
		for (int i = 0; i < subviewsCount; i++) {
			View view = addImageLayout.getChildAt(i);
			if (null != view.getTag() && view != addImageView) {
				if (view.getTag() instanceof String) {
					imageList.add((String) view.getTag());
				}
			}
		}
		outState.putStringArrayList("images", imageList);
		if (null != tmpImageName && tmpImageName.length() > 0) {
			// 刚拍的照片
			outState.putString("tmpImageName", tmpImageName);
		}

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (null != savedInstanceState) {
			// 地理位置
			if (savedInstanceState.containsKey("location")) {
				locationString = savedInstanceState.getString("location");
				if (null == locationString || locationString.length() < 1) {
					choiceLocationTextView.setText(R.string.news_where);
				} else {
					choiceLocationTextView.setText(locationString);
				}
			}
			// 内容
			if (savedInstanceState.containsKey("content")) {
				contentEditText
						.setText(savedInstanceState.getString("content"));
			}
			// 图片
			if (savedInstanceState.containsKey("images")) {
				ArrayList<String> imageList = savedInstanceState
						.getStringArrayList("images");
				for (String string : imageList) {
					addNewsImageView(string);
				}
			}

			// 最后拍的
			if (savedInstanceState.containsKey("tmpImageName")) {
				tmpImageName = savedInstanceState.getString("tmpImageName");
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				LogUtils.i("SD card is not avaiable/writeable right now.", 1);
				return;
			}
			switch (requestCode) {
			case TAKE_PHOTO:// 当选择拍照时调用
				// 图片压缩
				int[] screenSize = getScreenSize();
				if (FileUtil.tempToLocalPath(tmpImageName, screenSize[0],
						screenSize[1])) {
					addNewsImageView(FileUtil.BIG_IMAGE_PATH + tmpImageName);
				}
				break;
			case ALBUM_SELECT:// 当选择从本地获取图片时
				/*******************/

				@SuppressWarnings("unchecked")
				List<String> resultList = (List<String>) data
						.getSerializableExtra(GalleyActivity.INTENT_KEY_PHOTO_LIST);
				int[] screenSize1 = getScreenSize();
				long interval = System.currentTimeMillis() / 1000;
				// 循环处理图片
				for (String fileRealPath : resultList) {
					LogUtils.i(fileRealPath, 1);
					// 用户id+时间戳
					String fileName = UserManager.getInstance().getUser()
							.getUid()
							+ "" + interval + ".jpg";
					if (fileRealPath != null
							&& FileUtil.tempToLocalPath(fileRealPath, fileName,
									screenSize1[0], screenSize1[1])) {
						addNewsImageView(FileUtil.BIG_IMAGE_PATH + fileName);
					}
					// 命名规则以当前时间戳顺序加一
					interval++;
				}

				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						timerHandler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								// 恢复点击
								rightLayout.setEnabled(true);
							}
						});
					}
				}, 1000);

				break;

			// 删除选中的照片
			case PHOTO_DELETE:
				if (data.hasExtra(PublisPhotoHandelActivity.INTENT_KEY_DELETE_URL)) {
					String tmpFilePath = data
							.getStringExtra(PublisPhotoHandelActivity.INTENT_KEY_DELETE_URL);
					deleteNewsImageView(tmpFilePath);
				}
				break;
			}
		} else {
			// 恢复点击
			rightLayout.setEnabled(true);
		}

		// 地理位置
		if (resultCode == LOCATION_SELECT) {
			String location = data.getStringExtra("location");
			if (null != location && !"".equals(location)) {
				locationString = location;
				choiceLocationTextView.setText(location);
			} else {
				locationString = "";
				choiceLocationTextView.setText(R.string.news_where);
			}
		}
	}

	final Handler timerHandler = new Handler();
	final Handler photoHandler = new Handler();

	/**
	 * 发布动态完成发送广播
	 * */
	private void publishFinishBroadcast() {
		Intent mIntent = new Intent(KHConst.BROADCAST_NEWS_LIST_REFRESH);
		mIntent.putExtra(NewsConstants.PUBLISH_FINISH, "");
		// 发送广播
		LocalBroadcastManager.getInstance(PublishNewsActivity.this)
				.sendBroadcast(mIntent);
	}
}
