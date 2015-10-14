/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.khclub.base.easeim.activity;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.core.br;
import com.app.khclub.R;
import com.app.khclub.base.easeim.db.InviteMessgeDao;
import com.app.khclub.base.easeim.domain.InviteMessage.InviteMesageStatus;
import com.app.khclub.base.easeim.utils.UserUtils;
import com.app.khclub.base.helper.JsonRequestCallBack;
import com.app.khclub.base.helper.LoadDataHandler;
import com.app.khclub.base.manager.HttpManager;
import com.app.khclub.base.manager.UserManager;
import com.app.khclub.base.ui.view.CustomSelectPhotoDialog;
import com.app.khclub.base.ui.view.gallery.imageloader.GalleyActivity;
import com.app.khclub.base.utils.ConfigUtils;
import com.app.khclub.base.utils.FileUtil;
import com.app.khclub.base.utils.KHConst;
import com.app.khclub.base.utils.KHUtils;
import com.app.khclub.base.utils.LogUtils;
import com.app.khclub.base.utils.ToastUtil;
import com.easemob.chat.EMChatManager;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewGroupActivity extends BaseActivity {
	
	public static final int TAKE_PHOTO = 1;// 拍照
	public static final int ALBUM_SELECT = 2;// 相册选取
	public static final int PHOTO_RESOULT = 4;// 结果
	public static final int GET_DEPARTMENT_REQUEST_CODE = 5;
	public static final int CONTACT_RESULT = 100;// 通讯录选人
	public static final String IMAGE_UNSPECIFIED = "image/*";
	//创建成功
	public static final String NEW_TOPIC_OK = "newTopicOK*";

	// 图片名字
	private String tmpImageName;
	// 当前图片名字
	private String currentImageName;	
	
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private ImageView groupImageView;
//	private EditText introductionEditText;
//	private CheckBox checkBox;
//	private CheckBox memberCheckbox;
//	private LinearLayout openInviteContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
		groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
		groupImageView = (ImageView) findViewById(R.id.group_image);
		groupImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showChoiceImageAlert();
			}
		});
