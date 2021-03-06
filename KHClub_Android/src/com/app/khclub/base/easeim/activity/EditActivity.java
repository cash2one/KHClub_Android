package com.app.khclub.base.easeim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.utils.ToastUtil;

public class EditActivity extends BaseActivity{
	private EditText editText;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit);
		
		editText = (EditText) findViewById(R.id.edittext);
		String title = getIntent().getStringExtra("title");
		String data = getIntent().getStringExtra("data");
		if(title != null)
			((TextView)findViewById(R.id.tv_title)).setText(title);
		if(data != null)
			editText.setText(data);
		editText.setSelection(editText.length());
		
	}
	
	
	public void save(View view){
		if (editText.getText().toString().trim().length() < 1) {
			ToastUtil.show(this, R.string.toast_group_not_isnull);
			return;
		}
		
		if (editText.getText().toString().trim().length() > 30) {
			ToastUtil.show(this, R.string.toast_group_too_long);
			return;
		}
		
		setResult(RESULT_OK,new Intent().putExtra("data", editText.getText().toString()));
		finish();
	}
}
