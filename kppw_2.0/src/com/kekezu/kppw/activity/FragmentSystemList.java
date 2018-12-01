package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.RTPullListView.OnRefreshListener;
import com.kekezu.kppw.dataprocess.ChatDP;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 系统消息
 * @author cm
 *
 */
public class FragmentSystemList extends Fragment
{
	View view;
	RTPullListView pullListView;
	SimpleAdapter listAdapter;
	ProgressBar moreProgressBar;
	View Lastview;

	ArrayList<HashMap<String, Object>> chatlist;
	int page = 1; // 页码
	String status = "2";

	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fg_system_list, container, false);

		chatlist = ChatDP.getMessageList(page, status, getActivity());

		setTaskListView();
		return view;
	}

	public void setTaskListView()
	{
		pullListView = (RTPullListView) view.findViewById(R.id.listView1);

		listAdapter = new SimpleAdapter(getActivity(), chatlist,
				R.layout.fg_system_list_item, new String[] { "receive_time",
						"message_content" }, new int[] { R.id.textView2,
						R.id.textView4 });

		pullListView.setAdapter(listAdapter);

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		Lastview = layoutInflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) Lastview
				.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) Lastview
				.findViewById(R.id.footer_progress);
		pullListView.addFooterView(footerView);

		pullListView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
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
									getActivity()));
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
									getActivity()));

							// tasklist = TaskDP.getGoodTask(0, page,
							// UserTask.this);
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
