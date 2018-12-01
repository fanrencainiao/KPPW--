package com.kekezu.kppw.activity;

import java.util.Map;

import com.kekezu.kppw.R;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.UserDP;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 关于我们
 */
public class UserSetAbout extends Activity
{
	TextView text_title;
	ImageView imgBack;
	TextView textView1;
	TextView textView2;

	LoadingDialog progressDialog;
	Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_set_about);
		
		StatusBarUtil.setStatusBarLightMode(getWindow());
		ViewInit();

		progressDialog = new LoadingDialog(UserSetAbout.this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void ViewInit()
	{
		text_title = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);
		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);

		text_title.setText("关于");

		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish(); // 返回键finish页面
			}
		});
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			map = UserDP.getAboutUs(UserSetAbout.this);
			Message msg = mHandler.obtainMessage(1, map);
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
					progressDialog.dismiss();
					textView1.setText(str.get("title"));
					textView2.setText(Html.fromHtml(str.get("content")));
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(UserSetAbout.this, str.get("message"), 2000).show();
				}
				break;
			}
		}
	};

}
