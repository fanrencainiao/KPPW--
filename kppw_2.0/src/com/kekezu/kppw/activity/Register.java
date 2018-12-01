package com.kekezu.kppw.activity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.dataprocess.RegisterDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity
{
	ImageView imgBack;
	TextView tvLogin;

	EditText editPhone;
	EditText editPin;
	TextView btnPin;
	Button btnRegister;
	TextView textXieYi;

	TimeCount time;

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
		setContentView(R.layout.register_1);

		StatusBarUtil.setStatusBarLightMode(getWindow());
		EventBus.getDefault().register(this);
		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		editPhone = (EditText) findViewById(R.id.edit_phone);
		editPin = (EditText) findViewById(R.id.edit_pin);
		btnPin = (TextView) findViewById(R.id.btn_pin);
		btnRegister = (Button) findViewById(R.id.btn_register);
		textXieYi = (TextView) findViewById(R.id.text_reg_xieyi);
		tvLogin = (TextView) findViewById(R.id.text_login);

		time = new TimeCount(60000, 1000);

		imgBack.setOnClickListener(listener);
		btnPin.setOnClickListener(listener);
		btnRegister.setOnClickListener(listener);
		textXieYi.setOnClickListener(listener);
		tvLogin.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.text_login:
				finish();
				break;
			case R.id.img_back:
				onBackPressed();
				break;
			case R.id.btn_pin:
				getRegPin();
				break;
			case R.id.btn_register:
				regSubmit();
				break;
			case R.id.text_reg_xieyi:
				Intent intent = new Intent(Register.this, XieYi.class);
				intent.putExtra("code", "1");
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	};

	private void getRegPin()
	{
		if (StrFormat.formatStr(editPhone.getText().toString()))
		{
			if (editPhone.getText().length() == 11)
			{
				time.start();
				RegisterDP.userRegPin(editPhone.getText().toString(), "1", Register.this);
			}
			else
			{
				Toast.makeText(Register.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(Register.this, "请输入手机号", Toast.LENGTH_SHORT).show();
		}
	}

	protected void regSubmit()
	{
		if (StrFormat.formatStr(editPhone.getText().toString())
				&& StrFormat.formatStr(editPin.getText().toString()))
		{
			if (editPhone.getText().length() < 11)
			{
				Toast.makeText(Register.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
			}
			else
			{
				String str[] = RegisterDP.phoneCodeVertiy(editPhone.getText().toString(),
						editPin.getText().toString(), Register.this);

				if (str[0].equals("1000"))
				{
					Intent intent = new Intent(Register.this, Register2.class);
					intent.putExtra("phone", editPhone.getText().toString());
					startActivity(intent);
				}
				else
				{
					Toast.makeText(Register.this, str[1], Toast.LENGTH_SHORT).show();
				}
			}
		}
		else
		{
			if (!StrFormat.formatStr(editPhone.getText().toString()))
			{
				Toast.makeText(Register.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
			}

			else if (!StrFormat.formatStr(editPin.getText().toString()))
			{
				Toast.makeText(Register.this, "请输入验证码", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isRegone())
		{
			finish();
		}

	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	class TimeCount extends CountDownTimer
	{
		public TimeCount(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onFinish()
		{// 计时完毕
			btnPin.setClickable(true);
			btnPin.setTextColor(getResources().getColor(R.color.header_bg));
			btnPin.setText("获取验证码");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onTick(long millisUntilFinished)
		{// 计时过程
			btnPin.setClickable(false);// 防止重复点击
			btnPin.setTextColor(getResources().getColor(R.color.light_gray8));
			btnPin.setText(millisUntilFinished / 1000 + "s后重新获取");
		}
	}
}
