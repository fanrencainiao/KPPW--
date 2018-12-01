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

public class BankAdapter extends BaseAdapter
{
	private ViewHolder mHolder;
	ArrayList<HashMap<String, Object>> alist;
	Context mContext;

	public BankAdapter(ArrayList<HashMap<String, Object>> list, Context context)
	{
		alist = list;
		mContext = context;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// 只有当convertView不存在的时候才去inflate子元素
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.user_wallet_bank_item, null);
			mHolder = new ViewHolder();

			mHolder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
			mHolder.textView3 = (TextView) convertView.findViewById(R.id.textView3);
			mHolder.textView4 = (TextView) convertView.findViewById(R.id.textView4);
			mHolder.imageView = (ImageView) convertView.findViewById(R.id.ImageView01);
			;

			convertView.setTag(mHolder);
		}
		else
		{
			mHolder = (ViewHolder) convertView.getTag();
		}

		mHolder.textView1.setText(alist.get(position).get("bank_name").toString()
				+ alist.get(position).get("deposit_name").toString());

		mHolder.textView3.setText(StrFormat.getStarString(
				alist.get(position).get("account").toString(), 0, 4));

		mHolder.textView4.setText(alist.get(position).get("status_string").toString());

		if (alist.get(position).get("bank_name").equals("工商银行"))
		{
			Glide.with(mContext).load(R.drawable.b_gongshang).into(mHolder.imageView);
		}
		else if (alist.get(position).get("bank_name").equals("农业银行"))
		{
			Glide.with(mContext).load(R.drawable.b_nongye).into(mHolder.imageView);
		}
		else if (alist.get(position).get("bank_name").equals("交通银行"))
		{
			Glide.with(mContext).load(R.drawable.b_jiaotong).into(mHolder.imageView);
		}
		else if (alist.get(position).get("bank_name").equals("招商银行"))
		{
			Glide.with(mContext).load(R.drawable.b_zhaoshang).into(mHolder.imageView);
		}
		else if (alist.get(position).get("bank_name").equals("建设银行"))
		{
			Glide.with(mContext).load(R.drawable.b_jianshe).into(mHolder.imageView);
		}
		else if (alist.get(position).get("bank_name").equals("中国银行"))
		{
			Glide.with(mContext).load(R.drawable.b_zhongguo).into(mHolder.imageView);
		}
		else if (alist.get(position).get("bank_name").equals("邮政储蓄银行"))
		{
			Glide.with(mContext).load(R.drawable.b_youzheng).into(mHolder.imageView);
		}
		else if (alist.get(position).get("bank_name").equals("民生银行"))
		{
			Glide.with(mContext).load(R.drawable.b_minsheng).into(mHolder.imageView);
		}
		else if (alist.get(position).get("bank_name").equals("浦发银行"))
		{
			Glide.with(mContext).load(R.drawable.b_pufa).into(mHolder.imageView);
		}
		else if (alist.get(position).get("bank_name").equals("华夏银行"))
		{
			Glide.with(mContext).load(R.drawable.b_huaxia).into(mHolder.imageView);
		}

		return convertView;
	}

	private class ViewHolder
	{
		TextView textView1;
		TextView textView3;
		TextView textView4;
		ImageView imageView;
	}
}