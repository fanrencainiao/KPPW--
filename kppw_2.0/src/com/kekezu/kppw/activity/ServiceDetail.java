package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.mobileim.YWIMKit;
import com.bumptech.glide.Glide;
import com.kekezu.kppw.MyApp;
import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.ShopEvaluateAdapter;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.MyScrollView;
import com.kekezu.kppw.control.RTPullListView;
import com.kekezu.kppw.control.MyScrollView.OnScrollListener;
import com.kekezu.kppw.dataprocess.ShopDetailDP;
import com.kekezu.kppw.imcustom.LoginSampleHelper;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 服务商详情
 * 
 * @author cm
 * 
 */
public class ServiceDetail extends FragmentActivity implements OnScrollListener
{
	ImageView imgBack, imgShare;
	ImageView imgPic, img1, img2, img3, img4;
	TextView tvName, tvRate, tvScore, tvService, tvDesc, tvCity, tvEvl;

	TextView tvTag;
	GridView gridView;
	SimpleAdapter gAdapter;

	LinearLayout layTou;
	LinearLayout layview;
	LinearLayout search03, search04;
	TextView tvTitle;
	MyScrollView myScrollView;
	int searchLayoutTop;
	LinearLayout layCall;
	LinearLayout lay1;
	YWIMKit mIMKit;
	TextView tvSubmit;

	ShopDetailWork shopDetailWork;
	ShopDetailService shopDetailService;
	ShopDetailCase shopDetailCase;
	ShopDetailEvaluate shopDetailEvaluate;

	LoadingDialog progressDialog;
	Map<String, String> map;
	ArrayList<HashMap<String, String>> cateList;
	ArrayList<HashMap<String, String>> evlList;
	String strShopId;

	LinearLayout layColl;
	ImageView imgColl;

