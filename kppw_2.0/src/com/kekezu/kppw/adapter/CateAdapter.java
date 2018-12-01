package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kekezu.kppw.R;

public class CateAdapter extends BaseAdapter
{
	private ViewHolder mHolder;
	ArrayList<HashMap<String, Object>> alist;
	private int selectItem = -1;
	Context mContext;
	int type;// 大小分类标识

	public CateAdapter(Context context, ArrayList<HashMap<String, Object>> list,
			int intType)
	{
		mContext = context;
		alist = list;
		type = intType;
	}

	@Override
	public int getCount()
	{
		return alist.size();
	}

	@Override
	public Object getItem(int position)
	{
		return alist.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	public void setSelectItem(int selectItem)
	{
		this.selectItem = selectItem;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// 只有当convertView不存在的时候才去inflate子元素
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.industry_list_item2, null);
			mHolder = new ViewHolder();

			mHolder.textLine = (TextView) convertView.findViewById(R.id.text_line);

			mHolder.mtagName = (TextView) convertView.findViewById(R.id.text_name_name);

			convertView.setTag(mHolder);
		}
		else
		{
			mHolder = (ViewHolder) convertView.getTag();
		}

		mHolder.mtagName.setText((alist.get(position).get("name")).toString());

		if (type == 1)
		{
			if (position == selectItem)
			{
				// convertView.setBackgroundResource(R.color.light_gray2);

				mHolder.textLine.setBackgroundResource(R.color.header_bg);
				mHolder.mtagName.setBackgroundResource(R.color.light_gray2);
				mHolder.mtagName.setTextColor(mContext.getResources().getColor(
						R.color.header_bg));
			}
			else
			{
				mHolder.textLine.setBackgroundResource(R.color.main_bg);
				mHolder.mtagName.setBackgroundResource(R.color.main_bg);
				mHolder.mtagName.setTextColor(mContext.getResources().getColor(
						R.color.black));
				// convertView.setBackgroundResource(R.color.light_gray2);
			}
		}

		return convertView;
	}

	private class ViewHolder
	{
		TextView textLine;
		TextView mtagName;
	}
}
