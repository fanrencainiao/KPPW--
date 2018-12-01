package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kekezu.kppw.R;
import com.kekezu.kppw.utils.StrFormat;

//作品list带图片的适配器，异步加载
public class TaskAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;

	TaskAdapter()
	{
	}

	public TaskAdapter(Context context, ArrayList<HashMap<String, Object>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		TextView txTitle;
		TextView tvType;
		TextView tvSerDing, tvSerJi, tvSerYin, tvSerPing;
		TextView txBounty;
		TextView tvUnit;
		TextView txBountyStatus;
		TextView txViewCount;
		TextView txDeliveryCount;
		TextView txCate;
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
			view = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent,
					false);
			holder = new ViewHolder();
			holder.txTitle = (TextView) view.findViewById(R.id.text_task_title);
			holder.tvType = (TextView) view.findViewById(R.id.textView1);
			holder.tvSerDing = (TextView) view.findViewById(R.id.textView4);
			holder.tvSerJi = (TextView) view.findViewById(R.id.textView7);
			holder.tvSerYin = (TextView) view.findViewById(R.id.textView8);
			holder.tvSerPing = (TextView) view.findViewById(R.id.textView12);
			holder.txBounty = (TextView) view.findViewById(R.id.textView3);
			holder.tvUnit = (TextView) view.findViewById(R.id.tv_unit);
			holder.txBountyStatus = (TextView) view.findViewById(R.id.textView9);
			holder.txViewCount = (TextView) view.findViewById(R.id.text_index_item_count);
			holder.txDeliveryCount = (TextView) view
					.findViewById(R.id.text_index_item_rate);
			holder.txCate = (TextView) view.findViewById(R.id.textView6);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		holder.txTitle.setText(list.get(position).get("title").toString());
		if (list.get(position).get("task_type").equals("zhaobiao"))
		{
			holder.tvType.setText("招标");
			holder.tvUnit.setVisibility(View.GONE);
		}
		else
		{
			holder.tvType.setText("悬赏");
			holder.tvUnit.setVisibility(View.VISIBLE);
		}

		try
		{
			holder.tvSerDing.setVisibility(View.GONE);
			holder.tvSerJi.setVisibility(View.GONE);
			holder.tvSerYin.setVisibility(View.GONE);
			holder.tvSerPing.setVisibility(View.GONE);
			JSONArray jArray = new JSONArray(list.get(position).get("task_service")
					.toString());
			if (jArray.length() > 0)
			{
				for (int i = 0; i < jArray.length(); i++)
				{
					if (jArray.get(i).equals("ZHIDING"))
					{
						holder.tvSerDing.setVisibility(View.VISIBLE);
					}
					else if (jArray.get(i).equals("JIAJI"))
					{
						holder.tvSerJi.setVisibility(View.VISIBLE);
					}
					else if (jArray.get(i).equals("SOUSUOYINGQINGPINGBI"))
					{
						holder.tvSerYin.setVisibility(View.VISIBLE);
					}
					else if (jArray.get(i).equals("GAOJIANPINGBI"))
					{
						holder.tvSerPing.setVisibility(View.VISIBLE);
					}
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		holder.txBounty.setText(list.get(position).get("bounty").toString());
		holder.txBountyStatus.setText(list.get(position).get("bounty_status_desc")
				.toString());
		holder.txViewCount.setText(list.get(position).get("view_count").toString());
		holder.txDeliveryCount.setText(list.get(position).get("delivery_count")
				.toString());
		if (StrFormat.formatStr(list.get(position).get("cate_name").toString()))
		{
			holder.txCate.setText(list.get(position).get("cate_name").toString());
		}
		else
		{
			holder.txCate.setVisibility(View.GONE);
		}

		return view;
	}
}
