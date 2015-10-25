package com.app.khclub.login.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AreaCodeActivity extends BaseActivityWithTopBar {

	public final static String BACK_DATA = "Area Code";
	// 数据key
	private final static String AREA_NAME = "area_name";
	private final static String AREA_PHONE_NUM = "area_code";

	// listview
	@ViewInject(R.id.area_code_list)
	private PullToRefreshListView codeListview;
	// 符合显示格式的数据
	private List<Map<String, String>> dataList;
	// 主适配器
	private HelloHaAdapter<Map<String, String>> listAdapter;

	@Override
	public int setLayoutId() {
		return R.layout.activity_area_code;
	}

	@Override
	protected void setUpView() {
		setBarText(getResources().getString(R.string.select_area_code));
		dataInit();
		lisviewSet();
	}

	/**
	 * 数据初始化
	 * */
	private void dataInit() {
		dataList = new ArrayList<Map<String, String>>();
		Map<String, String> tempMap1 = new HashMap<String, String>();
		if (getResources().getConfiguration().locale.getCountry().equals("CN")
				|| getResources().getConfiguration().locale.getCountry()
						.equals("TW")) {
			// 中国
			tempMap1.put(AREA_NAME, "中国大陆");
			tempMap1.put(AREA_PHONE_NUM, "+86");
			dataList.add(tempMap1);
			// 新加坡
			Map<String, String> tempMap2 = new HashMap<String, String>();
			tempMap2.put(AREA_NAME, "新加坡");
			tempMap2.put(AREA_PHONE_NUM, "+65");
			dataList.add(tempMap2);
		} else {
			// 中国
			tempMap1.put(AREA_NAME, "China");
			tempMap1.put(AREA_PHONE_NUM, "+86");
			dataList.add(tempMap1);
			// 新加坡
			Map<String, String> tempMap2 = new HashMap<String, String>();
			tempMap2.put(AREA_NAME, "Singapore");
			tempMap2.put(AREA_PHONE_NUM, "+65");
			dataList.add(tempMap2);
		}
	}

	/**
	 * 数据绑定
	 * */
	private void lisviewSet() {
		// 设置刷新模式
		codeListview.setMode(Mode.DISABLED);
		//
		listAdapter = new HelloHaAdapter<Map<String, String>>(
				AreaCodeActivity.this, R.layout.area_code_item, dataList) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper,
					Map<String, String> item) {
				helper.setText(R.id.area_name, item.get(AREA_NAME));
				helper.setText(R.id.area_phone_code, item.get(AREA_PHONE_NUM));

				final int postion = helper.getPosition();
				OnClickListener listener = new OnClickListener() {

					@Override
					public void onClick(View view) {
						// 点击
						Intent intent = new Intent();
						intent.putExtra(BACK_DATA,
								dataList.get(postion).get(AREA_PHONE_NUM));
						setResult(RESULT_OK, intent);
						finishWithRight();
					}
				};
				helper.setOnClickListener(R.id.layout_area_code_rootview,
						listener);
			}
		};
		// 设置不可点击
		listAdapter.setItemsClickEnable(false);
		codeListview.setAdapter(listAdapter);
	}

}