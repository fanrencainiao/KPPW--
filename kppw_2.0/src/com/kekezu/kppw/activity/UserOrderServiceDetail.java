package com.kekezu.kppw.activity;

import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.bumptech.glide.Glide;
import com.kekezu.kppw.MyApp;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.CircleTransform;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.HireDP;
import com.kekezu.kppw.imcustom.LoginSampleHelper;

/**
 * 服务订单详情
 * 
 * @author cm
 * 
 */
public class UserOrderServiceDetail extends FragmentActivity
{
	LoadingDialog progressDialog;
	ImageView imgBack;
	TextView tvTitle;
	ImageView imgPic;
	TextView tvName;
	TextView tvCash;
	TextView tvEndTime;
	TextView tvEndDay;
	TextView tvRate, tvComplete;

	LinearLayout lyShop;
	LinearLayout lyLianxi;
	Button btnGuYong;

	String strId;
	Map<String, String> resMap;
	Intent intent;
	YWIMKit mIMKit;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_order_service_detail);
		strId = getIntent().getStringExtra("id");

		// 必需继承FragmentActivity,嵌套fragment只需要这行代码
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame1, new UserOrderServiceDetailFrame())
				.commitAllowingStateLoss();

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
		tvTitle = (TextView) findViewById(R.id.header_title);
		imgPic = (ImageView) findViewById(R.id.img_pic);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvCash = (TextView) findViewById(R.id.tv_cash);
		tvEndTime = (TextView) findViewById(R.id.tv_endtime);
		tvEndDay = (TextView) findViewById(R.id.tv_endday);
		tvRate = (TextView) findViewById(R.id.text_applauseRate);
		tvComplete = (TextView) findViewById(R.id.text_complete);

		lyShop = (LinearLayout) findViewById(R.id.ly_goshop);
		lyLianxi = (LinearLayout) findViewById(R.id.ly_lianxi);
		btnGuYong = (Button) findViewById(R.id.btn_guyong);

		tvTitle.setText("服务订单详情");

		imgBack.setOnClickListener(listener);
		lyShop.setOnClickListener(listener);
		lyLianxi.setOnClickListener(listener);
		btnGuYong.setOnClickListener(listener);

		setEnable();
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
			case R.id.ly_goshop:
				intent = new Intent(UserOrderServiceDetail.this, ShopDetail.class);
				intent.putExtra("shopId", resMap.get("shop_id"));
				startActivity(intent);
				break;
			case R.id.ly_lianxi:
				// 聊天
				Intent intent = mIMKit.getChattingActivityIntent(resMap.get("user_id"),
						MyApp.APP_KEY);
				startActivity(intent);
				break;
			case R.id.btn_guyong:
				setNextStep();
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
			resMap = HireDP.employUserDetail(strId, UserOrderServiceDetail.this);
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
				setTaskListView();
				progressDialog.dismiss();
				break;
			}
		}
	};

	// 根据订单的状态，改变底部按钮的颜色
	private void setEnable()
	{
		if (resMap.get("button_status").equals("1"))
		{
			btnGuYong.setBackgroundResource(R.color.light_gray7);
		}
		else if (resMap.get("button_status").equals("4"))
		{
			btnGuYong.setBackgroundResource(R.color.light_gray7);
		}
		else if (resMap.get("button_status").equals("5"))
		{
			btnGuYong.setBackgroundResource(R.color.light_gray7);
		}
		else if (resMap.get("button_status").equals("6"))
		{
			btnGuYong.setBackgroundResource(R.color.light_gray7);
		}
		else if (resMap.get("button_status").equals("9"))
		{
			btnGuYong.setBackgroundResource(R.color.light_gray7);
		}
		else if (resMap.get("button_status").equals("10"))
		{
			btnGuYong.setBackgroundResource(R.color.light_gray7);
		}
	}

	protected void setTaskListView()
	{
		Glide.with(this).load(resMap.get("avatar")).placeholder(R.drawable.erha)
				.error(R.drawable.erha).transform(new CircleTransform(this, 4))
				.into(imgPic);
		tvName.setText(resMap.get("username"));
		tvCash.setText(resMap.get("cash"));
		tvEndTime.setText(resMap.get("delivery_deadline"));
		tvEndDay.setText(resMap.get("days"));
		btnGuYong.setText(resMap.get("button_word"));
		// tvRate.setText(resMap.get("button_word"));
		tvComplete.setText(resMap.get("sales_num"));

	}

	// 根据订单的状态，给予不同操作的跳转
	protected void setNextStep()
	{
		if (resMap.get("button_status").equals("0"))
		{
			cancelEmploy();
		}
		else if (resMap.get("button_status").equals("2"))
		{
			acceptOrder();
		}
		else if (resMap.get("button_status").equals("3"))
		{
			Intent intent = new Intent(UserOrderServiceDetail.this, Evaluate.class);
			intent.putExtra("id", strId);
			intent.putExtra("pic", resMap.get("avatar"));
			intent.putExtra("name", resMap.get("username"));
			intent.putExtra("identity", resMap.get("type"));// 1：我是雇主 2：我是威客
			intent.putExtra("type", "service");
			startActivity(intent);
		}
		else if (resMap.get("button_status").equals("7"))
		{
			cancelOrder();
		}
		else if (resMap.get("button_status").equals("8"))
		{
			Intent intent = new Intent(UserOrderServiceDetail.this, UploadWork.class);
			intent.putExtra("id", strId);
			startActivity(intent);
		}
	}

	// 是否取消订单
	private void cancelEmploy()
	{
		new AlertDialog.Builder(UserOrderServiceDetail.this).setTitle("提示")
				.setMessage("威客尚未接单，是否取消委托")
				// 设置显示的内容
				.setPositiveButton("是", new DialogInterface.OnClickListener()
				{// 添加确定按钮
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (HireDP.dealEmploy(strId, "1",
										UserOrderServiceDetail.this).equals("1000"))
								{
									TestEvent event = new TestEvent();
									event.setOrderDetail(true);
									EventBus.getDefault().post(event);
								}
								else
								{
									Toast.makeText(
											UserOrderServiceDetail.this,
											HireDP.dealEmploy(strId, "1",
													UserOrderServiceDetail.this), 2000)
											.show();
								}
							}
						}).setNegativeButton("否", new DialogInterface.OnClickListener()
				{// 添加返回按钮

							@Override
							public void onClick(DialogInterface dialog, int which)
							{

							}

						}).show();// 在按键响应事件中显示此对话框
	}

	// 验收作品
	private void acceptOrder()
	{
		new AlertDialog.Builder(UserOrderServiceDetail.this).setTitle("提示")
				.setMessage("请确认相关作品")
				// 设置显示的内容
				.setPositiveButton("验收", new DialogInterface.OnClickListener()
				{// 添加确定按钮
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (HireDP.acceptEmployWork(strId,
										UserOrderServiceDetail.this).equals("1000"))
								{
									TestEvent event = new TestEvent();
									event.setOrderDetail(true);
									EventBus.getDefault().post(event);
								}
								else
								{
									Toast.makeText(
											UserOrderServiceDetail.this,
											HireDP.acceptEmployWork(strId,
													UserOrderServiceDetail.this), 2000)
											.show();
								}
							}
						}).setNegativeButton("维权", new DialogInterface.OnClickListener()
				{// 添加返回按钮

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								Intent intent = new Intent(UserOrderServiceDetail.this,
										TaskRights.class);
								intent.putExtra("id", strId);
								intent.putExtra("aaa", "2");
								startActivity(intent);
							}
						}).show();// 在按键响应事件中显示此对话框

	}

	// 是否接受订单
	private void cancelOrder()
	{
		new AlertDialog.Builder(UserOrderServiceDetail.this).setTitle("提示")
				.setMessage("是否愿意接受该雇主的雇佣")
				// 设置显示的内容
				.setPositiveButton("接受", new DialogInterface.OnClickListener()
				{// 添加确定按钮
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (HireDP.dealEmploy(strId, "2",
										UserOrderServiceDetail.this).equals("1000"))
								{
									TestEvent event = new TestEvent();
									event.setOrderDetail(true);
									EventBus.getDefault().post(event);
								}
								else
								{
									Toast.makeText(
											UserOrderServiceDetail.this,
											HireDP.dealEmploy(strId, "2",
													UserOrderServiceDetail.this), 2000)
											.show();
								}
							}
						}).setNegativeButton("拒绝", new DialogInterface.OnClickListener()
				{// 添加返回按钮

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (HireDP.dealEmploy(strId, "3",
										UserOrderServiceDetail.this).equals("1000"))
								{
									TestEvent event = new TestEvent();
									event.setOrderDetail(true);
									EventBus.getDefault().post(event);
								}
								else
								{
									Toast.makeText(
											UserOrderServiceDetail.this,
											HireDP.dealEmploy(strId, "3",
													UserOrderServiceDetail.this), 2000)
											.show();
								}
							}

						}).show();// 在按键响应事件中显示此对话框

	}

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isOrderDetail())
		{
			resMap = HireDP.employUserDetail(strId, this);
			initView();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}