package com.app.khclub.personal.ui.fragment;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.app.khclub.base.utils.KHConst;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OtherPersonalInfoFragment extends BaseFragment {

	//头像
	@ViewInject(R.id.head_image_view)
	private ImageView headImageView;
	//姓名
	@ViewInject(R.id.name_text_view)
	private TextView nameTextView;
	//职业
	@ViewInject(R.id.job_text_view)
	private TextView jobTextView;
	//公司
	@ViewInject(R.id.company_text_view)
	private TextView companyTextView;	
	//电话
	@ViewInject(R.id.phone_number_text_view)
	private TextView phoneTextView;
	//邮箱
	@ViewInject(R.id.email_text_view)
	private TextView emailTextView;
	//邮箱
	@ViewInject(R.id.address_text_view)
	private TextView addressTextView;	
	// 新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	
	
	@Override
	public int setLayoutId() {
		return R.layout.fragment_personal_info;
	}

	@Override
	public void loadLayout(View rootView) {
		// 显示头像的配置
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public void setUpViews(View rootView) {
		
	}
	
	//设置内容
	public void setUIWithModel(UserModel userModel) {
		//头像不为空
		if (null != userModel.getHead_image()) {
			ImageLoader.getInstance().displayImage(KHConst.ATTACHMENT_ADDR+userModel.getHead_image(), headImageView, headImageOptions);	
		}
		//姓名
		if (null != userModel.getName() && userModel.getName().length() > 0) {
			nameTextView.setText(userModel.getName());
		}else {
			nameTextView.setText(R.string.personal_none);
		}
		
		//职业
		if (null != userModel.getJob() && userModel.getJob().length() > 0) {
			jobTextView.setText(userModel.getJob());
		}else {
			jobTextView.setText(R.string.personal_none);
		}		
		//公司
		if (null != userModel.getCompany_name() && userModel.getCompany_name().length() > 0) {
			companyTextView.setText(userModel.getCompany_name());
		}else {
			companyTextView.setText(R.string.personal_none);
		}		
		
		if (userModel.getPhone_state() == UserModel.SeeAll) {
			//电话
			if (null != userModel.getPhone_num() && userModel.getPhone_num().length() > 0) {
				phoneTextView.setText(userModel.getPhone_num());
			}else {
				phoneTextView.setText(R.string.personal_none);
			}	
		}else {
			//电话不可见
			phoneTextView.setText("xxx");
		}
		
		if (userModel.getEmail_state() == UserModel.SeeAll) {
			//邮件
			if (null != userModel.getE_mail() && userModel.getE_mail().length() > 0) {
				emailTextView.setText(userModel.getE_mail());
			}else {
				emailTextView.setText(R.string.personal_none);
			}				
		}else {
			//邮件
			emailTextView.setText("xxx");
		}

		if (userModel.getAddress_state() == UserModel.SeeAll) {
			//地址
			if (null != userModel.getAddress() && userModel.getAddress().length() > 0) {
				addressTextView.setText(userModel.getAddress());
			}else {
				addressTextView.setText(R.string.personal_none);
			}				
		}else {
			addressTextView.setText("xxx");
		}

	}

}
