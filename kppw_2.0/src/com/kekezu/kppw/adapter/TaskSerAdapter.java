package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.utils.StrFormat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskSerAdapter extends BaseAdapter
{
	private ViewHolder mHolder;
	ArrayList<HashMap<String, String>> alist;
	@SuppressWarnings("unused")
	private int selectItem = -1;
	String strTag[];
	Context mContext;

	private boolean[] isCheck;

	public TaskSerAdapter(Context context, ArrayList<HashMap<String, String>> list,
			String str)
	{
		mContext = context;
		alist = list;
		strTag = str.split(",");

		Log.e("strTag", "" + strTag.length);

		if (alist != null)
		{
			isCheck = new boolean[alist.size()];
			for (int j = 0; j < strTag.length; j++)
			{
				for (int i = 0; i < alist.size(); i++)
				{
					if (strTag[j].equals(alist.get(i).get("id").toString()))
					{
						isCheck[i] = true;
						break;
					}
					else
					{
						isCheck[i] = false;
					}
				}
			}
		}
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

	@SuppressWarnings({ "deprecation"})
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// 只有当convertView不存在的时候才去inflate子元素
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.task_ser_list_item, null);
			mHolder = new ViewHolder();

			mHolder.tvTitle = (TextView) convertView.findViewById(R.id.textView1);
			mHolder.tvCash = (TextView) convertView.findViewById(R.id.textView2);
			mHolder.tvCheck = (TextView) convertView.findViewById(R.id.textView3);

			convertView.setTag(mHolder);
		}
		else
		{
			mHolder = (ViewHolder) convertView.getTag();
		}

		mHolder.tvTitle.setText(alist.get(position).get("title"));
		mHolder.tvCash.setText(alist.get(position).get("price") + "元");

		if (isCheck[position])
		{
			mHolder.tvCheck.setBackground(mContext.getResources().getDrawable(
					R.drawable.radio_check));
		}
		else
		{
			mHolder.tvCheck.setBackground(mContext.getResources().getDrawable(
					R.drawable.radio_checknull));
		}

		return convertView;
	}

	public void choiceState(int post)
	{
		isCheck[post] = isCheck[post] == true ? false : true;

		this.notifyDataSetChanged();
	}

	public String selectCheckName()
	{
		String string = "";
		for (int i = 0; i < isCheck.length; i++)
		{
			if (isCheck[i] == true)
			{
				string = alist.get(i).get("title").toString() + "," + string;
			}
		}
		if (StrFormat.formatStr(string))
		{
			return string.substring(0, string.length() - 1);
		}
		else
		{
			return string;
		}
	}

	public String selectCheckId()
	{
		String string = "";
		for (int i = 0; i < isCheck.length; i++)
		{
			if (isCheck[i] == true)
			{
				string = alist.get(i).get("id").toString() + "," + string;
			}
		}
		if (StrFormat.formatStr(string))
		{
			return string.substring(0, string.length() - 1);
		}
		else
		{
			return string;
		}
	}

	private class ViewHolder
	{
		TextView tvTitle;
		TextView tvCash;
		TextView tvCheck;
	}
}