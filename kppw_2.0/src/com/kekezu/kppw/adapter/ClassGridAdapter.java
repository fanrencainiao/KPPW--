package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassGridAdapter extends BaseAdapter
{
	private ArrayList<HashMap<String, Object>> list;
	Context context;

	public ClassGridAdapter(Context context,
			ArrayList<HashMap<String, Object>> list)
	{
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if (null == convertView)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.index_grid_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.tv = (TextView) convertView.findViewById(R.id.textView1);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv.setText(list.get(position).get("name").toString());
		Glide.with(context).load(list.get(position).get("pic").toString())
				.placeholder(R.drawable.ic_hoem_rmfw)
				.error(R.drawable.ic_hoem_rmfw).into(holder.iv);

		return convertView;
	}

	class ViewHolder
	{
		ImageView iv;
		TextView tv;
	}

}
