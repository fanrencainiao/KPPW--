package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.CircleTransform;

//分类list带图片的适配器，异步加载
public class ClassGoodAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;

	ClassGoodAdapter()
	{
	}

	public ClassGoodAdapter(Context context, ArrayList<HashMap<String, Object>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		ImageView mImageView;
		TextView recommend_name;
		TextView percent;
		TextView textView11;
		TextView textView12;
		TextView textView13;
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
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		final ViewHolder holder;
		if (convertView == null)
		{
			view = LayoutInflater.from(context).inflate(R.layout.index_list_item, parent,
					false);
			holder = new ViewHolder();
			holder.mImageView = (ImageView) view.findViewById(R.id.image_index_item_img);

			holder.recommend_name = (TextView) view
					.findViewById(R.id.text_index_item_title);
			holder.percent = (TextView) view.findViewById(R.id.text_index_item_rate);

			holder.textView11 = (TextView) view.findViewById(R.id.textView11);
			holder.textView12 = (TextView) view.findViewById(R.id.textView12);
			holder.textView13 = (TextView) view.findViewById(R.id.textView13);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		holder.textView12.setVisibility(View.GONE);
		holder.textView13.setVisibility(View.GONE);

		holder.textView11.setText(list.get(position).get("tags").toString());
		holder.recommend_name
				.setText(list.get(position).get("recommend_name").toString());
		holder.percent.setText(list.get(position).get("percent").toString() + "%");

		// imageLoader.displayImage(list.get(position).get("recommend_pic")
		// .toString(), holder.mImageView, options, animateFirstListener);

		Glide.with(context).load(list.get(position).get("recommend_pic").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.transform(new CircleTransform(context)).into(holder.mImageView);

		return view;
	}
}
