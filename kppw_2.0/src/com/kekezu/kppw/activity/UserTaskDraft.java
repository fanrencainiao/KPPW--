package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.TaskAdapter;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.RTPullListView.OnRefreshListener;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 任务草稿箱
 * 
 * @author cm
 * 
 */
public class UserTaskDraft extends Activity
{
	ImageView imgBack;
	TextView textTitle;

	RTPullListView pullListView;
	TaskAdapter listAdapter;
	ProgressBar moreProgressBar;
	View Lastview;

	Intent intent;

	ArrayList<HashMap<String, Object>> draftlist;
	int page = 1; // 页码
	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;

	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_draft);
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
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		pullListView = (RTPullListView) findViewById(R.id.pullListView);

		textTitle.setText("草稿箱");
		imgBack.setOnClickListener(listener);
		
		LayoutInflater layoutInflater = LayoutInflater.from(this);
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

				// 跳转到任务发布页，重新编辑后可以发布
				intent = new Intent(UserTaskDraft.this, TaskRelease.class);
				intent.putExtra("task_id", item.get("id").toString());
				intent.putExtra("taskType", item.get("task_type").toString());
				intent.putExtra("op", "update");
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
							draftlist.clear();
							page = 1;
							draftlist.addAll(TaskDP
									.getTaskDraft(page, UserTaskDraft.this));
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
							draftlist = TaskDP.getTaskDraft(page, UserTaskDraft.this);
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

	OnClickListener listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			finish();
		}
	};

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			draftlist = TaskDP.getTaskDraft(page, UserTaskDraft.this);
			Log.e("draftlist", "" + draftlist);

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
				setTaskDraftList();
				progressDialog.dismiss();
				break;
			}
		}
	};

	public void setTaskDraftList()
	{
		listAdapter = new TaskAdapter(this, draftlist);
		pullListView.setAdapter(listAdapter);
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

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isTaskDraft())
		{
			draftlist.clear();
			Thread t = new Thread(newTread1);
			t.start();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
