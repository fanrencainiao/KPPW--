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
import com.kekezu.kppw.utils.StrFormat;

//销售订单list带图片的适配器，异步加载
public class OrderSaleAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;

	OrderSaleAdapter()
	{
	}

	public OrderSaleAdapter(Context context,
			ArrayList<HashMap<String, Object>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		ImageView imgPic;
		TextView txName;
		TextView textView2;
		TextView textView4;
		TextView txGood;
		TextView txCity;
		TextView textView11;
		TextView textView5;
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
					R.layout.user_order_manege_item, parent, false);
			holder = new ViewHolder();
			holder.imgPic = (ImageView) view.findViewById(R.id.imageView1);
			holder.txName = (TextView) view.findViewById(R.id.textView1);
			holder.textView2 = (TextView) view.findViewById(R.id.textView2);
			holder.textView4 = (TextView) view.findViewById(R.id.textView4);
			holder.txGood = (TextView) view.findViewById(R.id.textView3);
			holder.txCity = (TextView) view.findViewById(R.id.textView9);
			holder.textView11 = (TextView) view.findViewById(R.id.textView11);
			holder.textView5 = (TextView) view.findViewById(R.id.textView5);
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		if (StrFormat.formatStr(list.get(position).get("cate_name").toString()))
		{
			holder.textView11.setVisibility(View.VISIBLE);
			holder.textView11.setText(list.get(position).get("cate_name")
					.toString());
		}
		else
		{
			holder.textView11.setVisibility(View.GONE);
		}

		holder.textView2.setText("￥");
		holder.textView4.setText(list.get(position).get("status").toString());
		holder.txName.setText(list.get(position).get("title").toString());
		holder.txGood.setText(list.get(position).get("cash").toString());
		holder.txCity.setText(list.get(position).get("created_at").toString());
		holder.textView5.setText(list.get(position).get("name").toString());

		Glide.with(context).load(list.get(position).get("avatar").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.into(holder.imgPic);

		return view;
	}
}
