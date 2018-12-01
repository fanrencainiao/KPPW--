package com.kekezu.kppw.activity;

import com.kekezu.kppw.R;
import com.kekezu.kppw.dataprocess.UserDP;

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

public class UserWallet extends Activity
{
	ImageView imgBack;
	LinearLayout layoutBankAuto;
	LinearLayout layoutAlipayAuto;
	TextView btn_wallet_recharge;
	TextView btn_wallet_cash;
	TextView textBalance;
	TextView tvDetail;
	String strBalance;
	Intent intent;

	Thread t;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet);

		t = new Thread(newTread1);
		t.start();

		ViewInit();
	}

	private void ViewInit()
	{
		textBalance = (TextView) findViewById(R.id.text_user_balance);
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvDetail = (TextView) findViewById(R.id.tv_wallet_detail);
		btn_wallet_recharge = (TextView) findViewById(R.id.btn_user_wallet_recharge);
		btn_wallet_cash = (TextView) findViewById(R.id.btn_user_wallet_cash);
		layoutBankAuto = (LinearLayout) findViewById(R.id.layout_bank_auto);
		layoutAlipayAuto = (LinearLayout) findViewById(R.id.layout_alipay_auto);

		imgBack.setOnClickListener(listener);
		tvDetail.setOnClickListener(listener);
		btn_wallet_recharge.setOnClickListener(listener);
		btn_wallet_cash.setOnClickListener(listener);
		layoutBankAuto.setOnClickListener(listener);
		layoutAlipayAuto.setOnClickListener(listener);
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
			case R.id.tv_wallet_detail:
				intent = new Intent(UserWallet.this, UserWalletDetail.class);
				startActivity(intent);
				break;
			case R.id.btn_user_wallet_recharge:
				// 充值
				intent = new Intent(UserWallet.this, UserWalletOutIn.class);
				intent.putExtra("type", "in");
				intent.putExtra("cash", strBalance);
				startActivity(intent);
				break;
			case R.id.btn_user_wallet_cash:
				// 提现
				intent = new Intent(UserWallet.this, UserWalletOutIn.class);
				intent.putExtra("type", "out");
				intent.putExtra("cash", strBalance);
				startActivity(intent);
				break;
			case R.id.layout_bank_auto:
				intent = new Intent(UserWallet.this, UserWalletBankList.class);
				intent.putExtra("type", "0");
				startActivity(intent);
				break;
			case R.id.layout_alipay_auto:
				intent = new Intent(UserWallet.this, UserWalletAlipayList.class);
				intent.putExtra("type", "0");
				startActivity(intent);
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
			strBalance = UserDP.getBalance(UserWallet.this);
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
				textBalance.setText(strBalance);
				break;
			}
		}
	};
}
