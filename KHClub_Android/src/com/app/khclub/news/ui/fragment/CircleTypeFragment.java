package com.app.khclub.news.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.app.khclub.R;
import com.app.khclub.base.adapter.HelloHaAdapter;
import com.app.khclub.base.adapter.HelloHaBaseAdapterHelper;
import com.app.khclub.base.ui.fragment.BaseFragment;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CircleTypeFragment extends BaseFragment {
	@ViewInject(R.id.circle_type_list)
	private ListView typeListView;

	@Override
	public int setLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragemt_classify;
	}

	@Override
	public void loadLayout(View rootView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUpViews(View rootView) {
		// TODO Auto-generated method stub
		
		initTypeList();
	}

	private void initTypeList() {
		// TODO Auto-generated method stub
		int type[] = { R.string.investment, R.string.business_trade, R.string.tea, R.string.liquor_cigar,
				R.string.money, R.string.artwork, R.string.travel, R.string.golf, R.string.college, R.string.luxury,
				R.string.car_yacht, R.string.other };
		List<String> data = toStringArray(type);
		HelloHaAdapter<String> adapter = new HelloHaAdapter<String>(getActivity(),
				R.layout.fragment_circle_list_type_item, data) {

			@Override
			protected void convert(HelloHaBaseAdapterHelper helper, String item) {
				// TODO Auto-generated method stub
                    helper.setText(R.id.typetext, item);
			}
		};
	    typeListView.setAdapter(adapter);

	}

	private List<String> toStringArray(int array[]) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			list.add(getString(array[i]));
		}
		return list;
	}

}
