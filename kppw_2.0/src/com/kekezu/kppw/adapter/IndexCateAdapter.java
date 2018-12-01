package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.activity.SearchResult;
import com.kekezu.kppw.activity.SearchTaskResult;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.pagerecyclerview.PageGridView;
import com.kekezu.kppw.utils.StrFormat;

public class IndexCateAdapter extends PageGridView.PagingAdapter<MyVH> implements
		PageGridView.OnItemClickListener
{
	public ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();

	Context context;

	int width;

	public IndexCateAdapter(ArrayList<HashMap<String, Object>> data, Context context)
	{
		this.mData.addAll(data);
		this.context = context;
	}

	@Override
	public MyVH onCreateViewHolder(ViewGroup parent, int viewType)
	{
		width = context.getResources().getDisplayMetrics().widthPixels / 4;
		View view = LayoutInflater.from(context).inflate(R.layout.index_cate_item,
				parent, false);
		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.height = width;
		params.width = width;
		view.setLayoutParams(params);
		return new MyVH(view);
	}

	@Override
	public void onBindViewHolder(MyVH holder, int position)
	{
		if (StrFormat.formatStr(mData.get(position).get("name").toString()))
		{
			holder.icon.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.icon.setVisibility(View.GONE);
		}
		holder.tv_title.setText(mData.get(position).get("name").toString());

		Glide.with(context).load(mData.get(position).get("pic").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha).into(holder.icon);
	}

	@Override
	public int getItemCount()
	{
		return mData.size();
	}

	@Override
	public ArrayList<HashMap<String, Object>> getData()
	{
		return mData;
	}

	@Override
	public HashMap<String, Object> getEmpty()
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", "-1");
		map.put("pid", "-1");
		map.put("name", "");
		map.put("pic", "");
		return map;
	}

	@Override
	public void onItemClick(PageGridView pageGridView, int position)
	{
		// String gridview = "";
		if (TextUtils.isEmpty(mData.get(position).get("name").toString()))
		{

		}
		else
		{
			Bundle bundle = new Bundle();
			bundle.putString("id", mData.get(position).get("id").toString());
			bundle.putString("name", mData.get(position).get("name").toString());

			if (TestData.getuserType(context) == 0)
			{
				Intent intent = new Intent(context, SearchResult.class);
				intent.putExtra("type", 0);
				intent.putExtra("search_type", "cate");
				intent.putExtra("cate_id", mData.get(position).get("id").toString());
				intent.putExtra("cate_name", mData.get(position).get("name").toString());
				context.startActivity(intent);
			}
			else
			{
				Intent intent = new Intent(context, SearchTaskResult.class);
				intent.putExtra("search_type", "cate");
				intent.putExtra("cate_id", mData.get(position).get("id").toString());
				intent.putExtra("cate_name", mData.get(position).get("name").toString());
				context.startActivity(intent);
			}
		}
	}
}

class MyVH extends RecyclerView.ViewHolder
{
	public TextView tv_title;
	public ImageView icon;

	public MyVH(View itemView)
	{
		super(itemView);
		tv_title = (TextView) itemView.findViewById(R.id.tv_title);
		icon = (ImageView) itemView.findViewById(R.id.icon);
	}
}
