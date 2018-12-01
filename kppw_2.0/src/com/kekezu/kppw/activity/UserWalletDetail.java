package com.kekezu.kppw.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.WalletDetailAdapter;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.RTPullListView.OnRefreshListener;
import com.kekezu.kppw.dataprocess.UserDP;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class UserWalletDetail extends Activity
{
	ImageView imgBack;
	TextView tvTitle;
	TextView tvDate;
	ImageView imgDate;
	ListView listView;
	ArrayList<HashMap<String, String>> dateList;
	SimpleAdapter dateAdapter;

	RTPullListView pullListView;
	View Lastview;
	ProgressBar moreProgressBar;
	WalletDetailAdapter itemAdapter;
	ArrayList<HashMap<String, Object>> financeList;
	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;
	int page = 1; // 页码
	int intType = 0;

	Thread t;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet_detail);

		t = new Thread(newTread1);
		t.start();

		ViewInit();

		selectMonthTime();
		setDateListView();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.header_title);
		tvDate = (TextView) findViewById(R.id.textView2);
		imgDate = (ImageView) findViewById(R.id.imageView1);
		listView = (ListView) findViewById(R.id.listView1);
		pullListView = (RTPullListView) findViewById(R.id.pullListView);

		tvTitle.setText("收支明细");
		listView.setVisibility(View.GONE);
		imgBack.setOnClickListener(listener);
		imgDate.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.img_back:
				finish();
				break;
			case R.id.imageView1:
				if (listView.getVisibility() == View.GONE)
				{
					listView.setVisibility(View.VISIBLE);
				}
				else
				{
					listView.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
		}
	};

	private void selectMonthTime()
	{
		dateList = new ArrayList<HashMap<String, String>>();
		Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例

		// Date now = ca.getTime();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
		for (int i = 0; i < 6; i++)
		{
			ca.setTime(new Date());
			ca.add(Calendar.MONTH, -i); // 月份减1
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("key", "" + i);
			map.put("value", sf.format(ca.getTime()));
			dateList.add(map);
		}
	}

	public void setDateListView()
	{
		
		dateAdapter = new SimpleAdapter(UserWalletDetail.this, dateList,
				R.layout.spinner_item, new String[] { "value" },
				new int[] { R.id.text_industry });
		listView.setAdapter(dateAdapter);

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				HashMap<String, String> item = (HashMap<String, String>) parent
						.getItemAtPosition(position);

				if (item.get("key").equals("0"))
				{
					tvDate.setText("本月");
				}
				else
				{
					tvDate.setText(item.get("value"));
					intType = Integer.valueOf(item.get("key"));
					financeList.removeAll(financeList);
					page = 1;
					financeList.addAll(UserDP.getFinanceList(page, intType,
							UserWalletDetail.this));
					myHandler.sendEmptyMessage(LOAD_NEW_INFO);
				}

				listView.setVisibility(View.GONE);
			}
		});
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			financeList = UserDP.getFinanceList(page, intType, UserWalletDetail.this);
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
				setFinanceList();
				break;
			}
		}
	};

	public void setFinanceList()
	{
		itemAdapter = new WalletDetailAdapter(financeList, UserWalletDetail.this);
		pullListView.setAdapter(itemAdapter);

		LayoutInflater layoutInflater = LayoutInflater.from(UserWalletDetail.this);
		Lastview = layoutInflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) Lastview
				.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) Lastview.findViewById(R.id.footer_progress);
		pullListView.addFooterView(footerView);

		// 下拉刷新监听器
		pullListView.setonRefreshListener(new OnRefreshListener()
		{
			public void onRefresh()
			{
				new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(2000);
							financeList.removeAll(financeList);
							page = 1;
							financeList.addAll(UserDP.getFinanceList(page, intType,
									UserWalletDetail.this));
							myHandler.sendEmptyMessage(LOAD_NEW_INFO);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}).start();
			}
		});

		footerView.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				moreProgressBar.setVisibility(View.VISIBLE);
				new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(2000);
							page++;
							financeList.addAll(UserDP.getFinanceList(page, intType,
									UserWalletDetail.this));
							myHandler.sendEmptyMessage(LOAD_MORE_SUCCESS);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}

	// 结果处理
	private Handler myHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case LOAD_MORE_SUCCESS:
				moreProgressBar.setVisibility(View.GONE);
				itemAdapter.notifyDataSetChanged();
				pullListView.setSelectionfoot();
				break;

			case LOAD_NEW_INFO:
				itemAdapter.notifyDataSetChanged();
				pullListView.onRefreshComplete();
				break;
			default:
				break;
			}
		}

	};

}
