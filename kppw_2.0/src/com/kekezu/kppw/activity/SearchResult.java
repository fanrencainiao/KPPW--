package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.CateAdapter;
import com.kekezu.kppw.adapter.ProviderAdapter;
import com.kekezu.kppw.adapter.ServiceAndGoodAdapter;
import com.kekezu.kppw.adapter.TaskAdapter;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RTPullListView;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class SearchResult extends Activity
{
	RTPullListView pullListView;
	ServiceAndGoodAdapter goodAdapter;
	TaskAdapter taskAdapter;
	ProviderAdapter providerAdapter;
	ProgressBar moreProgressBar;
	View Lastview;
	ArrayList<HashMap<String, Object>> alist;
	int page = 1; // 页码
	int category = 0;
	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;

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

	SimpleAdapter simpleAdapter;

	RelativeLayout layCate;
	RelativeLayout layAll;
	TextView tvCate, tvAll;
	TextView tvText;

	Intent intent;
	String strCateId = "";
	String strKeyChat = "";
	String Order = "";
	int type = 0;

	private Spinner spinner;
	private List<String> data_list;
	private ArrayAdapter<String> arr_adapter;

	ScrollView scrollView;
	LoadingDialog progressDialog;
	Map<String, String> mapSize;
	ViewGroup.LayoutParams params;
	ListView listViewRe;

	TextView text_search;
	EditText edit_keyWord;

	String strCateName = "";
	String SearchType;

	RelativeLayout relayBack;
	LinearLayout laySearch;
	ImageView imgBack;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		type = getIntent().getIntExtra("type", 0);
		SearchType = getIntent().getStringExtra("search_type");
		if (SearchType.equals("search"))
		{
			strKeyChat = getIntent().getStringExtra("keychat");
		}
		else
		{
			strCateId = getIntent().getStringExtra("cate_id");
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
		listViewRe = (ListView) findViewById(R.id.listView_reman);
		pullListView = (RTPullListView) findViewById(R.id.pullListView);
		text_search = (TextView) findViewById(R.id.text_search_search);
		edit_keyWord = (EditText) findViewById(R.id.edit_search_keyword);

		relayBack = (RelativeLayout) findViewById(R.id.lay_back);
		laySearch = (LinearLayout) findViewById(R.id.lay_search);
		imgBack = (ImageView) findViewById(R.id.img_back);

		layCate = (RelativeLayout) findViewById(R.id.lay_cate);
		layAll = (RelativeLayout) findViewById(R.id.lay_all);
		tvText = (TextView) findViewById(R.id.tv_textview);
		tvCate = (TextView) findViewById(R.id.tv_cate);
		tvAll = (TextView) findViewById(R.id.tv_all);

		setResultList();
		spinner = (Spinner) findViewById(R.id.spinner1);

		if (TestData.getuserType(this) == 0)
		{
			spinner.setVisibility(View.VISIBLE);
		}
		else
		{
			spinner.setVisibility(View.GONE);
		}

		if (SearchType.equals("search"))
		{
			laySearch.setVisibility(View.VISIBLE);
		}
		else
		{
			relayBack.setVisibility(View.VISIBLE);
		}

		// 数据
		data_list = new ArrayList<String>();
		data_list.add("找人才");
		data_list.add("找服务");
		data_list.add("找作品");

		// 适配器
		arr_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, data_list);
		// 加载适配器
		spinner.setAdapter(arr_adapter);
		spinner.setSelection(type);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id)
			{
				type = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		layCate.setOnClickListener(listener);
		layAll.setOnClickListener(listener);
		text_search.setOnClickListener(listener);
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
			case R.id.text_search_search:
				finish();
				break;
			case R.id.lay_cate:
				bigList = IndustryDP.getBigCate(SearchResult.this);
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

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			if (type == 0)
			{
				// mapSize = MallDP.getMallShop222(strKeyChat, strCate, 0, 0,
				// page,
				// SearchResult.this);
				// // 人才搜索
				// alist = MallDP.getMallData(mapSize.get("data"),
				// SearchResult.this);

				alist = MallDP.getMallShop(strKeyChat, strCateId, Order, page,
						SearchResult.this);
			}
			else if (type == 1)
			{
				// 服务搜索
				alist = MallDP.getMallWork(2, strKeyChat, strCateId, Order, page,
						SearchResult.this);
			}
			else if (type == 2)
			{
				// 作品搜索
				alist = MallDP.getMallWork(1, strKeyChat, strCateId, Order, page,
						SearchResult.this);
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
			if (type == 0)
			{
				// 人才搜索
				alist.addAll(MallDP.getMallShop(strKeyChat, strCateId, Order, page,
						SearchResult.this));
			}
			else if (type == 1)
			{
				// 服务搜索
				alist.addAll(MallDP.getMallWork(2, strKeyChat, strCateId, Order, page,
						SearchResult.this));
			}
			else if (type == 2)
			{
				// 作品搜索
				alist.addAll(MallDP.getMallWork(1, strKeyChat, strCateId, Order, page,
						SearchResult.this));
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
				if (type == 0)
				{
					providerAdapter.notifyDataSetChanged();
				}
				else if (type == 1)
				{
					// 服务搜索
					goodAdapter.notifyDataSetChanged();
				}
				else if (type == 2)
				{
					// 作品搜索
					goodAdapter.notifyDataSetChanged();
				}
				progressDialog.dismiss();
				break;
			}
		}
	};

	protected void setTaskListView()
	{
		if (type == 0)
		{
			providerAdapter = new ProviderAdapter(SearchResult.this, alist);
			pullListView.setAdapter(providerAdapter);
		}
		else if (type == 1)
		{
			goodAdapter = new ServiceAndGoodAdapter(SearchResult.this, alist, "1");
			pullListView.setAdapter(goodAdapter);
		}
		else if (type == 2)
		{
			goodAdapter = new ServiceAndGoodAdapter(SearchResult.this, alist, "1");
			pullListView.setAdapter(goodAdapter);
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

				if (type == 0)
				{
					// 人才搜索
					if (item.get("shop_open").toString().equals("1"))
					{
						Intent intent = new Intent(SearchResult.this, ShopDetail.class);
						intent.putExtra("shopId", item.get("id").toString());
						startActivity(intent);
					}
					else
					{
						Intent intent = new Intent(SearchResult.this, ServiceDetail.class);
						intent.putExtra("uid", item.get("uid").toString());
						startActivity(intent);
					}
				}
				else if (type == 1)
				{
					// 服务搜索
					Intent intent = new Intent(SearchResult.this, GoodDetail.class);
					intent.putExtra("id", item.get("id").toString());
					intent.putExtra("type", "2");
					startActivity(intent);
				}
				else if (type == 2)
				{
					// 作品搜索
					Intent intent = new Intent(SearchResult.this, GoodDetail.class);
					intent.putExtra("id", item.get("id").toString());
					intent.putExtra("type", "1");
					startActivity(intent);
				}
			}
		});

		footerView.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				progressDialog = new LoadingDialog(SearchResult.this);
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

							if (type == 0)
							{
								// 人才搜索
								alist.addAll(MallDP.getMallShop(strKeyChat, strCateId,
										Order, page, SearchResult.this));
							}
							else if (type == 1)
							{
								// 服务搜索
								alist.addAll(MallDP.getMallWork(2, strKeyChat, strCateId,
										Order, page, SearchResult.this));
							}
							else if (type == 2)
							{
								// 作品搜索
								alist.addAll(MallDP.getMallWork(1, strKeyChat, strCateId,
										Order, page, SearchResult.this));
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
				if (type == 0)
				{
					// 人才搜索
					providerAdapter.notifyDataSetChanged();
				}
				else if (type == 1)
				{
					// 服务搜索
					goodAdapter.notifyDataSetChanged();
				}
				else if (type == 2)
				{
					// 作品搜索
					goodAdapter.notifyDataSetChanged();
				}
				pullListView.setSelectionfoot();
				progressDialog.dismiss();
				break;
			case LOAD_NEW_INFO:
				if (type == 0)
				{
					providerAdapter.notifyDataSetChanged();
				}
				else if (type == 1)
				{
					// 服务搜索
					goodAdapter.notifyDataSetChanged();
				}
				else if (type == 2)
				{
					// 作品搜索
					goodAdapter.notifyDataSetChanged();
				}
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

				progressDialog = new LoadingDialog(SearchResult.this);
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

				progressDialog = new LoadingDialog(SearchResult.this);
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
		if (type == 0)
		{
			simpleAdapter = new SimpleAdapter(this, TestData.serviceMode(),
					R.layout.industry_list_item2, new String[] { "value" },
					new int[] { R.id.text_name_name });
		}
		else
		{
			simpleAdapter = new SimpleAdapter(this, TestData.GoodMode(),
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

				tvAll.setText(item.get("value").toString());
				Order = item.get("key").toString();
				page = 1;

				popWindow.dismiss();
				popWindow = null;

				progressDialog = new LoadingDialog(SearchResult.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread2);
				t.start();
			}
		});
	}

}
