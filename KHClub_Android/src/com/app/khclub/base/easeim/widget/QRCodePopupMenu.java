package com.app.khclub.base.easeim.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.app.khclub.R;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.model.UserModel;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.ToastUtil;
import com.lidroid.xutils.exception.HttpException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 二维码弹出框
 * */
public class QRCodePopupMenu extends PopupWindow {

	// 布局
	private View conentView;
	// 二维码图片
	private ImageView qrView;
	// 显示图片配置
	private DisplayImageOptions options;
	//
	private Context mContext;

	@SuppressLint("NewApi")
	public QRCodePopupMenu(final Context context) {
		init();
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 获取布局
		conentView = inflater.inflate(R.layout.popup_window_qr_code, null);
		// 根布局
		((LinearLayout) conentView.findViewById(R.id.layout_qr_root_view))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						QRCodePopupMenu.this.dismiss();
					}
				});
		// 获取二维码图
		qrView = (ImageView) conentView.findViewById(R.id.img_qr_code);
		// 设置PopupWindow的View
		this.setContentView(conentView);
		// 设置窗体的尺寸
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 背景变暗
		this.setBackgroundDrawable(new ColorDrawable(0x00000000));
		// 设置窗体动画效果
		this.setAnimationStyle(R.style.anim_qrcode_popup);
	}

	/**
	 * 初始化
	 * */
	private void init() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 显示窗体
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, 0, 0);
		} else {
			this.dismiss();
		}
	}

	/**
	 * 设置二维码
	 */
	public void setQRcode(boolean isGroup) {
		if (isGroup) {
			setGroupQRCode();
		} else {
			setUserQRCode();
		}
	}

	/**
	 * 设置群的二维码
	 * */
	private void setGroupQRCode() {

	}

	/**
	 * 设置用户的二维码
	 * */
	private void setUserQRCode() {
		// 从网上获取一次
		String path = KHConst.GET_USER_QRCODE + "?" + "user_id="
				+ UserManager.getInstance().getUser().getUid();
		HttpManager.get(path, new JsonRequestCallBack<String>(
				new LoadDataHandler<String>() {

					@Override
					public void onSuccess(JSONObject jsonResponse, String flag) {
						super.onSuccess(jsonResponse, flag);
						int status = jsonResponse
								.getInteger(KHConst.HTTP_STATUS);
						if (status == KHConst.STATUS_SUCCESS) {
							String qrpath = jsonResponse
									.getString(KHConst.HTTP_RESULT);
							// 本地缓存
							ImageLoader.getInstance()
									.displayImage(KHConst.ROOT_PATH + qrpath,
											qrView, options);
							UserManager.getInstance().getUser()
									.setQr_code(qrpath);
							UserManager.getInstance().saveAndUpdate();

						}
						if (status == KHConst.STATUS_FAIL) {
							ToastUtil.show(mContext, R.string.net_error);
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