//		introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
//		checkBox = (CheckBox) findViewById(R.id.cb_public);
//		memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
//		openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);
//		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked){
//					openInviteContainer.setVisibility(View.INVISIBLE);
//				}else{
//					openInviteContainer.setVisibility(View.VISIBLE);
//				}
//			}
//		});
	}

	/**
	 * @param v
	 */
	public void save(View v) {
		String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
		String name = groupNameEditText.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Intent intent = new Intent(this, AlertDialog.class);
			intent.putExtra("msg", str6);
			startActivity(intent);
		} else {
			// 进通讯录选人
			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), CONTACT_RESULT);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
		final String st2 = getResources().getString(R.string.Failed_to_create_groups);
		if (resultCode == RESULT_OK) {
			
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int[] screenSize = new int[] { dm.widthPixels, dm.heightPixels };
			// 头像需要缩放
			switch (requestCode) {
			case TAKE_PHOTO:// 当选择拍照时调用
				// 图片压缩
				if (FileUtil.tempToLocalPath(tmpImageName, screenSize[0],
						screenSize[1])) {
					displayImage(tmpImageName);
				}
				break;
			case ALBUM_SELECT:// 当选择从本地获取图片时
				if (data != null) {
					@SuppressWarnings("unchecked")
					List<String> resultList = (List<String>) data
							.getSerializableExtra(GalleyActivity.INTENT_KEY_PHOTO_LIST);
					// 循环处理图片
					for (String fileRealPath : resultList) {
						// 只取一张
						if (fileRealPath != null
								&& FileUtil.tempToLocalPath(fileRealPath,
										tmpImageName, screenSize[0],
										screenSize[1])) {
							displayImage(tmpImageName);
							break;
						}
					}
				}
				break;
			case CONTACT_RESULT:
				//新建群组
				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage(st1);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();
				String[] members = data.getStringArrayExtra("newmembers");
				// 参数设置
				RequestParams params = new RequestParams();
				params.addBodyParameter("user_id", UserManager.getInstance().getUser().getUid()+"");
				params.addBodyParameter("group_name", groupNameEditText.getText().toString().trim());
				if (currentImageName != null) {
					File uplodaFile = new File(FileUtil.BIG_IMAGE_PATH + currentImageName);
					if (!uplodaFile.exists()) {
						return;
					}
					params.addBodyParameter("image", uplodaFile);					
				}
				//成员拼接
				StringBuffer membersBuffer = new StringBuffer();
				for (int i=0; i< members.length; i++) {
					String string = members[i];
					if (i== members.length - 1) {
						membersBuffer.append(string);						
					}else {
						membersBuffer.append(string+",");	
					}
				}
				params.addBodyParameter("members", membersBuffer.toString());
				//先处理自己数据库
				HttpManager.post(KHConst.CREATE_GROUP, params,
						new JsonRequestCallBack<String>(new LoadDataHandler<String>() {
							@Override
							public void onSuccess(JSONObject jsonResponse, String flag) {
								super.onSuccess(jsonResponse, flag);
								int status = jsonResponse.getInteger(KHConst.HTTP_STATUS);
								progressDialog.dismiss();
								if (status == KHConst.STATUS_SUCCESS) {
									JSONObject result = jsonResponse.getJSONObject(KHConst.HTTP_RESULT);
									//缓存二维码和图片
									ConfigUtils.saveConfig(result.getString("group_id")+UserUtils.GROUP_AVATARKEY, KHConst.ATTACHMENT_ADDR+result.getString("group_cover"));
									ConfigUtils.saveConfig(result.getString("group_id")+UserUtils.GROUP_QRCODEKEY, KHConst.ROOT_PATH+result.getString("group_qr_code"));
									setResult(RESULT_OK);
									finish();
									// 删除临时文件
									File tmpFile = new File(FileUtil.BIG_IMAGE_PATH
											+ currentImageName);
									if (tmpFile.exists()) {
										tmpFile.delete();
									}
								}else {
									Toast.makeText(NewGroupActivity.this, st2, 1).show();									
								}
							}
							@Override
							public void onFailure(HttpException arg0, String arg1,
									String flag) {
								super.onFailure(arg0, arg1, flag);
								ToastUtil.show(NewGroupActivity.this, R.string.net_error);								
								progressDialog.dismiss();
							}
						}, null));					
				break;
			}
			
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					// 调用sdk创建群组方法
//					String groupName = groupNameEditText.getText().toString().trim();
//					String desc = introductionEditText.getText().toString();
//					String[] members = data.getStringArrayExtra("newmembers");
//					try {
//						if(checkBox.isChecked()){
//							//创建公开群，此种方式创建的群，可以自由加入
//							//创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
//						    EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true,200);
//						}else{
//							//创建不公开群
//						    EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(),200);
//						}
//						runOnUiThread(new Runnable() {
//							public void run() {
//								progressDialog.dismiss();
//								setResult(RESULT_OK);
//								finish();
//							}
//						});
//					} catch (final EaseMobException e) {
//						runOnUiThread(new Runnable() {
//							public void run() {
//								progressDialog.dismiss();
//								Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), 1).show();
//							}
//						});
//					}
//					
//				}
//			}).start();
		}
	}

	public void back(View view) {
		finish();
	}
	
	public void displayImage(String imagePath) {

		currentImageName = tmpImageName;

		DisplayImageOptions headImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar)
				.cacheInMemory(false).cacheOnDisk(false)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoader.getInstance().displayImage(
				"file://" + FileUtil.BIG_IMAGE_PATH + imagePath,
				groupImageView, headImageOptions);
	}
	
	private void showChoiceImageAlert() {

		// 设置为头像
		final CustomSelectPhotoDialog selectDialog = new CustomSelectPhotoDialog(
				this);
		selectDialog.show();
		selectDialog
				.setClicklistener(new CustomSelectPhotoDialog.ClickListenerInterface() {

					@Override
					public void onSelectGallery() {
						// 相册
						tmpImageName = KHUtils.getPhotoFileName() + "";
						// 相册
						Intent intentAlbum = new Intent(
								NewGroupActivity.this, GalleyActivity.class);
						intentAlbum.putExtra(
								GalleyActivity.INTENT_KEY_SELECTED_COUNT, 0);
						intentAlbum.putExtra(GalleyActivity.INTENT_KEY_ONE,
								true);
						startActivityForResult(intentAlbum, ALBUM_SELECT);
						selectDialog.dismiss();
					}

					@Override
					public void onSelectCamera() {
						// 相机
						Intent intentCamera = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						tmpImageName = KHUtils.getPhotoFileName() + "";
						File tmpFile = new File(FileUtil.TEMP_PATH
								+ tmpImageName);
						intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(tmpFile));
						startActivityForResult(intentCamera, TAKE_PHOTO);
						selectDialog.dismiss();
					}

				});
	}
}
