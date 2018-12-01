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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.RoundTransform;

//店铺list带图片的适配器，异步加载
public class ProviderAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;

	ProviderAdapter()
	{
	}

	public ProviderAdapter(Context context, ArrayList<HashMap<String, Object>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		ImageView imgPic;
		TextView txName;
		TextView txGood;
		TextView txTotal;
		TextView txCity;
		TextView textView11;
		TextView textView12;
		TextView textView13;

		@SuppressWarnings("unused")
		ImageView img1, img2, img3, img4;
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
			view = LayoutInflater.from(context).inflate(R.layout.list_provider_item,
					parent, false);
			holder = new ViewHolder();
			holder.imgPic = (ImageView) view.findViewById(R.id.imageView1);
			holder.txName = (TextView) view.findViewById(R.id.textView1);
			holder.txGood = (TextView) view.findViewById(R.id.textView3);
			holder.img1 = (ImageView) view.findViewById(R.id.imageView3);
			holder.img2 = (ImageView) view.findViewById(R.id.imageView4);
			holder.img3 = (ImageView) view.findViewById(R.id.imageView5);
			holder.img4 = (ImageView) view.findViewById(R.id.imageView6);
			holder.txTotal = (TextView) view.findViewById(R.id.textView5);
			holder.txCity = (TextView) view.findViewById(R.id.textView9);

			holder.textView11 = (TextView) view.findViewById(R.id.textView11);
			holder.textView12 = (TextView) view.findViewById(R.id.textView12);
			holder.textView13 = (TextView) view.findViewById(R.id.textView13);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		try
		{
			holder.textView11.setVisibility(View.GONE);
			holder.textView12.setVisibility(View.GONE);
			holder.textView13.setVisibility(View.GONE);
			JSONArray jArray = new JSONArray(list.get(position).get("cate_name")
					.toString());

			//Log.e("jArray.length()", "" + jArray.length());

			if (jArray.length() == 0)
			{
				holder.textView11.setVisibility(View.GONE);
				holder.textView12.setVisibility(View.GONE);
				holder.textView13.setVisibility(View.GONE);
			}
			else if (jArray.length() == 1)
			{
				holder.textView11.setVisibility(View.VISIBLE);
				holder.textView11.setText("" + jArray.get(0));
				holder.textView12.setVisibility(View.GONE);
				holder.textView13.setVisibility(View.GONE);
			}
			else if (jArray.length() == 2)
			{
				holder.textView11.setVisibility(View.VISIBLE);
				holder.textView12.setVisibility(View.VISIBLE);
				holder.textView11.setText("" + jArray.get(0));
				holder.textView12.setText("" + jArray.get(1));
				holder.textView13.setVisibility(View.GONE);
			}
			else if (jArray.length() == 3)
			{
				holder.textView11.setVisibility(View.VISIBLE);
				holder.textView12.setVisibility(View.VISIBLE);
				holder.textView13.setVisibility(View.VISIBLE);
				holder.textView11.setText("" + jArray.get(0));
				holder.textView12.setText("" + jArray.get(1));
				holder.textView13.setText("" + jArray.get(2));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}



		holder.txName.setText(list.get(position).get("shop_name").toString());
		holder.txGood.setText(list.get(position).get("percent").toString());
		holder.txTotal.setText(list.get(position).get("total_comment").toString());
		holder.txCity.setText(list.get(position).get("city_name").toString());

		Glide.with(context).load(list.get(position).get("shop_pic").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.transform(new RoundTransform(context, 3)).into(holder.imgPic);

		if (list.get(position).get("realname").toString().equals("0"))
		{
			Glide.with(context).load(R.drawable.cert1_gray).into(holder.img1);
		}
		else
		{
			Glide.with(context).load(R.drawable.cert).into(holder.img1);
		}
		if (list.get(position).get("email").toString().equals("0"))
		{
			Glide.with(context).load(R.drawable.cert2_gray).into(holder.img2);
		}
		else
		{
			Glide.with(context).load(R.drawable.cert2).into(holder.img2);
		}
		if (list.get(position).get("alipay").toString().equals("0"))
		{

		}
		else
		{

		}
		if (list.get(position).get("isEnterprise").toString().equals("0"))
		{
			Glide.with(context).load(R.drawable.cert4_gray).into(holder.img4);
		}
		else
		{
			Glide.with(context).load(R.drawable.cert4).into(holder.img4);
		}
		return view;
	}
}
