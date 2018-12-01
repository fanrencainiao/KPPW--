package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.IndexCateAdapter;
import com.kekezu.kppw.adapter.ProviderAdapter;
import com.kekezu.kppw.adapter.ServiceAndGoodAdapter;
import com.kekezu.kppw.adapter.TaskAdapter;
import com.kekezu.kppw.bean.ADInfo;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.ImageCycleView;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.MyScrollView;
import com.kekezu.kppw.dataprocess.IndexDP;
import com.kekezu.kppw.dataprocess.IndustryDP;
import com.kekezu.kppw.dataprocess.MallDP;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.pagerecyclerview.MyPageIndicator;
import com.kekezu.kppw.pagerecyclerview.PageGridView;
import com.kekezu.kppw.ruihui.NavigationBar;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.ConfigInc;
import com.kekezu.kppw.utils.StrFormat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.kekezu.kppw.control.MyScrollView.OnScrollListener;

/**
 * 首页
 * 
 * @author cm
 * 
 */
public class FragmentIndex extends Fragment implements OnScrollListener
{
	View view;
	EditText etSearch;
	ListView listView;
	TaskAdapter listAdapter;
	private MyScrollView scrollView;
	TextView tvCheck;
	LinearLayout layCheck;

	Intent intent;

	ImageView imageView1;
	ImageView imageView2;
	ImageView imageView3;
	ImageView imageView4;
	ImageView imageView5;
	ImageView imageView6;
	ImageView imageView7;
	ImageView imageView8;
	
	TextView tv1;

	ArrayList<HashMap<String, Object>> list;
	ArrayList<HashMap<String, Object>> blist;
	ArrayList<HashMap<String, Object>> slist;
	ArrayList<HashMap<String, Object>> servicelist;
	ArrayList<HashMap<String, Object>> goodlist;
	// 幻灯片控件
	ImageCycleView mAdView;
	// 幻灯片图片集合
	private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();

	GridView gridView;
	PageGridView pageGridView2;
	MyPageIndicator pageIndicator;

	int imageHeight;
	TextView tvPro, tvSer, tvGood, tvMore;
	int check = 1;

	LinearLayout layAAA, layRong1, lay1, layHide1;
	LinearLayout layRong2, lay2, layHide2;
	int searchLayoutTop;
	ViewGroup.LayoutParams params;
	TextView tvCode;

	LoadingDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fg_index, container, false);
		viewInit();

		// 页面打开是的loading
		progressDialog = new LoadingDialog(getActivity());
		progressDialog.show();
		progressDialog.setCancelable(false);
		// 数据请求
		Thread t = new Thread(newTread1);
		t.start();

		// 数据刷新
		EventBus.getDefault().register(this);
		return view;
	}

	// 控件初始化
	public void viewInit()
	{
		tv1 = (TextView) view.findViewById(R.id.textView1);
		
		mAdView = (ImageCycleView) view.findViewById(R.id.ad_view);
		etSearch = (EditText) view.findViewById(R.id.et_search);
		tvCode = (TextView) view.findViewById(R.id.tv_index_code);
		scrollView = (MyScrollView) view.findViewById(R.id.scrollView1);

		tvCheck = (TextView) view.findViewById(R.id.index_tv_check);
		layCheck = (LinearLayout) view.findViewById(R.id.lay_ser_check);
		listView = (ListView) view.findViewById(R.id.list_service);

		layAAA = (LinearLayout) view.findViewById(R.id.lay_aaa);
		layRong1 = (LinearLayout) view.findViewById(R.id.lay_rongqitou);
		layRong2 = (LinearLayout) view.findViewById(R.id.lay_rongqituijian);
		lay1 = (LinearLayout) view.findViewById(R.id.lay_1);
		lay2 = (LinearLayout) view.findViewById(R.id.lay_2);
		layHide1 = (LinearLayout) view.findViewById(R.id.lay_hide1);
		layHide2 = (LinearLayout) view.findViewById(R.id.lay_hide2);

		tvPro = (TextView) view.findViewById(R.id.tv_pro);
		tvSer = (TextView) view.findViewById(R.id.tv_ser);
		tvGood = (TextView) view.findViewById(R.id.tv_good);
		tvMore = (TextView) view.findViewById(R.id.tv_more);

		pageIndicator = (MyPageIndicator) view.findViewById(R.id.pageindicator);
		pageGridView2 = (PageGridView) view.findViewById(R.id.pagingGridView2);
		gridView = (GridView) view.findViewById(R.id.gridView1);

		etSearch.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					intent = new Intent(getActivity(), Search.class);
					intent.putExtra("type", 0);
					startActivity(intent);
				}
				return false;
			}
		});

		tvPro.setOnClickListener(listener);
		tvSer.setOnClickListener(listener);
		tvGood.setOnClickListener(listener);
		tvMore.setOnClickListener(listener);
		tvCode.setOnClickListener(listener);
		tv1.setOnClickListener(listener);

		scrollView.setOnScrollListener(FragmentIndex.this);
		listView.setOnItemClickListener(clickListener);
	}

	OnClickListener listener = new OnClickListener()
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.tv_pro:
				check = 1;
				tvPro.setTextColor(getResources().getColor(R.color.header_bg));
				tvSer.setTextColor(getResources().getColor(R.color.black));
				tvGood.setTextColor(getResources().getColor(R.color.black));

				listView.setAdapter(new ProviderAdapter(getActivity(), list));
				// 动态算出ListView的LayoutParams，并设置到ListView中
				params = StrFormat.getListViewParams(listView);
				listView.setLayoutParams(params);
				break;
			case R.id.tv_ser:
				check = 2;
				tvPro.setTextColor(getResources().getColor(R.color.black));
				tvSer.setTextColor(getResources().getColor(R.color.header_bg));
				tvGood.setTextColor(getResources().getColor(R.color.black));

				listView.setAdapter(new ServiceAndGoodAdapter(getActivity(), servicelist,
						"1"));
				Log.e("servicelist", servicelist.toString());
				// 动态算出ListView的LayoutParams，并设置到ListView中
				params = StrFormat.getListViewParams(listView);
				listView.setLayoutParams(params);
				break;
			case R.id.tv_good:
				check = 3;
				tvPro.setTextColor(getResources().getColor(R.color.black));
				tvSer.setTextColor(getResources().getColor(R.color.black));
				tvGood.setTextColor(getResources().getColor(R.color.header_bg));

				listView.setAdapter(new ServiceAndGoodAdapter(getActivity(), goodlist,
						"1"));
				// 动态算出ListView的LayoutParams，并设置到ListView中
				params = StrFormat.getListViewParams(listView);
				listView.setLayoutParams(params);
				break;
			case R.id.tv_more:
				if (check == 1)
				{
					TestEvent event = new TestEvent();
					event.setGetMore(true);
					EventBus.getDefault().post(event);
				}
				else if (check == 2)
				{
					intent = new Intent(getActivity(), MoreResult.class);
					intent.putExtra("type", check);
					startActivity(intent);
				}
				else if (check == 3)
				{
					intent = new Intent(getActivity(), MoreResult.class);
					intent.putExtra("type", check);
					startActivity(intent);
				}
				else if (check == 4)
				{
					TestEvent event = new TestEvent();
					event.setGetMore(true);
					EventBus.getDefault().post(event);
				}
				break;
			case R.id.tv_index_code:
				if (TestData.getuserType(getActivity()) == 0)
				{
					Intent intent = new Intent();
					intent.setClass(getActivity(), MipcaActivityCapture.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
				}
				else
				{
					Toast.makeText(getActivity(), "当前身份无此功能", 2000).show();
				}
				break;
			case R.id.textView1:
				startActivity(new Intent(getActivity(), NavigationBar.class));
				break;
			default:
				break;
			}
		}
	};

	// 扫描的请求值
	private final static int SCANNIN_GREQUEST_CODE = 520;

	OnItemClickListener clickListener = new OnItemClickListener()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			HashMap<String, Object> item = (HashMap<String, Object>) parent
					.getItemAtPosition(position);
			if (check == 1)
			{
				Intent intent = new Intent(getActivity(), ShopDetail.class);
				intent.putExtra("shopId", item.get("id").toString());
				startActivity(intent);
			}
			else if (check == 2)
			{
				Intent intent = new Intent(getActivity(), GoodDetail.class);
				intent.putExtra("id", item.get("id").toString());
				intent.putExtra("type", "2");
				startActivity(intent);
			}
			else if (check == 3)
			{
				Intent intent = new Intent(getActivity(), GoodDetail.class);
				intent.putExtra("id", item.get("id").toString());
				intent.putExtra("type", "1");
				startActivity(intent);
			}
			else if (check == 4)
			{
				intent = new Intent(getActivity(), TaskDetails.class);
				intent.putExtra("task_id", item.get("id").toString());
				startActivity(intent);
			}
		}
	};

	Handler handler = new Handler()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 1)
			{
				// 顶部banner
				blist = (ArrayList<HashMap<String, Object>>) msg.obj;
				for (int i = 0; i < blist.size(); i++)
				{
					ADInfo info = new ADInfo();
					info.setUrl(blist.get(i).get("ad_file").toString());
					info.setContent(blist.get(i).get("ad_url").toString());
					infos.add(info);
				}
				mAdView.setImageResources(infos);

				Thread t = new Thread(newTread2);
				t.start();

			}
			else if (msg.what == 2)
			{
				// 一级分类
				slist = (ArrayList<HashMap<String, Object>>) msg.obj;
				IndexCateAdapter adapter = new IndexCateAdapter(slist, getActivity());
				pageGridView2.setAdapter(adapter);
				pageGridView2.setOnItemClickListener(adapter);

				if (slist.size() > 8)
				{
					// 设置分页指示器
					pageGridView2.setPageIndicator(pageIndicator);
				}

				Thread t = new Thread(newTread3);
				t.start();
			}
			else if (msg.what == 3)
			{
				list = (ArrayList<HashMap<String, Object>>) msg.obj;
				if (TestData.getuserType(getActivity()) == 0) // 判断雇主还是威客身份
				{
					tvCheck.setText("最新推荐");
					layCheck.setVisibility(View.VISIBLE);
					check = 1;
					// 雇主身份展示推荐的服务商
					listView.setAdapter(new ProviderAdapter(getActivity(), list));

					Thread t = new Thread(newTread4);
					t.start();
				}
				else
				{
					layCheck.setVisibility(View.GONE);
					tvCheck.setText("推荐任务");
					check = 4;

					// 威客身份展示推荐任务
					listAdapter = new TaskAdapter(getActivity(), list);
					listView.setAdapter(listAdapter);
				}

				// 动态算出ListView的LayoutParams，并设置到ListView中
				params = StrFormat.getListViewParams(listView);
				listView.setLayoutParams(params);

				progressDialog.dismiss();
			}
		}
	};

	// 推荐服务，商品列表
	Runnable newTread4 = new Runnable()
	{
		@Override
		public void run()
		{
			servicelist = MallDP.hotShop(getActivity(), 3);
			goodlist = MallDP.hotShop(getActivity(), 2);
		}
	};

	Runnable newTread3 = new Runnable()
	{
		ArrayList<HashMap<String, Object>> runlist;

		@Override
		public void run()
		{
			// 根据威客雇主身份请求任务或者服务商推荐数据
			if (TestData.getuserType(getActivity()) == 0)
			{
				runlist = MallDP.hotShop(getActivity(), 1);
			}
			else
			{
				runlist = TaskDP.getIndexTask(getActivity());
			}

			Message msg = handler.obtainMessage(3, runlist);
			msg.sendToTarget();
		}
	};

	Runnable newTread1 = new Runnable()
	{
		ArrayList<HashMap<String, Object>> bannerlist;

		@Override
		public void run()
		{
			// banner图集合请求
			bannerlist = IndexDP.getIndexImageList(getActivity());
			Message msg = handler.obtainMessage(1, bannerlist);
			msg.sendToTarget();
		}

	};

	Runnable newTread2 = new Runnable()
	{
		// 一级分类请求
		ArrayList<HashMap<String, Object>> skilllist;

		@Override
		public void run()
		{
			skilllist = IndustryDP.getSkill(getActivity());
			Message msg = handler.obtainMessage(2, skilllist);
			msg.sendToTarget();
		}

	};

	// 身份切换后的数据，页面刷新
	@SuppressWarnings("deprecation")
	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isRefesh())
		{
			progressDialog = new LoadingDialog(getActivity());
			progressDialog.show();
			progressDialog.setCancelable(false);

			// 切换身份后，推荐内容刷新
			Thread t = new Thread(newTread3);
			t.start();

			scrollView.fullScroll(ScrollView.FOCUS_UP);

			if (layHide1.getParent() != lay1)
			{
				layRong1.removeView(layHide1);
				lay1.addView(layHide1);
				etSearch.setBackgroundColor(getResources().getColor(R.color.main_bg));
				tvCode.setBackground(getResources()
						.getDrawable(R.drawable.index_codenull));
				StatusBarUtil.StatusBarDarkMode(getActivity().getWindow());
			}

			if (layHide2.getParent() != lay2)
			{
				layRong2.removeView(layHide2);
				lay2.addView(layHide2);
			}
		}
	}

	@Override
	public void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	// 页面滑动时，头部的显示隐藏
	@SuppressWarnings("deprecation")
	@Override
	public void onScroll(int scrollY)
	{
		searchLayoutTop = layAAA.getBottom();
		if (scrollY > 0)
		{
			if (layHide1.getParent() != layRong1)
			{
				lay1.removeView(layHide1);
				layRong1.addView(layHide1);
				tvCode.setBackground(getResources().getDrawable(R.drawable.index_code));
				etSearch.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.r_bg_edittext_3_1));
				StatusBarUtil.getStatusBarLightMode(getActivity().getWindow());
			}

			if (scrollY >= searchLayoutTop - layHide1.getBottom())
			{
				if (layHide2.getParent() != layRong2)
				{
					lay2.removeView(layHide2);
					layRong2.addView(layHide2);
				}
			}
			else
			{
				if (layHide2.getParent() != lay2)
				{
					layRong2.removeView(layHide2);
					lay2.addView(layHide2);
				}
			}

		}
		else
		{
			if (layHide1.getParent() != lay1)
			{
				layRong1.removeView(layHide1);
				lay1.addView(layHide1);
				etSearch.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.r_bg_edittext_3));
				tvCode.setBackground(getResources()
						.getDrawable(R.drawable.index_codenull));
				StatusBarUtil.StatusBarDarkMode(getActivity().getWindow());
			}
		}
	}

	HashMap<String, String> mapCode;

	// 雇主身份二维码扫描结果处理
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == 520)
			{
				Bundle bundle = data.getExtras();
				// 显示扫描到的内容
				mapCode = IndustryDP.getQCode(bundle.getString("result"));
				if (!mapCode.isEmpty())
				{
					// Log.e("domain", mapCode.get("domain"));
					// 获取网站地址和扫描结果对比
					String s = ConfigInc.getServiceAdress(getActivity());
					if (mapCode.get("domain").equals(s.substring(0, s.length() - 5)))
					{
						if (mapCode.get("is_open").equals("1"))
						{
							Intent intent = new Intent(getActivity(), ShopDetail.class);
							intent.putExtra("shopId", mapCode.get("shop_id").toString());
							startActivity(intent);
						}
						else
						{
							Toast.makeText(getActivity(), "未发现店铺", 5000).show();
						}
					}
					else
					{
						Toast.makeText(getActivity(), "未发现店铺", 5000).show();
					}
				}
				else
				{
					Toast.makeText(getActivity(), "无法识别店铺", 5000).show();
				}
			}
			break;
		}
	}
}
