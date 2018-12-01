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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.RoundTransform;

//作品list带图片的适配器，异步加载
public class ServiceAndGoodAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;
	String strType;

	public ServiceAndGoodAdapter(Context context,
			ArrayList<HashMap<String, Object>> list, String str)
	{
		this.strType = str;
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		ImageView imgPic;
		TextView txName;
		TextView txGood;
		TextView txCash;
		TextView txUnit;
		TextView txTotal;
		TextView txNum;
		LinearLayout layBuy;
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
			view = LayoutInflater.from(context).inflate(R.layout.list_service_item,
					parent, false);
			holder = new ViewHolder();
			holder.layBuy = (LinearLayout) view.findViewById(R.id.lay_buy);
			holder.imgPic = (ImageView) view.findViewById(R.id.imageView1);
			holder.txName = (TextView) view.findViewById(R.id.textView1);
			holder.txGood = (TextView) view.findViewById(R.id.textView3);
			holder.txCash = (TextView) view.findViewById(R.id.textView6);
			holder.txUnit = (TextView) view.findViewById(R.id.textView7);
			holder.txTotal = (TextView) view.findViewById(R.id.textView5);
			holder.txNum = (TextView) view.findViewById(R.id.textView8);
			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		if (strType.equals("2"))
		{
			holder.layBuy.setVisibility(View.GONE);
		}
		holder.txName.setText(list.get(position).get("title").toString());
		holder.txGood.setText(list.get(position).get("percent").toString() + "%");
		holder.txCash.setText(list.get(position).get("cash").toString());
		holder.txUnit.setText(list.get(position).get("unit").toString());
		holder.txTotal.setText(list.get(position).get("sales_num").toString());
		holder.txNum.setText(list.get(position).get("sales_num").toString());

		Glide.with(context).load(list.get(position).get("cover").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.transform(new RoundTransform(context, 3)).into(holder.imgPic);

		return view;
	}
}
