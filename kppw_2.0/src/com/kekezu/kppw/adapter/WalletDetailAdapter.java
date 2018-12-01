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
import com.kekezu.kppw.utils.StrFormat;

public class WalletDetailAdapter extends BaseAdapter
{
	private ViewHolder mHolder;
	ArrayList<HashMap<String, Object>> alist;
	Context mcontext;

	public WalletDetailAdapter(ArrayList<HashMap<String, Object>> list, Context context)
	{
		alist = list;
		mcontext = context;
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
			convertView = LayoutInflater.from(mcontext).inflate(
					R.layout.user_wallet_detail_item, null);
			mHolder = new ViewHolder();
			mHolder.tvTitle = (TextView) convertView.findViewById(R.id.textView3);
			mHolder.tvData = (TextView) convertView.findViewById(R.id.textView4);
			mHolder.tvCash = (TextView) convertView.findViewById(R.id.textView5);
			convertView.setTag(mHolder);
		}
		else
		{
			mHolder = (ViewHolder) convertView.getTag();
		}

		mHolder.tvData.setText(StrFormat.getTime(alist.get(position).get("created_at")
				.toString()));

		if (alist.get(position).get("action").toString().equals("1"))
		{
			mHolder.tvCash.setText("-" + alist.get(position).get("cash").toString());
			mHolder.tvTitle.setText("发布任务");
		}
		else if (alist.get(position).get("action").toString().equals("2"))
		{
			mHolder.tvCash.setText("+" + alist.get(position).get("cash").toString());
			mHolder.tvTitle.setText("接受任务");
		}
		else if (alist.get(position).get("action").toString().equals("3"))
		{
			mHolder.tvCash.setText("+" + alist.get(position).get("cash").toString());
			mHolder.tvTitle.setText("用户充值");
		}
		else if (alist.get(position).get("action").toString().equals("4"))
		{
			mHolder.tvCash.setText("-" + alist.get(position).get("cash").toString());
			mHolder.tvTitle.setText("用户提现");
		}
		else if (alist.get(position).get("action").toString().equals("5"))
		{
			mHolder.tvCash.setText("-" + alist.get(position).get("cash").toString());
			mHolder.tvTitle.setText("购买增值服务");
		}
		else if (alist.get(position).get("action").toString().equals("6"))
		{
			mHolder.tvCash.setText("-" + alist.get(position).get("cash").toString());
			mHolder.tvTitle.setText("购买用户商品");
		}
		else if (alist.get(position).get("action").toString().equals("9"))
		{
			mHolder.tvCash.setText("+" + alist.get(position).get("cash").toString());
			mHolder.tvTitle.setText("出售商品");
		}
		else
		{
			mHolder.tvCash.setText("+" + alist.get(position).get("cash").toString());
			mHolder.tvTitle.setText("退款");
		}

		return convertView;
	}

	private class ViewHolder
	{
		TextView tvTitle;
		TextView tvData;
		TextView tvCash;
	}
}
