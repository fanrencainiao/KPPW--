package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.CaseDP;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 用户案例
 * 
 * @author cm
 * 
 */
public class UserShopCase extends Activity
{
	TextView text_title;
	ImageView imgBack;
	ImageView img_case_add;

	ListView listView;
	MyListAdapter listAdapter;
	ArrayList<HashMap<String, Object>> caselist;

	Intent intent;
	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_shop_case);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		ViewInit();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();

		EventBus.getDefault().register(this);
	}

	private void ViewInit()
	{
		text_title = (TextView) findViewById(R.id.header_title);
		text_title.setText("我的案例");

		imgBack = (ImageView) findViewById(R.id.img_back);
		img_case_add = (ImageView) findViewById(R.id.img_case_add);

		imgBack.setOnClickListener(listener);
		img_case_add.setOnClickListener(listener);

		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(itemClickListener);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener()
	{
		@SuppressWarnings("unchecked")
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			HashMap<String, Object> item = (HashMap<String, Object>) parent
					.getItemAtPosition(position);

			Intent intent = new Intent(UserShopCase.this, UserShopCaseDetails.class);
			intent.putExtra("id", item.get("id").toString());
			startActivity(intent);

		}
	};

	OnClickListener listener = new OnClickListener()
	{
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.img_back:
				finish();
				break;
			case R.id.img_case_add:
				intent = new Intent(UserShopCase.this, UserShopCaseAdd.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	};

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			caselist = CaseDP.getPersonCase(UserShopCase.this);
			Message msg = mHandler.obtainMessage(1);
			msg.sendToTarget();
		}
	};

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case 1:
				listAdapter = new MyListAdapter(caselist);
				listView.setAdapter(listAdapter);
				progressDialog.dismiss();
				break;
			}
		}
	};

	private class MyListAdapter extends BaseAdapter
	{
		private ViewHolder mHolder;
		ArrayList<HashMap<String, Object>> alist;

		MyListAdapter(ArrayList<HashMap<String, Object>> list)
		{
			alist = list;
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
				convertView = getLayoutInflater().inflate(R.layout.user_shop_case_list_item,
						null);
				mHolder = new ViewHolder();

				mHolder.textTitle = (TextView) convertView.findViewById(R.id.textView1);

				mHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);

				convertView.setTag(mHolder);
			}
			else
			{
				mHolder = (ViewHolder) convertView.getTag();
			}

			mHolder.textTitle.setText((alist.get(position).get("title")).toString());

			Glide.with(UserShopCase.this).load(alist.get(position).get("pic").toString())
					.placeholder(R.drawable.ic_alipay_bg).error(R.drawable.ic_alipay_bg)
					.into(mHolder.imageView);

			return convertView;
		}

		private class ViewHolder
		{
			ImageView imageView;
			TextView textTitle;
		}
	}

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isCaseAdd())
		{
			caselist.removeAll(caselist);
			caselist.addAll(CaseDP.getPersonCase(this));
			listAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

}
