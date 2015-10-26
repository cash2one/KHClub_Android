package com.app.khclub.login.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.mapcore2d.ar;
import com.app.khclub.R;
import com.app.khclub.base.app.KHApplication;
import com.app.khclub.base.easeim.Constant;
import com.app.khclub.base.easeim.KHHXSDKHelper;
import com.app.khclub.base.easeim.applib.controller.HXSDKHelper;
import com.app.khclub.base.easeim.db.UserDao;
import com.app.khclub.base.easeim.domain.User;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.ActivityManager;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.ui.activity.MainTabActivity;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.Md5Utils;
import com.app.khclub.base.utils.ToastUtil;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class RegisterActivity extends BaseActivityWithTopBar {

	private final static String INTENT_KEY = "username";

	// 是否是忘记密码
	private Boolean isFindPwd;
	// 用户 的电话号码
	private String userPhoneNumber;
	// 用户 区号
	private String areaNumber;	
	// 当前倒计时的值
	private int countdownValue = 0;
	// 倒计时对象
	private CountDownTimer verifyCountdownTimer = null;
	// 用户输入的验证码
	private String verifyCodeEditTextValue;	
	// 提示电话的textview
	@ViewInject(R.id.phone_prompt_textview)
	private TextView phonePromptTextView;
	// 验证码输入框
	@ViewInject(R.id.verificationcode_edittext)
	private EditText verifycodeEditText;
	// 密码
	private String password = "";
	// 返回按钮
	@ViewInject(R.id.base_tv_back)
	private TextView backTextView;
	// 页面标头
	@ViewInject(R.id.base_tv_title)
	private TextView titletTextView;
	// 完成按钮
	@ViewInject(R.id.next_button)
	private Button nextButton;
	// 密码框
	@ViewInject(R.id.passwd_edittext)
	private EditText passwdeEditText;
	// 重新验证
	@ViewInject(R.id.revalidated_textview)
	private TextView revalidatedTextView;	

	// 点击事件绑定
	@OnClick({ R.id.base_tv_back, R.id.next_button,
			R.id.register_activity, R.id.revalidated_textview })
	public void viewCickListener(View view) {
		switch (view.getId()) {
		case R.id.base_tv_back:
			backClick();
			break;
		case R.id.next_button:
			// 点击下一步
			nextClick();
			break;
		case R.id.revalidated_textview:
			getVerificationCode();
			break;
		case R.id.register_activity:
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			break;
		default:
			break;
		}
	}

	// 初始化数据
	private void init() {
		Intent intent = getIntent();
		userPhoneNumber = intent.getStringExtra(INTENT_KEY);
		isFindPwd = intent.getBooleanExtra("isFindPwd", false);
		if (intent.hasExtra(LoginActivity.INTENT_AREA_KEY)) {
			areaNumber = intent.getStringExtra(LoginActivity.INTENT_AREA_KEY).trim();
			areaNumber = areaNumber.replace("+", "");
		}else {
			areaNumber = "86";
		}
	}

	// 点击返回
	private void backClick() {
		
		finishWithRight();
				
	}

	// 点击下一步按钮
	private void nextClick() {
		password = passwdeEditText.getText().toString();
		// 判断输入值是否正确
		verifyCodeEditTextValue = verifycodeEditText.getText().toString();
		// 判断输入值是否正确
		if (verifyCodeEditTextValue.length() == 0) {
			ToastUtil.show(RegisterActivity.this, getString(R.string.login_verification_not_null));
		}else if (password.length() < 6) {
			ToastUtil.show(RegisterActivity.this, getString(R.string.login_password_length_six));
		} else {
			// 忘记密码
			if (isFindPwd) {
				findPwd();
			} else {
				// 注册
				startRegister();
			}
		}
	}

	// 找回密码
	private void findPwd() {
		showLoading(getString(R.string.uploading), false);
		try {
			//先验证验证码
			SMSSDK.submitVerificationCode(areaNumber, userPhoneNumber, verifycodeEditText.getText().toString().trim());			
		} catch (Exception e) {
			hideLoading();
			ToastUtil.show(this, getString(R.string.net_error));
		}
	}
	
	// 找回密码
	private void finishPwd() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", userPhoneNumber);
		params.addBodyParameter("password", Md5Utils.encode(password));

		HttpManager.post(KHConst.FIND_PWD, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							hideLoading();
							JSONObject result = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							UserModel userMd = new UserModel();
							userMd.setContentWithJson(result);
							UserManager.getInstance().setUser(userMd);
							// 数据持久化
							UserManager.getInstance().saveAndUpdate();
							IMLogin();
						}

						if (status == KHConst.STATUS_FAIL) {
							hideLoading();
							ToastUtil.show(RegisterActivity.this, getString(R.string.net_error));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						ToastUtil.show(RegisterActivity.this, getString(R.string.net_error));
					}
				}, null));
	}

	// 开始注册
	private void startRegister() {
		RegisterActivity.this.showLoading(getString(R.string.uploading), false);
		try {
			//先验证验证码 测试注释掉
			SMSSDK.submitVerificationCode(areaNumber, userPhoneNumber, verifycodeEditText.getText().toString().trim());			
		} catch (Exception e) {
			hideLoading();
			ToastUtil.show(RegisterActivity.this, getString(R.string.net_error));
		}
	}
	
	//验证成功 完成注册
	private void finishRegister() {
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", userPhoneNumber);
		params.addBodyParameter("password", Md5Utils.encode(password));
//		params.addBodyParameter("verify_code", verifyCodeEditTextValue);

		HttpManager.post(KHConst.REGISTER_USER, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							hideLoading();
							JSONObject result = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							// 设置用户实例
							UserModel userMd = new UserModel();
							userMd.setContentWithJson(result);
							UserManager.getInstance().setUser(userMd);
							// 数据持久化
							UserManager.getInstance().saveAndUpdate();
							IMLogin();
						}

						if (status == KHConst.STATUS_FAIL) {
							hideLoading();
							ToastUtil.show(RegisterActivity.this, getString(R.string.net_error));
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						ToastUtil.show(RegisterActivity.this, getString(R.string.net_error));
					}

				}, null));
	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_register_layout;
	}

	@Override
	protected void setUpView() {
		
		init();
		if (isFindPwd) {
			titletTextView.setText(getString(R.string.login_modify_password));
		}else {
			titletTextView.setText(getString(R.string.login_register_title));	
		}
		
		// RelativeLayout rlBar = (RelativeLayout)
		// findViewById(R.id.layout_base_title);
		// rlBar.setBackgroundResource(R.color.main_clear);
		
		// 填写从短信SDK应用后台注册得到的APPKEY 
		String APPKEY = "b87c4ca25bcb";
		// 填写从短信SDK应用后台注册得到的APPSECRET
		String APPSECRET = "675c44e12327b136c3f99c07c43ff82a";
		//初始化验证码
		SMSSDK.initSDK(this,APPKEY,APPSECRET);
		//初始化获取一次验证码
		SMSSDK.getVerificationCode(areaNumber,userPhoneNumber);	
		phonePromptTextView.setText(getString(R.string.login_verification_send)+"：" + userPhoneNumber);
		revalidatedTextView.setEnabled(false);
		verifyCountdownTimer = new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				countdownValue = (int) millisUntilFinished / 1000;
				revalidatedTextView.setText(countdownValue + "s "+getString(R.string.login_resend));
			}
			@Override
			public void onFinish() {
				countdownValue = 0;
				revalidatedTextView.setEnabled(true);
				revalidatedTextView.setText(getString(R.string.login_resend));
			}
		};
		// 开始倒计时
		verifyCountdownTimer.start();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			//验证码接收器
			EventHandler eh=new EventHandler(){
				@Override
				public void afterEvent(int event, int result, Object data) {
					Message msg = new Message();
					msg.arg1 = event;
					msg.arg2 = result;
					msg.obj = data;
					handler.sendMessage(msg);
				}
			};
			SMSSDK.registerEventHandler(eh);
		} catch (Exception e) {
		}
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try{
			SMSSDK.unregisterAllEventHandler();
		} catch (Exception e) {
		}		
	}

	@Override
	protected void loadLayout(View v) {

	}

	// 获取验证码
	private void getVerificationCode() {
		
		try {
			//发送验证码
			SMSSDK.getVerificationCode(areaNumber,userPhoneNumber);	
			verifyCountdownTimer.start();
			showLoading(getString(R.string.downloading), true);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@SuppressLint("HandlerLeak") 
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			hideLoading();
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event="+event);
			
			if (result == SMSSDK.RESULT_COMPLETE) {
				//短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
					//完成注册或者找回密码
					if (isFindPwd) {
						finishPwd();
					}else {
						finishRegister();
					}
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					ToastUtil.show(RegisterActivity.this, getString(R.string.login_verification_arrivied));	
					
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
					
				}
			} else {
				((Throwable) data).printStackTrace();
				ToastUtil.show(RegisterActivity.this, getString(R.string.login_verification_error));
			}
			
		}
		
	};
	
	/**
	 * 重写返回操作
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			backClick();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
	
	private void IMLogin(){
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(KHUtils.selfCommonIMID(), KHConst.IM_PWD, new EMCallBack() {

			@Override
			public void onSuccess() {
				// 登陆成功，保存用户名密码
				KHApplication.getInstance().setUserName(KHUtils.selfCommonIMID());
				KHApplication.getInstance().setPassword(KHConst.IM_PWD);
				
				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
				    EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					// 处理好友和群组
					initializeContacts();
					//提示用
					runOnUiThread(new Runnable() {
						public void run() {
							hideLoading();
							if (!isFindPwd) {
								ToastUtil.show(RegisterActivity.this, getString(R.string.login_register_successful));					
							}else {
								ToastUtil.show(RegisterActivity.this, getString(R.string.pwd_update_successful));
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					// 取好友或者群聊失败，不让进入主页面
					runOnUiThread(new Runnable() {
						public void run() {
							hideLoading();
							KHHXSDKHelper.getInstance().logout(true,null);
							Toast.makeText(getApplicationContext(), R.string.login_failure_failed, 1).show();
						}
					});
					return;
				}
				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
						UserManager.getInstance().getUser().getName());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				
				//跳转主页
				final Intent intent = new Intent(RegisterActivity.this, MainTabActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);		
				
				finish();
				//把前面的finish掉
				for (Activity activity:ActivityManager.getActivityStack()) {
					if (activity.getClass().equals(LoginActivity.class)) {
						activity.finish();
						break;
					}
				}
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				hideLoading();
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	private void initializeContacts() {
		//HX用User的方式 设计通知请求
		Map<String, User> userlist = new HashMap<String, User>();
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		String strChat = getResources().getString(
				R.string.Application_and_notify);
		newFriends.setNick(strChat);

		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		User groupUser = new User();
		String strGroup = getResources().getString(R.string.group_chat);
		groupUser.setUsername(Constant.GROUP_USERNAME);
		groupUser.setNick(strGroup);
		groupUser.setHeader("");
		userlist.put(Constant.GROUP_USERNAME, groupUser);
		
		// 添加"Robot" 更换为 名片列表
		User robotUser = new User();
//		String strRobot = getResources().getString(R.string.robot_chat);
		String strRobot = getResources().getString(R.string.personal_collect_card);
		robotUser.setUsername(Constant.CHAT_ROBOT);
		robotUser.setNick(strRobot);
		robotUser.setHeader("");
		userlist.put(Constant.CHAT_ROBOT, robotUser);
		// 存入内存
		((KHHXSDKHelper)HXSDKHelper.getInstance()).setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}
	
}
