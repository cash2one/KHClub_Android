package com.shs.contact.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.shs.contact.R;
import com.shs.contact.control.HttpManager;
import com.shs.contact.helper.JsonRequestCallBack;
import com.shs.contact.helper.LoadDataHandler;
import com.shs.contact.ui.app.SHSApplication;
import com.shs.contact.utils.Md5Utils;
import com.shs.contact.utils.SHSConst;
import com.shs.contact.utils.ToastUtil;

/**
 * Created by wenhai on 2016/2/24.
 */
public class SecondLoginActivity extends BaseActivity {
    //用户名
    private String username;
    private String token;
    private int userID;
    //登陆按钮
    @ViewInject(R.id.login)
    private Button loginButton;
    //密码框
    @ViewInject(R.id.user_password)
    private EditText editPassword;
    //找回密码按钮
    @ViewInject(R.id.find_password)
    private TextView findPwdTextView;

    @OnClick({R.id.find_password, R.id.login})
    public void viewCickListener(View view) {
        switch (view.getId()) {
            case R.id.login:
                jumpMainPage();
                break;
            case R.id.find_password:

                break;
            default:
                break;
        }
    }


    @Override
    public int setLayoutId() {
        return R.layout.activity_secondlogin;
    }

    private void jumpMainPage() {
        String pwd = editPassword.getText().toString().trim();
        RequestParams params = new RequestParams();
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", Md5Utils.encode(pwd));
        HttpManager.post(SHSConst.LOGINUSER, params, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
            @Override
            public void onSuccess(JSONObject jsonResponse, String flag) {
                super.onSuccess(jsonResponse, flag);
                int status = jsonResponse.getInteger(SHSConst.HTTP_STATUS);
                switch (status) {
                    case SHSConst.STATUS_SUCCESS:
                        JSONObject result = jsonResponse.getJSONObject(SHSConst.HTTP_RESULT);
                        userID = result.getInteger("id");
                        token = result.getString("login_token");
                        saveLoginInfo();
                        break;
                    case SHSConst.STATUS_FAIL:
                        ToastUtil.show(SecondLoginActivity.this,"登陆失败");
                        break;
                }
            }

            @Override
            public void onFailure(HttpException e, String arg1, String flag) {
                super.onFailure(e, arg1, flag);
                ToastUtil.show(SecondLoginActivity.this, getString(R.string.net_error));
            }
        }, null));
    }

    private void saveLoginInfo() {
        SHSApplication app = SHSApplication.getInstance();
        app.setToken(token);
        app.setUserID(userID);
        Intent intent=new Intent(this,ContactsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void loadLayout(View v) {

    }

    @Override
    protected void setUpView() {
        username = getIntent().getStringExtra(LoginActivity.INTENT_KEY);
    }
}
