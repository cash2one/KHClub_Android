package com.app.khclub.news.ui.activity;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.model.CirclePageModel;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PublisAnnouncementActivity extends BaseActivityWithTopBar {

	private CirclePageModel circleData;
	  @ViewInject(R.id.content_edit)
      private EditText editContent;
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.acitivity_publish_announcement;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		TextView sendBtn = addRightBtn(getResources().getString(
				R.string.publish_news));
		sendBtn.setTextColor(getResources().getColor(R.color.main_white));
		sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendAnnouncement();
			}
		});
	}

	protected void sendAnnouncement() {
		// TODO Auto-generated method stub
		
		RequestParams params = new RequestParams();
		circleData = (CirclePageModel) getIntent().getSerializableExtra("data");
		
		// 内容
		String content= editContent.getText().toString().trim();
		//Log.i("wwww", content);
		if(content.length()==0){
			Toast.makeText(PublisAnnouncementActivity.this,
					R.string.news_enter_content,
					Toast.LENGTH_SHORT).show();
			       return ;
		}else if(content.length()>140){
			Toast.makeText(PublisAnnouncementActivity.this,
					R.string.announcement_content_too_long,
					Toast.LENGTH_SHORT).show();
			 return ;
		}else {
			params.addBodyParameter("content_text",content);
		}
		// 用户id
				params.addBodyParameter("user_id", circleData.getUserID() + "");
				//.i("wwww", circleData.getUserID());
				// 圈子id
				params.addBodyParameter("circle_id", circleData.getCircleId() + "");
				//Log.i("wwww", circleData.getCircleId());
		HttpManager.post(KHConst.POST_NEW_NOTICE, params,
				new JsonRequestCallBack<String>(new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						hideLoading();
						int status = jsonResponse.getIntValue("status");
						switch (status) {
						case KHConst.STATUS_SUCCESS:
							// toast
							ToastUtil.show(PublisAnnouncementActivity.this,
									R.string.news_publish_success);
							//hideLoading();
							finishWithRight();
							//publishFinishBroadcast();
							break;
						case KHConst.STATUS_FAIL:
							hideLoading();
							Toast.makeText(PublisAnnouncementActivity.this,
									R.string.news_publish_fail,
									Toast.LENGTH_SHORT).show();
							break;
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						hideLoading();
						Toast.makeText(PublisAnnouncementActivity.this,
								R.string.net_error, Toast.LENGTH_SHORT).show();
					}
				}, null));
		finish();
	}

}
