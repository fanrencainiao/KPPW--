package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.BankAdapter;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.BankDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 银行卡列表
 * 
 * @author cm
 * 
 */
public class UserWalletBankList extends Activity
{
	ImageView imgBack;
	TextView text_title;
	ListView listBank;
	LinearLayout btnAddBank;
	BankAdapter listAdapter;

	ArrayList<HashMap<String, Object>> bankList;
	LoadingDialog progressDialog;
	Intent intent;
	String strType;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet_bank_list);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		strType = getIntent().getStringExtra("type");
		ViewInit();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();

		EventBus.getDefault().register(this);
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		text_title = (TextView) findViewById(R.id.header_title);
		text_title.setText("银行卡认证");
		btnAddBank = (LinearLayout) findViewById(R.id.lay_add_bank);
		listBank = (ListView) findViewById(R.id.list_bank);

		listBank.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				if (strType.equals("0"))
				{
					if (bankList.get(position).get("status").equals("0")
							|| bankList.get(position).get("status").equals("1"))
					{
						intent = new Intent(UserWalletBankList.this,
								UserWalletBankAuth.class);

						intent.putExtra("id", bankList.get(position).get("id").toString());
						intent.putExtra("bank_name",
								bankList.get(position).get("bank_name").toString());
						intent.putExtra("deposit_name",
								bankList.get(position).get("deposit_name").toString());
						intent.putExtra("account", bankList.get(position).get("account")
								.toString());

						startActivity(intent);
					}
					else if (bankList.get(position).get("status").equals("2"))
					{
						intent = new Intent(UserWalletBankList.this,
								UserWalletBankDetail.class);

						intent.putExtra("bank_name",
								bankList.get(position).get("bank_name").toString());
						intent.putExtra("deposit_name",
								bankList.get(position).get("deposit_name").toString());
						intent.putExtra("account", bankList.get(position).get("account")
								.toString());

						intent.putExtra("deposit_area",
								bankList.get(position).get("deposit_area").toString());

						startActivity(intent);
					}
				}
				else
				{
					if (bankList.get(position).get("status").equals("2"))
					{
						String strAcc = bankList
								.get(position)
								.get("account")
								.toString()
								.substring(
										bankList.get(position).get("account").toString()
												.length() - 4,
										bankList.get(position).get("account").toString()
												.length());
						Intent intent = new Intent();
						intent.putExtra("bank", bankList.get(position).get("bank_name")
								+ " " + strAcc);
						intent.putExtra("account", bankList.get(position).get("account")
								.toString());
						setResult(333, intent);
						finish();
					}
					else
					{
						Toast.makeText(UserWalletBankList.this, "请选择绑定完成的银行卡", 2000)
								.show();
					}
				}

			}
		});

		imgBack.setOnClickListener(listener);
		btnAddBank.setOnClickListener(listener);
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
			case R.id.lay_add_bank:
				intent = new Intent(UserWalletBankList.this, UserWalletBankAdd.class);
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
			bankList = BankDP.getBankList(UserWalletBankList.this);
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
				listAdapter = new BankAdapter(bankList, UserWalletBankList.this);
				listBank.setAdapter(listAdapter);

				// 动态算出ListView的LayoutParams，并设置到ListView中
				ViewGroup.LayoutParams params = StrFormat.getListViewParams(listBank);
				listBank.setLayoutParams(params);
				
				progressDialog.dismiss();
				break;
			}
		}
	};

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isBankAdd())
		{
			Thread t = new Thread(newTread1);
			t.start();
			listAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
