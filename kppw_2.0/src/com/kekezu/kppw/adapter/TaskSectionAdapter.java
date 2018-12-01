package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.kekezu.kppw.R;

//商品list带图片的适配器，异步加载
public class TaskSectionAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, Object>> list;
	String strStatus;
	String strRole;

	public TaskSectionAdapter(Context context, ArrayList<HashMap<String, Object>> list,
			String string, String string2)
	{
		this.context = context;
		this.list = list;
		this.strStatus = string;
		this.strRole = string2;
	}

	private class ViewHolder
	{
		TextView tvSection;
		TextView tvBili;
		TextView tvCash;
		TextView tvDesc;
		TextView tvTag;
		EditText etTag;
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
			view = LayoutInflater.from(context).inflate(R.layout.task_price_detail_item,
					parent, false);
			holder = new ViewHolder();

			holder.tvSection = (TextView) view.findViewById(R.id.textView3);
			holder.tvBili = (TextView) view.findViewById(R.id.TextView02);
			holder.tvCash = (TextView) view.findViewById(R.id.textView4);
			holder.tvDesc = (TextView) view.findViewById(R.id.tv_desc);
			holder.tvTag = (TextView) view.findViewById(R.id.textView5);
			holder.etTag = (EditText) view.findViewById(R.id.editText1);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		if (strRole.equals("employer"))
		{
			if (strStatus.equals("0"))
			{
				holder.etTag.setText(" ");
				holder.tvTag.setVisibility(View.VISIBLE);
				holder.etTag.setVisibility(View.GONE);
			}
			else if (strStatus.equals("1"))
			{
				holder.tvDesc.setVisibility(View.VISIBLE);
				holder.tvTag.setVisibility(View.GONE);
				holder.etTag.setVisibility(View.GONE);
				holder.tvDesc.setText("备注：" + list.get(position).get("desc").toString());
			}
			else if (strStatus.equals("2"))
			{
				holder.etTag.setText(" ");
				holder.tvTag.setVisibility(View.VISIBLE);
				holder.etTag.setVisibility(View.GONE);
			}
			else if (strStatus.equals("3"))
			{
				holder.tvDesc.setVisibility(View.VISIBLE);
				holder.tvTag.setVisibility(View.GONE);
				holder.etTag.setVisibility(View.GONE);
				holder.tvDesc.setText("备注：" + list.get(position).get("desc").toString());
			}
		}
		else
		{
			holder.tvDesc.setVisibility(View.VISIBLE);
			holder.tvTag.setVisibility(View.GONE);
			holder.etTag.setVisibility(View.GONE);
			holder.tvDesc.setText("备注：" + list.get(position).get("desc").toString());
		}

		holder.tvSection.setText(list.get(position).get("name").toString());
		holder.tvBili.setText("付款比例：" + list.get(position).get("percent").toString()
				+ "%");
		holder.tvCash.setText("付款金额：" + list.get(position).get("price").toString() + "元");

		// holder.etTag.setOnTouchListener(this); // 正确写法
		// holder.etTag.setOnFocusChangeListener(this);
		// holder.etTag.setTag(position);
		//
		// if (selectedEditTextPosition != -1 && position ==
		// selectedEditTextPosition)
		// { // 保证每个时刻只有一个EditText能获取到焦点
		// holder.etTag.requestFocus();
		// }
		// else
		// {
		// holder.etTag.clearFocus();
		// }

		holder.tvTag.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				holder.tvTag.setVisibility(View.GONE);
				holder.etTag.setVisibility(View.VISIBLE);
			}
		});

		return view;
	}
}
