package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;

import com.alibaba.mobileim.YWIMKit;
import com.bumptech.glide.Glide;
import com.kekezu.kppw.MyApp;
import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.ShopEvaluateAdapter;
import com.kekezu.kppw.bean.UserBean;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.MyScrollView;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.MyScrollView.OnScrollListener;
import com.kekezu.kppw.dataprocess.WorkDP;
import com.kekezu.kppw.dataprocess.WorkDetailDP;
import com.kekezu.kppw.imcustom.LoginSampleHelper;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.ConfigInc;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GoodDetail extends Activity implements OnScrollListener
{
	LoadingDialog progressDialog;
	ImageView imgBack;
	TextView tvTitle;
	ImageView imgShare;
	MyScrollView myScrollView;
	ImageView imgBg;
	TextView tvTagDesc, tvTagEvl;
	TextView tvGoodTitle, tvGoodCash, tvGoodUnit, tvPercent, tvSale, tvDesc;
	TextView tvAvg, tvSpeed, tvQuality, tvAttitude;
	RatingBar rating1, rating2, rating3;
	LinearLayout layoutHide;
	TextView tvNoData;
	LinearLayout layCall;
	LinearLayout layShop;

	LinearLayout layTou;
	LinearLayout search01, search02;
	TextView tvEvalAll, tvEval1, tvEval2, tvEval3;
	TextView tvHandle;

	RTPullListView pullListView;
	ShopEvaluateAdapter itemAdapter;
	ProgressBar moreProgressBar;
	View Lastview;
	ArrayList<HashMap<String, String>> workList;
	int page = 1; // 页码
	String strType = "0";
	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int LOAD_NEW_INFO = 5;
	ViewGroup.LayoutParams params;

	String strId;
	String type;
	Map<String, String> map;
	Intent intent;
	YWIMKit mIMKit;
	FinalDb db;
	List<UserBean> users;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.good_detail);

		strId = getIntent().getStringExtra("id");
		type = getIntent().getStringExtra("type");

		initView();
		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void initView()
	{
		db = ConfigInc.getCreateDB(this);
		users = db.findAll(UserBean.class);
		mIMKit = LoginSampleHelper.getInstance().getIMKit();

		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		imgShare = (ImageView) findViewById(R.id.img_share);
		myScrollView = (MyScrollView) findViewById(R.id.myScrollView);
		imgBg = (ImageView) findViewById(R.id.img_bg);
		tvGoodTitle = (TextView) findViewById(R.id.tv_good_title);
		tvGoodCash = (TextView) findViewById(R.id.tv_good_cash);
		tvGoodUnit = (TextView) findViewById(R.id.tv_good_unit);
		tvPercent = (TextView) findViewById(R.id.tv_good_percent);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		tvSale = (TextView) findViewById(R.id.tv_good_sale);
		search01 = (LinearLayout) findViewById(R.id.search01);
		search02 = (LinearLayout) findViewById(R.id.search02);
		layTou = (LinearLayout) findViewById(R.id.lay_tou);
		layoutHide = (LinearLayout) findViewById(R.id.lay_mygj);
		tvNoData = (TextView) findViewById(R.id.tv_nodate);
		tvHandle = (TextView) findViewById(R.id.btn_task_tougao);
		layCall = (LinearLayout) findViewById(R.id.lay_call);
		layShop = (LinearLayout) findViewById(R.id.lay_shop);

		tvTagDesc = (TextView) findViewById(R.id.tv_good_desc);
		tvTagEvl = (TextView) findViewById(R.id.tv_good_evl);

		tvAvg = (TextView) findViewById(R.id.tv_avg);
		tvSpeed = (TextView) findViewById(R.id.tv_1);
		tvQuality = (TextView) findViewById(R.id.tv_2);
		tvAttitude = (TextView) findViewById(R.id.tv_3);
		rating1 = (RatingBar) findViewById(R.id.rating_1);
		rating2 = (RatingBar) findViewById(R.id.rating_2);
		rating3 = (RatingBar) findViewById(R.id.rating_3);

		tvEvalAll = (TextView) findViewById(R.id.tv_good_eval_all);
		tvEval1 = (TextView) findViewById(R.id.tv_good_eval_1);
		tvEval2 = (TextView) findViewById(R.id.tv_good_eval_2);
		tvEval3 = (TextView) findViewById(R.id.tv_good_eval_3);

		pullListView = (RTPullListView) findViewById(R.id.pullListView);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
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
							workList.addAll(WorkDetailDP.getWorkEvsluate(type, strId,
									strType, page, GoodDetail.this));
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

		tvTitle.setFocusable(true);
		tvTitle.setFocusableInTouchMode(true);
		tvTitle.requestFocus();

		imgBack.setOnClickListener(listener);
		imgShare.setOnClickListener(listener);
		tvEvalAll.setOnClickListener(listener);
		tvEval1.setOnClickListener(listener);
		tvEval2.setOnClickListener(listener);
		tvEval3.setOnClickListener(listener);
		myScrollView.setOnScrollListener(this);
		tvHandle.setOnClickListener(listener);
		layCall.setOnClickListener(listener);
		layShop.setOnClickListener(listener);
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

	OnClickListener listener = new OnClickListener()
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.img_back:
				finish();
				break;
			case R.id.img_share:

				break;
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
			case R.id.btn_task_tougao:
				if (users.size() > 0)
				{
					if (type.equals("1"))
					{
						progressDialog = new LoadingDialog(GoodDetail.this);
						progressDialog.setMessage("订单生成中。。。");
						progressDialog.show();
						progressDialog.setCancelable(false);

						Thread t = new Thread(newTread3);
						t.start();
					}
					else
					{
						intent = new Intent(GoodDetail.this, GoodHire.class);
						intent.putExtra("uid", map.get("uid"));
						intent.putExtra("username", map.get("username"));
						intent.putExtra("srevice_id", strId);
						intent.putExtra("title", map.get("title"));
						intent.putExtra("content", map.get("desc"));
						startActivity(intent);
					}
				}
				else
				{
					Toast.makeText(GoodDetail.this, "您还未登录，请先登录", 2000).show();
				}
				break;
			case R.id.lay_call:
				intent = mIMKit.getChattingActivityIntent(map.get("uid"), MyApp.APP_KEY);
				startActivity(intent);
				break;
			case R.id.lay_shop:
				intent = new Intent(GoodDetail.this, ShopDetail.class);
				intent.putExtra("shopId", map.get("shop_id").toString());
				startActivity(intent);
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
			map = WorkDetailDP.getWorkDetails(type, strId, GoodDetail.this);
			workList = WorkDetailDP.getWorkEvsluate(type, strId, strType, page,
					GoodDetail.this);
			Message msg = mHandler.obtainMessage(1, map);
			msg.sendToTarget();
		}
	};

	Map<String, String> resOrder;
	Runnable newTread3 = new Runnable()
	{
		@Override
		public void run()
		{
			resOrder = WorkDP.buyGoods(strId, GoodDetail.this);
			Message msg = mHandler.obtainMessage(3, map);
			msg.sendToTarget();
		}
	};

	private Handler mHandler = new Handler()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case 1:
				Map<String, String> str = (Map<String, String>) msg.obj;
				if (str.get("code").equals("1000"))
				{
					setViewValues();
					progressDialog.dismiss();
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(GoodDetail.this, str.get("message"), 2000).show();
					finish();
				}
				break;
			case 3:
				progressDialog.dismiss();
				if (resOrder.get("code").equals("1000"))
				{
					intent = new Intent(GoodDetail.this, GoodPay.class);
					intent.putExtra("id", resOrder.get("order_id"));
					intent.putExtra("title", map.get("title"));
					intent.putExtra("cash", map.get("cash"));
					startActivity(intent);
				}
				else
				{

					Toast.makeText(GoodDetail.this, resOrder.get("message"), 2000).show();
				}
				break;
			}
		}
	};

	protected void setViewValues()
	{
		Log.e("good_map", "" + map);

		Glide.with(this).load(map.get("cover")).error(R.drawable.erha).into(imgBg);
		tvGoodTitle.setText(map.get("title"));
		tvGoodCash.setText(map.get("cash"));
		tvGoodUnit.setText("元/" + map.get("unit"));
		tvPercent.setText(map.get("percent") + "%");
		tvSale.setText(map.get("sales_num"));
		tvDesc.setText(Html.fromHtml(map.get("desc")));

		// float a = (Float.valueOf(map.get("speed_score").toString())
		// + Float.valueOf(map.get("quality_score").toString()) +
		// Float.valueOf(map
		// .get("attitude_score").toString())) / 3;
		//
		// tvAvg.setText("" + a);

		tvAvg.setText(map.get("avg_score"));
		tvSpeed.setText(map.get("speed_score"));
		tvQuality.setText(map.get("quality_score"));
		tvAttitude.setText(map.get("attitude_score"));
		rating1.setRating(Float.valueOf(map.get("speed_score")));
		rating2.setRating(Float.valueOf(map.get("quality_score")));
		rating3.setRating(Float.valueOf(map.get("attitude_score")));

		if (type.equals("1"))
		{
			tvTagDesc.setText("作品内容");
			tvTagEvl.setText("作品评价(" + workList.size() + ")");
		}
		else
		{
			tvTagDesc.setText("服务内容");
			tvTagEvl.setText("服务评价(" + workList.size() + ")");
		}

		if (workList.size() <= 0)
		{
			layoutHide.setVisibility(View.VISIBLE);
			tvNoData.setText("暂无评论");
			pullListView.setVisibility(View.GONE);
			tvEvalAll.setVisibility(View.GONE);
			tvEval1.setVisibility(View.GONE);
			tvEval2.setVisibility(View.GONE);
			tvEval3.setVisibility(View.GONE);
		}
		else
		{
			tvEvalAll.setText("全部(" + workList.get(0).get("total") + ")");
			tvEval1.setText("好评(" + workList.get(0).get("good_num") + ")");
			tvEval2.setText("中评(" + workList.get(0).get("middle_num") + ")");
			tvEval3.setText("差评(" + workList.get(0).get("bad_num") + ")");

			itemAdapter = new ShopEvaluateAdapter(GoodDetail.this, workList);
			pullListView.setAdapter(itemAdapter);

			// 动态算出ListView的LayoutParams，并设置到ListView中
			params = StrFormat.getListViewParams(pullListView);
			pullListView.setLayoutParams(params);
		}
	}

	private void listRefreshData()
	{
		page = 1;
		workList.removeAll(workList);
		workList.addAll(WorkDetailDP.getWorkEvsluate(type, strId, strType, page,
				GoodDetail.this));
		myHandler.sendEmptyMessage(LOAD_NEW_INFO);
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

	@Override
	public void onScroll(int scrollY)
	{
		if (scrollY > 0)
		{
			if (layTou.getParent() != search01)
			{
				search02.removeView(layTou);
				search01.addView(layTou);
				if (type.equals("1"))
				{
					tvTitle.setText("作品详情");
				}
				else
				{
					tvTitle.setText("服务详情");
				}

				StatusBarUtil.setStatusBarLightMode(getWindow());
				Glide.with(this).load(R.drawable.ic_toolbar_back).into(imgBack);
				Glide.with(this).load(R.drawable.ic_work_share).into(imgShare);
			}
		}
		else
		{
			if (layTou.getParent() != search02)
			{
				search01.removeView(layTou);
				search02.addView(layTou);
				tvTitle.setText("");
				StatusBarUtil.StatusBarDarkMode(getWindow());
				Glide.with(this).load(R.drawable.ic_toolbar_backnull).into(imgBack);
				Glide.with(this).load(R.drawable.ic_work_sharenull).into(imgShare);
			}
		}
	}
}
