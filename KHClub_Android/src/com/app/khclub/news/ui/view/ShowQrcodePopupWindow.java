package com.app.khclub.news.ui.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.app.khclub.R;
import com.app.khclub.base.easeim.widget.QRCodePopupMenu;
import com.app.khclub.base.ui.activity.BigImgLookActivity;
import com.app.khclub.base.ui.view.CustomListViewDialog;
import com.app.khclub.base.ui.view.CustomListViewDialog.ClickCallBack;
import com.app.khclub.base.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.animation.AnimatorSet.Builder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ShowQrcodePopupWindow extends PopupWindow {
	// 布局
	private View conentView;
	// 二维码图片
	private ImageView qrView;
	// 显示图片配置
	private DisplayImageOptions options;
	//
	private Context mContext;
	// 地址
	private String groupID;
	// 判断是否是长点击
	boolean isLongClick = false;
	private LayoutInflater inflater;

	@SuppressLint("NewApi")
	public ShowQrcodePopupWindow(final Context context) {

		mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 获取布局
		conentView = inflater.inflate(R.layout.qrcode_show_popupwindow, null);
		// 获取二维码图
		init();
		qrView = (ImageView) conentView.findViewById(R.id.window_show_qrcode);
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
		this.setBackgroundDrawable(new ColorDrawable(0xff000000));
		// 设置窗体动画效果
		this.setAnimationStyle(R.style.anim_qrcode_popup);

	}

	/**
	 * 初始化
	 */
	private void init() {
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading_default)
				.showImageOnFail(R.drawable.loading_default).cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		conentView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ShowQrcodePopupWindow.this.dismiss();
			}
		});

	}

	/**
	 * 显示窗体
	 */
	public void showPopupWindow(View parent, boolean sign) {
		if (!this.isShowing()) {
			// this.showAsDropDown(parent, -1, -1);
			this.showAtLocation(parent, Gravity.CENTER, 0, 0);
			isLongClick = sign;
			longClick();
		} else {
			this.dismiss();

		}
	}

	private void longClick() {
		// TODO Auto-generated method stub
		if (isLongClick) {
			conentView.setOnLongClickListener(new OnLongClickListener() {

				private AlertDialog dialog;

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					// 长按弹出确定对话框
					AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
				    View view = inflater.inflate(R.layout.pupopwindow_save, null);
					TextView save = (TextView) view.findViewById(R.id.tv_save);
					ImageView cancel = (ImageView) view.findViewById(R.id.cancel_save);
					dialog = builder.show();
			         WindowManager.LayoutParams params =dialog.getWindow().getAttributes();
			         params.width=200;
			         params.height=100;
			        dialog.getWindow().setAttributes(params);
					dialog.setContentView(view);
					//dialog.show();
//					final PopupWindow popupWindow = new PopupWindow();
//					popupWindow.setContentView(view);
//					popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//					//点击下载图片
					save.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							int nameIndex = getGroupID().lastIndexOf("/");
							String Name = getGroupID().substring(nameIndex + 1);
							download(getGroupID(), Name);
							dialog.dismiss();
						}
					});
					//点击下载图片
					cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
					return true;
				}
			});
		}
	}

	/**
	 * 设置二维码
	 */
	public void setQRcode(boolean isGroup) {
		if (isGroup) {
			setGroupQRCode();

		} else {

		}
	}

	/**
	 * 设置群的二维码
	 */
	private void setGroupQRCode() {
		// 显示图片
		// ImageLoader.getInstance().displayImage(groupID, qrView, options);

		Log.i("wx", "ss");
		// UserUtils.setGroupQRCode(mContext, groupID, qrView);
	}

	public String getGroupID() {
		return groupID;

	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
		ImageLoader.getInstance().displayImage(groupID, qrView, options);
	}

	// 下载图片
	private void download(String Url, final String imageName) {
		HttpUtils http = new HttpUtils();
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File appDir = new File(Environment.getExternalStorageDirectory(), "KHClub");
			http.download(Url, appDir + "/" + imageName, true, true, new RequestCallBack<File>() {
				@Override
				public void onStart() {
				}

				@Override
				public void onLoading(long total, long current, boolean isUploading) {
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					ToastUtil.show(mContext, msg);
				}

				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					ToastUtil.show(mContext,
							mContext.getString(R.string.save_picture_as) + responseInfo.result.getPath());
					// 通知图库更新
					mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
							Uri.parse("file://" + Environment.getExternalStorageDirectory())));
				}
			});
		} else {
			ToastUtil.show(mContext, R.string.sdcard_inexistence);
		}
	}

}
