package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.RTPullListView.OnRefreshListener;
import com.kekezu.kppw.dataprocess.ChatDP;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SystemMsg extends Activity
{
	TextView textTitle;
	ImageView imgBack;

	RTPullListView pullListView;
	SimpleAdapter listAdapter;
	ProgressBar moreProgressBar;
	View Lastview;

	ArrayList<HashMap<String, Object>> chatlist;
	int page = 1; // 页码
	String status;

	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;

	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fg_system_list);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		status = getIntent().getStringExtra("type");
		initView();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void initView()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		pullListView = (RTPullListView) findViewById(R.id.listView1);

		if (status.equals("1"))
		{
			textTitle.setText("系统消息");
		}
		else
		{
			textTitle.setText("交易动态");
		}

		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			chatlist = ChatDP.getMessageList(page, status, SystemMsg.this);
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
				progressDialog.dismiss();
				setChatListView();
				break;
			}
		}
	};

	public void setChatListView()
	{

		listAdapter = new SimpleAdapter(this, chatlist, R.layout.fg_system_list_item,
				new String[] { "receive_time", "message_content" }, new int[] {
						R.id.textView2, R.id.textView4 });

		pullListView.setAdapter(listAdapter);

		LayoutInflater layoutInflater = LayoutInflater.from(this);
		Lastview = layoutInflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) Lastview
				.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) Lastview.findViewById(R.id.footer_progress);
		pullListView.addFooterView(footerView);

		pullListView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{

			}
		});

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
							chatlist.clear();
							page = 1;
							chatlist.addAll(ChatDP.getMessageList(page, status,
									SystemMsg.this));
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
							chatlist.addAll(ChatDP.getMessageList(page, status,
									SystemMsg.this));
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
				listAdapter.notifyDataSetChanged();
				pullListView.setSelectionfoot();
				break;
			case LOAD_NEW_INFO:
				listAdapter.notifyDataSetChanged();
				pullListView.onRefreshComplete();
				break;
			default:
				break;
			}
		}

	};
}
