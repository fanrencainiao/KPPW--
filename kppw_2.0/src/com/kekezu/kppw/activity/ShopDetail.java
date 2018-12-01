package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.mobileim.YWIMKit;
import com.bumptech.glide.Glide;
import com.kekezu.kppw.MyApp;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.CustomViewPager;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.MyGridView;
import com.kekezu.kppw.control.MyScrollView;
import com.kekezu.kppw.control.MyScrollView.OnScrollListener;
import com.kekezu.kppw.dataprocess.ShopDetailDP;
import com.kekezu.kppw.imcustom.LoginSampleHelper;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 店铺详情
 * 
 * @author cm
 * 
 */
public class ShopDetail extends FragmentActivity implements OnScrollListener
{
	ImageView imgBack, imgShare;
	ImageView imgPic, img1, img2, img3, img4;
	TextView tvName, tvRate, tvScore, tvService, tvDesc;

	MyGridView gridView;
	SimpleAdapter gAdapter;

	LinearLayout layTou;
	LinearLayout layview;
	LinearLayout layHide, search01, search02, search03, search04;
	TextView tvTitle;
	MyScrollView myScrollView;
	int searchLayoutTop;
	LinearLayout layCall;
	LinearLayout lay1;
	YWIMKit mIMKit;
	TextView tvSubmit;
	TextView tvTag;

	private CustomViewPager viewPager;
	private List<Fragment> fragmentsList = new ArrayList<Fragment>();
	ShopDetailWork shopDetailWork;
	ShopDetailService shopDetailService;
	ShopDetailCase shopDetailCase;
	ShopDetailEvaluate shopDetailEvaluate;
	LinearLayout layout1;
	LinearLayout layout2;
	LinearLayout layout3;
	LinearLayout layout4;
	TextView tvGood, tvSer, tvCase, tvEvl, tvCity;
	TextView textView1;
	TextView textView2;
	TextView textView3;
	TextView textView4;
	private FragmentPagerAdapter mAdapter;

	LoadingDialog progressDialog;
	Map<String, String> map;
	ArrayList<HashMap<String, String>> cateList;
	String strShopId;
	String strFocused;

