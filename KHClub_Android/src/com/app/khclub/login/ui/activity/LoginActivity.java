package com.app.khclub.login.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.KeyEvent;
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
import com.app.khclub.base.ui.activity.BaseActivity;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.ToastUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@SuppressLint("ResourceAsColor")
public class LoginActivity extends BaseActivity {

	//area key
	public static String INTENT_AREA_KEY = "areaKey"; 
	// 用户名输入框
	@ViewInject(R.id.usernameEt)
	private EditText usernameEt;
	// 登录注册按钮
	@ViewInject(R.id.loginRegisterBtn)
	private Button loginRegisterBtn;
	// 布局文件
	@ViewInject(R.id.login_activity)
	private RelativeLayout loginLayout;
	// 区号
	@ViewInject(R.id.area_code_btn)
	private TextView areatTView;

	@OnClick(value = { R.id.loginRegisterBtn, R.id.login_activity,
			R.id.area_code_btn })
	public void loginOrRegisterClick(View v) {

		switch (v.getId()) {
		// 登录或者注册判断
		case R.id.loginRegisterBtn:
			loginOrRegister();
			break;
		case R.id.login_activity:
			try {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						0);
			} catch (Exception e) {
			}
			break;
		case R.id.area_code_btn:
			// 跳转到动态详情
			Intent intentToAreaCode = new Intent(LoginActivity.this,
					AreaCodeActivity.class);
			startActivityForResult(intentToAreaCode, 1);
			LoginActivity.this.overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		default:
			break;
		}
	}

	/**
	 * 登录或者注册跳转
	 */
	public void loginOrRegister() {
		final String username = usernameEt.getText().toString().trim();
		if (username.length() < 1) {
			ToastUtil.show(this, getString(R.string.login_username_not_null));
			return;
		}

		// 网络请求 hud
		showLoading(getString(R.string.login_wait_a_minute), true);

		RequestParams params = new RequestParams();
		params.addBodyParameter("username", username);

		HttpManager.post(KHConst.IS_USER, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						// TODO Auto-generated method stub
						super.onSuccess(jsonResponse, flag);

						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						switch (status) {
						case KHConst.STATUS_SUCCESS:
							hideLoading();

							JSONObject result = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							// 登录
							int loginDirection = 1;
							// 注册
							int registerDirection = 2;
							int direction = result.getIntValue("direction");
							if (direction == loginDirection) {
								// 登录
								Intent intent = new Intent(LoginActivity.this,
										SecondLoginActivity.class);
								intent.putExtra("username", username);
								intent.putExtra(INTENT_AREA_KEY, areatTView.getText().toString());
								startActivityWithRight(intent);
							}

							if (direction == registerDirection) {
								// 注册
								Intent intent = new Intent(LoginActivity.this,
										RegisterActivity.class);
								intent.putExtra("username", usernameEt
										.getText().toString().trim());
								intent.putExtra(INTENT_AREA_KEY, areatTView.getText().toString());
								startActivityWithRight(intent);
							}

							break;
						case KHConst.STATUS_FAIL:
							hideLoading();
							Toast.makeText(
									LoginActivity.this,
									jsonResponse
											.getString(getString(R.string.net_error)),
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						Toast.makeText(LoginActivity.this,
								getString(R.string.net_error),
								Toast.LENGTH_SHORT).show();
					}

				}, null));
	}

	/**
	 * 重写返回操作
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			ActivityManager.getInstence().exitApplication();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			areatTView.setText(data.getStringExtra(AreaCodeActivity.BACK_DATA));
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}

	@Override
	protected void loadLayout(View v) {

	}

	@Override
	protected void setUpView() {
		areatTView.setText("+86");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
