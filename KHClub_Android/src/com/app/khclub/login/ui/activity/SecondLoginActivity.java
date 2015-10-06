package com.app.khclub.login.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
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
					hideLoading();
					//登录成功用户信息注入
//					JSONObject result = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
//					UserManager.getInstance().getUser().setContentWithJson(result);
//					//数据持久化
//					UserManager.getInstance().saveAndUpdate();
					
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
					
					break; 
				case KHConst.STATUS_FAIL:
					hideLoading();
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
