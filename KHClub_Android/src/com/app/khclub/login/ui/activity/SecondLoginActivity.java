package com.app.khclub.login.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
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
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.ui.activity.MainTabActivity;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.Md5Utils;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class SecondLoginActivity extends BaseActivityWithTopBar {

	//用户名输入框
	@ViewInject(R.id.passwordEt)
	private EditText passwordEt;
	//登录注册按钮
	@ViewInject(R.id.loginBtn)
	private Button loginBtn;
	//找回密码按钮
	@ViewInject(R.id.find_pwd_text_view)
	private TextView findPwdTextView;	
	//布局文件
	@ViewInject(R.id.second_login_activity)
	private RelativeLayout secondLoginLayout;
	
	private String username;
	
	@OnClick(value={R.id.loginBtn,R.id.second_login_activity,R.id.find_pwd_text_view})
	public void viewClick(View v) {
		
		switch (v.getId()) {
		case R.id.loginBtn:
			//登录
			login();
			break;
		case R.id.find_pwd_text_view:
			//找回密码
			findPwd();
			break;	
		case R.id.second_login_activity:
			//收键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	        break;
		default:
			break;
		}
	}
	
	public void login() {
		final String password = passwordEt.getText().toString().trim();
		if (null==password || "".equals(password)) {
			//密码不能为空
			Toast.makeText(SecondLoginActivity.this, getString(R.string.login_password_not_null),
					Toast.LENGTH_SHORT).show();
			return; 
		}
		
		//网络请求 
		showLoading(getString(R.string.login_loginning), true);
		RequestParams params = new RequestParams();
		params.addBodyParameter("username", username);
		params.addBodyParameter("password", Md5Utils.encode(password));
		
		HttpManager.post(KHConst.LOGIN_USER, params, new JsonRequestCallBack<String>(new LoadDataHandler<String>(){
			
			@Override
			public void onSuccess(JSONObject jsonResponse, String flag) {
				// TODO Auto-generated method stub
				super.onSuccess(jsonResponse, flag);
				int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
				switch (status) {
				case KHConst.STATUS_SUCCESS:
					//登录成功用户信息注入
					JSONObject result = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
					UserManager.getInstance().getUser().setContentWithJson(result);
					//数据持久化
					UserManager.getInstance().saveAndUpdate();
					hxLogin();
					break; 
				case KHConst.STATUS_FAIL:
					//1为封禁 2为黑名单
					int msg = KHUtils.stringToInt(jsonResponse.getString(KHConst.HTTP_MESSAGE));
					String errorString = getString(R.string.login_password_error);
					if (msg == 1) {
						errorString = getString(R.string.login_black_list);
					}
					Toast.makeText(SecondLoginActivity.this, errorString,
							Toast.LENGTH_SHORT).show();
				}
			}
			@Override
			public void onFailure(HttpException arg0, String arg1, String flag) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, flag);
				hideLoading();
				Toast.makeText(SecondLoginActivity.this, getString(R.string.net_error),
						Toast.LENGTH_SHORT).show();
			}
			
		}, null));
	}
	
	//环信登录
	public void hxLogin() {
		
		///测试环境下直接注册！！！！
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(KHConst.KH + UserManager.getInstance().getUser().getUid(), KHConst.IM_PWD);
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), 0).show();
							IMTestLogin();
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							int errorCode=e.getErrorCode();
							if(errorCode==EMError.NONETWORK_ERROR){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ALREADY_EXISTS){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.UNAUTHORIZED){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.ILLEGAL_USER_NAME){
							    Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
							}
							
							IMTestLogin();
						}
					});
				}
			}
		}).start();
	}
	
	private void IMTestLogin(){
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(KHUtils.selfCommonIMID(), KHConst.IM_PWD, new EMCallBack() {

			@Override
			public void onSuccess() {
				hideLoading();
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
							Toast.makeText(getApplicationContext(), "success", 1).show();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					// 取好友或者群聊失败，不让进入主页面
					runOnUiThread(new Runnable() {
						public void run() {
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
				Intent intent = new Intent(SecondLoginActivity.this, MainTabActivity.class);
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
		
		// 添加"Robot"
		User robotUser = new User();
		String strRobot = getResources().getString(R.string.robot_chat);
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

	//找回密码
	public void findPwd() {

    	Intent intent = new Intent(SecondLoginActivity.this, RegisterActivity.class);
    	intent.putExtra("username", username);
    	intent.putExtra("isFindPwd", true);
    	startActivityWithRight(intent);			
		
	}
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_second_login;
	}

	@SuppressLint("ResourceAsColor") @Override
	protected void setUpView() {
		//设置用户名
		Intent intent =	getIntent();
		setUsername(intent.getStringExtra("username"));
		setBarText(getString(R.string.login_login_title));
		RelativeLayout rlBar = (RelativeLayout) findViewById(R.id.layout_base_title);
		rlBar.setBackgroundResource(R.color.main_clear);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();

	}
	@Override
	public void onPause() {
		super.onPause();
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