	RTPullListView pullListView;
	ShopEvaluateAdapter itemAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_detail);

		strShopId = getIntent().getStringExtra("uid");
		initView();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void initView()
	{
		mIMKit = LoginSampleHelper.getInstance().getIMKit();

		imgBack = (ImageView) findViewById(R.id.img_back);
		imgShare = (ImageView) findViewById(R.id.img_share);

		imgPic = (ImageView) findViewById(R.id.img_pic);
		img1 = (ImageView) findViewById(R.id.imageView3);
		img2 = (ImageView) findViewById(R.id.imageView4);
		img3 = (ImageView) findViewById(R.id.imageView5);
		img4 = (ImageView) findViewById(R.id.imageView6);
		tvName = (TextView) findViewById(R.id.tx_name);
		tvRate = (TextView) findViewById(R.id.tv_rate);
		tvScore = (TextView) findViewById(R.id.tv_score);
		tvService = (TextView) findViewById(R.id.tv_service);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		tvCity = (TextView) findViewById(R.id.tv_city);
		gridView = (GridView) findViewById(R.id.gridView1);
		tvTag = (TextView) findViewById(R.id.tv_tag);
		tvEvl = (TextView) findViewById(R.id.tv_evl);

		layTou = (LinearLayout) findViewById(R.id.lay_tou);
		layview = (LinearLayout) findViewById(R.id.layout_view);
		search03 = (LinearLayout) findViewById(R.id.search03);
		search04 = (LinearLayout) findViewById(R.id.search04);
		myScrollView = (MyScrollView) findViewById(R.id.myScrollView);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvSubmit = (TextView) findViewById(R.id.btn_task_tougao);

		layCall = (LinearLayout) findViewById(R.id.lay_call);
		layColl = (LinearLayout) findViewById(R.id.lay_coll);
		imgColl = (ImageView) findViewById(R.id.img_task_collection);

		pullListView = (RTPullListView) findViewById(R.id.pullListView);

		myScrollView.setOnScrollListener(this);
		imgBack.setOnClickListener(listener);
		tvSubmit.setOnClickListener(listener);
		layCall.setOnClickListener(listener);
		layColl.setOnClickListener(listener);
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			map = ShopDetailDP.getuserDetail(strShopId, ServiceDetail.this);
			evlList = ShopDetailDP.taskCommentList(strShopId, "0", ServiceDetail.this);
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
				Log.e("map", "" + map);
				if (map.get("code").equals("1000"))
				{
					setViewValues();
					progressDialog.dismiss();
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(ServiceDetail.this, map.get("message"), 2000).show();
					finish();
				}
				break;
			}
		}
	};

	protected void setViewValues()
	{
		Glide.with(this).load(map.get("avatar")).into(imgPic);

		if (map.get("realname").equals("1"))
		{
			Glide.with(this).load(R.drawable.cert1_gray).into(img1);
		}
		else
		{
			Glide.with(this).load(R.drawable.cert).into(img1);
		}
		if (map.get("email").equals("1"))
		{
			Glide.with(this).load(R.drawable.cert2).into(img2);
		}
		else
		{
			Glide.with(this).load(R.drawable.cert2_gray).into(img2);
		}
		if (map.get("isEnterprise").equals("1"))
		{
			Glide.with(this).load(R.drawable.cert4).into(img4);
		}
		else
		{
			Glide.with(this).load(R.drawable.cert4_gray).into(img4);
		}

		tvName.setText(map.get("name"));
		tvRate.setText(map.get("good_comment_rate") + "%");
		tvScore.setText(map.get("avg_score"));
		tvService.setText(map.get("service_num"));
		tvDesc.setText(map.get("introduce"));
		tvCity.setText(map.get("city_name"));

		cateList = ShopDetailDP.getShopCate(map.get("cate_name"));
		gAdapter = new SimpleAdapter(ServiceDetail.this, cateList,
				R.layout.industry_list_item_blue, new String[] { "cate" },
				new int[] { R.id.textView1 });
		gridView.setAdapter(gAdapter);
		tvTag.setText("技能标签(" + cateList.size() + ")");
		//tvEvl.setText("评价(" + map.get("total_comment") + ")");

		setEvlValue();
	}

	ViewGroup.LayoutParams params;

	private void setEvlValue()
	{
		itemAdapter = new ShopEvaluateAdapter(this, evlList);
		pullListView.setAdapter(itemAdapter);

		// 动态算出ListView的LayoutParams，并设置到ListView中
		params = StrFormat.getListViewParams(pullListView);
		pullListView.setLayoutParams(params);
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
			case R.id.lay_call:
				Intent intent = mIMKit.getChattingActivityIntent(map.get("uid"),
						MyApp.APP_KEY);
				startActivity(intent);
				break;
			case R.id.btn_task_tougao:
				intent = new Intent(ServiceDetail.this, GoodHire.class);
				intent.putExtra("uid", map.get("uid"));
				intent.putExtra("username", "");
				intent.putExtra("srevice_id", strShopId);
				intent.putExtra("title", "");
				intent.putExtra("content", "");
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus)
		{
			searchLayoutTop = 69;// 获取searchLayout的顶部位置
		}
	}

	// 监听滚动Y值变化，通过addView和removeView来实现悬停效果
	@Override
	public void onScroll(int scrollY)
	{
		if (scrollY > 0)
		{
			if (layTou.getParent() != search03)
			{
				search04.removeView(layTou);
				search03.addView(layTou);

				StatusBarUtil.setStatusBarLightMode(getWindow());
				tvTitle.setText("人才详情");
				Glide.with(this).load(R.drawable.ic_toolbar_back).into(imgBack);
				Glide.with(this).load(R.drawable.ic_work_share).into(imgShare);
			}
		}
		else
		{
			if (layTou.getParent() != search04)
			{
				search03.removeView(layTou);
				search04.addView(layTou);

				StatusBarUtil.StatusBarDarkMode(getWindow());
				tvTitle.setText("");
				Glide.with(this).load(R.drawable.ic_toolbar_backnull).into(imgBack);
				Glide.with(this).load(R.drawable.ic_work_sharenull).into(imgShare);
			}
		}
	}
}
