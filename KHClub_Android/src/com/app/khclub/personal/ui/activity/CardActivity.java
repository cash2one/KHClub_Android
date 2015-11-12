package com.app.khclub.personal.ui.activity;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.ConfigUtils;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CardActivity extends BaseActivityWithTopBar{
	
	//是不是自己
	public static String INTENT_IS_SELF_KEY = "isSelf";
	
	//头像
	@ViewInject(R.id.head_image_view)
	private ImageView headImageView;
	// 姓名
	@ViewInject(R.id.name_text_view)
	private TextView nameTextView;
	// 职位
	@ViewInject(R.id.job_text_view)
	private TextView jobTextView;
	// 公司
	@ViewInject(R.id.company_text_view)
	private TextView companyTextView;	
	// 地址
	@ViewInject(R.id.address_text_view)
	private TextView addressTextView;	
	// 邮箱
	@ViewInject(R.id.email_text_view)
	private TextView emailTextView;
	// 电话
	@ViewInject(R.id.phone_text_view)
	private TextView phoneTextView;
	// 头衔1
	@ViewInject(R.id.title_1_text_view)
	private TextView title1TextView;	
	// 头衔2
	@ViewInject(R.id.title_2_text_view)
	private TextView title2TextView;	
	// 头衔3
	@ViewInject(R.id.title_3_text_view)
	private TextView title3TextView;
	// 头衔4
	@ViewInject(R.id.title_4_text_view)
	private TextView title4TextView;	
	// 新图片缓存工具 头像
	DisplayImageOptions headImageOptions;	
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_card;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		
		// 显示头像的配置
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		
		if (getIntent().getBooleanExtra(INTENT_IS_SELF_KEY, false)) {
			//如果是自己
			setContent(UserManager.getInstance().getUser());
			setSelfTitle();
		}else {
			//别人
			getPersonalInformation(getIntent().getIntExtra(OtherPersonalActivity.INTENT_KEY, 0));
		}
	}

	//设置内容
	private void setContent(UserModel userModel){
		
		//头像不为空
		if (null != userModel.getHead_image()) {
			ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR+userModel.getHead_image(), headImageView, headImageOptions);	
		}
		//姓名
		nameTextView.setText(KHUtils.emptyRetunNone(userModel.getName()));
		//职业
		jobTextView.setText(KHUtils.emptyRetunNone(userModel.getJob()));
		//公司
		companyTextView.setText(KHUtils.emptyRetunNone(userModel.getCompany_name()));
		//电话
		phoneTextView.setText(KHUtils.emptyRetunNone(userModel.getPhone_num()));
		//邮件
		emailTextView.setText(KHUtils.emptyRetunNone(userModel.getE_mail()));
		//地址
		addressTextView.setText(KHUtils.emptyRetunNone(userModel.getAddress()));
	}
	
	//如果是自己的名片
	private void setSelfTitle(){
		//四个头衔
		title1TextView.setText(KHUtils.emptyRetunNone(ConfigUtils.getStringConfig(KHConst.TITLE_1_KEY)));
		title2TextView.setText(KHUtils.emptyRetunNone(ConfigUtils.getStringConfig(KHConst.TITLE_2_KEY)));
		title3TextView.setText(KHUtils.emptyRetunNone(ConfigUtils.getStringConfig(KHConst.TITLE_3_KEY)));
		title4TextView.setText(KHUtils.emptyRetunNone(ConfigUtils.getStringConfig(KHConst.TITLE_4_KEY)));
	}
	
	// 获取用户信息
	private void getPersonalInformation(int uid) {

		String path = KHConst.PERSONAL_INFO + "?" + "uid=" + uid
				+ "&current_id=" + UserManager.getInstance().getUser().getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							JSONObject jResult = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
							//注入数据
							UserModel otherUserModel = new UserModel();
							otherUserModel.setContentWithJson(jResult);
							setContent(otherUserModel);
							
							//设置头衔
							
						}

						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(CardActivity.this,
									R.string.downloading_fail);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1,
							String flag) {
						super.onFailure(arg0, arg1, flag);
						ToastUtil.show(CardActivity.this,
								R.string.net_error);
					}

				}, null));
	}
	
}
