package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.CircleTransform;
import com.kekezu.kppw.utils.StrFormat;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//稿件list带图片的适配器，异步加载
public class ManListItemAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;

	ManListItemAdapter()
	{
	}

	public ManListItemAdapter(Context context, ArrayList<HashMap<String, Object>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		ImageView imgAvatar;
		TextView textName;
		TextView textSection;
		TextView textTime;
		TextView textPercent;
		TextView textDesc;
		TextView tvPrice;
		ImageView imgStatus;
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
			view = LayoutInflater.from(context).inflate(R.layout.manuscript_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
			holder.textName = (TextView) view.findViewById(R.id.text_name);
			holder.textSection = (TextView) view.findViewById(R.id.textView3);
			holder.textTime = (TextView) view.findViewById(R.id.textView1);
			holder.textPercent = (TextView) view.findViewById(R.id.text_percent);
			holder.textDesc = (TextView) view.findViewById(R.id.text_desc);
			holder.tvPrice = (TextView) view.findViewById(R.id.tv_price);
			holder.imgStatus = (ImageView) view.findViewById(R.id.img_status);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		if (StrFormat.formatStr(list.get(position).get("price").toString()))
		{
			holder.tvPrice.setVisibility(View.VISIBLE);
			holder.tvPrice.setText("报价："+list.get(position).get("price").toString()+"元");
		}
		if (StrFormat.formatStr(list.get(position).get("delivery_sort").toString()))
		{
			holder.textSection.setVisibility(View.VISIBLE);
			holder.textSection
					.setText(list.get(position).get("delivery_sort").toString());
		}
		holder.textName.setText(list.get(position).get("nickname").toString());
		holder.textTime.setText(list.get(position).get("created_at").toString());
		holder.textPercent.setText(list.get(position).get("percent").toString() + "%");
		holder.textDesc.setText(Html.fromHtml(list.get(position).get("desc").toString()));

		Glide.with(context).load(list.get(position).get("avatar").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.transform(new CircleTransform(context)).into(holder.imgAvatar);
		if (list.get(position).get("status").equals("1"))
		{
			holder.imgStatus.setImageResource(R.drawable.ic_particulars_yzb);
		}
		else
		{
			holder.imgStatus.setVisibility(View.GONE);
		}
		return view;
	}
}