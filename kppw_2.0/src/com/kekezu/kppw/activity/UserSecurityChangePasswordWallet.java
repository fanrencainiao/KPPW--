package com.kekezu.kppw.activity;

import com.kekezu.kppw.R;
import com.kekezu.kppw.dataprocess.RegisterDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 修改钱包密码
 * 
 * @author cm
 * 
 */
public class UserSecurityChangePasswordWallet extends Activity
{
	TextView textTitle;
	ImageView imgBack;
	EditText editOldPwd;
	EditText editPassWord;
	EditText editRePassWord;
	Button btnSubmit;

	EditText editPhone;
	EditText editPin;
	TextView btnPin;
	EditText editNewPwd;
	EditText editReNewPwd;

	TextView textView1;
	TextView textView2;
	TextView textView3;
	TextView textView4;

	LinearLayout layoutChangePwd;
	LinearLayout layoutRetrievePwd;
	
	TimeCount time;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_security_change_password_wallet);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		ViewInit();
	}

	private void ViewInit()
	{
		textTitle = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);
		editOldPwd = (EditText) findViewById(R.id.edit_oldpwd);
		editPassWord = (EditText) findViewById(R.id.edit_password);
		editRePassWord = (EditText) findViewById(R.id.edit_repassword);
		btnSubmit = (Button) findViewById(R.id.btn_submit);

		editPhone = (EditText) findViewById(R.id.edit_phone);
		editPin = (EditText) findViewById(R.id.edit_pin);
		btnPin = (TextView) findViewById(R.id.btn_pin);
		editNewPwd = (EditText) findViewById(R.id.edit_newpwd);
		editReNewPwd = (EditText) findViewById(R.id.edit_repwd);

		textTitle.setText("钱包密码");
		time = new TimeCount(60000, 1000);

		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		textView4 = (TextView) findViewById(R.id.textView4);

		layoutChangePwd = (LinearLayout) findViewById(R.id.change_pwd);
		layoutRetrievePwd = (LinearLayout) findViewById(R.id.retrieve_pwd);

		textView1.setOnClickListener(listener);
		textView2.setOnClickListener(listener);
		imgBack.setOnClickListener(listener);
		btnPin.setOnClickListener(listener);
		btnSubmit.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener()
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.textView1:
				setUiChange();
				textView1.setTextColor(getResources().getColor(R.color.header_bg));
				textView4.setBackgroundColor(getResources().getColor(R.color.header_bg));
				layoutChangePwd.setVisibility(View.VISIBLE);
				break;
			case R.id.textView2:
				setUiChange();
				textView2.setTextColor(getResources().getColor(R.color.header_bg));
				textView3.setBackgroundColor(getResources().getColor(R.color.header_bg));
				layoutRetrievePwd.setVisibility(View.VISIBLE);
				break;
			case R.id.img_back:
				onBackPressed();
				break;
			case R.id.btn_pin:
				getRePwdPin();
				break;
			case R.id.btn_submit:
				if (layoutChangePwd.getVisibility() != View.GONE)
				{
					changePwd();
				}
				else
				{
					retrievePwd();
				}
				break;
			default:
				break;
			}
		}
	};

	private void getRePwdPin()
	{
		if (StrFormat.formatStr(editPhone.getText().toString()))
		{
			time.start();
			RegisterDP.userRegPin(editPhone.getText().toString(), "2",
					UserSecurityChangePasswordWallet.this);
		}
		else
		{
			Toast.makeText(UserSecurityChangePasswordWallet.this, "请输入正确的手机号",
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void changePwd()
	{
		if (StrFormat.formatStr(editOldPwd.getText().toString())
				&& StrFormat.formatStr(editPassWord.getText().toString())
				&& StrFormat.formatStr(editRePassWord.getText().toString()))
		{
			if (editPassWord.getText().toString()
					.equals(editRePassWord.getText().toString()))
			{
				String str[] = RegisterDP.updatePayCode(editOldPwd.getText().toString(),
						editPassWord.getText().toString(), editRePassWord.getText()
								.toString(), UserSecurityChangePasswordWallet.this);

				// 对返回的数据进行判断
				if (str[0].equals("1000"))
				{
					finish();
					Toast.makeText(this, "修改成功，请牢记您的支付密码", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(UserSecurityChangePasswordWallet.this, "确认密码和新密码不一致",
						Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			if (StrFormat.formatStr(editOldPwd.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePasswordWallet.this, "请输入原始密码",
						Toast.LENGTH_SHORT).show();
			}
			else if (StrFormat.formatStr(editPassWord.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePasswordWallet.this, "请输入新密码",
						Toast.LENGTH_SHORT).show();
			}
			else if (StrFormat.formatStr(editRePassWord.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePasswordWallet.this, "请输入确认密码",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	protected void retrievePwd()
	{
		if (StrFormat.formatStr(editPhone.getText().toString())
				&& StrFormat.formatStr(editPin.getText().toString())
				&& StrFormat.formatStr(editNewPwd.getText().toString())
				&& StrFormat.formatStr(editReNewPwd.getText().toString()))
		{
			if (editNewPwd.getText().toString().equals(editReNewPwd.getText().toString()))
			{
				String str[] = RegisterDP.payPwdReset(editPhone.getText().toString(),
						editPin.getText().toString(), editNewPwd.getText().toString(),
						editReNewPwd.getText().toString(),
						UserSecurityChangePasswordWallet.this);

				// 对返回的数据进行判断
				if (str[0].equals("1000"))
				{
					finish();
					Toast.makeText(this, "重置成功，请牢记您的支付密码", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(UserSecurityChangePasswordWallet.this, "确认密码和新密码不一致",
						Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			if (StrFormat.formatStr(editPhone.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePasswordWallet.this, "请输入绑定的手机",
						Toast.LENGTH_SHORT).show();
			}
			else if (StrFormat.formatStr(editPin.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePasswordWallet.this, "请输入验证码",
						Toast.LENGTH_SHORT).show();
			}
			else if (StrFormat.formatStr(editNewPwd.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePasswordWallet.this, "请设置新密码",
						Toast.LENGTH_SHORT).show();
			}
			else if (StrFormat.formatStr(editReNewPwd.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePasswordWallet.this, "请输入确认密码",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void setUiChange()
	{
		textView1.setTextColor(getResources().getColor(R.color.light_gray10));
		textView2.setTextColor(getResources().getColor(R.color.light_gray10));
		textView3.setBackgroundColor(getResources().getColor(R.color.light_gray2));
		textView4.setBackgroundColor(getResources().getColor(R.color.light_gray2));

		layoutChangePwd.setVisibility(View.GONE);
		layoutRetrievePwd.setVisibility(View.GONE);

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
