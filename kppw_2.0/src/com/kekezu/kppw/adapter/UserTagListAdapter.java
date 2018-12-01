package com.kekezu.kppw.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.kekezu.kppw.R;
import com.kekezu.kppw.activity.UserInfoTag;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.MyGridView;
import com.kekezu.kppw.dataprocess.IndustryDP;

//商品list带图片的适配器，异步加载
public class UserTagListAdapter extends BaseAdapter
{
	// ImageLoader
	private Context context;
	private ArrayList<HashMap<String, String>> list;

	UserTagListAdapter()
	{
	}

	public UserTagListAdapter(Context context, ArrayList<HashMap<String, String>> list)
	{
		this.context = context;
		this.list = list;
	}

	private class ViewHolder
	{
		TextView textView;
		MyGridView mGridView;
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
			view = LayoutInflater.from(context).inflate(R.layout.user_tag_listitem,
					parent, false);
			holder = new ViewHolder();
			holder.textView = (TextView) view.findViewById(R.id.textView2);
			holder.mGridView = (MyGridView) view.findViewById(R.id.gridView1);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		holder.textView.setText(list.get(position).get("name").toString());
		setGridValues(holder.mGridView,
				IndustryDP.getAllSkill(list.get(position).get("second_cate")), context);
		adapter.notifyDataSetChanged();
		return view;
	}

	UserTagGridAdapter adapter;

	private void setGridValues(MyGridView mGridView,
			ArrayList<HashMap<String, String>> smallList, final Context context)
	{
		adapter = new UserTagGridAdapter(context, smallList, ",");
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings({ "unchecked", "deprecation" })
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);
				TextView textView = (TextView) view.findViewById(R.id.text_name_name);

				String string[] = IndustryDP.skillSave(item.get("id").toString(), item
						.get("isChecked").toString(), context);

				if (item.get("isChecked").toString().equals("0"))
				{
					if (string[0].equals("1000"))
					{
						textView.setBackgroundColor(context.getResources().getColor(
								R.color.header_bg));
						textView.setTextColor(context.getResources().getColor(
								R.color.main_bg));

						TestEvent event = new TestEvent();
						event.setTag(true);
						EventBus.getDefault().post(event);
						
						UserInfoTag.getTagData(string[2]);
					}
					else
					{
						Toast.makeText(context, string[1], 2000).show();
					}
				}
				else
				{
					if (string[0].equals("1000"))
					{
						textView.setBackgroundColor(context.getResources().getColor(
								R.color.light_gray2));
						textView.setTextColor(context.getResources().getColor(
								R.color.black));
						TestEvent event = new TestEvent();
						event.setTag(true);
						EventBus.getDefault().post(event);
						
						UserInfoTag.getTagData(string[2]);
					}
					else
					{
						Toast.makeText(context, string[1], 2000).show();
					}
				}
			}
		});
	}
}
