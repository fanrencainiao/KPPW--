package com.kekezu.kppw.activity;

import com.kekezu.kppw.R;
import com.kekezu.kppw.dataprocess.OtherDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 意见反馈
 * 
 * @author cm
 * 
 */
public class UserOpinion extends Activity
{
	ImageView imgBack;
	TextView tvTitle;
	TextView textConserve;
	EditText editDesc;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_opinion);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.header_title);
		textConserve = (TextView) findViewById(R.id.text_conserve);
		editDesc = (EditText) findViewById(R.id.edit_desc);

		tvTitle.setText("我要反馈");
		imgBack.setOnClickListener(listener);
		textConserve.setOnClickListener(listener);

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
			case R.id.text_conserve:
				conserveOpinion();
				break;

			default:
				break;
			}

		}
	};

	protected void conserveOpinion()
	{
		if (StrFormat.formatStr(editDesc.getText().toString()))
		{
			OtherDP.getUserOpinion(editDesc.getText().toString(), this);
		}
		else
		{
			Toast.makeText(UserOpinion.this, "请输入反馈信息！", Toast.LENGTH_SHORT).show();
		}
	}
}
