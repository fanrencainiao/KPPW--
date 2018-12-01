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

public class RetrievePassword1 extends Activity
{
	ImageView imgBack;
	EditText editPhone;
	EditText editPin;
	TextView btnPin;
	Button btnNext;

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
		setContentView(R.layout.retrieve_password_1);
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
		btnNext = (Button) findViewById(R.id.btn_next);

		time = new TimeCount(60000, 1000);

		imgBack.setOnClickListener(listener);
		btnPin.setOnClickListener(listener);
		btnNext.setOnClickListener(listener);
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
			case R.id.btn_pin:
				getRegPin();
				break;
			case R.id.btn_next:
				retNext();
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
				RegisterDP.userRegPin(editPhone.getText().toString(),"2",
						RetrievePassword1.this);
			}
			else
			{
				Toast.makeText(RetrievePassword1.this, "请输入正确的手机号码", Toast.LENGTH_SHORT)
						.show();
			}
		}
		else
		{
			Toast.makeText(RetrievePassword1.this, "请输入正确的手机号", Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void retNext()
	{
		if (StrFormat.formatStr(editPhone.getText().toString())
				&& StrFormat.formatStr(editPin.getText().toString()))
		{
			String str[] = RegisterDP.userRetPwd(editPhone.getText().toString(), editPin
					.getText().toString(), RetrievePassword1.this);

			// 对返回的数据进行判断
			if (str[0].equals("1000"))
			{
				Intent intent = new Intent(RetrievePassword1.this,
						RetrievePassword2.class);
				intent.putExtra("token", str[2]);
				startActivity(intent);

			}
			else
			{
				Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			if (StrFormat.formatStr(editPhone.getText().toString()) == false)
			{
				Toast.makeText(RetrievePassword1.this, "请输入正确的手机号", Toast.LENGTH_SHORT)
						.show();
			}
			else if (StrFormat.formatStr(editPin.getText().toString()) == false)
			{
				Toast.makeText(RetrievePassword1.this, "非法验证码", Toast.LENGTH_SHORT)
						.show();
			}
		}
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

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isRetrieve())
		{
			finish();
		}

	}

	@Override
	public void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
