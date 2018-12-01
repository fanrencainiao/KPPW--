package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.UserTagListAdapter;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.IndustryDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 个人标签设置
 * 
 * @author cm
 * 
 */
public class UserInfoTag extends Activity
{
	LoadingDialog progressDialog;
	ListView listView;
	ArrayList<HashMap<String, String>> biglist;
	UserTagListAdapter adapter;
	// ArrayList<HashMap<String, Object>> smallList;
	ViewGroup.LayoutParams params;
	String bigId;
	ImageView imgBack;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_tag222);

		StatusBarUtil.setStatusBarLightMode(getWindow());

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();

		ViewInit();
		EventBus.getDefault().register(this);
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		listView = (ListView) findViewById(R.id.listView1);

		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			biglist = IndustryDP.getAllSkill("", UserInfoTag.this);
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
				adapter = new UserTagListAdapter(UserInfoTag.this, biglist);
				listView.setAdapter(adapter);
				// 动态算出ListView的LayoutParams，并设置到ListView中
				params = StrFormat.getListViewParams(listView);
				listView.setLayoutParams(params);
				progressDialog.dismiss();
				break;
			}
		}
	};

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isTag())
		{
			progressDialog = new LoadingDialog(this);
			progressDialog.show();
			progressDialog.setCancelable(false);

			Thread t = new Thread(newTread1);
			t.start();

			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	static String strTag;

	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent();
		intent.putExtra("tag", strTag);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	}

	public static void getTagData(String string)
	{
		strTag = string;
	}
}
