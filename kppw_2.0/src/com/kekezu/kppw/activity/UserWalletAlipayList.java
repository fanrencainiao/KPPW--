package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.AuthDP;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 支付宝认证列表
 * 
 * @author cm
 * 
 */
public class UserWalletAlipayList extends Activity
{
	ImageView imgBack;
	TextView text_title;
	ListView listAlipay;
	SimpleAdapter listAdapter;
	LinearLayout btnAddAliPay;

	ArrayList<HashMap<String, Object>> aliList;
	Intent intent;
	LoadingDialog progressDialog;
	String strType;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet_alipay_list);
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
		btnAddAliPay = (LinearLayout) findViewById(R.id.lay_add_bank);
		listAlipay = (ListView) findViewById(R.id.list_alipay);

		listAlipay.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				if (strType.equals("0"))
				{
					if (aliList.get(position).get("status").equals("1"))
					{
						// 根据认证的状态，跳转
						intent = new Intent(UserWalletAlipayList.this,
								UserWalletAlipayAuth.class);
						intent.putExtra("id", aliList.get(position).get("id").toString());
						intent.putExtra("alipay_name",
								aliList.get(position).get("alipay_name").toString());
						startActivity(intent);
					}
				}
				else
				{
					if (aliList.get(position).get("status").equals("2"))
					{
						Intent intent = new Intent();
						intent.putExtra("alipay",
								aliList.get(position).get("alipay_account").toString());
						setResult(444, intent);
						finish();
					}
					else
					{
						Toast.makeText(UserWalletAlipayList.this, "请选择绑定完成的账号", 2000)
								.show();
					}
				}
			}
		});

		text_title.setText("支付宝认证");
		imgBack.setOnClickListener(listener);
		btnAddAliPay.setOnClickListener(listener);
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
				intent = new Intent(UserWalletAlipayList.this, UserWalletAlipayAdd.class);
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
			aliList = AuthDP.alipayAuthList(UserWalletAlipayList.this);
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
				listAdapter = new SimpleAdapter(UserWalletAlipayList.this, aliList,
						R.layout.user_wallet_alipay_item, new String[] { "status_string",
								"alipay_account" }, new int[] { R.id.text_status,
								R.id.text_account });
				listAlipay.setAdapter(listAdapter);

				// 动态算出ListView的LayoutParams，并设置到ListView中
				ViewGroup.LayoutParams params = StrFormat.getListViewParams(listAlipay);
				listAlipay.setLayoutParams(params);
				
				progressDialog.dismiss();
				break;
			}
		}
	};

	@Subscribe
	public void onEvent(TestEvent event)
	{
		// 添加支付宝后，列表刷新
		if (event.isAliAdd())
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
