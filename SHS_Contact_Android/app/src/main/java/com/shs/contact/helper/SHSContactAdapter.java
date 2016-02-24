package com.shs.contact.helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Abstraction class of a BaseAdapter in which you only need to provide the
 * convert() implementation.<br/>
 * Using the provided BaseAdapterHelper, your code is minimalist.
 * 
 * @param <T>
 *            The type of the items in the list.
 */
public abstract class SHSContactAdapter<T> extends
		SHSContactBaseAdapter<T, SHSContactBaseAdapterHelper>
{

	/**
	 * Create a QuickAdapter.
	 * 
	 * @param context
	 *            The context.
	 * @param layoutResId
	 *            The layout resource id of each item.
	 */
	public SHSContactAdapter(Context context, int layoutResId)
	{
		super(context, layoutResId);
	}

	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with some
	 * initialization data.
	 * 
	 * @param context
	 *            The context.
	 * @param layoutResId
	 *            The layout resource id of each item.
	 * @param data
	 *            A new list is created out of this one to avoid mutable list
	 */
	//只有一种items
	public SHSContactAdapter(Context context, int layoutResId, List<T> data)
	{
		super(context, layoutResId, data);
	}

	//多种items 
	public SHSContactAdapter(Context context, List<T> data,
							 MultiItemTypeSupport<T> multiItemSupport)
	{
		super(context, data, multiItemSupport);
	}

	protected SHSContactBaseAdapterHelper getAdapterHelper(int position,
			View convertView, ViewGroup parent)
	{

		if (mMultiItemSupport != null)
		{
			return SHSContactBaseAdapterHelper.get(
					context,
					convertView,
					parent,
					mMultiItemSupport.getLayoutId(position, data.get(position)),
					position);
		} else
		{
			return SHSContactBaseAdapterHelper.get(context, convertView, parent, layoutResId, position);
		}
	}

}
