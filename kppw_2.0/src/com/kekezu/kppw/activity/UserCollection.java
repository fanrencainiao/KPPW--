package com.kekezu.kppw.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.ProviderAdapter;
import com.kekezu.kppw.adapter.TaskCollAdapter;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.MallDP;
import com.kekezu.kppw.dataprocess.ShopDetailDP;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.pullrefresh.PullToRefreshBase;
import com.kekezu.kppw.pullrefresh.PullToRefreshMenuView;
import com.kekezu.kppw.pullrefresh.SwipeMenu;
import com.kekezu.kppw.pullrefresh.SwipeMenuCreator;
import com.kekezu.kppw.pullrefresh.SwipeMenuItem;
import com.kekezu.kppw.pullrefresh.SwipeMenuListView;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

/**
 * Created by caobo on 2016/11/1 0001. ListView上拉刷新、下拉加載更多+左滑刪除
 */

public class UserCollection extends Activity implements
		PullToRefreshBase.OnRefreshListener<SwipeMenuListView>
{

	private PullToRefreshMenuView refreshlistview;
	private SwipeMenuListView swipeMenuListView;

	// 标记下拉index
	@SuppressWarnings("unused")
	private int pullDownIndex = 0;
	// 标记上拉index
	@SuppressWarnings("unused")
	private int pullUpIndex = 0;
	private Handler handler = new Handler();

	ArrayList<HashMap<String, Object>> taskFocusList;
	int page = 1; // 页码
	ImageView imgBack;
	TextView text_title;
	LoadingDialog progressDialog;
	ProviderAdapter providerAdapter;
	TaskCollAdapter taskAdapter;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_coll);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		initView();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();

	}

	private void initView()
	{
		text_title = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);

		refreshlistview = (PullToRefreshMenuView) findViewById(R.id.refreshlistview);
		refreshlistview.setPullLoadEnabled(false);
		refreshlistview.setScrollLoadEnabled(true);
		refreshlistview.setOnRefreshListener(this);
		swipeMenuListView = refreshlistview.getRefreshableView();

		text_title.setText("我的收藏");
		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView)
	{
		onPullDown();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView)
	{
		onPullUp();
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			if (TestData.getuserType(UserCollection.this) == 0)
			{
				taskFocusList = MallDP.getShopColl(page, UserCollection.this);
			}
			else
			{
				taskFocusList = TaskDP.getMyFocus(page, UserCollection.this);
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

	public void setTaskListView()
	{
		if (TestData.getuserType(UserCollection.this) == 0)
		{
			providerAdapter = new ProviderAdapter(this, taskFocusList);
			swipeMenuListView.setAdapter(providerAdapter);
		}
		else
		{
			taskAdapter = new TaskCollAdapter(this, taskFocusList);
			swipeMenuListView.setAdapter(taskAdapter);
		}

		refreshlistview.onRefreshComplete();

		// 创建左滑弹出的item
		SwipeMenuCreator creator = new SwipeMenuCreator()
		{
			@Override
			public void create(SwipeMenu menu)
			{
				// 创建Item
				SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
				// 设置item的背景颜色
				openItem.setBackground(new ColorDrawable(Color.RED));
				// 设置item的宽度
				openItem.setWidth(StrFormat.dip2px(UserCollection.this, 90));
				// 设置item标题
				openItem.setTitle("删除");
				// 设置item字号
				openItem.setTitleSize(18);
				// 设置item字体颜色
				openItem.setTitleColor(Color.WHITE);
				// 添加到ListView的Item布局当中
				menu.addMenuItem(openItem);

			}
		};
		// set creator
		swipeMenuListView.setMenuCreator(creator);
		// 操作删除按钮的点击事件
		swipeMenuListView
				.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()
				{
					@Override
					public boolean onMenuItemClick(final int position, SwipeMenu menu,
							int index)
					{
						refreshlistview.onRefreshComplete();
						if (TestData.getuserType(UserCollection.this) == 0)
						{
							// 删除收藏店铺
							ShopDetailDP.delShopFocus(
									taskFocusList.get(position).get("id").toString(),
									UserCollection.this);
							taskFocusList.remove(position);
							providerAdapter.notifyDataSetChanged();
						}
						else
						{
							// 删除收藏任务
							TaskDP.delTaskFocus(taskFocusList.get(position).get("id")
									.toString(), UserCollection.this);
							taskFocusList.remove(position);
							taskAdapter.notifyDataSetChanged();
						}

						return false;
					}
				});

		swipeMenuListView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);
				if (TestData.getuserType(UserCollection.this) == 0)
				{
					intent = new Intent(UserCollection.this, ShopDetail.class);
					intent.putExtra("shopId", item.get("id").toString());
					startActivity(intent);
				}
				else
				{
					intent = new Intent(UserCollection.this, TaskDetails.class);
					intent.putExtra("task_id", item.get("id").toString());
					startActivity(intent);
				}
			}
		});

		// 操作ListView左滑时的手势操作，这里用于处理上下左右滑动冲突：开始滑动时则禁止下拉刷新和上拉加载手势操作，结束滑动后恢复上下拉操作
		swipeMenuListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener()
		{
			@Override
			public void onSwipeStart(int position)
			{
				refreshlistview.setPullRefreshEnabled(false);
			}

			@Override
			public void onSwipeEnd(int position)
			{
				refreshlistview.setPullRefreshEnabled(true);
			}
		});
	}

	/**
	 * 下拉刷新添加数据到List集合
	 */
	public void onPullDown()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(1000);
					// pullData.addFirst("下拉刷新数据" + pullDownIndex);
					pullDownIndex++;
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							refreshlistview.onRefreshComplete();
							page = 1;
							if (TestData.getuserType(UserCollection.this) == 0)
							{
								taskFocusList.clear();
								taskFocusList.addAll(MallDP.getShopColl(page,
										UserCollection.this));
								providerAdapter.notifyDataSetChanged();
							}
							else
							{
								taskFocusList.clear();
								taskFocusList.addAll(TaskDP.getMyFocus(page,
										UserCollection.this));
								taskAdapter.notifyDataSetChanged();
							}
						}
					});
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 上拉加载添加数据到List集合
	 */
	public void onPullUp()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(1000);
					// pullData.addLast("上拉加载数据" + pullUpIndex);
					pullUpIndex++;
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							refreshlistview.onRefreshComplete();
							page++;
							if (TestData.getuserType(UserCollection.this) == 0)
							{
								taskFocusList.addAll(MallDP.getShopColl(page,
										UserCollection.this));
								providerAdapter.notifyDataSetChanged();
							}
							else
							{
								taskFocusList.addAll(TaskDP.getMyFocus(page,
										UserCollection.this));
								taskAdapter.notifyDataSetChanged();
							}
						}
					});
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
}
