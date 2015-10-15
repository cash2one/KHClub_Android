package com.app.khclub.message.ui.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.easeim.activity.GroupSimpleDetailActivity;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.personal.ui.activity.OtherPersonalActivity;
import com.easemob.chat.EMGroupInfo;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

//同校的人
public class SearchActivity extends BaseActivityWithTopBar {

	// 搜索et
	@ViewInject(R.id.search_edit_text)
	private EditText searchEditText;

	@OnClick({ R.id.txt_search_btn, R.id.add_contacts_layout })
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.txt_search_btn:
			// 搜索
			searchUserOrGroup();
			break;
		case R.id.add_contacts_layout:
			// 添加通讯录好友
			Intent intentToAddContacts = new Intent(SearchActivity.this,
					ContactsUserActivity.class);
			startActivityWithRight(intentToAddContacts);
			break;
		default:
			break;
		}
	}

	@Override
	public int setLayoutId() {
		return R.layout.activity_search_user;
	}

	@Override
	protected void setUpView() {
		setBarText("查找好友/群");
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			/* 隐藏软键盘 */
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			if (inputMethodManager.isActive()) {
				inputMethodManager.hideSoftInputFromWindow(SearchActivity.this
						.getCurrentFocus().getWindowToken(), 0);
			}
			// 点击查询
			searchUserOrGroup();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	// //////////////////////////////////private
	// method//////////////////////////////////////

	/**
	 * 查询
	 * */
	private void searchUserOrGroup() {
		// 当前的ID
		final String currentID = searchEditText.getText().toString().trim();
		if (currentID.equals("")) {
			ToastUtil.show(getApplicationContext(), "内容不能为空");
			return;
		}
		String path = KHConst.SEARCH_USER_OR_GROUP + "?" + "target_id="
				+ currentID;
		LogUtils.i(path, 1);
		showLoading("正在查找...", false);
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();

						if (jsonResponse.getInteger(KHConst.HTTP_STATUS) == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							String type = jResult.getString("type");
							if (type.equals("1")) {
								// 跳转到群结果页面
								EMGroupInfo	group = new EMGroupInfo(currentID, jResult.getString("group_name"));
								Intent intent = new Intent(SearchActivity.this, GroupSimpleDetailActivity.class).
		                        putExtra("groupinfo", group);
								startActivityWithRight(intent);
							} else if (type.equals("0")) {
								// 跳转到其他人页面
								Intent intent = new Intent(SearchActivity.this,
										OtherPersonalActivity.class);
								intent.putExtra(
										OtherPersonalActivity.INTENT_KEY,
										KHUtils.stringToInt(jResult
												.getString("user_id")));
								startActivityWithRight(intent);
							}
						} else {
							ToastUtil.show(SearchActivity.this, "查询失败");
						}

					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						ToastUtil.show(SearchActivity.this, "网络故障请检查");
					}
				}, null));
	}
}
