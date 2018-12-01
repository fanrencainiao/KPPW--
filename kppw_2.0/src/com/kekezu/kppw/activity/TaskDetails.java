package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import com.alibaba.mobileim.YWIMKit;
import com.bumptech.glide.Glide;
import com.kekezu.kppw.MyApp;
import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.ManListItemAdapter;
import com.kekezu.kppw.adapter.TaskEvaluateAdapter;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.bean.UserBean;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RoundTransform;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.imcustom.LoginSampleHelper;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.ConfigInc;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 任务详情页
 * 
 * @author cm
 * 
 */
public class TaskDetails extends Activity
{
	LoadingDialog progressDialog;
	ImageView imgBack;
	TextView header_title;
	TextView tvTaskType, tvDing, tvJI, tvYin, tvPing;
	TextView tvTitle, tvCash, tvName, tvTime, tvCate, tvCity, tvLook, tvStatus,
			tvCashStatus, tvEndTime, tvDesc, tvDescMore;
	LinearLayout layoutHide;
	LinearLayout layGaojian, layJiaofu, layWeiQuan, layPingJia;
	LinearLayout layCall;
	LinearLayout layColl;
	ImageView imgColl;

	TextView tvWorkNum, tvJiaoNum, tvWeiNum, tvPingNum;
	TextView tvLine1, tvLine2, tvLine3, tvLine4;
	TextView tvUnit;

	ListView listView;
	ManListItemAdapter manAdapter;
	TaskEvaluateAdapter evlAdapter;

	Map<String, String> map;
	ArrayList<HashMap<String, Object>> workList;
	ArrayList<HashMap<String, Object>> jiaofuList;
	ArrayList<HashMap<String, Object>> weiquanList;
	ArrayList<HashMap<String, String>> pingjiaList;
	String strTaskId;
	Boolean flag = true;
	TextView tvHandle;
	LinearLayout layHandle;
	LinearLayout layFujian;
	LayoutParams params;

