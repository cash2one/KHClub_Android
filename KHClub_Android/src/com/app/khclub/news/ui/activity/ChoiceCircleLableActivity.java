package com.app.khclub.news.ui.activity;

import com.app.khclub.R;
import com.app.khclub.base.ui.activity.BaseActivityWithTopBar;
import com.app.khclub.base.utils.ToastUtil;
import com.app.khclub.news.ui.utils.CircleTypeConstant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class ChoiceCircleLableActivity extends BaseActivityWithTopBar {
	private static final int CHOICE_RESOULT = 5;
	@ViewInject(R.id.circle_lable1)
	private TextView labletext1;
	@ViewInject(R.id.circle_lable2)
	private TextView labletext2;
	@ViewInject(R.id.circle_lable3)
	private TextView labletext3;
	@ViewInject(R.id.circle_lable4)
	private TextView labletext4;
	@ViewInject(R.id.circle_lable5)
	private TextView labletext5;
	@ViewInject(R.id.circle_lable6)
	private TextView labletext6;
	@ViewInject(R.id.circle_lable7)
	private TextView labletext7;
	@ViewInject(R.id.circle_lable8)
	private TextView labletext8;
	@ViewInject(R.id.circle_lable9)
	private TextView labletext9;
	@ViewInject(R.id.circle_lable10)
	private TextView labletext10;
	@ViewInject(R.id.circle_lable11)
	private TextView labletext11;
	@ViewInject(R.id.circle_lable12)
	private TextView labletext12;
	@ViewInject(R.id.chioce_circle_label)
	private TextView chiocelable;
	private int category_id=-1;

	@OnClick(value = { R.id.base_ll_right_btns, R.id.circle_lable2, R.id.circle_lable3, R.id.circle_lable4,
			R.id.circle_lable5, R.id.circle_lable6, R.id.circle_lable7, R.id.circle_lable8, R.id.circle_lable9,
			R.id.circle_lable10, R.id.circle_lable11, R.id.circle_lable12, R.id.circle_lable1 })
	private void clickEvent(View view) {
		switch (view.getId()) {
		// 添加图片点击
		case R.id.base_ll_right_btns:
			// 选择标签
			choiceLable();
			break;
		case R.id.circle_lable1:
			chiocelable.setText(labletext1.getText());
			category_id = CircleTypeConstant.INVESTMENT;
			break;
		case R.id.circle_lable2:
			chiocelable.setText(labletext2.getText());
			category_id = CircleTypeConstant.BUSINESS_TRADE;
			break;
		case R.id.circle_lable3:
			chiocelable.setText(labletext3.getText());
			category_id = CircleTypeConstant.TEA;
			break;
		case R.id.circle_lable4:
			chiocelable.setText(labletext4.getText());
			category_id = CircleTypeConstant.LIQUOR_CIGAR;
			break;
		case R.id.circle_lable5:
			chiocelable.setText(labletext5.getText());
			category_id = CircleTypeConstant.MONEY;
			break;
		case R.id.circle_lable6:
			chiocelable.setText(labletext6.getText());
			category_id = CircleTypeConstant.ARTWORK;
			break;
		case R.id.circle_lable7:
			chiocelable.setText(labletext7.getText());
			category_id = CircleTypeConstant.TRAVEL;
			break;
		case R.id.circle_lable8:
			chiocelable.setText(labletext8.getText());
			category_id = CircleTypeConstant.GOLF;
			break;
		case R.id.circle_lable9:
			chiocelable.setText(labletext9.getText());
			category_id = CircleTypeConstant.COLLEGE;
			break;
		case R.id.circle_lable10:
			chiocelable.setText(labletext10.getText());
			category_id = CircleTypeConstant.LUXURY;
			break;
		case R.id.circle_lable11:
			chiocelable.setText(labletext11.getText());
			category_id = CircleTypeConstant.CAR_YACHT;
			break;
		case R.id.circle_lable12:
			chiocelable.setText(labletext12.getText());
			category_id = CircleTypeConstant.OTHER;
			break;
		default:
			break;
		}
	}

	private void choiceLable() {
		// TODO Auto-generated method stub
		if(category_id==-1){
			ToastUtil.show(this, "必须选择一个分类");
			return ;
		}else {
			Intent  intent =new Intent();
			intent.putExtra("categoryid", category_id);
			setResult(RESULT_OK, intent);
			finishWithRight();
		}
		
	};

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_choice_label;
	}

	@Override
	protected void setUpView() {
		// TODO Auto-generated method stub
		setBarText("选择标签");
		addRightBtn("完成");
	}
   
}
