package com.kekezu.kppw.activity;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.dataprocess.RegisterDP;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Register2 extends Activity
{
	ImageView imgBack;

	EditText editUserName;
	EditText editPassWord;
	Button btnRegister;

	String strPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// sdk2.3以后再主线程里面是是建议访问网络的，所以要更改activity的启动模式。
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_2);

		strPhone = getIntent().getStringExtra("phone");

		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		editUserName = (EditText) findViewById(R.id.edit_username);
		editPassWord = (EditText) findViewById(R.id.edit_password);
		btnRegister = (Button) findViewById(R.id.btn_register);

		imgBack.setOnClickListener(listener);
		btnRegister.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.img_back:
				onBackPressed();
				break;
			case R.id.btn_register:
				regSubmit();
				break;
			default:
				break;
			}

		}
	};

	protected void regSubmit()
	{
		if (StrFormat.formatStr(editPassWord.getText().toString())
				&& StrFormat.formatStr(editUserName.getText().toString()))
		{

			String str[] = RegisterDP.userReg(strPhone,
					editPassWord.getText().toString(), editUserName.getText().toString(),
					Register2.this);

			if (str[0].equals("1000"))
			{
				Toast.makeText(Register2.this, "注册成功", Toast.LENGTH_SHORT).show();
				finish();
				TestEvent event = new TestEvent();
				event.setRegone(true);
				EventBus.getDefault().post(event);
			}
			else
			{
				Toast.makeText(Register2.this, str[1], Toast.LENGTH_SHORT).show();
			}
		}
		else
		{

			if (!StrFormat.formatStr(editUserName.getText().toString()))
			{
				Toast.makeText(Register2.this, "请设置用户名", Toast.LENGTH_SHORT).show();
			}
			else if (!StrFormat.formatStr(editPassWord.getText().toString()))
			{
				Toast.makeText(Register2.this, "请设置登录密码", Toast.LENGTH_SHORT).show();
			}

		}
	}
}
