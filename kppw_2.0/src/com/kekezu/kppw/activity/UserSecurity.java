package com.kekezu.kppw.activity;

import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.AuthDP;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 账号信息
 * 
 * @author cm
 * 
 */
public class UserSecurity extends Activity
{
	LoadingDialog progressDialog;
	TextView textTitle;
	ImageView imgBack;

	LinearLayout layoutChangePwd;
	LinearLayout layoutChangePwdWallet;
	LinearLayout layoutIdentityAuto;

	TextView textStatus;
	Map<String, String> map;

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_security);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		EventBus.getDefault().register(this);
		ViewInit();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		layoutChangePwd = (LinearLayout) findViewById(R.id.layout_change_pwd);
		layoutChangePwdWallet = (LinearLayout) findViewById(R.id.layout_change_pwd_wallet);
		layoutIdentityAuto = (LinearLayout) findViewById(R.id.layout_identity_auto);
		textStatus = (TextView) findViewById(R.id.text_auth_status);

		textTitle.setText("账号管理");
		imgBack.setOnClickListener(listener);
		layoutChangePwd.setOnClickListener(listener);
		layoutChangePwdWallet.setOnClickListener(listener);
		layoutIdentityAuto.setOnClickListener(listener);
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
			case R.id.layout_change_pwd:
				intent = new Intent(UserSecurity.this, UserSecurityChangePassword.class);
				startActivity(intent);
				break;
			case R.id.layout_change_pwd_wallet:
				intent = new Intent(UserSecurity.this,
						UserSecurityChangePasswordWallet.class);
				startActivity(intent);
				break;
			case R.id.layout_identity_auto:
				if (map.get("status").equals("0"))
				{

				}
				else if (map.get("status").equals("1"))
				{
					// 认证成功
				}
				else if (map.get("status").equals("2"))
				{
					intent = new Intent(UserSecurity.this, UserSecurityIdentity.class);
					startActivity(intent);
				}
				else if (map.get("status").equals("3"))
				{
					intent = new Intent(UserSecurity.this, UserSecurityIdentity.class);
					startActivity(intent);
				}
				break;
			default:
				break;
			}

		}
	};

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			map = AuthDP.realnameAuthInfo(UserSecurity.this);
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
				initData();
				progressDialog.dismiss();
				break;
			}
		}
	};

	private void initData()
	{
		if (map.get("status").equals("0"))
		{
			textStatus.setText("等待审核");
		}
		else if (map.get("status").equals("1"))
		{
			textStatus.setText("已认证");
		}
		else if (map.get("status").equals("2"))
		{
			textStatus.setText("认证失败");
		}
		else if (map.get("status").equals("3"))
		{
			textStatus.setText("未认证");
		}
	}

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isAuth())
		{
			map = AuthDP.realnameAuthInfo(this);
			initData();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
