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

//任务list带图片的适配器，异步加载
public class TaskCollAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;

	TaskCollAdapter()
	{
	}

	public TaskCollAdapter(Context context,
			ArrayList<HashMap<String, Object>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		TextView txTitle;
		TextView txDate;
		TextView txCount;
		TextView txRate;
		TextView txMon;

		TextView textView6;
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
					R.layout.task_list_item, parent, false);
			holder = new ViewHolder();

			holder.txTitle = (TextView) view.findViewById(R.id.text_task_title);
			holder.txDate = (TextView) view.findViewById(R.id.textView5);
			holder.txCount = (TextView) view
					.findViewById(R.id.text_index_item_count);
			holder.txRate = (TextView) view
					.findViewById(R.id.text_index_item_rate);
			holder.txMon = (TextView) view.findViewById(R.id.textView3);

			holder.textView6 = (TextView) view.findViewById(R.id.textView6);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		if (list.get(position).get("cate_name").toString().equals("null"))
		{
			holder.textView6.setVisibility(View.GONE);
		}
		else
		{
			holder.textView6.setVisibility(View.VISIBLE);
			holder.textView6.setText(list.get(position).get("cate_name")
					.toString());
		}

		holder.txTitle.setText(list.get(position).get("title").toString());
		holder.txDate.setText(list.get(position).get("created_at").toString());
		holder.txCount.setText(list.get(position).get("view_count").toString());
		holder.txRate.setText(list.get(position).get("delivery_count")
				.toString());
		holder.txMon.setText(list.get(position).get("bounty").toString());

		return view;
	}
}
