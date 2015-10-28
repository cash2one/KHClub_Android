package com.app.khclub.personal.ui.fragment;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OtherPersonalInfoFragment extends BaseFragment {

	// 头像
	@ViewInject(R.id.head_image_view)
	private ImageView headImageView;
	// 姓名
	@ViewInject(R.id.name_text_view)
	private TextView nameTextView;
	// 二级姓名
	@ViewInject(R.id.second_name_text_view)
	private TextView secondNameTextView;
	// 职业
	@ViewInject(R.id.job_text_view)
	private TextView jobTextView;
	// 公司
	@ViewInject(R.id.company_text_view)
	private TextView companyTextView;
	// 电话
	@ViewInject(R.id.phone_number_text_view)
	private TextView phoneTextView;
	// 邮箱
	@ViewInject(R.id.email_text_view)
	private TextView emailTextView;
	// 邮箱
	@ViewInject(R.id.address_text_view)
	private TextView addressTextView;
	// 收藏按钮
	@ViewInject(R.id.text_collect_btn)
	private ImageView collectBtn;
	// 新图片缓存工具 头像
	DisplayImageOptions headImageOptions;
	// 是否正在传输数据
	private boolean isUploadData = false;
	// 用户ID
	private int userId = -1;
	// 是否收藏了该名片
	private boolean isCollected = false;

	@OnClick({ R.id.text_collect_btn })
	private void clickEvent(View view) {
		switch (view.getId()) {
		case R.id.text_collect_btn:
			// 收藏与取消收藏名片
			if (isCollected) {
				collectCardDelete(String.valueOf(userId));
				isCollected = false;
			} else {
				collectCard(String.valueOf(userId));
				isCollected = true;
			}

			break;
		default:
			break;
		}

	}

	@Override
	public int setLayoutId() {
		return R.layout.fragment_personal_info;
	}

	@Override
	public void loadLayout(View rootView) {
		// 显示头像的配置
		headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public void setUpViews(View rootView) {

	}

	// 设置内容
	public void setUIWithModel(UserModel userModel, boolean isFriend,
			int isCollect, String remark) {
		// 获取uid
		userId = userModel.getUid();
		if (isCollect == 0) {
			isCollected = false;
		} else {
			isCollected = true;
		}

		// 头像不为空
		if (null != userModel.getHead_image()) {
			ImageLoader.getInstance().displayImage(
					KHConst.ATTACHMENT_ADDR + userModel.getHead_image(),
					headImageView, headImageOptions);
		}
		// 姓名
		if (null != userModel.getName() && userModel.getName().length() > 0) {
			nameTextView.setText(userModel.getName());
		} else {
			nameTextView.setText(R.string.personal_none);
		}
		// 备注
		if (null != remark && remark.length() > 0) {
			nameTextView.setText(remark);
			secondNameTextView.setText(userModel.getName());
		}

		// 职业
		if (null != userModel.getJob() && userModel.getJob().length() > 0) {
			jobTextView.setText(userModel.getJob());
		} else {
			jobTextView.setText(R.string.personal_none);
		}
		// 公司
		if (null != userModel.getCompany_name()
				&& userModel.getCompany_name().length() > 0) {
			companyTextView.setText(userModel.getCompany_name());
		} else {
			companyTextView.setText(R.string.personal_none);
		}

		// 电话
		if (null != userModel.getPhone_num()
				&& userModel.getPhone_num().length() > 0) {
			phoneTextView.setText(userModel.getPhone_num());
		} else {
			phoneTextView.setText(R.string.personal_none);
		}
		// 邮件
		if (null != userModel.getE_mail() && userModel.getE_mail().length() > 0) {
			emailTextView.setText(userModel.getE_mail());
		} else {
			emailTextView.setText(R.string.personal_none);
		}
		// 地址
		if (null != userModel.getAddress()
				&& userModel.getAddress().length() > 0) {
			addressTextView.setText(userModel.getAddress());
		} else {
			addressTextView.setText(R.string.personal_none);
		}

		// 不是好友
		if (!isFriend) {
			if (userModel.getPhone_state() == UserModel.SeeOnlyFriends) {
				// 电话不可见
				phoneTextView.setText("********");
			}
			if (userModel.getEmail_state() == UserModel.SeeOnlyFriends) {
				// 邮件
				emailTextView.setText("********");
			}
			if (userModel.getAddress_state() == UserModel.SeeOnlyFriends) {
				// 地址
				addressTextView.setText("********");
			}
		}

		// 收藏按钮
		if ((userId != UserManager.getInstance().getUser().getUid())
				&& !isFriend) {
			// 不是自己的主页并且不是好友
			collectBtn.setVisibility(View.VISIBLE);
			
			// 如果未收藏
			if (isCollected) {
				collectBtn.setImageResource(R.drawable.iconfont_collected);
			} else {
				collectBtn.setImageResource(R.drawable.iconfont_collect);
			}
		}
	}

	// 设置姓名部分 PS：这个位置也显示备注
	public void setNameTextView(String name, String secondName) {
		// 姓名部分
		if (null != name && name.length() > 0) {
			nameTextView.setText(name);
		} else {
			nameTextView.setText(R.string.personal_none);
		}
		// 二级
		secondNameTextView.setText(secondName);

	}

	/**
	 * 收藏名片网络请求
	 * 
	 * @param 被收藏者的Id
	 */
	private void collectCard(String targetId) {
		if (!isUploadData) {
			isUploadData = true;
			collectBtn.setImageResource(R.drawable.iconfont_collected);
			// 参数设置
			RequestParams params = new RequestParams();
			params.addBodyParameter("user_id", String.valueOf(UserManager
					.getInstance().getUser().getUid()));
			params.addBodyParameter("target_id", targetId);

			HttpManager.post(KHConst.COLLECT_CARD, params,
					new JsonRequestCallBack<String>(
							new LoadDataHandler<String>() {

								@Override
								public void onSuccess(JSONObject jsonResponse,
										String flag) {
									super.onSuccess(jsonResponse, flag);
									int status = jsonResponse
											.getInteger(KHConst.HTTP_STATUS);
									if (status == KHConst.STATUS_SUCCESS) {
										// 收藏成功
										ToastUtil
												.show(getActivity(),
														getResources()
																.getString(
																		R.string.collect_success));
									}

									if (status == KHConst.STATUS_FAIL) {
										collectBtn
												.setImageResource(R.drawable.iconfont_collect);
										ToastUtil.show(
												getActivity(),
												getResources().getString(
														R.string.collect_fail));
									}
									isUploadData = false;
								}

								@Override
								public void onFailure(HttpException arg0,
										String arg1, String flag) {
									super.onFailure(arg0, arg1, flag);
									collectBtn
											.setImageResource(R.drawable.iconfont_collect);
									ToastUtil.show(
											getActivity(),
											getResources().getString(
													R.string.collect_fail));
									isUploadData = false;
								}
							}, null));
		}
	}

	/**
	 * 取消收藏名片
	 * */
	private void collectCardDelete(String targetId) {

		if (!isUploadData) {
			isUploadData = true;
			collectBtn.setImageResource(R.drawable.iconfont_collect);
			// 参数设置
			RequestParams params = new RequestParams();
			params.addBodyParameter("user_id", String.valueOf(UserManager
					.getInstance().getUser().getUid()));
			params.addBodyParameter("target_id", targetId);

			HttpManager.post(KHConst.COLLECT_CARD_DELETE, params,
					new JsonRequestCallBack<String>(
							new LoadDataHandler<String>() {

								@Override
								public void onSuccess(JSONObject jsonResponse,
										String flag) {
									super.onSuccess(jsonResponse, flag);
									int status = jsonResponse
											.getInteger(KHConst.HTTP_STATUS);
									if (status == KHConst.STATUS_SUCCESS) {
										ToastUtil
												.show(getActivity(),
														getResources()
																.getString(
																		R.string.cancel_collect_success));
									}

									if (status == KHConst.STATUS_FAIL) {
										collectBtn
												.setImageResource(R.drawable.iconfont_collect);
										ToastUtil
												.show(getActivity(),
														getResources()
																.getString(
																		R.string.cancel_collect_fail));
									}
									isUploadData = false;
								}

								@Override
								public void onFailure(HttpException arg0,
										String arg1, String flag) {
									super.onFailure(arg0, arg1, flag);
									collectBtn
											.setImageResource(R.drawable.iconfont_collected);
									ToastUtil
											.show(getActivity(),
													getResources()
															.getString(
																	R.string.cancel_collect_fail));
									isUploadData = false;
								}
							}, null));
		}
	}
}
