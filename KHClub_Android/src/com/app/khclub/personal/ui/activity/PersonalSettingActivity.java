package com.app.khclub.personal.ui.activity;


import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.KHHXSDKModel;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.ActivityManager;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.NewVersionCheckManager;
import com.app.khclub.base.manager.NewVersionCheckManager.VersionCallBack;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.ui.view.CustomAlertDialog;
import com.app.khclub.base.ui.view.CustomSelectPhotoDialog;
import com.app.khclub.base.ui.view.gallery.imageloader.GalleyActivity;
import com.app.khclub.base.utils.DataCleanManager;
import com.app.khclub.base.utils.FileUtil;
import com.app.khclub.base.utils.HttpCacheUtils;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.login.ui.activity.LoginActivity;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalSettingActivity extends BaseActivityWithTopBar {

	public static final int TAKE_PHOTO = 1;// 拍照
	public static final int ALBUM_SELECT = 2;// 相册选取
	public static final int PHOTO_ZOOM = 3;// 缩放
	public static final int PHOTO_RESOULT = 4;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private String tmpImageName = "";// 临时文件名
	
	//头像
	@ViewInject(R.id.head_image_view)
	private ImageView headImageView;
	// 姓名
	@ViewInject(R.id.name_text_view)
	private TextView nameTextView;
	// 职位
	@ViewInject(R.id.job_text_view)
	private TextView jobTextView;
	// 公司
	@ViewInject(R.id.company_text_view)
	private TextView companyTextView;	
	// 地址
	@ViewInject(R.id.address_text_view)
	private TextView addressTextView;	
	// 性别
	@ViewInject(R.id.sex_text_view)
	private TextView sexTextView;	
	// 邮箱
	@ViewInject(R.id.email_text_view)
	private TextView emailTextView;
	// 电话
	@ViewInject(R.id.phone_text_view)
	private TextView phoneTextView;
	// 签名
	@ViewInject(R.id.sign_text_view)
	private TextView signTextView;	
	/**
	 * 打开声音提示imageview
	 */
	@ViewInject(R.id.iv_switch_open_sound)
	private ImageView iv_switch_open_sound;
	/**
	 * 关闭声音提示imageview
	 */
	@ViewInject(R.id.iv_switch_close_sound)
	private ImageView iv_switch_close_sound;
	/**
	 * 打开消息震动提示
	 */
	@ViewInject(R.id.iv_switch_open_vibrate)
	private ImageView iv_switch_open_vibrate;
	/**
	 * 关闭消息震动提示
	 */
	@ViewInject(R.id.iv_switch_close_vibrate)
	private ImageView iv_switch_close_vibrate;
	
	private UserModel userModel;
	// 新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	private EMChatOptions chatOptions;
	private KHHXSDKModel model;
	
	@OnClick(value = { R.id.name_layout, R.id.sign_layout,R.id.company_layout,R.id.phone_layout,R.id.version_text_view,
			R.id.address_layout,R.id.email_layout,R.id.logout_button,R.id.rl_switch_sound,R.id.rl_switch_vibrate,
			R.id.sex_layout, R.id.head_image_view,R.id.head_layout, R.id.job_layout, R.id.clear_cache_text_view})
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 姓名
		case R.id.name_layout:
			nameClick();
			break;
		// 职位
		case R.id.job_layout:
			jobClick();
			break;
		// 签名 电话 住址 公司 邮箱
		case R.id.sign_layout:
		case R.id.phone_layout:
		case R.id.address_layout:
		case R.id.company_layout:
		case R.id.email_layout:
			Intent intent = new Intent(this,PersonalDescActivity.class);
			switch (view.getId()) {
			case R.id.sign_layout:
				intent.putExtra(PersonalDescActivity.INTENT_KEY, PersonalDescActivity.SIGN_KEY);				
				break;
			case R.id.phone_layout:
				intent.putExtra(PersonalDescActivity.INTENT_KEY, PersonalDescActivity.PHONE_KEY);
				break;
			case R.id.address_layout:
				intent.putExtra(PersonalDescActivity.INTENT_KEY, PersonalDescActivity.ADDRESS_KEY);
				break;
			case R.id.company_layout:
				intent.putExtra(PersonalDescActivity.INTENT_KEY, PersonalDescActivity.COMPANY_KEY);
				break;
			case R.id.email_layout:
				intent.putExtra(PersonalDescActivity.INTENT_KEY, PersonalDescActivity.EMAIL_KEY);
				break;				
			}
			startActivityForResult(intent, 1);
			this.overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		// 性别
		case R.id.sex_layout:
			sexClick();
			break;
		// 头像点击
		case R.id.head_layout:
			// dialog
			// 设置为头像
			final CustomSelectPhotoDialog selectDialog = new CustomSelectPhotoDialog(this);
			selectDialog.show();
			selectDialog
					.setClicklistener(new CustomSelectPhotoDialog.ClickListenerInterface() {

						@Override
						public void onSelectGallery() {
							// 相册
							tmpImageName = KHUtils.getPhotoFileName() + "";
							Intent intentAlbum = new Intent(PersonalSettingActivity.this,GalleyActivity.class);
							intentAlbum
									.putExtra(
											GalleyActivity.INTENT_KEY_SELECTED_COUNT,
											0);
							intentAlbum.putExtra(GalleyActivity.INTENT_KEY_ONE,
									true);
							startActivityForResult(intentAlbum, ALBUM_SELECT);
							selectDialog.dismiss();
						}

						@Override
						public void onSelectCamera() {
							// 相机
							Intent intentCamera = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							tmpImageName = KHUtils.getPhotoFileName() + "";
							File tmpFile = new File(FileUtil.TEMP_PATH
									+ tmpImageName);
							intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(tmpFile));
							startActivityForResult(intentCamera, TAKE_PHOTO);
							selectDialog.dismiss();
						}

					});
			break;
		case R.id.clear_cache_text_view:
			//清除缓存
			clearCache();
			break;
		case R.id.logout_button:
			//退出
			logout();
			break;
		case R.id.version_text_view:
			//版本检测
			checkVersion();
			break;
		case R.id.rl_switch_sound:
			//声音
			if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
				iv_switch_open_sound.setVisibility(View.INVISIBLE);
				iv_switch_close_sound.setVisibility(View.VISIBLE);
				chatOptions.setNoticeBySound(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
			} else {
				iv_switch_open_sound.setVisibility(View.VISIBLE);
				iv_switch_close_sound.setVisibility(View.INVISIBLE);
				chatOptions.setNoticeBySound(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgSound(true);
			}
			break;
		case R.id.rl_switch_vibrate:
			//震动
			if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
				iv_switch_close_vibrate.setVisibility(View.VISIBLE);
				chatOptions.setNoticedByVibrate(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(false);
			} else {
				iv_switch_open_vibrate.setVisibility(View.VISIBLE);
				iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
				chatOptions.setNoticedByVibrate(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(true);
			}
			break;			
			
		default:
			break;
		}
	}
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_personal_setting;
	}

	@Override
	protected void setUpView() {
		setBarText(getString(R.string.personal_setting));
		userModel = UserManager.getInstance().getUser();
		// 显示头像的配置
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		init();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// 图片返回值
		if (resultCode == Activity.RESULT_OK) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				LogUtils.i("SD card is not avaiable/writeable right now.", 1);
				return;
			}
			// 头像需要缩放
			switch (requestCode) {
			case TAKE_PHOTO:// 当选择拍照时调用
				// 头像 获取剪切
				File tmpFile = new File(FileUtil.TEMP_PATH + tmpImageName);
				startPhotoZoom(Uri.fromFile(tmpFile));
				break;
			case ALBUM_SELECT:// 当选择从本地获取图片时
				// 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
				if (data != null) {
						List<String> resultList = (List<String>) data
								.getSerializableExtra(GalleyActivity.INTENT_KEY_PHOTO_LIST);
						// 循环处理图片
						for (String fileRealPath : resultList) {
							// 只取一张
							File tmpFile1 = new File(fileRealPath);
							startPhotoZoom(Uri.fromFile(tmpFile1));
							break;
						}
				}
				break;
			case PHOTO_RESOULT:// 返回的结果
				if (data != null) {
					if (null != tmpImageName) {
						// 删除临时文件
						File file = new File(FileUtil.TEMP_PATH + tmpImageName);
						if (file.exists()) {
							file.delete();
						}
						uploadImage(FileUtil.HEAD_PIC_PATH + tmpImageName);
					}
				}
				break;
			}
		} else if (resultCode == PersonalDescActivity.SIGN_KEY) {
			// 签名返回
			String signString = data.getStringExtra(PersonalDescActivity.INTENT_KEY);
			if (null == signString || "".equals(signString)) {
				signTextView.setText(R.string.personal_none);
			} else {
				signTextView.setText(signString);
			}
			uploadInformation("signature", signString);
			
		}else if (resultCode == PersonalDescActivity.PHONE_KEY || resultCode == PersonalDescActivity.EMAIL_KEY ||
				resultCode == PersonalDescActivity.ADDRESS_KEY || resultCode == PersonalDescActivity.COMPANY_KEY) {
			//等有可见状态的四个
			//内容
			String contentString = data.getStringExtra(PersonalDescActivity.INTENT_KEY);
			boolean hasContent = false;
			if (null != contentString && !"".equals(contentString)) {
				hasContent = true;
			}
			//值
			String field = "";
			String value = contentString;
			String stateField = "";
			int stateValue = data.getIntExtra(PersonalDescActivity.INTENT_STATE_KEY, 0);
			
			switch (resultCode) {
				case PersonalDescActivity.PHONE_KEY:
					//有内容
					if (hasContent) {
						phoneTextView.setText(contentString);
					}else{
						phoneTextView.setText(R.string.personal_none);
					}
					field = "phone_num";
					stateField = "phone_state";
					break;
				case PersonalDescActivity.EMAIL_KEY:
					//有内容
					if (hasContent) {
						emailTextView.setText(contentString);
					}else{
						emailTextView.setText(R.string.personal_none);
					}
					field = "e_mail";
					stateField = "email_state";					
					break;
				case PersonalDescActivity.ADDRESS_KEY:
					//有内容
					if (hasContent) {
						addressTextView.setText(contentString);
					}else{
						addressTextView.setText(R.string.personal_none);
					}
					field = "address";
					stateField = "address_state";
					break;
				case PersonalDescActivity.COMPANY_KEY:
					//有内容
					if (hasContent) {
						companyTextView.setText(contentString);
					}else{
						companyTextView.setText(R.string.personal_none);
					}
					field = "company_name";
					stateField = "company_state";
					break;
				default:
					return;
			}
			//上传
			uploadStateInformation(field, value, stateField, stateValue);
		} else {
			File file = new File(FileUtil.TEMP_PATH + tmpImageName);
			if (file.exists()) {
				file.delete();
			}
			tmpImageName = "";
		}

	}

	// ////////////////////private method////////////////////////
	private void init(){
		//头像不为空
		if (null != userModel.getHead_image()) {
			ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR+userModel.getHead_image(), headImageView, headImageOptions);	
		}
		//姓名
		if (null != userModel.getName() && userModel.getName().length() > 0) {
			nameTextView.setText(userModel.getName());
		}else {
			nameTextView.setText(R.string.personal_none);
		}
		//职业
		if (null != userModel.getJob() && userModel.getJob().length() > 0) {
			jobTextView.setText(userModel.getJob());
		}else {
			jobTextView.setText(R.string.personal_none);
		}		
		//公司
		if (null != userModel.getCompany_name() && userModel.getCompany_name().length() > 0) {
			companyTextView.setText(userModel.getCompany_name());
		}else {
			companyTextView.setText(R.string.personal_none);
		}		
		//电话
		if (null != userModel.getPhone_num() && userModel.getPhone_num().length() > 0) {
			phoneTextView.setText(userModel.getPhone_num());
		}else {
			phoneTextView.setText(R.string.personal_none);
		}
		//邮件
		if (null != userModel.getE_mail() && userModel.getE_mail().length() > 0) {
			emailTextView.setText(userModel.getE_mail());
		}else {
			emailTextView.setText(R.string.personal_none);
		}	
		//地址
		if (null != userModel.getAddress() && userModel.getAddress().length() > 0) {
			addressTextView.setText(userModel.getAddress());
		}else {
			addressTextView.setText(R.string.personal_none);
		}
		//性别
		if (userModel.getSex() == UserModel.SexBoy) {
			sexTextView.setText(R.string.personal_sex_male);
		}else {
			sexTextView.setText(R.string.personal_sex_female);
		}
		//签名
		if (null != userModel.getSignature() && userModel.getSignature().length() > 0) {
			signTextView.setText(userModel.getSignature());
		}else {
			signTextView.setText(R.string.personal_none);
		}
		
		chatOptions = EMChatManager.getInstance().getChatOptions();
		model = (KHHXSDKModel) HXSDKHelper.getInstance().getModel();
		// 是否打开声音
		// sound notification is switched on or not?
		if (model.getSettingMsgSound()) {
			iv_switch_open_sound.setVisibility(View.VISIBLE);
			iv_switch_close_sound.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_sound.setVisibility(View.INVISIBLE);
			iv_switch_close_sound.setVisibility(View.VISIBLE);
		}
		
		// 是否打开震动
		// vibrate notification is switched on or not?
		if (model.getSettingMsgVibrate()) {
			iv_switch_open_vibrate.setVisibility(View.VISIBLE);
			iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
			iv_switch_close_vibrate.setVisibility(View.VISIBLE);
		}
	}
	
	// 开启缩放
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		intent.putExtra("outputX", 960);
		intent.putExtra("outputY", 960);

		intent.putExtra("scaleUpIfNeeded", true);// 黑边
		intent.putExtra("scale", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		// intent.putExtra("return-data", true);

		File headDir = new File(FileUtil.HEAD_PIC_PATH);
		if (!headDir.exists()) {
			headDir.mkdir();
		}
		File tmpFile = new File(FileUtil.HEAD_PIC_PATH + tmpImageName);
		// 地址
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
		startActivityForResult(intent, PHOTO_RESOULT);
	}

	// 姓名点击
	private void nameClick() {

		// dialog
		Builder nameAlertDialog = new AlertDialog.Builder(this);
		LinearLayout textViewLayout = (LinearLayout) View.inflate(
				this, R.layout.dialog_text_view, null);
		nameAlertDialog.setView(textViewLayout);
		final EditText et_search = (EditText) textViewLayout
				.findViewById(R.id.name_edit_text);
		et_search.setText(userModel.getName());
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
				String name = et_search.getText().toString().trim();
				if (name.length() < 1) {
					ToastUtil.show(PersonalSettingActivity.this, R.string.personal_name_not_null);
					return;
				}
				if (name.length() > 64) {
					ToastUtil.show(PersonalSettingActivity.this, R.string.personal_name_too_long);
					return;
				}
				uploadInformation("name", name);
				nameTextView.setText(name);
				dialog.dismiss();
			}
		});

	}
	
	// 职位点击
	private void jobClick() {

		// dialog
		Builder nameAlertDialog = new AlertDialog.Builder(this);
		LinearLayout textViewLayout = (LinearLayout) View.inflate(
				this, R.layout.dialog_job_text_view, null);
		nameAlertDialog.setView(textViewLayout);
		final EditText et_search = (EditText) textViewLayout
				.findViewById(R.id.job_edit_text);
		et_search.setText(userModel.getJob());
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

		//点击
		TextView confirmTextView = (TextView) textViewLayout
				.findViewById(R.id.tv_custom_alert_dialog_confirm);
		confirmTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String job = et_search.getText().toString().trim();
				uploadInformation("job", job);
				jobTextView.setText(job);
				dialog.dismiss();
			}
		});

	}

	// 性别点击
	private void sexClick() {

		// dialog
		LinearLayout sexViewLayout = (LinearLayout) View.inflate(this,
				R.layout.dialog_sex_view, null);
		final Dialog sexAlertDialog = new AlertDialog.Builder(this)
				.create();
		sexAlertDialog.show();
		sexAlertDialog.setContentView(sexViewLayout);
		sexAlertDialog.setCanceledOnTouchOutside(true);
		// 设置大小
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		WindowManager.LayoutParams params = sexAlertDialog.getWindow()
				.getAttributes();
		params.width = (int) (width * 0.8);
		sexAlertDialog.getWindow().setAttributes(params);

		// 性别男
		TextView boyTextView = (TextView) sexViewLayout
				.findViewById(R.id.boy_text_view);
		boyTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sexTextView.setText(R.string.personal_sex_male);
				uploadInformation("sex", "" + 0);
				sexAlertDialog.dismiss();
			}
		});
		// 性别女
		TextView girlTextView = (TextView) sexViewLayout
				.findViewById(R.id.girl_text_view);
		girlTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sexTextView.setText(R.string.personal_sex_female);
				uploadInformation("sex", "" + 1);
				sexAlertDialog.dismiss();
			}
		});

	}

	//上传信息
	private void uploadInformation(final String field, final String value) {

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", userModel.getUid() + "");
		params.addBodyParameter("field", field);
		params.addBodyParameter("value", value);

		HttpManager.post(KHConst.CHANGE_PERSONAL_INFORMATION, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							// 设置数据
							if ("name".equals(field)) {
								// 姓名
								userModel.setName(value);
							} else if ("signature".equals(field)) {
								// 签名
								userModel.setSignature(value);
							}else if ("sex".equals(field)) {
								// 性别
								userModel.setSex(Integer.parseInt(value));
							}else if ("job".equals(field)) {
								// 职位
								userModel.setJob(value);
							}
							// 本地持久化
							UserManager.getInstance().saveAndUpdate();
						}
						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(PersonalSettingActivity.this, R.string.net_error);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(PersonalSettingActivity.this, R.string.net_error);
					}
				}, null));
	}
	
	//上传有可见状态的信息
	private void uploadStateInformation(final String field, final String value, final String stateField, final int stateValue) {

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", userModel.getUid() + "");
		params.addBodyParameter("field", field);
		params.addBodyParameter("value", value);
		params.addBodyParameter("state_field", stateField);
		params.addBodyParameter("state_value", stateValue+"");

		HttpManager.post(KHConst.CHANGE_PERSONAL_INFORMATION_STATE, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							// 设置数据
							if ("phone_num".equals(field)) {
								// 电话
								userModel.setPhone_num(value);
								userModel.setPhone_state(stateValue);
							} else if ("e_mail".equals(field)) {
								// 邮箱
								userModel.setE_mail(value);
								userModel.setEmail_state(stateValue);
							}else if ("company_name".equals(field)) {
								// 公司
								userModel.setCompany_name(value);
								userModel.setCompany_state(stateValue);								
							}else if ("address".equals(field)) {
								// 地址
								userModel.setAddress(value);
								userModel.setAddress_state(stateValue);								
							}
							// 本地持久化
							UserManager.getInstance().saveAndUpdate();
						}
						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(PersonalSettingActivity.this, R.string.net_error);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(PersonalSettingActivity.this, R.string.net_error);
					}
				}, null));
	}

	// 上传头像
	private void uploadImage(final String path) {

		// 参数设置
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", userModel.getUid() + "");
		File uplodaFile = new File(path);
		if (!uplodaFile.exists()) {
			return;
		}
		params.addBodyParameter("image", uplodaFile);
		// 类型
		params.addBodyParameter("field", "head_image");
		
		showLoading(getResources().getString(R.string.uploading), true);
		HttpManager.post(KHConst.CHANGE_INFORMATION_IMAGE, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						LogUtils.i(jsonResponse.toJSONString(), 1);
						if (status == KHConst.STATUS_SUCCESS) {
							String serverPath = jsonResponse.getJSONObject(
									KHConst.HTTP_RESULT).getString("image");
								// 头像有缩略图
								String subPath = jsonResponse.getJSONObject(KHConst.HTTP_RESULT).getString("subimage");
								userModel.setHead_image(serverPath);
								userModel.setHead_sub_image(subPath);
								ImageLoader.getInstance().displayImage(
										"file://" + FileUtil.HEAD_PIC_PATH
												+ tmpImageName, headImageView,headImageOptions);
								ImageLoader.getInstance().displayImage(
										KHConst.ATTACHMENT_ADDR + serverPath,
										headImageView, headImageOptions);

							// 本地持久化
							UserManager.getInstance().saveAndUpdate();
							// 删除临时文件
							File tmpFile = new File(path);
							if (tmpFile.exists()) {
								tmpFile.delete();
							}
							tmpImageName = "";
						}

						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(PersonalSettingActivity.this, R.string.net_error);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						hideLoading();
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(PersonalSettingActivity.this, R.string.net_error);
					}
				}, null));

	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable("tmpImageName", tmpImageName);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		if (null != savedInstanceState) {
			tmpImageName = savedInstanceState.getString("tmpImageName");
		}
	}

	
	//清除缓存
	private void clearCache(){
		try {
			DataCleanManager.clearAllCache(this);
			//清除缓存
			HttpCacheUtils.clearHttpCache();
			ToastUtil.show(this, R.string.personal_clear_ok);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//退出
	private void logout(){
		final CustomAlertDialog confirmDialog = new CustomAlertDialog(
				this, getString(R.string.personal_confirm_logout), getString(R.string.alert_confirm), getString(R.string.alert_cancel));
		confirmDialog.show();
		confirmDialog.setClicklistener(new CustomAlertDialog.ClickListenerInterface() {
					@Override
					public void doConfirm() {
						
						final ProgressDialog pd = new ProgressDialog(PersonalSettingActivity.this);
						String st = getResources().getString(R.string.Are_logged_out);
						pd.setMessage(st);
						pd.setCanceledOnTouchOutside(false);
						pd.show();
						KHHXSDKHelper.getInstance().logout(true,new EMCallBack() {
							
							@Override
							public void onSuccess() {
								runOnUiThread(new Runnable() {
									public void run() {
										pd.dismiss();
										// 重新显示登陆页面
						                //清空数据
						                UserManager.getInstance().clear();
						                UserManager.getInstance().setUser(new UserModel());
						                Intent exit = new Intent(PersonalSettingActivity.this, LoginActivity.class);
						                exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
										startActivity(exit);
//										ActivityManager.getInstence().exitApplication();
										
									}
								});
							}
							
							@Override
							public void onProgress(int progress, String status) {
								
							}
							
							@Override
							public void onError(int code, String message) {
								runOnUiThread(new Runnable() {
									
									public void run() {
										// TODO Auto-generated method stub
										pd.dismiss();
									}
								});
							}
						});
						
						
						confirmDialog.dismiss();
					}

					@Override
					public void doCancel() {
						confirmDialog.dismiss();
					}
				});	  
	}
	
	//检测版本
	private void checkVersion() {
		
		showLoading(getString(R.string.downloading), true);
		new NewVersionCheckManager(this, this).checkNewVersion(true, new VersionCallBack() {
			@Override
			public void finish() {
				hideLoading();
			}
		});
	} 
}
