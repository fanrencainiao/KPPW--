package com.kekezu.kppw.activity;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.dataprocess.RegisterDP;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RetrievePassword2 extends Activity
{
	ImageView imgBack;
	EditText editPassWord1;
	EditText editPassWord2;
	Button btnOk;

	Intent intent;
	String strToken;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.retrieve_password_2);

		intent = getIntent();
		strToken = intent.getStringExtra("token");

		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		editPassWord1 = (EditText) findViewById(R.id.edit_password1);
		editPassWord2 = (EditText) findViewById(R.id.edit_password2);
		btnOk = (Button) findViewById(R.id.btn_ok);

		btnOk.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				retPassWord();
			}
		});

		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

	}

	protected void retPassWord()
	{
		if (StrFormat.formatStr(editPassWord1.getText().toString())
				&& StrFormat.formatStr(editPassWord2.getText().toString()))
		{
			if (editPassWord1.getText().toString()
					.equals(editPassWord2.getText().toString()))
			{
				String str[] = RegisterDP.userRetNewPwd(editPassWord1.getText()
						.toString(), editPassWord2.getText().toString(), strToken,
						RetrievePassword2.this);

				// 对返回的数据进行判断
				if (str[0].equals("1000"))
				{
					Toast.makeText(this, "重置成功，请用新的密码登录", Toast.LENGTH_SHORT).show();

					TestEvent event = new TestEvent();
					event.setRetrieve(true);
					EventBus.getDefault().post(event);
					finish();
				}
				else
				{
					Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(RetrievePassword2.this, "确认密码和新密码不一致", Toast.LENGTH_SHORT);
			}
		}
		else
		{
			if (StrFormat.formatStr(editPassWord1.getText().toString()) == false)
			{
				Toast.makeText(RetrievePassword2.this, "请输入新密码", Toast.LENGTH_SHORT);
			}
			else if (StrFormat.formatStr(editPassWord2.getText().toString()) == false)
			{
				Toast.makeText(RetrievePassword2.this, "请输入确认密码", Toast.LENGTH_SHORT);
			}
		}
	}
}
