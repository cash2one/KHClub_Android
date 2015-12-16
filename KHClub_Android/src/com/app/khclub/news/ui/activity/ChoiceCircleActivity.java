package com.app.khclub.news.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.news.ui.model.CircleItemModel;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChoiceCircleActivity extends BaseActivityWithTopBar {

	@ViewInject(R.id.follow_circle_list)
	private ListView listView;
	private List<String> choiceList = new ArrayList<String>();
	
	// 位置listview的适配器
	private HelloHaAdapter<CircleItemModel> circleAdapter;
	// 图片配置
	private DisplayImageOptions options;	
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_choice_circle;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		// 显示图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();		
		
		initList();
		getCollectCardData();
	}
	
	private void initList(){
		// 设置内容
		circleAdapter = new HelloHaAdapter<CircleItemModel>(this, R.layout.adapter_choice_circle) {
			@Override
			protected void convert(final HelloHaBaseAdapterHelper helper, final CircleItemModel item) {
				// 圈子标题
				helper.setText(R.id.circle_name, item.getCircle_name());
				helper.setText(R.id.circle_people_count, item.getFollow_quantity());
				ImageView headImageView = helper.getView(R.id.circle_image);
				String image = item.getCircle_cover_sub_image();
				// 加入图片
				if (image.length() > 0) {
					ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR + image, headImageView,
							options);
				} else {
					headImageView.setImageResource(R.drawable.loading_default);
				}
				
				helper.getView().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (choiceList.contains(helper.getPosition()+"")) {
							choiceList.remove(helper.getPosition()+"");
							helper.getView().setBackgroundResource(R.color.main_white);
						}else {
							choiceList.add(helper.getPosition()+"");
							helper.getView().setBackgroundResource(R.color.main_light_gary);
						}						
					}
				});
			}
		};

		// 适配器绑定
		listView.setAdapter(circleAdapter);		
		
	}

	/**
	 * 获所有收藏的名片信息
	 * */
	private void getCollectCardData() {
		String path = KHConst.GET_MY_FOLLOW_CIRCLE_LIST + "?" + "&user_id="
				+ UserManager.getInstance().getUser().getUid();
		LogUtils.i("path=" + path);
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse
									.getJSONObject(KHConst.HTTP_RESULT);
							// 获取数据列表
							List<CircleItemModel> list = JSON.parseArray(jResult.getString("list"), CircleItemModel.class);
							circleAdapter.replaceAll(list);							
						}

						if (status == KHConst.STATUS_FAIL) {
							
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
					}
				}, null));
	}	

}
