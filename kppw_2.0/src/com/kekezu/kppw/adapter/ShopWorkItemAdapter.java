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

//店铺作品list带图片的适配器，异步加载
public class ShopWorkItemAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;

	ShopWorkItemAdapter()
	{
	}

	public ShopWorkItemAdapter(Context context,
			ArrayList<HashMap<String, Object>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		ImageView imageView;
		TextView textView1;
		TextView textView3;
		TextView textView4;
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
			view = LayoutInflater.from(context).inflate(
					R.layout.shop_detail_item, parent, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.imageView1);
			holder.textView1 = (TextView) view.findViewById(R.id.textView1);
			holder.textView3 = (TextView) view.findViewById(R.id.textView3);
			holder.textView4 = (TextView) view.findViewById(R.id.textView4);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		holder.textView1.setText(list.get(position).get("title").toString());
		holder.textView3.setText(list.get(position).get("cash").toString());
		holder.textView4.setText(list.get(position).get("sales_num").toString()
				+ "人付款");

		Glide.with(context).load(list.get(position).get("cover").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.into(holder.imageView);

		return view;
	}
}
