package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.CircleTransform;

//作品评价list带图片的适配器，异步加载
public class WorkEvaluateAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;

	WorkEvaluateAdapter()
	{
	}

	public WorkEvaluateAdapter(Context context,
			ArrayList<HashMap<String, Object>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		ImageView imageView1;
		TextView tv1;
		TextView tv2;
		TextView tv3;
		TextView tv4;
		RatingBar ratingBar1;
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
					R.layout.work_evaluate_item, parent, false);
			holder = new ViewHolder();
			holder.imageView1 = (ImageView) view.findViewById(R.id.imageView1);
			holder.tv1 = (TextView) view.findViewById(R.id.textView1);
			holder.tv2 = (TextView) view.findViewById(R.id.textView2);
			holder.tv3 = (TextView) view.findViewById(R.id.textView3);
			holder.tv4 = (TextView) view.findViewById(R.id.textView4);
			holder.ratingBar1 = (RatingBar) view.findViewById(R.id.Rating1);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		holder.tv1.setText(list.get(position).get("name").toString());
		holder.tv2.setText(list.get(position).get("comment_desc").toString());
		holder.tv3.setText(list.get(position).get("cash").toString());
		holder.tv4.setText(list.get(position).get("created_at").toString());
		holder.ratingBar1.setRating(Float.valueOf(list.get(position)
				.get("total_score").toString()));

		Glide.with(context).load(list.get(position).get("avatar").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.transform(new CircleTransform(context))
				.into(holder.imageView1);

		return view;
	}
}
