package com.kekezu.kppw.activity;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.dataprocess.AuthDP;
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
 * 添加支付宝绑定
 * 
 * @author cm
 * 
 */
public class UserWalletAlipayAdd extends Activity
{
	TextView textTitle;
	ImageView imgBack;

	EditText editName;
	EditText editAccount;
	EditText editConfirm;
	TextView btnAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet_alipay_add);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		ViewInit();
	}

	private void ViewInit()
	{
		textTitle = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);

		editName = (EditText) findViewById(R.id.edit_name);
		editAccount = (EditText) findViewById(R.id.edit_account);
		editConfirm = (EditText) findViewById(R.id.edit_confirm);
		btnAdd = (TextView) findViewById(R.id.btn_add_alipay);

		textTitle.setText("添加支付宝");
		btnAdd.setOnClickListener(listener);
		imgBack.setOnClickListener(listener);
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
			case R.id.btn_add_alipay:
				addAlipay();
				break;
			default:
				break;
			}

		}
	};

	/**
	 * 输入框判断
	 */
	protected void addAlipay()
	{
		if (StrFormat.formatStr(editName.getText().toString())
				&& StrFormat.formatStr(editAccount.getText().toString())
				&& StrFormat.formatStr(editConfirm.getText().toString()))
		{
			if (editAccount.getText().toString().equals(editConfirm.getText().toString()))
			{
				// 传递添加支付宝需要的参数，请求服务
				String str[] = AuthDP.addAlipay(editName.getText().toString(),
						editAccount.getText().toString(), editConfirm.getText()
								.toString(), UserWalletAlipayAdd.this);

				// 对返回的数据进行判断
				if (str[0].equals("1000"))
				{
					Toast.makeText(this, "申请成功，等待后台打款确认", Toast.LENGTH_SHORT).show();

					TestEvent event = new TestEvent();
					event.setAliAdd(true);
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
				Toast.makeText(UserWalletAlipayAdd.this, "确认账号和支付宝账号不符",
						Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			if (StrFormat.formatStr(editName.getText().toString()) == false)
			{
				Toast.makeText(UserWalletAlipayAdd.this, "请输入账号名称", Toast.LENGTH_SHORT)
						.show();
			}
			else if (StrFormat.formatStr(editAccount.getText().toString()) == false)
			{
				Toast.makeText(UserWalletAlipayAdd.this, "请输入支付宝账号", Toast.LENGTH_SHORT)
						.show();
			}
			else if (StrFormat.formatStr(editConfirm.getText().toString()) == false)
			{
				Toast.makeText(UserWalletAlipayAdd.this, "请输入确认账号", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
}
