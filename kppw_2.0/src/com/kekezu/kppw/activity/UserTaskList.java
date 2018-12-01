package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.TaskAdapter;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.RTPullListView.OnRefreshListener;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class UserTaskList extends Activity
{
	ImageView imgBack;
	TextView tvTitle;

	TextView textView1;
	TextView textView2;
	TextView textView3;
	TextView textView4;

	RTPullListView pullListView;
	TaskAdapter listItemAdapter;
	ProgressBar moreProgressBar;
	View Lastview;
	ArrayList<HashMap<String, Object>> tasklist;
	int page = 1; // 页码
	LoadingDialog progressDialog;
	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;
	Intent intent;

	String strStutas;
	String strType;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_draft);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		strStutas = getIntent().getStringExtra("stutas");
		strType = getIntent().getStringExtra("type");
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
		tvTitle = (TextView) findViewById(R.id.header_title);
		pullListView = (RTPullListView) findViewById(R.id.pullListView);

		if (strStutas.equals("1"))
		{
			tvTitle.setText("待审核");
		}
		else if (strStutas.equals("2"))
		{
			tvTitle.setText("投标中");
		}
		else if (strStutas.equals("3"))
		{
			tvTitle.setText("选标中");
		}
		else if (strStutas.equals("4"))
		{
			tvTitle.setText("工作中");
		}
		else if (strStutas.equals("5"))
		{
			tvTitle.setText("评价中");
		}
		else if (strStutas.equals("6"))
		{
			tvTitle.setText("已完成");
		}
		else if (strStutas.equals("7"))
		{
			tvTitle.setText("维权中");
		}
		else if (strStutas.equals("8"))
		{
			tvTitle.setText("已关闭");
		}
		imgBack.setOnClickListener(listener);
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
			if (TestData.getuserType(UserTaskList.this) == 0)
			{
				tasklist = TaskDP.getGoodTask(Integer.valueOf(strStutas), page, strType,
						UserTaskList.this);
			}
			else
			{
				tasklist = TaskDP.getWitkeyTask(Integer.valueOf(strStutas), page,
						strType, UserTaskList.this);
			}
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
				setTaskListView();
				progressDialog.dismiss();
				break;
			}
		}
	};

	// 任务列表的数据适配填充
	public void setTaskListView()
	{
		listItemAdapter = new TaskAdapter(this, tasklist);
		pullListView.setAdapter(listItemAdapter);

		LayoutInflater layoutInflater = LayoutInflater.from(UserTaskList.this);
		Lastview = layoutInflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) Lastview
				.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) Lastview.findViewById(R.id.footer_progress);
		pullListView.addFooterView(footerView);

		pullListView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);

				intent = new Intent(UserTaskList.this, TaskDetails.class);
				intent.putExtra("task_id", item.get("id").toString());
				startActivity(intent);
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
							tasklist.removeAll(tasklist);
							page = 1;
							if (TestData.getuserType(UserTaskList.this) == 0)
							{
								tasklist.addAll(TaskDP.getGoodTask(
										Integer.valueOf(strStutas), page, strType,
										UserTaskList.this));
							}
							else
							{
								tasklist.addAll(TaskDP.getWitkeyTask(
										Integer.valueOf(strStutas), page, strType,
										UserTaskList.this));
							}
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
							if (TestData.getuserType(UserTaskList.this) == 0)
							{
								tasklist.addAll(TaskDP.getGoodTask(
										Integer.valueOf(strStutas), page, strType,
										UserTaskList.this));
							}
							else
							{
								tasklist.addAll(TaskDP.getWitkeyTask(
										Integer.valueOf(strStutas), page, strType,
										UserTaskList.this));
							}
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
				listItemAdapter.notifyDataSetChanged();
				pullListView.setSelectionfoot();
				break;

			case LOAD_NEW_INFO:
				listItemAdapter.notifyDataSetChanged();
				pullListView.onRefreshComplete();
				break;
			default:
				break;
			}
		}
	};
}