	YWIMKit mIMKit;
	String strFocused;
	String check = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_detail);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		strTaskId = getIntent().getStringExtra("task_id");
		initView();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
		EventBus.getDefault().register(this);
	}

	private void initView()
	{
		mIMKit = LoginSampleHelper.getInstance().getIMKit();

		imgBack = (ImageView) findViewById(R.id.img_back);
		header_title = (TextView) findViewById(R.id.header_title);
		tvTitle = (TextView) findViewById(R.id.tv_task_title);
		tvCashStatus = (TextView) findViewById(R.id.textView5);
		tvCash = (TextView) findViewById(R.id.tv_task_cash);
		tvName = (TextView) findViewById(R.id.tv_task_name);
		tvTime = (TextView) findViewById(R.id.tv_task_time);
		tvCate = (TextView) findViewById(R.id.tv_task_cate);
		tvCity = (TextView) findViewById(R.id.tv_task_city);
		tvLook = (TextView) findViewById(R.id.tv_task_look);
		tvStatus = (TextView) findViewById(R.id.tv_task_status);
		tvEndTime = (TextView) findViewById(R.id.tv_task_endtime);
		tvDesc = (TextView) findViewById(R.id.tv_task_desc);
		tvDescMore = (TextView) findViewById(R.id.tv_task_descmore);
		layoutHide = (LinearLayout) findViewById(R.id.lay_mygj);
		listView = (ListView) findViewById(R.id.listView1);
		tvWorkNum = (TextView) findViewById(R.id.tv_task_worknum);
		tvJiaoNum = (TextView) findViewById(R.id.tv_task_jiaofu);
		tvWeiNum = (TextView) findViewById(R.id.tv_task_weiquan);
		tvPingNum = (TextView) findViewById(R.id.tv_task_pingjia);
		tvLine1 = (TextView) findViewById(R.id.tv_gaojian);
		tvLine2 = (TextView) findViewById(R.id.tv_jiaofu);
		tvLine3 = (TextView) findViewById(R.id.tv_weiquan);
		tvLine4 = (TextView) findViewById(R.id.tv_pingjia);
		tvUnit = (TextView) findViewById(R.id.tv_unit);

		tvTaskType = (TextView) findViewById(R.id.tv_tasktype);
		tvDing = (TextView) findViewById(R.id.tv_ding);
		tvJI = (TextView) findViewById(R.id.tv_ji);
		tvYin = (TextView) findViewById(R.id.tv_yin);
		tvPing = (TextView) findViewById(R.id.tv_ping);
		tvHandle = (TextView) findViewById(R.id.btn_task_tougao);
		layHandle = (LinearLayout) findViewById(R.id.layout_handle);
		layCall = (LinearLayout) findViewById(R.id.lay_call);
		layColl = (LinearLayout) findViewById(R.id.lay_coll);
		imgColl = (ImageView) findViewById(R.id.img_task_collection);

		layFujian = (LinearLayout) findViewById(R.id.lay_fujian);
		layGaojian = (LinearLayout) findViewById(R.id.lay_gaojian);
		layJiaofu = (LinearLayout) findViewById(R.id.lay_jiaofu);
		layWeiQuan = (LinearLayout) findViewById(R.id.lay_weiquan);
		layPingJia = (LinearLayout) findViewById(R.id.lay_pingjia);

		header_title.setText("任务详情");
		tvTitle.setFocusable(true);
		tvTitle.setFocusableInTouchMode(true);
		tvTitle.requestFocus();

		tvDing.setVisibility(View.GONE);
		tvJI.setVisibility(View.GONE);
		tvYin.setVisibility(View.GONE);
		tvPing.setVisibility(View.GONE);

		imgBack.setOnClickListener(listener);
		tvDescMore.setOnClickListener(listener);
		tvHandle.setOnClickListener(listener);
		layGaojian.setOnClickListener(listener);
		layJiaofu.setOnClickListener(listener);
		layWeiQuan.setOnClickListener(listener);
		layPingJia.setOnClickListener(listener);
		layCall.setOnClickListener(listener);
		layColl.setOnClickListener(listener);
	}

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
			case R.id.tv_task_descmore:
				if (flag)
				{
					flag = false;
					tvDescMore.setText("收起");
					// tvDesc.setEllipsize(null); // 展开
					tvDesc.setSingleLine(flag);
				}
				else
				{
					flag = true;
					tvDescMore.setText("展开更多");
					// tvDesc.setEllipsize(TextUtils.TruncateAt.END); // 收缩
					tvDesc.setLines(5);
				}
				break;
			case R.id.lay_gaojian:
				check = "1";
				defultmianColor();
				tvLine1.setBackgroundColor(getResources().getColor(R.color.header_bg));
				manAdapter = new ManListItemAdapter(TaskDetails.this, workList);
				listView.setAdapter(manAdapter);
				params = StrFormat.getListViewParams(listView);
				listView.setLayoutParams(params);
				break;
			case R.id.lay_jiaofu:
				check = "2";
				defultmianColor();
				tvLine2.setBackgroundColor(getResources().getColor(R.color.header_bg));
				manAdapter = new ManListItemAdapter(TaskDetails.this, jiaofuList);
				listView.setAdapter(manAdapter);
				params = StrFormat.getListViewParams(listView);
				listView.setLayoutParams(params);
				break;
			case R.id.lay_weiquan:
				check = "3";
				defultmianColor();
				tvLine3.setBackgroundColor(getResources().getColor(R.color.header_bg));
				manAdapter = new ManListItemAdapter(TaskDetails.this, weiquanList);
				listView.setAdapter(manAdapter);
				params = StrFormat.getListViewParams(listView);
				listView.setLayoutParams(params);
				break;
			case R.id.lay_pingjia:
				check = "4";
				defultmianColor();
				tvLine4.setBackgroundColor(getResources().getColor(R.color.header_bg));
				evlAdapter = new TaskEvaluateAdapter(TaskDetails.this, pingjiaList);
				listView.setAdapter(evlAdapter);
				params = StrFormat.getListViewParams(listView);
				listView.setLayoutParams(params);
				break;
			case R.id.btn_task_tougao:
				handleNext();
				break;
			case R.id.lay_call:
				Intent intent = mIMKit.getChattingActivityIntent(map.get("uid"),
						MyApp.APP_KEY);
				startActivity(intent);
				break;
			case R.id.lay_coll:
				if (strFocused.equals("0"))
				{
					Glide.with(TaskDetails.this).load(R.drawable.ic_wkxq_xin2)
							.into(imgColl);
					Toast.makeText(TaskDetails.this, "已收藏", 2000).show();
					Thread t = new Thread(newTread2);
					t.start();
				}
				else
				{
					Glide.with(TaskDetails.this).load(R.drawable.tab_collect)
							.into(imgColl);
					Toast.makeText(TaskDetails.this, "已取消", 2000).show();
					Thread t = new Thread(newTread2);
					t.start();
				}
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
			map = TaskDP.getTaskDetails(strTaskId, TaskDetails.this);
			attachmentList = TaskDP.getAttachment(map.get("attachment"));
			workList = TaskDP.getWorkInfo(map.get("work_list"));
			jiaofuList = TaskDP.deliveryList(strTaskId, TaskDetails.this);
			weiquanList = TaskDP.rightList(strTaskId, TaskDetails.this);
			pingjiaList = TaskDP.commentList(strTaskId, TaskDetails.this);
			Message msg = mHandler.obtainMessage(1, map);
			msg.sendToTarget();
		}
	};

	Runnable newTread2 = new Runnable()
	{
		@Override
		public void run()
		{
			if (strFocused.equals("0"))
			{
				strFocused = "1";
				TaskDP.addTaskFocus(map.get("id"), TaskDetails.this);
			}
			else
			{
				strFocused = "0";
				TaskDP.delTaskFocus(map.get("id"), TaskDetails.this);
			}
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
					progressDialog.dismiss();
					setViewValues();
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(TaskDetails.this, str.get("message"), 2000).show();
					finish();
				}
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	protected void setViewValues()
	{
		tvTitle.setText(map.get("title"));

		if (map.get("task_type").equals("xuanshang"))
		{
			tvTaskType.setText("悬赏");
			tvUnit.setVisibility(View.VISIBLE);
		}
		else
		{
			tvTaskType.setText("招标");
			tvUnit.setVisibility(View.GONE);
		}

		try
		{
			JSONArray jArray = new JSONArray(map.get("task_service"));
			if (jArray.length() > 0)
			{
				for (int i = 0; i < jArray.length(); i++)
				{
					if (jArray.get(i).equals("ZHIDING"))
					{
						tvDing.setVisibility(View.VISIBLE);
					}
					else if (jArray.get(i).equals("JIAJI"))
					{
						tvJI.setVisibility(View.VISIBLE);
					}
					else if (jArray.get(i).equals("SOUSUOYINGQINGPINGBI"))
					{
						tvYin.setVisibility(View.VISIBLE);
					}
					else if (jArray.get(i).equals("GAOJIANPINGBI"))
					{
						tvPing.setVisibility(View.VISIBLE);
					}
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		tvCash.setText(map.get("bounty"));
		tvCashStatus.setText(map.get("bounty_status_desc"));
		tvName.setText(map.get("name"));
		tvTime.setText(map.get("begin_at").substring(0, 10));
		tvCate.setText(map.get("cate_name"));
		tvCity.setText(map.get("city_name"));
		tvLook.setText(map.get("view_count") + "人浏览");
		tvStatus.setText(map.get("status_desc"));
		tvEndTime.setText(map.get("time_desc"));
		tvDesc.setText(Html.fromHtml(map.get("desc")));
		tvWorkNum.setText("投稿记录(" + map.get("work_list_count") + ")");
		tvDescMore.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		setTaskAttachment();

		if (!map.get("delivery_list_count").equals("0"))
		{
			layJiaofu.setVisibility(View.VISIBLE);
			tvJiaoNum.setText("交付记录(" + map.get("delivery_list_count") + ")");
		}
		if (!map.get("right_list_count").equals("0"))
		{
			layWeiQuan.setVisibility(View.VISIBLE);
			tvWeiNum.setText("维权(" + map.get("right_list_count") + ")");
		}
		if (!map.get("comment_list_count").equals("0"))
		{
			layPingJia.setVisibility(View.VISIBLE);
			tvPingNum.setText("评价(" + map.get("comment_list_count") + ")");
		}

		if (workList.size() <= 0)
		{
			listView.setVisibility(View.GONE);
			layoutHide.setVisibility(View.VISIBLE);
		}
		else
		{
			listView.setVisibility(View.VISIBLE);
			layoutHide.setVisibility(View.GONE);
			Log.e("workList", "" + workList);
			manAdapter = new ManListItemAdapter(this, workList);
			listView.setAdapter(manAdapter);
			listView.setOnItemClickListener(ltemListener);

			LayoutParams params = StrFormat.getListViewParams(listView);
			listView.setLayoutParams(params);
		}

		Log.e("role", map.get("role"));
		Log.e("employee_button", map.get("employee_button"));
		Log.e("button_desc", map.get("employee_button_desc"));
		strFocused = map.get("focused");
		if (strFocused.equals("0"))
		{
			Glide.with(this).load(R.drawable.tab_collect).into(imgColl);
		}
		else
		{
			Glide.with(this).load(R.drawable.ic_wkxq_xin2).into(imgColl);
		}

		if (map.get("role").equals("employer"))
		{
			if (map.get("task_type").equals("xuanshang"))
			{
				layHandle.setVisibility(View.GONE);
			}
			else
			{
				if (map.get("employer_button").equals("2"))
				{
					tvHandle.setBackgroundColor(getResources().getColor(
							R.color.light_gray8));
					// tvHandle.setText("未登录");
				}
				else
				{
					tvHandle.setText(map.get("employer_button_desc"));
				}
			}
		}
		else
		{
			tvHandle.setText(map.get("employee_button_desc"));
			if (map.get("employee_button").equals("0"))
			{
				tvHandle.setBackgroundColor(getResources().getColor(R.color.light_gray8));
				// tvHandle.setText("未登录");
			}
			else if (map.get("employee_button").equals("1"))
			{

			}
			else if (map.get("employee_button").equals("2"))
			{
				tvHandle.setBackgroundColor(getResources().getColor(R.color.light_gray8));
			}
			else if (map.get("employee_button").equals("3"))
			{

			}
			else if (map.get("employee_button").equals("4"))
			{

			}
			else if (map.get("employee_button").equals("5"))
			{
				tvHandle.setBackgroundColor(getResources().getColor(R.color.light_gray8));
			}
			else if (map.get("employee_button").equals("6"))
			{
				// 等待确认付款方式
			}
			else if (map.get("employee_button").equals("7"))
			{

			}
			else if (map.get("employee_button").equals("8"))
			{

			}
			else if (map.get("employee_button").equals("9"))
			{

			}
			else if (map.get("employee_button").equals("10"))
			{

			}
			else if (map.get("employee_button").equals("11"))
			{
				tvHandle.setBackgroundColor(getResources().getColor(R.color.light_gray8));
			}
			else if (map.get("employee_button").equals("12"))
			{

			}
			else if (map.get("employee_button").equals("13"))
			{

			}
			else if (map.get("employee_button").equals("14"))
			{

			}
		}
	}

	ArrayList<HashMap<String, Object>> attachmentList;

	private void setTaskAttachment()
	{
		layFujian.removeAllViews();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		float density = displayMetrics.density;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int) (48 * density), (int) (48 * density));
		params.rightMargin = 36;

		for (int i = 0; i < attachmentList.size(); i++)
		{
			ImageView newImg = new ImageView(this);
			Glide.with(this).load(attachmentList.get(i).get("url"))
					.error(R.drawable.ic_fujian).transform(new RoundTransform(this, 3))
					.into(newImg);
			layFujian.addView(newImg, params);

			final int finalI = i;

			newImg.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (attachmentList.get(finalI).get("type").equals("png")
							|| attachmentList.get(finalI).get("type").equals("gif")
							|| attachmentList.get(finalI).get("type").equals("jpg")
							|| attachmentList.get(finalI).get("type").equals("jepg"))
					{
						imgDialogShow(TaskDetails.this,
								attachmentList.get(finalI).get("url").toString());
					}
					else
					{
						new AlertDialog.Builder(TaskDetails.this)
								.setMessage("是否确认下载")
								// 设置显示的内容
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener()
										{// 添加确定按钮
											@Override
											public void onClick(DialogInterface dialog,
													int which)
											{// 确定按钮的响应事件
												StrFormat.getDownFile(
														TaskDetails.this,
														attachmentList.get(finalI)
																.get("url").toString(),
														attachmentList.get(finalI)
																.get("name").toString());
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener()
										{// 添加返回按钮
											@Override
											public void onClick(DialogInterface dialog,
													int which)
											{// 响应事件

											}

										}).show();// 在按键响应事件中显示此对话框
					}
				}
			});
		}
	}

	FinalDb db;
	List<UserBean> users;
	Intent intent;

	@SuppressWarnings("deprecation")
	private void handleNext()
	{
		db = ConfigInc.getCreateDB(this);
		users = db.findAll(UserBean.class);

		if (users.size() > 0)
		{
			if (map.get("role").equals("employee"))
			{
				if (map.get("employee_button").equals("1"))
				{
					intent = new Intent(TaskDetails.this, TaskManuscriptDelivery.class);
					intent.putExtra("taskType", map.get("task_type"));
					intent.putExtra("task_id", map.get("id"));
					startActivity(intent);
				}
				else if (map.get("employee_button").equals("2"))
				{

				}
				else if (map.get("employee_button").equals("3"))
				{

				}
				else if (map.get("employee_button").equals("4"))
				{

				}
				else if (map.get("employee_button").equals("5"))
				{

				}
				else if (map.get("employee_button").equals("6"))
				{
					intent = new Intent(TaskDetails.this, TaskPriceDetails.class);
					intent.putExtra("taskid", map.get("id"));
					startActivity(intent);
				}
				else if (map.get("employee_button").equals("7"))
				{
					intent = new Intent(TaskDetails.this, TaskAgreementCreate.class);
					intent.putExtra("taskType", map.get("task_type"));
					intent.putExtra("sort", map.get("delivery_sort"));
					intent.putExtra("task_id", map.get("id"));
					startActivity(intent);
				}
				else if (map.get("employee_button").equals("8"))
				{

				}
				else if (map.get("employee_button").equals("9"))
				{

				}
				else if (map.get("employee_button").equals("10"))
				{
					intent = new Intent(TaskDetails.this, Evaluate.class);
					intent.putExtra("type", "task");
					intent.putExtra("role", map.get("role"));
					intent.putExtra("task_id", strTaskId);
					intent.putExtra("work_id", map.get("delivery_work_id"));
					intent.putExtra("name", map.get("to_comment_name"));
					intent.putExtra("pic", map.get("to_comment_avatar"));
					startActivity(intent);
				}
				else if (map.get("employee_button").equals("11"))
				{

				}
				else if (map.get("employee_button").equals("12"))
				{

				}
				else if (map.get("employee_button").equals("13"))
				{

				}
				else if (map.get("employee_button").equals("14"))
				{

				}
			}
			else
			{
				if (map.get("employer_button").equals("1"))
				{

				}
				else if (map.get("employer_button").equals("2"))
				{

				}
				else if (map.get("employer_button").equals("3"))
				{

				}
				else if (map.get("employer_button").equals("4"))
				{
					intent = new Intent(TaskDetails.this, TaskReleasePay.class);
					intent.putExtra("strResult", "task");
					intent.putExtra("taskid", map.get("id"));
					startActivity(intent);
				}
				else if (map.get("employer_button").equals("5"))
				{
					intent = new Intent(TaskDetails.this, TaskPriceDetails.class);
					intent.putExtra("taskid", map.get("id"));
					startActivity(intent);
				}
				else if (map.get("employer_button").equals("6"))
				{

				}
				else if (map.get("employer_button").equals("7"))
				{

				}
				else if (map.get("employer_button").equals("8"))
				{
					if (map.get("task_type").equals("zhaobiao"))
					{
						intent = new Intent(TaskDetails.this, Evaluate.class);
						intent.putExtra("type", "task");
						intent.putExtra("role", map.get("role"));
						intent.putExtra("task_id", strTaskId);
						intent.putExtra("work_id", map.get("delivery_work_id"));
						intent.putExtra("name", map.get("to_comment_name"));
						intent.putExtra("pic", map.get("to_comment_avatar"));
						startActivity(intent);
					}
					else
					{
						Toast.makeText(TaskDetails.this, "当前任务模式需到交付详情页评价", 2000);
					}
				}
				else if (map.get("employer_button").equals("9"))
				{

				}
				else if (map.get("employer_button").equals("10"))
				{

				}
				else if (map.get("employer_button").equals("11"))
				{

				}
			}
		}
		else
		{
			tvHandle.setBackgroundColor(getResources().getColor(R.color.light_gray8));
			Toast.makeText(TaskDetails.this, "你还未登录，请先登录", 2000).show();
		}

	}

	OnItemClickListener ltemListener = new OnItemClickListener()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			HashMap<String, Object> item = (HashMap<String, Object>) arg0
					.getItemAtPosition(arg2);
			if (TestData.getuserType(TaskDetails.this) == 0)
			{
				if (check.equals("1") || check.equals("2"))
				{
					intent = new Intent(TaskDetails.this, TaskManuscriptDetails.class);
					intent.putExtra("work_id", item.get("id").toString());
					intent.putExtra("task_id", map.get("id"));
					intent.putExtra("task_type", map.get("task_type"));
					startActivity(intent);
				}
			}
		}
	};

	@SuppressWarnings("deprecation")
	void defultmianColor()
	{
		tvLine1.setBackgroundColor(getResources().getColor(R.color.main_bg));
		tvLine2.setBackgroundColor(getResources().getColor(R.color.main_bg));
		tvLine3.setBackgroundColor(getResources().getColor(R.color.main_bg));
		tvLine4.setBackgroundColor(getResources().getColor(R.color.main_bg));
	}

	private PopupWindow popImg;
	View popView;
	ImageView imgView;

	protected void imgDialogShow(Context context, String string)
	{
		if (popImg == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			popView = layoutInflater.inflate(R.layout.image_dialog, null);
			popImg = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
		}

		popImg.setAnimationStyle(android.R.style.Animation);
		popImg.setFocusable(true);
		popImg.setOutsideTouchable(true);
		StrFormat.fitPopupWindowOverStatusBar(popImg, true);
		// 实例化一个ColorDrawable颜色白色,设置SelectPicPopupWindow弹出窗体的背景
		popImg.setBackgroundDrawable(new ColorDrawable(0xb0000000));
		popImg.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popImg.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popImg.showAtLocation(popView, 0, 0, 0);

		imgView = (ImageView) popView.findViewById(R.id.imageView1);
		Glide.with(context).load(string).error(R.drawable.ic_fujian).into(imgView);
		imgView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				popImg.dismiss();
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isManu_add())
		{
			defultmianColor();
			tvLine1.setBackgroundColor(getResources().getColor(R.color.header_bg));
			workList.clear();
			attachmentList.clear();
			jiaofuList.clear();
			weiquanList.clear();
			pingjiaList.clear();
			
			progressDialog = new LoadingDialog(this);
			progressDialog.show();
			progressDialog.setCancelable(false);

			Thread t = new Thread(newTread1);
			t.start();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
