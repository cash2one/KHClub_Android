package com.app.khclub.personal.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.app.khclub.R;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class PersonalFragment extends BaseFragment {

	@ViewInject(R.id.vPager)
	private ViewPager mPager;//页卡内容
	//签名
	@ViewInject(R.id.sign_text_view)
	private TextView signTextView;
	
	@OnClick({R.id.base_tv_back})
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.base_tv_back:
			
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_main_personal;
	}

	@Override
	public void loadLayout(View rootView) {
		initViewPager();
	}

	@Override
	public void setUpViews(View rootView) {

	}
	
	@Override
	public void onResume() {
		super.onResume();
		UserModel userModel = UserManager.getInstance().getUser();
		//姓名
		if (null != userModel.getSignature() && userModel.getSignature().length() > 0) {
			signTextView.setText(userModel.getName());
		}else {
			signTextView.setText("暂无");
		}
	}
	
	 /**
	  * 初始化ViewPager
	 */
	 private void initViewPager() {
	     mPager.setAdapter(new MessageFragmentPagerAdapter(getActivity().getSupportFragmentManager()));
	     mPager.setCurrentItem(0);
	 }
	 
	 private class MessageFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
	
	     public MessageFragmentPagerAdapter(FragmentManager fm) {
	         super(fm);
	     }
	
	     @Override
	     public Fragment getItem(int i) {
	         Fragment fragment = null;
	         switch (i) {
	             case 0:
	             	fragment = new PersonalInfoFragment();
	                 break;
	             case 1:
	             	fragment = new PersonalQrcodeFragment();
	                 break;               
	
	         }
	         return fragment;
	     }
	
	     @Override
	     public int getCount() {
	         return 2;
	     }
	 }

}
