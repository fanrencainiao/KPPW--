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

//商品list带图片的适配器，异步加载
public class UserTagGridAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, String>> alist;
	String strTag[];
	@SuppressWarnings("unused")
	private int selectItem = -1;

	@SuppressWarnings("unused")
	private boolean[] isCheck;

	UserTagGridAdapter()
	{
	}

	public UserTagGridAdapter(Context context, ArrayList<HashMap<String, String>> list,
			String str)
	{
		this.context = context;
		this.alist = list;
		strTag = str.split(",");

		// if (alist != null)
		// {
		// isCheck = new boolean[alist.size()];
		// for (int j = 0; j < strTag.length; j++)
		// {
		// for (int i = 0; i < alist.size(); i++)
		// {
		// if (strTag[j].equals(alist.get(i).get("id").toString()))
		// {
		// isCheck[i] = true;
		// break;
		// }
		// else
		// {
		// isCheck[i] = false;
		// }
		// }
		// }
		// }
	}

	private class ViewHolder
	{
		TextView textView;
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

	@SuppressWarnings({ "deprecation" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		final ViewHolder holder;
		if (convertView == null)
		{
			view = LayoutInflater.from(context).inflate(R.layout.user_tag_griditem,
					parent, false);
			holder = new ViewHolder();
			holder.textView = (TextView) view.findViewById(R.id.text_name_name);
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		holder.textView.setText(alist.get(position).get("name").toString());

		if (alist.get(position).get("isChecked").toString().equals("1"))
		{
			holder.textView.setBackgroundColor(context.getResources().getColor(
					R.color.header_bg));
			holder.textView
					.setTextColor(context.getResources().getColor(R.color.main_bg));
		}
		else
		{
			holder.textView.setBackgroundColor(context.getResources().getColor(
					R.color.light_gray2));
			holder.textView.setTextColor(context.getResources().getColor(R.color.black));
		}

		return view;
	}
}
