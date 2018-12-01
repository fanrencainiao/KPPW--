package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.ClassGridAdapter;
import com.kekezu.kppw.adapter.ProviderAdapter;
import com.kekezu.kppw.adapter.TaskAdapter;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.MyGridView;
import com.kekezu.kppw.dataprocess.ClassifyDP;
import com.kekezu.kppw.dataprocess.IndustryDP;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 首页大分类对应的二级分类
 * 
 * @author cm
 * 
 */
public class IndexClassifyList extends Activity
{
	ImageView imgBack;
	TextView text_title;
	TextView textLook;
	ListView listView;
	String strId;
	Intent intent;
	int page = 1;

	ArrayList<HashMap<String, Object>> list;
	ArrayList<HashMap<String, Object>> slist;
	ArrayList<HashMap<String, Object>> slist1 = new ArrayList<>();
	ArrayList<HashMap<String, Object>> slist2 = new ArrayList<>();

	MyGridView gridView;

	SimpleAdapter gridAdapter;
	MyGridView gridView2;

	LoadingDialog progressDialog;
	ViewGroup.LayoutParams params;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classify_list);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		strId = getIntent().getStringExtra("id");
		ViewInit();

		progressDialog = new LoadingDialog(IndexClassifyList.this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		text_title = (TextView) findViewById(R.id.header_title);
		textLook = (TextView) findViewById(R.id.text_look);
		listView = (ListView) findViewById(R.id.listView1);
		gridView = (MyGridView) findViewById(R.id.gridView1);
		gridView2 = (MyGridView) findViewById(R.id.gridView2);
		text_title.setText("分类展示");

		text_title.setFocusable(true);
		text_title.setFocusableInTouchMode(true);
		text_title.requestFocus();

		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		textLook.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (slist.size() > 12)
				{
					for (int i = 0; i < slist.size() - 12; i++)
					{
						slist2.add(slist.get(i));
					}
					gridAdapter.notifyDataSetChanged();
					textLook.setVisibility(View.GONE);
				}

			}
		});
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			slist = IndustryDP.secondSkill(strId, IndexClassifyList.this);
			Message msg = mHandler.obtainMessage(1);
			msg.sendToTarget();
		}
	};

	Runnable newTread2 = new Runnable()
	{
		@Override
		public void run()
		{
			if (TestData.getuserType(IndexClassifyList.this) == 0)
			{
				list = ClassifyDP.serviceByCate(strId, page, IndexClassifyList.this);
				Log.e("list", "" + list);
			}
			else
			{
				list = ClassifyDP.taskByCate(strId, page, IndexClassifyList.this);
			}
			Message msg = mHandler.obtainMessage(2);
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
				setGridValue();
				Thread t = new Thread(newTread2);
				t.start();
			case 2:
				setListValue();
				progressDialog.dismiss();
				break;
			}
		}
	};

	protected void setGridValue()
	{
		// 判断二级分类个数，前4个存入slist1，后面所有的存入slist2
		for (int i = 0; i < slist.size(); i++)
		{
			if (i < 4)
			{
				slist1.add(slist.get(i));
			}
			else if (i > 3 & i < 12)
			{
				slist2.add(slist.get(i));
			}
		}

		ClassGridAdapter adapter = new ClassGridAdapter(this, slist1);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				intent = new Intent(IndexClassifyList.this, MoreResult.class);
				intent.putExtra("type", "1");
				intent.putExtra("cate_id", slist.get(position).get("id").toString());
				intent.putExtra("cate_name", slist.get(position).get("name").toString());
				startActivity(intent);
			}
		});

		gridAdapter = new SimpleAdapter(this, slist2, R.layout.industry_list_item_blue,
				new String[] { "name" }, new int[] { R.id.textView1 });
		gridView2.setAdapter(gridAdapter);
		gridView2.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				intent = new Intent(IndexClassifyList.this, MoreResult.class);
				intent.putExtra("type", "1");
				intent.putExtra("cate_id", slist2.get(position).get("id").toString());
				intent.putExtra("cate_name", slist2.get(position).get("name").toString());
				startActivity(intent);
			}
		});
	}

	/**
	 * 展示一级分类对应的商品
	 */
	public void setListValue()
	{
		if (TestData.getuserType(IndexClassifyList.this) == 0)
		{
			listView.setAdapter(new ProviderAdapter(this, list));
		}
		else
		{
			listView.setAdapter(new TaskAdapter(this, list));
		}

		// 动态算出ListView的LayoutParams，并设置到ListView中
		params = StrFormat.getListViewParams(listView);
		listView.setLayoutParams(params);

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings({ "unchecked", "unused" })
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);

				// intent = new Intent(ClassifyList.this, WitkeyDetails.class);
				// intent.putExtra("recommend_id",
				// item.get("recommend_id").toString());
				// startActivity(intent);
			}
		});
	}
}
