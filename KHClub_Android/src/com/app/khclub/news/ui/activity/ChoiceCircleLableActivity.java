package com.app.khclub.news.ui.activity;

import java.util.ArrayList;
import java.util.List;

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
	//private static final int CHOICE_RESOULT = 5;
	private List<TextView> lableList=null;
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
			setChoiceColor(labletext1);
			chiocelable.setText(labletext1.getText());
			category_id = CircleTypeConstant.INVESTMENT;
			break;
		case R.id.circle_lable2:
			setChoiceColor(labletext2);
			chiocelable.setText(labletext2.getText());
			category_id = CircleTypeConstant.BUSINESS_TRADE;
			break;
		case R.id.circle_lable3:
			setChoiceColor(labletext3);
			chiocelable.setText(labletext3.getText());
			category_id = CircleTypeConstant.TEA;
			break;
		case R.id.circle_lable4:
			setChoiceColor(labletext4);
			chiocelable.setText(labletext4.getText());
			category_id = CircleTypeConstant.LIQUOR_CIGAR;
			break;
		case R.id.circle_lable5:
			setChoiceColor(labletext5);
			chiocelable.setText(labletext5.getText());
			category_id = CircleTypeConstant.MONEY;
			break;
		case R.id.circle_lable6:
			setChoiceColor(labletext6);
			chiocelable.setText(labletext6.getText());
			category_id = CircleTypeConstant.ARTWORK;
			break;
		case R.id.circle_lable7:
			setChoiceColor(labletext7);
			chiocelable.setText(labletext7.getText());
			category_id = CircleTypeConstant.TRAVEL;
			break;
		case R.id.circle_lable8:
			setChoiceColor(labletext8);
			chiocelable.setText(labletext8.getText());
			category_id = CircleTypeConstant.GOLF;
			break;
		case R.id.circle_lable9:
			setChoiceColor(labletext9);
			chiocelable.setText(labletext9.getText());
			category_id = CircleTypeConstant.COLLEGE;
			break;
		case R.id.circle_lable10:
			setChoiceColor(labletext10);
			chiocelable.setText(labletext10.getText());
			category_id = CircleTypeConstant.LUXURY;
			break;
		case R.id.circle_lable11:
			setChoiceColor(labletext11);
			chiocelable.setText(labletext11.getText());
			category_id = CircleTypeConstant.CAR_YACHT;
			break;
		case R.id.circle_lable12:
			setChoiceColor(labletext12);
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
			ToastUtil.show(this, getString(R.string.must_choice_category));
			return ;
		}else {
			Intent  intent =new Intent();
			intent.putExtra("categoryid", category_id);
			showLoading(getString(R.string.loading), true);
			setResult(RESULT_OK, intent);
			finishWithRight();
			hideLoading();
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
		initlableList();
		setBarText(getString(R.string.choice_category));
		addRightBtn(getString(R.string.alert_finish));
	}

	private void initlableList() {
		// TODO Auto-generated method stub
		lableList=new ArrayList<TextView>();
		lableList.add(labletext1);
		lableList.add(labletext2);
		lableList.add(labletext3);
		lableList.add(labletext4);
		lableList.add(labletext5);
		lableList.add(labletext6);
		lableList.add(labletext7);
		lableList.add(labletext8);
		lableList.add(labletext9);
		lableList.add(labletext10);
		lableList.add(labletext11);
		lableList.add(labletext12);
	}
	private void setChoiceColor(TextView textView){
		chiocelable.setVisibility(View.VISIBLE);
		for(TextView tView:lableList){
			if(tView==textView){
				textView.setTextColor(getResources().getColor(R.color.main_deep_white));
				textView.setBackgroundResource(R.drawable.round_lable_press);
			}else {
				tView.setBackgroundResource(R.drawable.round_lable_border);
				tView.setTextColor(getResources().getColor(R.color.main_gold));
			}
		}
	}
}
