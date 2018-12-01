package com.kekezu.kppw.activity;

import com.kekezu.kppw.R;
import com.kekezu.kppw.dataprocess.ManuscriptDP;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class XieYi extends Activity
{
	ImageView imgBack;
	TextView textTitle;

	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xieyi);

		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);

		textView = (TextView) findViewById(R.id.textView1);

		textTitle.setText("协议");

		textView.setText(Html.fromHtml(ManuscriptDP.agreementDetail(getIntent()
				.getStringExtra("code"), this)));

		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();

			}
		});
	}
}
