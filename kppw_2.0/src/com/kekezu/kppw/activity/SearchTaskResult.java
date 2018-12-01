package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.CateAdapter;
import com.kekezu.kppw.adapter.TaskAdapter;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.dataprocess.IndustryDP;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchTaskResult extends Activity
{
	RTPullListView pullListView;
	TaskAdapter taskAdapter;
	ProgressBar moreProgressBar;
	View Lastview;
	ArrayList<HashMap<String, Object>> alist;
	int page = 1; // 页码
	int category = 0;
	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;

	ArrayList<HashMap<String, Object>> bigList;
	ArrayList<HashMap<String, Object>> smallList;

	Intent intent;
	String strCate = "";
	String strCateName = "";
	String strKeyChat = "";
	int intType = 0;
	String strDesc = "";

	ScrollView scrollView;
	TextView tvNoDate;
	LoadingDialog progressDialog;
	Map<String, String> mapSize;
	ViewGroup.LayoutParams params;
	ListView listViewRe;

	private PopupWindow popWindow;
	View popView;
	LinearLayout hotLayout;
	ListView listView;
	CateAdapter listAdapter;
	ListView gridView;
	CateAdapter listAdapter2;
	SimpleAdapter simpleAdapter;

	RelativeLayout layCate, layMode, layDesc;
	TextView tvCate, tvMode, tvAll;
	TextView tvText;
	EditText edit_keyWord;
	TextView text_search;

	String SearchType;

	RelativeLayout relayBack;
	LinearLayout laySearch;
	ImageView imgBack;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_task_result);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		SearchType = getIntent().getStringExtra("search_type");
		if (SearchType.equals("search"))
		{
			strKeyChat = getIntent().getStringExtra("keychat");
		}
		else
		{
			strCate = getIntent().getStringExtra("cate_id");
			strCateName = getIntent().getStringExtra("cate_name");
		}

		initView();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();

	}

	private void initView()
	{
		scrollView = (ScrollView) findViewById(R.id.scrollView1);
		tvNoDate = (TextView) findViewById(R.id.tv_nodate);
		listViewRe = (ListView) findViewById(R.id.listView_reman);
		pullListView = (RTPullListView) findViewById(R.id.pullListView);

		relayBack = (RelativeLayout) findViewById(R.id.lay_back);
		laySearch = (LinearLayout) findViewById(R.id.lay_search);
		imgBack = (ImageView) findViewById(R.id.img_back);

		layCate = (RelativeLayout) findViewById(R.id.lay_cate);
		layMode = (RelativeLayout) findViewById(R.id.lay_mode);
		layDesc = (RelativeLayout) findViewById(R.id.lay_desc);
		tvCate = (TextView) findViewById(R.id.tv_cate);
		tvMode = (TextView) findViewById(R.id.tv_mode);
		tvAll = (TextView) findViewById(R.id.tv_all);
		tvText = (TextView) findViewById(R.id.tv_textview);
		edit_keyWord = (EditText) findViewById(R.id.edit_search_keyword);
		text_search = (TextView) findViewById(R.id.text_search_search);

		if (SearchType.equals("search"))
		{
			laySearch.setVisibility(View.VISIBLE);
		}
		else
		{
			relayBack.setVisibility(View.VISIBLE);
		}

		layCate.setOnClickListener(listener);
		layMode.setOnClickListener(listener);
		layDesc.setOnClickListener(listener);
		text_search.setOnClickListener(listener);
		imgBack.setOnClickListener(listener);

		setResultList();
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
			case R.id.text_search_search:
				finish();
				break;
			case R.id.lay_cate:
				bigList = IndustryDP.getBigCate(SearchTaskResult.this);
				smallList = IndustryDP.getCate((bigList.get(0).get("children_task"))
						.toString());
				showPopupWindow();
				break;
			case R.id.lay_mode:
				showMode("mode");
				break;
			case R.id.lay_desc:
				showMode("desc");
				break;
			default:
				break;
			}
		}
	};

	// 行业的popwindow
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
				page = 1;
				strCate = item.get("id").toString();

				popWindow.dismiss();
				popWindow = null;

				progressDialog = new LoadingDialog(SearchTaskResult.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread2);
				t.start();

				myHandler.sendEmptyMessage(LOAD_NEW_INFO);
			}
		});

		hotLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tvCate.setText("全部分类");
				page = 1;
				strCate = "";

				popWindow.dismiss();
				popWindow = null;

				progressDialog = new LoadingDialog(SearchTaskResult.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread2);
				t.start();
			}
		});
	}

	private void showMode(final String str)
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
		if (str.equals("mode"))
		{
			simpleAdapter = new SimpleAdapter(this, TestData.taskMode(),
					R.layout.industry_list_item2, new String[] { "value" },
					new int[] { R.id.text_name_name });
		}
		else
		{
			simpleAdapter = new SimpleAdapter(this, TestData.taskDesc(),
					R.layout.industry_list_item2, new String[] { "value" },
					new int[] { R.id.text_name_name });
		}

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

				page = 1;
				if (str.equals("mode"))
				{
					intType = Integer.valueOf(item.get("key").toString());
					tvMode.setText(item.get("value").toString());
				}
				else
				{
					strDesc = item.get("key").toString();
					tvAll.setText(item.get("value").toString());
				}

				popWindow.dismiss();
				popWindow = null;

				progressDialog = new LoadingDialog(SearchTaskResult.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread2);
				t.start();
			}
		});
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			// 任务搜索
			mapSize = TaskDP.getTask222(strCate, 0, page, strKeyChat, "",
					SearchTaskResult.this);
			if (mapSize.get("datasize").equals("0"))
			{
				alist = TaskDP.getTask333(mapSize.get("recommend"));
			}
			else
			{
				alist = TaskDP.getTask(strCate, intType, page, strKeyChat, strDesc,
						SearchTaskResult.this);
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
			// 任务搜索
			mapSize = TaskDP.getTask222(strCate, 0, page, strKeyChat, "",
					SearchTaskResult.this);
			if (mapSize.get("datasize").equals("0"))
			{
				alist = TaskDP.getTask333(mapSize.get("recommend"));
			}
			else
			{
				alist = TaskDP.getTask(strCate, intType, page, strKeyChat, strDesc,
						SearchTaskResult.this);
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
				setTaskListView();
				progressDialog.dismiss();
				break;
			case 2:
				setTaskListView();
				progressDialog.dismiss();
				break;
			}
		}
	};

	protected void setTaskListView()
	{
		taskAdapter = new TaskAdapter(SearchTaskResult.this, alist);
		if (mapSize.get("datasize").equals("0"))
		{
			scrollView.setVisibility(View.VISIBLE);
			tvNoDate.setText("暂未找到相应内容");
			listViewRe.setAdapter(taskAdapter);
			// 动态算出ListView的LayoutParams，并设置到ListView中
			params = StrFormat.getListViewParams(listViewRe);
			listViewRe.setLayoutParams(params);

			listViewRe.setOnItemClickListener(new OnItemClickListener()
			{
				@SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id)
				{
					HashMap<String, Object> item = (HashMap<String, Object>) parent
							.getItemAtPosition(position);
					intent = new Intent(SearchTaskResult.this, TaskDetails.class);
					intent.putExtra("task_id", item.get("id").toString());
					startActivity(intent);
				}
			});
		}
		else
		{
			scrollView.setVisibility(View.GONE);
			pullListView.setAdapter(taskAdapter);
			taskAdapter.notifyDataSetChanged();
		}
	}

	public void setResultList()
	{
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
				intent = new Intent(SearchTaskResult.this, TaskDetails.class);
				intent.putExtra("task_id", item.get("id").toString());
				startActivity(intent);
			}
		});

		footerView.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				progressDialog = new LoadingDialog(SearchTaskResult.this);
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
							alist.addAll(TaskDP.getTask(strCate, intType, page,
									strKeyChat, strDesc, SearchTaskResult.this));
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
				taskAdapter.notifyDataSetChanged();
				pullListView.setSelectionfoot();
				progressDialog.dismiss();
				break;
			case LOAD_NEW_INFO:
				taskAdapter.notifyDataSetChanged();
				pullListView.onRefreshComplete();
				progressDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};
}
