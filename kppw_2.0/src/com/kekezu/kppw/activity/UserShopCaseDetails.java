package com.kekezu.kppw.activity;

import java.util.Map;

import com.kekezu.kppw.R;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.CaseDP;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 案例详情
 * 
 * @author cm
 * 
 */
public class UserShopCaseDetails extends Activity
{
	TextView text_title;
	ImageView imgBack;
	TextView textTitle;
	TextView textDesc;

	Map<String, String> caseMap;
	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_shop_case_details);
		ViewInit();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void ViewInit()
	{
		text_title = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.text_case_title);
		textDesc = (TextView) findViewById(R.id.text_case_desc);

		text_title.setText("案例详情");
		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			caseMap = CaseDP.getCaseInfo(getIntent().getStringExtra("id"),
					UserShopCaseDetails.this);
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
				textTitle.setText(caseMap.get("title"));
				textDesc.setText(Html.fromHtml(caseMap.get("desc")));
				progressDialog.dismiss();
				break;
			}
		}
	};
}
