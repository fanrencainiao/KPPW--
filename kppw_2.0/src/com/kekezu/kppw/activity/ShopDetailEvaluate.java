package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.ShopEvaluateAdapter;
import com.kekezu.kppw.control.BaseFragment;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.dataprocess.ShopDetailDP;
import com.kekezu.kppw.utils.StrFormat;

/**
 * 店铺商品列表
 * 
 * @author cm
 * 
 */
public class ShopDetailEvaluate extends BaseFragment
{
	View view;
	String strShopId;
	TextView tvAvg, tvSpeed, tvQuality, tvAttitude, tvAll, tv1, tv2, tv3;
	RatingBar rating1, rating2, rating3;
	TextView tvEvalAll, tvEval1, tvEval2, tvEval3;

	RTPullListView pullListView;
	ShopEvaluateAdapter itemAdapter;
	ProgressBar moreProgressBar;
	View Lastview;
	ArrayList<HashMap<String, String>> workList;
	int page = 1; // 页码
	Map<String, String> map;
	String strType = "0";
	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;

	ViewGroup.LayoutParams params;

	public ShopDetailEvaluate(String strShopId2)
	{
		strShopId = strShopId2;
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.view_shoporgood_comment;
	}

	@Override
	protected void initView()
	{
		tvAvg = (TextView) findView(R.id.tv_avg);
		tvSpeed = (TextView) findView(R.id.tv_1);
		tvQuality = (TextView) findView(R.id.tv_2);
		tvAttitude = (TextView) findView(R.id.tv_3);

		tvAll = (TextView) findView(R.id.tv_good_eval_all);
		rating1 = (RatingBar) findView(R.id.rating_1);
		rating2 = (RatingBar) findView(R.id.rating_2);
		rating3 = (RatingBar) findView(R.id.rating_3);
		tv1 = (TextView) findView(R.id.tv_good_eval_1);
		tv2 = (TextView) findView(R.id.tv_good_eval_2);
		tv3 = (TextView) findView(R.id.tv_good_eval_3);

		tvEvalAll = (TextView) findView(R.id.tv_good_eval_all);
		tvEval1 = (TextView) findView(R.id.tv_good_eval_1);
		tvEval2 = (TextView) findView(R.id.tv_good_eval_2);
		tvEval3 = (TextView) findView(R.id.tv_good_eval_3);

		pullListView = (RTPullListView) findView(R.id.pullListView);
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		Lastview = layoutInflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView = (RelativeLayout) Lastview
				.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) Lastview.findViewById(R.id.footer_progress);
		pullListView.addFooterView(footerView);

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
							workList.addAll(ShopDetailDP.getShopEvalList(ShopDetailDP
									.getShopEval(strShopId, page, strType, getActivity())
									.get("data").toString()));
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

		tvEvalAll.setOnClickListener(listener);
		tvEval1.setOnClickListener(listener);
		tvEval2.setOnClickListener(listener);
		tvEval3.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener()
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.tv_good_eval_all:
				setEvalText();
				tvEvalAll.setBackground(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3));
				tvEvalAll.setTextColor(getResources().getColor(R.color.main_bg));
				strType = "0";
				listRefreshData();
				break;
			case R.id.tv_good_eval_1:
				setEvalText();
				tvEval1.setBackground(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3));
				tvEval1.setTextColor(getResources().getColor(R.color.main_bg));
				strType = "1";
				listRefreshData();
				break;
			case R.id.tv_good_eval_2:
				setEvalText();
				tvEval2.setBackground(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3));
				tvEval2.setTextColor(getResources().getColor(R.color.main_bg));
				strType = "2";
				listRefreshData();
				break;
			case R.id.tv_good_eval_3:
				setEvalText();
				tvEval3.setBackground(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3));
				tvEval3.setTextColor(getResources().getColor(R.color.main_bg));
				strType = "3";
				listRefreshData();
				break;
			default:
				break;
			}
		}
	};

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
		map = ShopDetailDP.getShopEval(strShopId, page, strType, getActivity());

		tvAvg.setText(map.get("avg_score"));
		tvSpeed.setText(map.get("speed_score"));
		tvQuality.setText(map.get("quality_score"));
		tvAttitude.setText(map.get("attitude_score"));

		tvAll.setText("全部(" + map.get("all_comment") + ")");
		tv1.setText("好评(" + map.get("good_comment") + ")");
		tv2.setText("中评(" + map.get("mid_comment") + ")");
		tv3.setText("差评(" + map.get("bad_comment") + ")");
		rating1.setRating(Float.valueOf(map.get("speed_score")));
		rating2.setRating(Float.valueOf(map.get("quality_score")));
		rating3.setRating(Float.valueOf(map.get("attitude_score")));

		workList = ShopDetailDP.getShopEvalList(map.get("data"));
		itemAdapter = new ShopEvaluateAdapter(getActivity(), workList);
		pullListView.setAdapter(itemAdapter);

		// 动态算出ListView的LayoutParams，并设置到ListView中
		params = StrFormat.getListViewParams(pullListView);
		pullListView.setLayoutParams(params);
	}

	@SuppressWarnings("deprecation")
	protected void setEvalText()
	{
		tvEvalAll.setBackground(getResources().getDrawable(
				R.drawable.r_bg_textview_yuan3_2));
		tvEvalAll.setTextColor(getResources().getColor(R.color.header_bg));
		tvEval1.setBackground(getResources()
				.getDrawable(R.drawable.r_bg_textview_yuan3_2));
		tvEval1.setTextColor(getResources().getColor(R.color.header_bg));
		tvEval2.setBackground(getResources()
				.getDrawable(R.drawable.r_bg_textview_yuan3_2));
		tvEval2.setTextColor(getResources().getColor(R.color.header_bg));
		tvEval3.setBackground(getResources()
				.getDrawable(R.drawable.r_bg_textview_yuan3_2));
		tvEval3.setTextColor(getResources().getColor(R.color.header_bg));
	}

	private void listRefreshData()
	{
		map = ShopDetailDP.getShopEval(strShopId, page, strType, getActivity());
		page = 1;
		workList.removeAll(workList);
		workList.addAll(ShopDetailDP.getShopEvalList(map.get("data")));
		myHandler.sendEmptyMessage(LOAD_NEW_INFO);

		// 动态算出ListView的LayoutParams，并设置到ListView中
		params = StrFormat.getListViewParams(pullListView);
		pullListView.setLayoutParams(params);
	}
}
