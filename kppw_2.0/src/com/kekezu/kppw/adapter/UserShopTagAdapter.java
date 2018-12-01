package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserShopTagAdapter extends
		RecyclerView.Adapter<UserShopTagAdapter.MyViewHolder>
{
	private ArrayList<HashMap<String, Object>> mDatas;
	private ArrayList<HashMap<String, Object>> clickObjectList;
	private LayoutInflater mInflater;
	Context mcontext;

	public UserShopTagAdapter(Context context, ArrayList<HashMap<String, Object>> datas)
	{
		mInflater = LayoutInflater.from(context);
		clickObjectList = new ArrayList<>();
		mDatas = datas;
		mcontext = context;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		MyViewHolder holder = new MyViewHolder(mInflater.inflate(
				R.layout.industry_list_item, parent, false));
		return holder;
	}

	public ArrayList<HashMap<String, Object>> getClickList()
	{
		return clickObjectList;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(final MyViewHolder holder, final int position)
	{
		holder.tv.setText(mDatas.get(position).get("value").toString());

		HashMap<String, Object> object = mDatas.get(position);
		if (clickObjectList.contains(object))
		{
			holder.tv.setBackgroundResource(R.drawable.ic_button_submit);
			holder.tv.setTextColor(mcontext.getResources().getColor(R.color.main_bg));
		}
		else
		{
			holder.tv.setBackgroundResource(R.drawable.ic_jx);
			holder.tv.setTextColor(mcontext.getResources().getColor(R.color.light_gray8));
		}
		holder.tv.setTag(object);
	}

	@Override
	public int getItemCount()
	{
		return mDatas.size();
	}

	class MyViewHolder extends ViewHolder
	{
		TextView tv;

		public MyViewHolder(View view)
		{
			super(view);
			tv = (TextView) view.findViewById(R.id.textView1);

			tv.setOnClickListener(new View.OnClickListener()
			{
				@SuppressWarnings("unchecked")
				@Override
				public void onClick(View view)
				{
					HashMap<String, Object> object = (HashMap<String, Object>) view
							.getTag();

					if (clickObjectList.size() > 3)
					{
						clickObjectList.remove(object);
					}
					else
					{
						if (clickObjectList.contains(object))
						{
							clickObjectList.remove(object);
						}
						else
						{
							clickObjectList.add(object);
						}
					}
					notifyDataSetChanged();
				}
			});
		}
	}
}