package com.kekezu.kppw.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.CateAdapter;
import com.kekezu.kppw.adapter.ProviderAdapter;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.RTPullListView.OnRefreshListener;
import com.kekezu.kppw.dataprocess.IndustryDP;
import com.kekezu.kppw.dataprocess.MallDP;
import com.kekezu.kppw.dataprocess.TestData;

/**
 * 服务商大厅
 * 
 * @author cm
 * 
 */
public class FragmentMall extends Fragment implements OnClickListener
{
	View view;
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
	ProviderAdapter itemAdapter;
	ProgressBar moreProgressBar;
	View Lastview;
	ArrayList<HashMap<String, Object>> shopList;
	int page = 1; // 页码
	String Order = "";
	String strCateId = "";
	String strKey = "";

	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;
	SimpleAdapter simpleAdapter;
	LoadingDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fg_mall, container, false);
		initView();

		progressDialog = new LoadingDialog(getActivity());
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();

		return view;
	}

	// 界面初始化
	private void initView()
	{
		imgSearch = (ImageView) view.findViewById(R.id.img_search);
		layCate = (RelativeLayout) view.findViewById(R.id.lay_cate);
		layAll = (RelativeLayout) view.findViewById(R.id.lay_all);
		tvCate = (TextView) view.findViewById(R.id.tv_cate);
		tvAll = (TextView) view.findViewById(R.id.tv_all);
		tvText = (TextView) view.findViewById(R.id.tv_textview);
		pullListView = (RTPullListView) view.findViewById(R.id.pullListView);

		layCate.setOnClickListener(this);
		layAll.setOnClickListener(this);
		imgSearch.setOnClickListener(this);
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			shopList = MallDP.getMallShop(strKey, strCateId, Order, page, getActivity());
			Message msg = mHandler.obtainMessage(1);
			msg.sendToTarget();
		}
	};

	Runnable newTread2 = new Runnable()
	{
		@Override
		public void run()
		{
			shopList.clear();
			shopList.addAll(MallDP.getMallShop(strKey, strCateId, Order, page,
					getActivity()));
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
				setMallShop();
				progressDialog.dismiss();
				break;
			case 2:
				itemAdapter.notifyDataSetChanged();
				progressDialog.dismiss();
				break;
			}
		}
	};

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.lay_cate:
			bigList = IndustryDP.getBigCate(getActivity());
			smallList = IndustryDP.getCate((bigList.get(0).get("children_task"))
					.toString());
			showPopupWindow();
			break;
		case R.id.lay_all:
			showMode();
			break;
		case R.id.img_search:
			Intent intent = new Intent(getActivity(), Search.class);
			intent.putExtra("type", 0);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	public void setMallShop()
	{
		itemAdapter = new ProviderAdapter(getActivity(), shopList);
		pullListView.setAdapter(itemAdapter);

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
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

				if (item.get("shop_open").toString().equals("1"))
				{
					Intent intent = new Intent(getActivity(), ShopDetail.class);
					intent.putExtra("shopId", item.get("id").toString());
					startActivity(intent);
				}
				else
				{
					Intent intent = new Intent(getActivity(), ServiceDetail.class);
					intent.putExtra("uid", item.get("uid").toString());
					startActivity(intent);
				}
			}
		});

		// 下拉刷新监听器
		pullListView.setonRefreshListener(new OnRefreshListener()
		{
			public void onRefresh()
			{
				progressDialog = new LoadingDialog(getActivity());
				progressDialog.show();
				progressDialog.setCancelable(false);
				
				new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep(2000);
							shopList.removeAll(shopList);
							page = 1;
							shopList.addAll(MallDP.getMallShop(strKey, strCateId, Order,
									page, getActivity()));
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
				progressDialog = new LoadingDialog(getActivity());
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
							shopList.addAll(MallDP.getMallShop(strKey, strCateId, Order,
									page, getActivity()));
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
				progressDialog.dismiss();
				break;
			case LOAD_NEW_INFO:
				itemAdapter.notifyDataSetChanged();
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
			LayoutInflater layoutInflater = (LayoutInflater) getActivity()
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
		listAdapter = new CateAdapter(getActivity(), bigList, 1);
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
		listAdapter2 = new CateAdapter(getActivity(), smallList, 2);
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

				progressDialog = new LoadingDialog(getActivity());
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

				progressDialog = new LoadingDialog(getActivity());
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
			LayoutInflater layoutInflater = (LayoutInflater) getActivity()
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
		simpleAdapter = new SimpleAdapter(getActivity(), TestData.serviceMode(),
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

				progressDialog = new LoadingDialog(getActivity());
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread2);
				t.start();
			}
		});
	}
}