	LinearLayout layColl;
	ImageView imgColl;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_detail);

		strShopId = getIntent().getStringExtra("shopId");

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
		gridView = (MyGridView) findViewById(R.id.gridView1);
		tvTag = (TextView) findViewById(R.id.tv_tag);

		layTou = (LinearLayout) findViewById(R.id.lay_tou);
		layview = (LinearLayout) findViewById(R.id.layout_view);
		layHide = (LinearLayout) findViewById(R.id.lay_hide);
		search01 = (LinearLayout) findViewById(R.id.search01);
		search02 = (LinearLayout) findViewById(R.id.search02);
		search03 = (LinearLayout) findViewById(R.id.search03);
		search04 = (LinearLayout) findViewById(R.id.search04);
		myScrollView = (MyScrollView) findViewById(R.id.myScrollView);
		tvTitle = (TextView) findViewById(R.id.tv_title);

		layout1 = (LinearLayout) findViewById(R.id.layout_1);
		layout2 = (LinearLayout) findViewById(R.id.layout_2);
		layout3 = (LinearLayout) findViewById(R.id.layout_3);
		layout4 = (LinearLayout) findViewById(R.id.layout_4);
		viewPager = (CustomViewPager) findViewById(R.id.pager);
		textView1 = (TextView) findViewById(R.id.tv_check1);
		textView2 = (TextView) findViewById(R.id.tv_check2);
		textView3 = (TextView) findViewById(R.id.tv_check3);
		textView4 = (TextView) findViewById(R.id.tv_check4);
		tvSubmit = (TextView) findViewById(R.id.btn_task_tougao);

		tvGood = (TextView) findViewById(R.id.textView1xs);
		tvSer = (TextView) findViewById(R.id.textView3rsa);
		tvCase = (TextView) findViewById(R.id.textView5vsdw);
		tvEvl = (TextView) findViewById(R.id.TextView03);

		layCall = (LinearLayout) findViewById(R.id.lay_call);
		layColl = (LinearLayout) findViewById(R.id.lay_coll);
		imgColl = (ImageView) findViewById(R.id.img_task_collection);

		myScrollView.setOnScrollListener(this);
		imgBack.setOnClickListener(listener);
		layout1.setOnClickListener(listener);
		layout2.setOnClickListener(listener);
		layout3.setOnClickListener(listener);
		layout4.setOnClickListener(listener);
		tvSubmit.setOnClickListener(listener);

		layCall.setOnClickListener(listener);
		layColl.setOnClickListener(listener);
	}

	Runnable newTread2 = new Runnable()
	{
		@Override
		public void run()
		{
			if (strFocused.equals("0"))
			{
				strFocused = "1";
				ShopDetailDP.addShopFocus(strShopId, ShopDetail.this);
			}
			else
			{
				strFocused = "0";
				ShopDetailDP.delShopFocus(strShopId, ShopDetail.this);
			}
		}
	};

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			map = ShopDetailDP.getShopDetails(strShopId, ShopDetail.this);
			strFocused = ShopDetailDP.collectStatus(strShopId, ShopDetail.this);
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
					InitViewPager();
					setTextViewColor(1);
					progressDialog.dismiss();
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(ShopDetail.this, map.get("message"), 2000).show();
					finish();
				}
				break;
			}
		}
	};

	protected void setViewValues()
	{
		Glide.with(this).load(map.get("shop_pic")).into(imgPic);

		if (map.get("realname").equals("0"))
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

		tvName.setText(map.get("shop_name"));
		tvRate.setText(map.get("good_comment_rate") + "%");
		tvScore.setText(map.get("avg_score"));
		tvService.setText(map.get("total_service"));
		tvDesc.setText(map.get("shop_desc"));
		tvCity.setText(map.get("city_name"));

		if (strFocused.equals("0"))
		{
			Glide.with(this).load(R.drawable.tab_collect).into(imgColl);
		}
		else
		{
			Glide.with(this).load(R.drawable.ic_wkxq_xin2).into(imgColl);
		}

		cateList = ShopDetailDP.getShopCate(map.get("cate_name"));
		gAdapter = new SimpleAdapter(ShopDetail.this, cateList,
				R.layout.industry_list_item_blue, new String[] { "cate" },
				new int[] { R.id.textView1 });
		gridView.setAdapter(gAdapter);
		tvTag.setText("技能标签(" + cateList.size() + ")");

		tvGood.setText("作品(" + map.get("goods_num") + ")");
		tvSer.setText("服务(" + map.get("service_num") + ")");
		tvCase.setText("案例(" + map.get("success_case_num") + ")");
		tvEvl.setText("评价(" + map.get("comment_num") + ")");
	}

	ViewGroup.LayoutParams params;
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
			case R.id.layout_1:
				setTextViewColor(1);
				viewPager.setCurrentItem(0);
				break;
			case R.id.layout_2:
				setTextViewColor(2);
				viewPager.setCurrentItem(1);
				break;
			case R.id.layout_3:
				setTextViewColor(3);
				viewPager.setCurrentItem(2);
				break;
			case R.id.layout_4:
				setTextViewColor(4);
				viewPager.setCurrentItem(3);
				break;
			case R.id.btn_task_tougao:
				intent = new Intent(ShopDetail.this, GoodHire.class);
				intent.putExtra("uid", map.get("uid"));
				intent.putExtra("username", "");
				intent.putExtra("srevice_id", strShopId);
				intent.putExtra("title", "");
				intent.putExtra("content", "");
				startActivity(intent);
				break;
			case R.id.lay_coll:
				if (strFocused.equals("0"))
				{
					Glide.with(ShopDetail.this).load(R.drawable.ic_wkxq_xin2)
							.into(imgColl);
					Toast.makeText(ShopDetail.this, "已收藏", 2000).show();
					Thread t = new Thread(newTread2);
					t.start();
				}
				else
				{
					Glide.with(ShopDetail.this).load(R.drawable.tab_collect)
							.into(imgColl);
					Toast.makeText(ShopDetail.this, "已取消", 2000).show();
					Thread t = new Thread(newTread2);
					t.start();
				}
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
			searchLayoutTop = layHide.getBottom();// 获取searchLayout的顶部位置
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

			if (scrollY >= searchLayoutTop - search03.getBottom())
			{
				if (layview.getParent() != search01)
				{
					search02.removeView(layview);
					search01.addView(layview);
				}
			}
			else
			{
				if (layview.getParent() != search02)
				{
					search01.removeView(layview);
					search02.addView(layview);
				}
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

	@SuppressWarnings("deprecation")
	private void InitViewPager()
	{
		shopDetailWork = new ShopDetailWork(strShopId);
		shopDetailService = new ShopDetailService(strShopId);
		shopDetailCase = new ShopDetailCase(strShopId);
		shopDetailEvaluate = new ShopDetailEvaluate(strShopId);

		fragmentsList.add(shopDetailWork);
		fragmentsList.add(shopDetailService);
		fragmentsList.add(shopDetailCase);
		fragmentsList.add(shopDetailEvaluate);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public int getCount()
			{
				return fragmentsList.size();
			}

			@Override
			public Fragment getItem(int arg0)
			{
				return fragmentsList.get(arg0);
			}
		};

		viewPager.setAdapter(mAdapter);
		viewPager.setOffscreenPageLimit(fragmentsList.size());
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				switch (position)
				{
				case 0:
					setTextViewColor(1);
					break;
				case 1:
					setTextViewColor(2);
					break;
				case 2:
					setTextViewColor(3);
					break;
				case 3:
					setTextViewColor(4);
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{

			}
		});
		viewPager.setCurrentItem(0);

	}

	@SuppressWarnings("deprecation")
	private void setTextViewColor(int index)
	{
		switch (index)
		{
		case 1:
			textView1.setBackgroundColor(getResources().getColor(R.color.header_bg));
			textView2.setBackgroundColor(getResources().getColor(R.color.main_bg));
			textView3.setBackgroundColor(getResources().getColor(R.color.main_bg));
			textView4.setBackgroundColor(getResources().getColor(R.color.main_bg));
			break;
		case 2:
			textView1.setBackgroundColor(getResources().getColor(R.color.main_bg));
			textView2.setBackgroundColor(getResources().getColor(R.color.header_bg));
			textView3.setBackgroundColor(getResources().getColor(R.color.main_bg));
			textView4.setBackgroundColor(getResources().getColor(R.color.main_bg));
			break;
		case 3:
			textView1.setBackgroundColor(getResources().getColor(R.color.main_bg));
			textView2.setBackgroundColor(getResources().getColor(R.color.main_bg));
			textView3.setBackgroundColor(getResources().getColor(R.color.header_bg));
			textView4.setBackgroundColor(getResources().getColor(R.color.main_bg));
			break;
		case 4:
			textView1.setBackgroundColor(getResources().getColor(R.color.main_bg));
			textView2.setBackgroundColor(getResources().getColor(R.color.main_bg));
			textView3.setBackgroundColor(getResources().getColor(R.color.main_bg));
			textView4.setBackgroundColor(getResources().getColor(R.color.header_bg));
			break;
		default:
			break;
		}

	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter
	{
		public List<Activity> mListViews;

		public MyPagerAdapter(List<Activity> mListViews)
		{
			this.mListViews = mListViews;
		}

		@Override
		public void finishUpdate(View arg0)
		{
		}

		@Override
		public int getCount()
		{
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
		}

		@Override
		public Parcelable saveState()
		{
			return null;
		}

		@Override
		public void startUpdate(View arg0)
		{
		}
	}
}
