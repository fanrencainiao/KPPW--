package com.kekezu.kppw.activity;

import java.util.Map;

import com.kekezu.kppw.R;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.dataprocess.UserDP;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserOrder extends Activity
{
	LoadingDialog progressDialog;
	ImageView imgBack;
	TextView tvTitle;

	TextView textView1;
	TextView textView2;
	TextView textView3;
	TextView textView4;

	TextView tv1, tv2, tv3;
	RelativeLayout layPay, layOk, layOther;

	RelativeLayout relay4, relay5, relay6, relay7;
	TextView tv4, tv5, tv6, tv7;

	LinearLayout layGood;
	LinearLayout laySer;
	Intent intent;
	String strType = "1";
	Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_order);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		initView();
		initData();
	}

	private void initData()
	{
		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void initView()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.header_title);

		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView4 = (TextView) findViewById(R.id.textView4);

		tv1 = (TextView) findViewById(R.id.TextView03);
		tv2 = (TextView) findViewById(R.id.TextView01);
		tv3 = (TextView) findViewById(R.id.imageView8);
		tv4 = (TextView) findViewById(R.id.TextView09);
		layPay = (RelativeLayout) findViewById(R.id.relay_pay);
		layOk = (RelativeLayout) findViewById(R.id.relay_ok);
		layOther = (RelativeLayout) findViewById(R.id.relay_other);

		tv4 = (TextView) findViewById(R.id.TextView09);
		tv5 = (TextView) findViewById(R.id.TextView07);
		tv6 = (TextView) findViewById(R.id.TextView05);
		tv7 = (TextView) findViewById(R.id.TextView15);
		relay4 = (RelativeLayout) findViewById(R.id.relay_4);
		relay5 = (RelativeLayout) findViewById(R.id.relay_5);
		relay6 = (RelativeLayout) findViewById(R.id.relay_6);
		relay7 = (RelativeLayout) findViewById(R.id.relay_7);

		layGood = (LinearLayout) findViewById(R.id.lay_good);
		laySer = (LinearLayout) findViewById(R.id.lay_ser);

		tvTitle.setText("我的订单");

		if (TestData.getuserType(this) == 0)
		{
			textView1.setText("购买的作品");
			textView2.setText("购买的服务");
		}
		else
		{
			textView1.setText("售出的作品");
			textView2.setText("售出的服务");
		}

		imgBack.setOnClickListener(listener);
		textView1.setOnClickListener(listener);
		textView2.setOnClickListener(listener);
		layPay.setOnClickListener(listener);
		layOk.setOnClickListener(listener);
		layOther.setOnClickListener(listener);

		relay4.setOnClickListener(listener);
		relay5.setOnClickListener(listener);
		relay6.setOnClickListener(listener);
		relay7.setOnClickListener(listener);
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
				onBackPressed();
				break;
			case R.id.textView1:
				setUiChange();
				strType = "1";
				textView1.setTextColor(getResources().getColor(R.color.header_bg));
				textView4.setBackgroundColor(getResources().getColor(R.color.header_bg));
				layGood.setVisibility(View.VISIBLE);
				laySer.setVisibility(View.GONE);
				initData();
				break;
			case R.id.textView2:
				strType = "2";
				setUiChange();
				textView2.setTextColor(getResources().getColor(R.color.header_bg));
				textView3.setBackgroundColor(getResources().getColor(R.color.header_bg));
				layGood.setVisibility(View.GONE);
				laySer.setVisibility(View.VISIBLE);
				initData();
				break;
			case R.id.relay_pay:
				intent = new Intent(UserOrder.this, UserOrderList.class);
				intent.putExtra("stutas", "2");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_ok:
				intent = new Intent(UserOrder.this, UserOrderList.class);
				intent.putExtra("stutas", "3");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_other:
				intent = new Intent(UserOrder.this, UserOrderList.class);
				intent.putExtra("stutas", "4");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_4:
				intent = new Intent(UserOrder.this, UserOrderList.class);
				intent.putExtra("stutas", "0");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_5:
				intent = new Intent(UserOrder.this, UserOrderList.class);
				intent.putExtra("stutas", "1,2");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_6:
				intent = new Intent(UserOrder.this, UserOrderList.class);
				intent.putExtra("stutas", "3,4");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_7:
				intent = new Intent(UserOrder.this, UserOrderList.class);
				intent.putExtra("stutas", "5,6,9,7,8");
				intent.putExtra("type", strType);
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
			if (TestData.getuserType(UserOrder.this) == 0)
			{
				map = UserDP.myBuyGoodsCount(strType, UserOrder.this);
			}
			else
			{
				map = UserDP.mySaleGoodsCount(strType, UserOrder.this);
			}

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

	@SuppressWarnings("deprecation")
	protected void setTaskListView()
	{
		if (strType.equals("1"))
		{
			if (map.get("is_pay").equals("0"))
			{
				tv1.setText("");
				tv1.setBackgroundColor(getResources().getColor(R.color.white_1));
			}
			else
			{
				tv1.setText(map.get("is_pay"));
				tv1.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3_4));
			}

			if (map.get("finish").equals("0"))
			{
				tv2.setText("");
				tv2.setBackgroundColor(getResources().getColor(R.color.white_1));
			}
			else
			{
				tv2.setText(map.get("finish"));
				tv2.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3_4));
			}

			if (map.get("other").equals("0"))
			{
				tv3.setText("");
				tv3.setBackgroundColor(getResources().getColor(R.color.white_1));
			}
			else
			{
				tv3.setText(map.get("other"));
				tv3.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3_4));
			}
		}
		else
		{
			if (map.get("is_wait").equals("0"))
			{
				tv4.setText("");
				tv4.setBackgroundColor(getResources().getColor(R.color.white_1));
			}
			else
			{
				tv4.setText(map.get("is_wait"));
				tv4.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3_4));
			}

			if (map.get("working").equals("0"))
			{
				tv5.setText("");
				tv5.setBackgroundColor(getResources().getColor(R.color.white_1));
			}
			else
			{
				tv5.setText(map.get("working"));
				tv5.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3_4));
			}

			if (map.get("finish").equals("0"))
			{
				tv6.setText("");
				tv6.setBackgroundColor(getResources().getColor(R.color.white_1));
			}
			else
			{
				tv6.setText(map.get("finish"));
				tv6.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3_4));
			}

			if (map.get("other").equals("0"))
			{
				tv7.setText("");
				tv7.setBackgroundColor(getResources().getColor(R.color.white_1));
			}
			else
			{
				tv7.setText(map.get("other"));
				tv7.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.r_bg_textview_yuan3_4));
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void setUiChange()
	{
		textView1.setTextColor(getResources().getColor(R.color.black));
		textView2.setTextColor(getResources().getColor(R.color.black));
		textView3.setBackgroundColor(getResources().getColor(R.color.light_gray2));
		textView4.setBackgroundColor(getResources().getColor(R.color.light_gray2));
	}
}
