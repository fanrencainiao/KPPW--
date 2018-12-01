package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.ServiceAndGoodAdapter;
import com.kekezu.kppw.control.BaseFragment;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.dataprocess.ShopDetailDP;
import com.kekezu.kppw.utils.StrFormat;

/**
 * 店铺服务列表
 * 
 * @author cm
 * 
 */
public class ShopDetailService extends BaseFragment
{
	RTPullListView pullListView;
	ServiceAndGoodAdapter itemAdapter;
	ProgressBar moreProgressBar;
	View Lastview;
	ArrayList<HashMap<String, Object>> serviceList;
	int page = 1; // 页码

	String strShopId;
	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;

	ViewGroup.LayoutParams params;

	public ShopDetailService(String strShopId2)
	{
		strShopId = strShopId2;
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.shop_detail_good_service;
	}

	@Override
	protected void initView()
	{
		pullListView = (RTPullListView) findView(R.id.pullListView);
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
				Intent intent = new Intent(getActivity(), GoodDetail.class);
				intent.putExtra("id", item.get("id").toString());
				intent.putExtra("type", "2");
				startActivity(intent);

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
							serviceList.addAll(ShopDetailDP.getShopService(strShopId,
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

				params = StrFormat.getListViewParams(pullListView);
				pullListView.setLayoutParams(params);
				break;
			case LOAD_NEW_INFO:
				itemAdapter.notifyDataSetChanged();
				pullListView.onRefreshComplete();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void initData()
	{
		myHandler.sendEmptyMessage(LOAD_NEW_INFO);
		serviceList = ShopDetailDP.getShopService(strShopId, page, getActivity());
		itemAdapter = new ServiceAndGoodAdapter(getActivity(), serviceList, "1");
		pullListView.setAdapter(itemAdapter);

		// 动态算出ListView的LayoutParams，并设置到ListView中
		params = StrFormat.getListViewParams(pullListView);
		pullListView.setLayoutParams(params);
	}
}
