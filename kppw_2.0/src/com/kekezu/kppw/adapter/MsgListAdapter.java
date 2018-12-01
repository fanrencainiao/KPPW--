package com.kekezu.kppw.adapter;

import java.util.List;
import java.util.Map;

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

public class MsgListAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private List<Map<String, Object>> list;

	public MsgListAdapter(Context context, List<Map<String, Object>> chaterList)
	{
		this.context = context;
		this.list = chaterList;
	}

	private class ViewHolder
	{
		ImageView iv;
		TextView txName;
		TextView txCount;
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
			view = LayoutInflater.from(context).inflate(R.layout.item_chater,
					parent, false);
			holder = new ViewHolder();

			holder.iv = (ImageView) view.findViewById(R.id.iv_avatar);
			holder.txName = (TextView) view.findViewById(R.id.tv_nickname);
			holder.txCount = (TextView) view.findViewById(R.id.tv_number);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		Glide.with(context).load(list.get(position).get("avatar").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.transform(new CircleTransform(context)).into(holder.iv);

		holder.txName.setText(list.get(position).get("nickname").toString());
		holder.txCount.setText(list.get(position).get("num").toString());

		return view;
	}
}
