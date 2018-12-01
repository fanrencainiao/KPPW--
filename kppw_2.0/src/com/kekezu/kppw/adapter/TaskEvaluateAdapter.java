package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.CircleTransform;
import com.kekezu.kppw.utils.StrFormat;

//作品评价list带图片的适配器，异步加载
public class TaskEvaluateAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, String>> list;

	TaskEvaluateAdapter()
	{
	}

	public TaskEvaluateAdapter(Context context, ArrayList<HashMap<String, String>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		ImageView imgPic, imgGood;
		TextView tvName;
		TextView tvDesc;
		@SuppressWarnings("unused")
		TextView tv1, tv2, tv3, tv11, tv22, tv33;
		RatingBar ratingBar1, ratingBar2, ratingBar3;
		LinearLayout layHide;
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
	public boolean areAllItemsEnabled()
	{
		return false;
	}

	@Override
	public boolean isEnabled(int position)
	{
		return false;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		final ViewHolder holder;
		if (convertView == null)
		{
			view = LayoutInflater.from(context).inflate(R.layout.task_eval_item, parent,
					false);
			holder = new ViewHolder();
			holder.imgPic = (ImageView) view.findViewById(R.id.img_pic);
			holder.imgGood = (ImageView) view.findViewById(R.id.img_good);
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);
			holder.tvDesc = (TextView) view.findViewById(R.id.tv_content);

			holder.tv1 = (TextView) view.findViewById(R.id.tv_1);
			holder.tv2 = (TextView) view.findViewById(R.id.tv_2);
			holder.tv3 = (TextView) view.findViewById(R.id.tv_3);
			holder.tv11 = (TextView) view.findViewById(R.id.tv_1_1);
			holder.tv22 = (TextView) view.findViewById(R.id.tv_2_2);
			holder.tv33 = (TextView) view.findViewById(R.id.tv_3_3);
			holder.ratingBar1 = (RatingBar) view.findViewById(R.id.ratingBar1);
			holder.ratingBar2 = (RatingBar) view.findViewById(R.id.ratingBar2);
			holder.ratingBar3 = (RatingBar) view.findViewById(R.id.ratingBar3);
			holder.layHide = (LinearLayout) view.findViewById(R.id.lay_hide);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		Glide.with(context).load(list.get(position).get("avatar").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.transform(new CircleTransform(context)).into(holder.imgPic);

		if (list.get(position).get("type").equals("1"))
		{
			Glide.with(context).load(R.drawable.evl_good).into(holder.imgGood);
		}
		else if (list.get(position).get("type").equals("2"))
		{
			Glide.with(context).load(R.drawable.evl_mid).into(holder.imgGood);
		}
		else if (list.get(position).get("type").equals("3"))
		{
			Glide.with(context).load(R.drawable.evl_bad).into(holder.imgGood);
		}

		holder.tvName.setText(list.get(position).get("nickname").toString());
		holder.tvDesc.setText(list.get(position).get("to_desc").toString() + "："
				+ list.get(position).get("comment").toString());

		holder.tv11.setText(list.get(position).get("speed_score").toString());
		holder.ratingBar1.setRating(Float.valueOf(list.get(position).get("speed_score")
				.toString()));

		if (StrFormat.formatStr(list.get(position).get("quality_score").toString()))
		{
			holder.tv22.setText(list.get(position).get("quality_score").toString());
			holder.ratingBar2.setRating(Float.valueOf(list.get(position)
					.get("quality_score").toString()));
		}
		else
		{
			holder.tv22.setText("0");
			holder.ratingBar2.setRating(0);
		}

		if (StrFormat.formatStr(list.get(position).get("attitude_score").toString()))
		{
			holder.tv33.setText(list.get(position).get("attitude_score").toString());
			holder.ratingBar3.setRating(Float.valueOf(list.get(position)
					.get("attitude_score").toString()));
		}
		else
		{
			holder.layHide.setVisibility(View.GONE);
			holder.tv1.setText("付款及时性：");
			holder.tv2.setText("合作愉快：");
		}
		return view;
	}
}
