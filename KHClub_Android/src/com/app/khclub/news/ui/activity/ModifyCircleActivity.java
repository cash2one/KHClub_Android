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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.app.khclub.news.ui.model.CirclePageModel;
import com.app.khclub.news.ui.model.NewsConstants;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ModifyCircleActivity extends BaseActivityWithTopBar {

	private static final String CIRCLEDETAIL = "circledetail";
	private static final String CIRCLE_ID = "circle_id";
	public static final int TAKE_PHOTO = 1;// 拍照
	public static final int ALBUM_SELECT = 2;// 相册选取
	public static final int PHOTO_ZOOM = 3; // 缩放
	public static final int PHOTO_RESOULT = 4;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	protected static final String INTENT_KEY_ONE = "one_pic";
	private boolean isClub = true;
	private boolean isModifycover = false;
	private boolean isModifyrqCode = false;
	private String filepath, qrcodefilepath;
	private String clubid;
	// 上传头像
	@ViewInject(R.id.cover_image)
	private ImageView coverImageView;

	// 上传二维码
	@ViewInject(R.id.create_edit_server_qrcode)
	private ImageView qrImageView;
	// 圈子称呢
	@ViewInject(R.id.create_edit_name)
	private EditText clubnameEditText;
	// 圈子介绍
	@ViewInject(R.id.create_edit_introduce)
	private EditText clubintroduceEditText;
	// 圈子地址
	@ViewInject(R.id.create_edit_address)
	private EditText clubaddressEditText;
	// 圈子电话
	@ViewInject(R.id.create_edit_phone)
	private EditText clubphoneEditText;
	// 公众号名称
	@ViewInject(R.id.create_edit_server)
	private EditText clubserverEditText;
	// 圈子网址
	@ViewInject(R.id.create_edit_url)
	private EditText cluburlEditText;

	@ViewInject(R.id.addImageView)
	private ImageView addImageView;
	// 发布按钮
	@ViewInject(R.id.base_ll_right_btns)
	private LinearLayout rightLayout;
	// 起始左边间距
	// private int oriMarginLeft;
	// 点击加号的image弹窗
	// private AlertDialog imageDialog;
	private CustomSelectPhotoDialog selectDialog;
	// 临时文件名
	private String tmpImageName;
	// 地点
	private String locationString;
	// 图片加载配置
	DisplayImageOptions headImageOptions;

	@OnClick(value = { R.id.addImageView, R.id.base_ll_right_btns, R.id.publish_news_layout,
			R.id.create_edit_server_qrcode, R.id.cover_image })
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 添加图片点击
		case R.id.base_ll_right_btns:
			ModifyCircle();
			break;
		case R.id.publish_news_layout:
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			break;
		// 上传头像
		case R.id.cover_image:
			isClub = true;
			isModifycover = true;
			showChoiceImageAlert();
			break;
		// 上传二位码
		case R.id.create_edit_server_qrcode:
			isClub = false;
			isModifyrqCode = true;
			showChoiceImageAlert();
			break;
		default:
			break;
		}
	}

	private void ModifyCircle() {
		RequestParams params = new RequestParams();
		// TODO Auto-generated method stub
		// 限制圈子名不能为空且不能超过七个字
		if ("".equals(clubnameEditText.getText().toString().trim())) {
			ToastUtil.show(this, R.string.news_content_image_empty);
			
			return;
		} else if (clubnameEditText.getText().toString().length() > 7) {
			ToastUtil.show(this, R.string.circle_name_limit);
			return;
		} else {
			// 圈子称呢
			params.addBodyParameter("circle_name", clubnameEditText.getText().toString());
			circleModel.setCircleName(clubnameEditText.getText().toString());
		}

		if (clubintroduceEditText.getText().toString().length() > 200) {
			ToastUtil.show(this, R.string.circle_introduce_limit);
			return;
		} else {
			// 圈子介绍
			params.addBodyParameter("circle_detail", clubintroduceEditText.getText().toString());
			circleModel.setCircleDetail(clubintroduceEditText.getText().toString());
		}
		final UserModel userModel = UserManager.getInstance().getUser();
		showLoading(getResources().getString(R.string.uploading), false);

		// 圈子id
		params.addBodyParameter("circle_id", clubid);
		//Log.i("wwww", clubid);
		// 上传圈子封面
		if (isModifycover) {
			File imgfile = new File(filepath);
			// 
			if (!imgfile.exists()) {
				return;
			}
			params.addBodyParameter("file", imgfile);
			
		}
		// 二维码
		if (isModifyrqCode) {
			if (!isClub) {
				File qrcodeimgfile = new File(qrcodefilepath);
				// Log.i("wwww", qrcodeimgfile.toString());
				params.addBodyParameter("qrcodefile", qrcodeimgfile);
			}
		}
		// 圈子地址
		params.addBodyParameter("address", clubaddressEditText.getText().toString());
		circleModel.setAdress(clubaddressEditText.getText().toString());
		// 圈子电话
		params.addBodyParameter("phone_num", clubphoneEditText.getText().toString());
		circleModel.setPhoneNum(clubphoneEditText.getText().toString());
		// 微信公众号
		params.addBodyParameter("wx_num", clubserverEditText.getText().toString());
		circleModel.setWxNum(clubserverEditText.getText().toString());
		// 圈子网址
		params.addBodyParameter("circle_web", cluburlEditText.getText().toString());
		circleModel.setCircleUrl(cluburlEditText.getText().toString());
		// 发布
		HttpManager.post(KHConst.MODIFY_CIRCLE, params, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				super.onSuccess(jsonResponse, flag);
				hideLoading();
				int status = jsonResponse.getIntValue("status");
				//Log.i("wwww", status+"");
				switch (status) {
				case KHConst.STATUS_SUCCESS:
					// toast
					JSONObject result = jsonResponse.getJSONObject("result");
					//Log.i("wwww", result.toString());
					ToastUtil.show(ModifyCircleActivity.this,R.string.modify_success);
					result();
					hideLoading();
					jumpCirPage();
					finishWithRight();
					// publishFinishBroadcast();
					break;
				case KHConst.STATUS_FAIL:
					hideLoading();
					Toast.makeText(ModifyCircleActivity.this, R.string.modify_fail, Toast.LENGTH_SHORT).show();
					break;
				}
			}

			private void result() {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.putExtra(CIRCLEDETAIL, circleModel);
				setResult(RESULT_OK, intent);
			}

			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				super.onFailure(arg0, arg1, flag);
				hideLoading();
				Toast.makeText(ModifyCircleActivity.this, R.string.net_error, Toast.LENGTH_SHORT).show();
			}
		}, null));

	}

	private void jumpCirPage() {
		// TODO Auto-generated method stub
		Intent circleIntent = new Intent(ModifyCircleActivity.this, CirclePageActivity.class);
		circleIntent.putExtra(CIRCLE_ID, clubid);
		startActivity(circleIntent);
	}
	private void showChoiceImageAlert() {
		// 设置为头像
		if (selectDialog == null) {
			selectDialog = new CustomSelectPhotoDialog(this);
			selectDialog.setClicklistener(new CustomSelectPhotoDialog.ClickListenerInterface() {
				@Override
				public void onSelectGallery() {
					// 相册
					Intent intentAlbum = new Intent(ModifyCircleActivity.this, GalleyActivity.class);
					// if (imageCount < 0) {
					// imageCount = 0;;
					// }
					intentAlbum.putExtra(GalleyActivity.INTENT_KEY_SELECTED_COUNT, 0);
					intentAlbum.putExtra(INTENT_KEY_ONE, true);
					startActivityForResult(intentAlbum, ALBUM_SELECT);
					// rightLayout.setEnabled(false);
					selectDialog.dismiss();
				}

				@Override
				public void onSelectCamera() {
					// 拍照
					Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (!isClub) {
						tmpImageName = "qrcode" + KHUtils.getPhotoFileName() + "";
					} else {
						tmpImageName = KHUtils.getPhotoFileName() + "";
					}
					LogUtils.i(tmpImageName, 1);
					File tmpFile = new File(FileUtil.TEMP_PATH + tmpImageName);
					intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
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

		if (isClub) {
			// 设置圈子头像
			ImageLoader.getInstance().displayImage("file://" + filePath, coverImageView, headImageOptions);

		} else {
			// 设置二维码
			ImageLoader.getInstance().displayImage("file://" + filePath, qrImageView, headImageOptions);

		}

	}

	// /////////////////////////////////////Override//////////////////////////////////////////
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_modify_circle;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void setUpView() {
		// 标题
		setBarText(getString(R.string.modify_circle));
		/*
		 * // 设置图片大小 RelativeLayout.LayoutParams rlParams =
		 * (RelativeLayout.LayoutParams) addImageView .getLayoutParams();
		 * rlParams.height = rlParams.width = (getWindowManager()
		 * .getDefaultDisplay().getWidth() - space * 3 - oriMarginLeft * 2) / 4;
		 * addImageView.setLayoutParams(rlParams); // 添加完成按钮
		 */
		TextView sendBtn = addRightBtn("下一步");
		sendBtn.setTextColor(getResources().getColor(R.color.main_white));
		// bitmap初始化
		headImageOptions = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(false)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).bitmapConfig(Bitmap.Config.RGB_565).build();
		initViewData();
	}

	private void initViewData() {
		circleModel = (CirclePageModel) getIntent().getSerializableExtra(CIRCLEDETAIL);
		if (circleModel == null) {
			return;
		}
		imgLoader = ImageLoader.getInstance();
		// 加载原有头像
		
		clubid=circleModel.getCircleId();
		//Log.e("wwww", clubid+"111");
		
		imgLoader.displayImage(circleModel.getCircleCoverSubImage(), coverImageView, headImageOptions);

		imgLoader.displayImage(circleModel.getWxqrCode(), qrImageView, headImageOptions);
		// 圈子名称
		clubnameEditText.setText(circleModel.getCircleName());
		// 圈子介绍
		clubintroduceEditText.setText(circleModel.getCircleDetail());
		// 圈子地址
		clubaddressEditText.setText(circleModel.getAdress());
		// 圈子电话
		clubphoneEditText.setText(circleModel.getPhoneNum());
		// 公众号名称
		clubserverEditText.setText(circleModel.getWxNum());
		// 圈子网址
		cluburlEditText.setText(circleModel.getCircleUrl());
	}

	@Override
	// 销毁的时候清空缓存
	protected void onDestroy() {
		super.onDestroy();
	}

	// /防止内存不够用
	@Override
	protected void onSaveInstanceState(Bundle outState) {

		// 内容
		// outState.putString("content", contentEditText.getText().toString());
		// 地点
		outState.putString("location", locationString);
		// 图片
		ArrayList<String> imageList = new ArrayList<String>();

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

			if (savedInstanceState.containsKey("images")) {
				ArrayList<String> imageList = savedInstanceState.getStringArrayList("images");
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

				if (FileUtil.tempToLocalPath(tmpImageName, screenSize[0], screenSize[1])) {
					if (isClub) {
						filepath = FileUtil.BIG_IMAGE_PATH + tmpImageName;
						//Log.i("wwww", filepath);
						circleModel.setCircleCoverImage(tmpImageName);
						addNewsImageView(filepath);
					} else {
						qrcodefilepath = FileUtil.BIG_IMAGE_PATH + tmpImageName;
						//Log.i("wwww", qrcodefilepath);
						circleModel.setCircleCoverImage(tmpImageName);
						addNewsImageView(qrcodefilepath);
					}
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
					String fileName;
					if (isClub) {
						fileName = UserManager.getInstance().getUser().getUid() + "" + interval + ".jpg";
					} else {
						fileName = "qrcode" + UserManager.getInstance().getUser().getUid() + "" + interval + ".jpg";
					}
					if (fileRealPath != null
							&& FileUtil.tempToLocalPath(fileRealPath, fileName, screenSize1[0], screenSize1[1])) {
						if (isClub) {
							filepath = FileUtil.BIG_IMAGE_PATH + fileName;
							circleModel.setCircleCoverImage(fileName);
							addNewsImageView(filepath);
						}
						if (!isClub) {
							qrcodefilepath = FileUtil.BIG_IMAGE_PATH + fileName;
							circleModel.setWxqrCode(fileName);
							addNewsImageView(qrcodefilepath);
						}
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

			// //删除选中的照片
			// case PHOTO_DELETE:
			// if
			// (data.hasExtra(PublisPhotoHandelActivity.INTENT_KEY_DELETE_URL))
			// {
			// String tmpFilePath =
			// data.getStringExtra(PublisPhotoHandelActivity.INTENT_KEY_DELETE_URL);
			// deleteNewsImageView(tmpFilePath);
			// }
			// break;
			}
		} else {
			// 恢复点击
			rightLayout.setEnabled(true);
		}
	}

	final Handler timerHandler = new Handler();
	final Handler photoHandler = new Handler();
	private ImageLoader imgLoader;
	private CirclePageModel circleModel;

	/**
	 * 发布动态完成发送广播
	 */
//	 private void publishFinishBroadcast() {
//	     Intent mIntent = new Intent(KHConst.BROADCAST_NEWS_LIST_REFRESH);
//	    mIntent.putExtra(NewsConstants.PUBLISH_FINISH, "");
//	     //发送广播
//	    LocalBroadcastManager.getInstance(ModifyCircleActivity.this).sendBroadcast(mIntent);
//	 }

}
