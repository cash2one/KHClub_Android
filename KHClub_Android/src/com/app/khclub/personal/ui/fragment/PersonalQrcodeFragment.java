package com.app.khclub.personal.ui.fragment;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.ToastUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalQrcodeFragment extends BaseFragment {

	@ViewInject(R.id.qr_code_image_view)
	private ImageView qrcodeImageView;
	
	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_personal_qrcode;
	}

	@Override
	public void loadLayout(View rootView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUpViews(View rootView) {
		final DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.loading_default)
		.showImageOnFail(R.drawable.loading_default)
		.cacheInMemory(true).cacheOnDisk(true)
		.bitmapConfig(Bitmap.Config.RGB_565).build();
		UserModel user = UserManager.getInstance().getUser();
		//不存在获取 存在
		if (user.getQr_code() != null && user.getQr_code().length() > 0) {
			ImageLoader.getInstance().displayImage(KHConst.ROOT_PATH+user.getQr_code(), qrcodeImageView, options);	
		}
		
		//从网上获取一次
		String path = KHConst.GET_USER_QRCODE+"?"+"user_id="+user.getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							String qrpath = jsonResponse.getString(KHConst.HTTP_RESULT);
							//本地缓存
							ImageLoader.getInstance().displayImage(KHConst.ROOT_PATH+qrpath, qrcodeImageView, options);
							UserManager.getInstance().getUser().setQr_code(qrpath);
							UserManager.getInstance().saveAndUpdate();
							
						}
						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(getActivity(), R.string.net_error);
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
