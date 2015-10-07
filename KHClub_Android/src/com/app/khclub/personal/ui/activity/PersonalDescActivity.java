package com.app.khclub.personal.ui.activity;

import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.khclub.R;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class PersonalDescActivity extends BaseActivityWithTopBar {

	//编辑内容
	@ViewInject(R.id.desc_edit_text)
	private EditText descEditText;
	//可见选择框
	@ViewInject(R.id.state_check_box)
	private CheckBox stateBox;
	//可见外部布局
	@ViewInject(R.id.state_layout)
	private LinearLayout stateLayout;	
	
	//intent
	public static final String INTENT_KEY = "intentKey";
	//状态返回
	public static final String INTENT_STATE_KEY = "stateKey";
	//签名
	public static final int SIGN_KEY = 100;
	//电话
	public static final int PHONE_KEY = 101;
	//住址
	public static final int ADDRESS_KEY = 102;
	//公司
	public static final int COMPANY_KEY = 103;
	//邮箱
	public static final int EMAIL_KEY = 104;
	//当前类型
	private int currentType = SIGN_KEY;
	
	@OnClick(value={R.id.base_ll_right_btns,R.id.personal_sign_activity})
	private void clickEvent(View view){
		
		switch (view.getId()) {
		case R.id.base_ll_right_btns:
			//返回传值
			Intent intent = new Intent();
			intent.putExtra(INTENT_KEY, descEditText.getText().toString().trim());
			int checked = 0;
			if (stateBox.isChecked()) {
				checked = 1;
			}
			intent.putExtra(INTENT_STATE_KEY, checked);
			setResult(currentType, intent);
			finishWithRight();
			break;
		case R.id.personal_sign_activity:
			//收键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		default:
			break;
		}
		
	}
	
	@Override
	public int setLayoutId() {
		try {
			Intent intent = getIntent();
			currentType = intent.getIntExtra(INTENT_KEY, SIGN_KEY);
		} catch (Exception e) {
		}
		
		if (currentType == PHONE_KEY || currentType == EMAIL_KEY || currentType == COMPANY_KEY) {
			return R.layout.activity_personal_phone;
		}
		return R.layout.activity_personal_sign;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		switch (currentType) {
		case SIGN_KEY:
			//签名
			setBarText(getResources().getString(R.string.personal_signature)); 
			descEditText.setHint(R.string.personal_enter_sign);
			descEditText.setText(UserManager.getInstance().getUser().getSignature());
			stateLayout.setVisibility(View.GONE);
			break;
		case ADDRESS_KEY:
			//地址
			setBarText(getResources().getString(R.string.personal_address)); 
			descEditText.setHint(R.string.personal_enter_address);
			descEditText.setText(UserManager.getInstance().getUser().getAddress());
			//状态
			if (UserManager.getInstance().getUser().getAddress_state() == 1) {
				stateBox.setChecked(true);	
			}
			break;			
		case PHONE_KEY:
			//电话
			setBarText(getResources().getString(R.string.personal_phone)); 
			descEditText.setHint(R.string.personal_enter_phone);
			descEditText.setText(UserManager.getInstance().getUser().getPhone_num());
			//状态
			if (UserManager.getInstance().getUser().getPhone_state() == 1) {
				stateBox.setChecked(true);	
			}
			break;
		case COMPANY_KEY:
			//公司
			setBarText(getResources().getString(R.string.personal_company)); 
			descEditText.setHint(R.string.personal_enter_company);
			descEditText.setText(UserManager.getInstance().getUser().getCompany_name());
			//状态
			if (UserManager.getInstance().getUser().getCompany_state() == 1) {
				stateBox.setChecked(true);	
			}
			break;
		case EMAIL_KEY:
			//邮箱
			setBarText(getResources().getString(R.string.personal_email)); 
			descEditText.setHint(R.string.personal_enter_email);
			descEditText.setText(UserManager.getInstance().getUser().getE_mail());
			//状态
			if (UserManager.getInstance().getUser().getEmail_state() == 1) {
				stateBox.setChecked(true);	
			}
			break;			
		default:
			break;
		}
		
		addRightBtn(getResources().getString(R.string.alert_save));
		descEditText.setSelection(descEditText.getText().length());
	}

}
