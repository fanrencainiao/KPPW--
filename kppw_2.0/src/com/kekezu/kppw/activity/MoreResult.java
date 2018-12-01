package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.CateAdapter;
import com.kekezu.kppw.adapter.ServiceAndGoodAdapter;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.RTPullListView.OnRefreshListener;
import com.kekezu.kppw.dataprocess.IndustryDP;
import com.kekezu.kppw.dataprocess.MallDP;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MoreResult extends Activity
{
	ImageView imBack;
	TextView text_title;
	ImageView imgSearch;
	RelativeLayout layCate;
	RelativeLayout layAll;
	TextView tvCate, tvAll;
	TextView tvText;

	private PopupWindow popWindow;
	View popView;
	ArrayList<HashMap<String, Object>> bigList;// 一级分类集合
	ArrayList<HashMap<String, Object>> smallList;// 二级分类集合
	LinearLayout hotLayout;
	TextView textLine;
	ListView listView;
	CateAdapter listAdapter;
	ListView gridView;
	CateAdapter listAdapter2;
	RelativeLayout layoutCateHeaher;
	TextView tvCateTitle;
	ImageView imgCateBack;

	RTPullListView pullListView;
	ServiceAndGoodAdapter adapter;
	ProgressBar moreProgressBar;
	View Lastview;
	ArrayList<HashMap<String, Object>> alist;
	int page = 1; // 页码
	int category = 0;

	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;

	String strCateId = "";
	String Order = "";
	int type = 0;
	Intent intent;
	SimpleAdapter simpleAdapter;
	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_result);

		StatusBarUtil.setStatusBarLightMode(getWindow());
		type = getIntent().getIntExtra("type", 0);
		ViewInit();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();

	}

	private void ViewInit()
	{
		imBack = (ImageView) findViewById(R.id.img_back);
		text_title = (TextView) findViewById(R.id.header_title);
		pullListView = (RTPullListView) findViewById(R.id.pullListView);
		imgSearch = (ImageView) findViewById(R.id.img_search);
		layCate = (RelativeLayout) findViewById(R.id.lay_cate);
		layAll = (RelativeLayout) findViewById(R.id.lay_all);
		tvText = (TextView) findViewById(R.id.tv_textview);
		tvCate = (TextView) findViewById(R.id.tv_cate);
		tvAll = (TextView) findViewById(R.id.tv_all);

		if (type == 2)
		{
			text_title.setText("找服务");
		}
		else if (type == 3)
		{
			text_title.setText("找作品");
		}

		imBack.setOnClickListener(listener);
		imgSearch.setOnClickListener(listener);
		layCate.setOnClickListener(listener);
		layAll.setOnClickListener(listener);
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			if (type == 2)
			{
				alist = MallDP
						.getMallWork(2, "", strCateId, Order, page, MoreResult.this);
			}
			else if (type == 3)
			{
				alist = MallDP
						.getMallWork(1, "", strCateId, Order, page, MoreResult.this);
			}
			Message msg = mHandler.obtainMessage(1);
			msg.sendToTarget();
		}
	};

	Runnable newTread2 = new Runnable()
	{
		@Override
		public void run()
		{
			alist.clear();
			if (type == 2)
			{
				alist.addAll(MallDP.getMallWork(2, "", strCateId, Order, page,
						MoreResult.this));
			}
			else if (type == 3)
			{
				alist.addAll(MallDP.getMallWork(1, "", strCateId, Order, page,
						MoreResult.this));
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
				setMoreList();
				progressDialog.dismiss();
				break;
			case 2:
				adapter.notifyDataSetChanged();
				progressDialog.dismiss();
				break;
			}
		}
	};

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
			case R.id.img_search:
				intent = new Intent(MoreResult.this, Search.class);
				if (type == 2)
				{
					intent.putExtra("type", 1);
				}
				else if (type == 3)
				{
					intent.putExtra("type", 2);
				}
				startActivity(intent);
				break;
			case R.id.lay_cate:
				bigList = IndustryDP.getBigCate(MoreResult.this);
				smallList = IndustryDP.getCate((bigList.get(0).get("children_task"))
						.toString());
				showPopupWindow();
				break;
			case R.id.lay_all:
				showMode();
				break;
			default:
				break;
			}
		}
	};

	// 服务列表
	public void setMoreList()
	{
		adapter = new ServiceAndGoodAdapter(MoreResult.this, alist, "1");
		pullListView.setAdapter(adapter);

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

				if (type == 2)
				{
					Intent intent = new Intent(MoreResult.this, GoodDetail.class);
					intent.putExtra("id", item.get("id").toString());
					intent.putExtra("type", "2");
					startActivity(intent);
				}
				else if (type == 3)
				{
					Intent intent = new Intent(MoreResult.this, GoodDetail.class);
					intent.putExtra("id", item.get("id").toString());
					intent.putExtra("type", "1");
					startActivity(intent);
				}
			}
		});

		// 下拉刷新监听器
		pullListView.setonRefreshListener(new OnRefreshListener()
		{
			public void onRefresh()
			{
				
				progressDialog = new LoadingDialog(MoreResult.this);
				progressDialog.show();
				progressDialog.setCancelable(false);
				
				new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(2000);
							alist.removeAll(alist);
							page = 1;
							if (type == 2)
							{
								alist.addAll(MallDP.getMallWork(2, "", strCateId, Order,
										page, MoreResult.this));
							}
							else if (type == 3)
							{
								alist.addAll(MallDP.getMallWork(1, "", strCateId, Order,
										page, MoreResult.this));
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
				progressDialog = new LoadingDialog(MoreResult.this);
				progressDialog.show();
				progressDialog.setCancelable(false);
				
				moreProgressBar.setVisibility(View.VISIBLE);
				new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(2000);
							page++;

							if (type == 2)
							{
								alist.addAll(MallDP.getMallWork(2, "", strCateId, Order,
										page, MoreResult.this));
							}
							else if (type == 3)
							{
								alist.addAll(MallDP.getMallWork(1, "", strCateId, Order,
										page, MoreResult.this));
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
				adapter.notifyDataSetChanged();
				pullListView.setSelectionfoot();
				progressDialog.dismiss();
				break;

			case LOAD_NEW_INFO:
				adapter.notifyDataSetChanged();
				pullListView.onRefreshComplete();
				progressDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};

	/*
	 * 行业
	 */
	private void showPopupWindow()
	{
		popWindow = null;
		if (popWindow == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			popView = layoutInflater.inflate(R.layout.industry2, null);

			popWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
		}

		popWindow.setAnimationStyle(android.R.style.Animation);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		// 实例化一个ColorDrawable颜色白色,设置SelectPicPopupWindow弹出窗体的背景
		popWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
		// 设置好参数之后再show
		popWindow.showAsDropDown(tvText);
		popWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸

		popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow.showAtLocation(popView, 0, 0, 0);

		hotLayout = (LinearLayout) popView.findViewById(R.id.layout_hot_cate);

		layoutCateHeaher = (RelativeLayout) popView
				.findViewById(R.id.layout_heaher_title);
		tvCateTitle = (TextView) popView.findViewById(R.id.header_title);
		imgCateBack = (ImageView) popView.findViewById(R.id.img_back);

		tvCateTitle.setText("行业选择");
		layoutCateHeaher.setVisibility(View.GONE);

		imgCateBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				popWindow.dismiss();
			}
		});

		listView = (ListView) popView.findViewById(R.id.listView1);
		listAdapter = new CateAdapter(this, bigList, 1);
		listView.setAdapter(listAdapter);
		listAdapter.setSelectItem(0);
		listView.getHeight();

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				listAdapter.setSelectItem(position);
				@SuppressWarnings({ "unused", "unchecked" })
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);

				smallList.clear();
				smallList.addAll(IndustryDP.getCate((bigList.get(position)
						.get("children_task")).toString()));
				listAdapter.notifyDataSetChanged();
				listAdapter2.notifyDataSetChanged();
			}
		});

		gridView = (ListView) popView.findViewById(R.id.listView2);
		listAdapter2 = new CateAdapter(this, smallList, 2);
		gridView.setAdapter(listAdapter2);

		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);

				tvCate.setText(item.get("name").toString());
				strCateId = item.get("id").toString();
				page = 1;

				popWindow.dismiss();
				popWindow = null;

				progressDialog = new LoadingDialog(MoreResult.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread2);
				t.start();

			}
		});

		hotLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tvCate.setText("全部分类");
				strCateId = "";
				page = 1;

				popWindow.dismiss();
				popWindow = null;

				progressDialog = new LoadingDialog(MoreResult.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread2);
				t.start();
			}
		});
	}

	// 排序
	private void showMode()
	{
		popWindow = null;
		if (popWindow == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			popView = layoutInflater.inflate(R.layout.list, null);
			popWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
		}

		popWindow.setAnimationStyle(android.R.style.Animation);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		// 实例化一个ColorDrawable颜色白色,设置SelectPicPopupWindow弹出窗体的背景
		popWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
		// 设置好参数之后再show
		popWindow.showAsDropDown(tvText);
		popWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow.showAtLocation(popView, 0, 0, 0);
		hotLayout = (LinearLayout) popView.findViewById(R.id.layout_hot_cate);

		listView = (ListView) popView.findViewById(R.id.listView1);
		simpleAdapter = new SimpleAdapter(this, TestData.GoodMode(),
				R.layout.industry_list_item2, new String[] { "value" },
				new int[] { R.id.text_name_name });

		listView.setAdapter(simpleAdapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);

				tvAll.setText(item.get("value").toString());
				Order = item.get("key").toString();
				page = 1;

				popWindow.dismiss();
				popWindow = null;

				progressDialog = new LoadingDialog(MoreResult.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread2);
				t.start();
			}
		});
	}

}
