package com.shs.contact.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.shs.contact.R;
import com.shs.contact.control.HttpManager;
import com.shs.contact.helper.JsonRequestCallBack;
import com.shs.contact.helper.LoadDataHandler;
import com.shs.contact.utils.SHSConst;

/**
 * Created by wenhai on 2016/2/22.
 */
public class LoginActivity extends BaseActivity {
    public final static String INTENT_KEY = "username";
    @ViewInject(R.id.username)
    private EditText editText;

    @OnClick(R.id.login)
    public void viewCickListener(View view) {
        switch (view.getId()) {
            case R.id.login:
                loginClick();
                break;
            default:
                break;
        }
    }

    private void loginClick() {
        String userphone = editText.getText().toString();
        judgeRegister(userphone);
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void loadLayout(View v) {

    }

    @Override
    protected void setUpView() {

    }

    private void judgeRegister(final String userphone) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("username", userphone);
        Log.i("wx", SHSConst.ISUSER + "?username=" + userphone);
        HttpManager.post(SHSConst.ISUSER, params, new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

                    private Intent intent;

                    @Override
                    public void onSuccess(JSONObject jsonResponse, String flag) {
                        // TODO Auto-generated method stub
                        super.onSuccess(jsonResponse, flag);
                        Log.i("wx", jsonResponse.toJSONString() + "");
                        int status = jsonResponse.getInteger(SHSConst.HTTP_STATUS);
                        switch (status) {
                            case SHSConst.STATUS_SUCCESS:
                                JSONObject result = jsonResponse.getJSONObject(SHSConst.HTTP_RESULT);
                                String direction = result.getString("direction");

                                if ("2".equals(direction)) {
                                    intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                    intent.putExtra(INTENT_KEY, userphone);
                                    startActivity(intent);
                                }else {
                                    intent=new Intent(LoginActivity.this,SecondLoginActivity.class);
                                    intent.putExtra(INTENT_KEY, userphone);
                                    startActivity(intent);
                                }
                                break;
                            case SHSConst.STATUS_FAIL:
                                hideLoading();
                                Toast.makeText(LoginActivity.this, "网络错误",
                                        Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String arg1, String flag) {
                        // TODO Auto-generated method stub
                        super.onFailure(e, arg1, flag);
                        //hideLoading();
                        Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                }, null));
    }
}
