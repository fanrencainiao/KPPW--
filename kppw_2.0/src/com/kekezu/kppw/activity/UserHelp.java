package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.RTPullListView.OnRefreshListener;
import com.kekezu.kppw.dataprocess.OtherDP;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 帮助中心
 * 
 * @author cm
 * 
 */
public class UserHelp extends Activity
{
	ImageView imgBack;
	TextView text_title;
	RelativeLayout layout_opinion;

	RTPullListView pullListView;
	SimpleAdapter listAdapter;
	ProgressBar moreProgressBar;
	View Lastview;

	ArrayList<HashMap<String, Object>> helplist;
	int page = 1; // 页码

	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_help);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		//helplist = OtherDP.getUserHelp(page, this);
		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		text_title = (TextView) findViewById(R.id.header_title);
		layout_opinion = (RelativeLayout) findViewById(R.id.layout_user_opinion);

		text_title.setText("帮助");

		layout_opinion.setOnClickListener(listener);
		imgBack.setOnClickListener(listener);
		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
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
			case R.id.layout_user_opinion:
				// 建议反馈
				intent = new Intent(UserHelp.this, UserOpinion.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};

	public void setTaskDraftList()
	{
		pullListView = (RTPullListView) findViewById(R.id.pullListView);

		listAdapter = new SimpleAdapter(this, helplist, R.layout.help_list_item,
				new String[] { "title", "content" }, new int[] { R.id.textView1,
						R.id.textView3 });

		pullListView.setAdapter(listAdapter);

		LayoutInflater layoutInflater = LayoutInflater.from(this);
		Lastview = layoutInflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) Lastview
				.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) Lastview.findViewById(R.id.footer_progress);
		pullListView.addFooterView(footerView);

		// pullListView.setOnItemClickListener(new OnItemClickListener()
		// {
		// @SuppressWarnings("unchecked")
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id)
		// {
		// HashMap<String, Object> item = (HashMap<String, Object>) parent
		// .getItemAtPosition(position);
		//
		// intent = new Intent(TaskDraft.this, TaskDetails.class);
		// intent.putExtra("task_id", item.get("task_id").toString());
		// startActivity(intent);
		// }
		// });

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
							helplist.clear();
							page = 1;
							helplist.addAll(OtherDP.getUserHelp(page, UserHelp.this));
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
							helplist = OtherDP.getUserHelp(page, UserHelp.this);
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
