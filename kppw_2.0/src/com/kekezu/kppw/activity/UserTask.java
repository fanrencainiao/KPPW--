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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserTask extends Activity
{
	LoadingDialog progressDialog;
	ImageView imgBack;
	TextView tvDraft;
	TextView textView1;
	TextView textView2;
	TextView textView3;
	TextView textView4;

	RelativeLayout relay1, relay2, relay3, relay4, relay5, relay6, relay7, relay8;
	TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;
	Intent intent;

	String strType = "1";
	Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_task);
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
		tvDraft = (TextView) findViewById(R.id.tv_draft);
		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView4 = (TextView) findViewById(R.id.textView4);

		relay1 = (RelativeLayout) findViewById(R.id.relay_1);
		relay2 = (RelativeLayout) findViewById(R.id.relay_2);
		relay3 = (RelativeLayout) findViewById(R.id.relay_3);
		relay4 = (RelativeLayout) findViewById(R.id.relay_4);
		relay5 = (RelativeLayout) findViewById(R.id.relay_5);
		relay6 = (RelativeLayout) findViewById(R.id.relay_6);
		relay7 = (RelativeLayout) findViewById(R.id.relay_7);
		relay8 = (RelativeLayout) findViewById(R.id.relay_8);

		tv1 = (TextView) findViewById(R.id.TextView03);
		tv2 = (TextView) findViewById(R.id.TextView01);
		tv3 = (TextView) findViewById(R.id.imageView8);
		tv4 = (TextView) findViewById(R.id.TextView09);
		tv5 = (TextView) findViewById(R.id.TextView07);
		tv6 = (TextView) findViewById(R.id.TextView05);
		tv7 = (TextView) findViewById(R.id.TextView15);
		tv8 = (TextView) findViewById(R.id.TextView13);

		imgBack.setOnClickListener(listener);
		tvDraft.setOnClickListener(listener);
		textView1.setOnClickListener(listener);
		textView2.setOnClickListener(listener);

		relay1.setOnClickListener(listener);
		relay2.setOnClickListener(listener);
		relay3.setOnClickListener(listener);
		relay4.setOnClickListener(listener);
		relay5.setOnClickListener(listener);
		relay6.setOnClickListener(listener);
		relay7.setOnClickListener(listener);
		relay8.setOnClickListener(listener);

		if (TestData.getuserType(UserTask.this) == 0)
		{
			tvDraft.setVisibility(View.VISIBLE);
		}
		else
		{
			tvDraft.setVisibility(View.GONE);
		}
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
			case R.id.tv_draft:
				intent = new Intent(UserTask.this, UserTaskDraft.class);
				startActivity(intent);
				break;
			case R.id.textView1:
				strType = "1";
				setUiChange();
				textView1.setTextColor(getResources().getColor(R.color.header_bg));
				textView4.setBackgroundColor(getResources().getColor(R.color.header_bg));
				initData();
				break;
			case R.id.textView2:
				strType = "2";
				setUiChange();
				textView2.setTextColor(getResources().getColor(R.color.header_bg));
				textView3.setBackgroundColor(getResources().getColor(R.color.header_bg));
				initData();
				break;
			case R.id.relay_1:
				intent = new Intent(UserTask.this, UserTaskList.class);
				intent.putExtra("stutas", "1");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_2:
				intent = new Intent(UserTask.this, UserTaskList.class);
				intent.putExtra("stutas", "2");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_3:
				intent = new Intent(UserTask.this, UserTaskList.class);
				intent.putExtra("stutas", "3");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_4:
				intent = new Intent(UserTask.this, UserTaskList.class);
				intent.putExtra("stutas", "4");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_5:
				intent = new Intent(UserTask.this, UserTaskList.class);
				intent.putExtra("stutas", "5");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_6:
				intent = new Intent(UserTask.this, UserTaskList.class);
				intent.putExtra("stutas", "6");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_7:
				intent = new Intent(UserTask.this, UserTaskList.class);
				intent.putExtra("stutas", "7");
				intent.putExtra("type", strType);
				startActivity(intent);
				break;
			case R.id.relay_8:
				intent = new Intent(UserTask.this, UserTaskList.class);
				intent.putExtra("stutas", "8");
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
			if (TestData.getuserType(UserTask.this) == 0)
			{
				map = UserDP.indexCount(strType, UserTask.this);
			}
			else
			{
				map = UserDP.myAcceptCount(strType, UserTask.this);
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
	private void setUiChange()
	{
		textView1.setTextColor(getResources().getColor(R.color.black));
		textView2.setTextColor(getResources().getColor(R.color.black));
		textView3.setBackgroundColor(getResources().getColor(R.color.light_gray2));
		textView4.setBackgroundColor(getResources().getColor(R.color.light_gray2));
	}

	@SuppressWarnings("deprecation")
	protected void setTaskListView()
	{
		if (map.get("verify").equals("0"))
		{
			tv1.setText("");
			tv1.setBackgroundColor(getResources().getColor(R.color.white_1));
		}
		else
		{
			tv1.setText(map.get("verify"));
			tv1.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.r_bg_textview_yuan3_4));
		}

		if (map.get("bid").equals("0"))
		{
			tv2.setText("");
			tv2.setBackgroundColor(getResources().getColor(R.color.white_1));
		}
		else
		{
			tv2.setText(map.get("bid"));
			tv2.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.r_bg_textview_yuan3_4));
		}

		if (map.get("choose").equals("0"))
		{
			tv3.setText("");
			tv3.setBackgroundColor(getResources().getColor(R.color.white_1));
		}
		else
		{
			tv3.setText(map.get("choose"));
			tv3.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.r_bg_textview_yuan3_4));
		}

		if (map.get("work_in").equals("0"))
		{
			tv4.setText("");
			tv4.setBackgroundColor(getResources().getColor(R.color.white_1));
		}
		else
		{
			tv4.setText(map.get("work_in"));
			tv4.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.r_bg_textview_yuan3_4));
		}

		if (map.get("comment").equals("0"))
		{
			tv5.setText("");
			tv5.setBackgroundColor(getResources().getColor(R.color.white_1));
		}
		else
		{
			tv5.setText(map.get("comment"));
			tv5.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.r_bg_textview_yuan3_4));
		}

		tv6.setText("");
		tv6.setBackgroundColor(getResources().getColor(R.color.white_1));

		if (map.get("right").equals("0"))
		{
			tv7.setText("");
			tv7.setBackgroundColor(getResources().getColor(R.color.white_1));
		}
		else
		{
			tv7.setText(map.get("right"));
			tv7.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.r_bg_textview_yuan3_4));
		}

		tv8.setText("");
		tv8.setBackgroundColor(getResources().getColor(R.color.white_1));
	}
}
