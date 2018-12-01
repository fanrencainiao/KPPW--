package com.kekezu.kppw.activity;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.dataprocess.BankDP;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 验证银行卡绑定
 * 
 * @author cm
 * 
 */
public class UserWalletBankAuth extends Activity
{
	TextView text_title;
	ImageView imgBack;

	String strId;
	String strBankName;
	String strDepositName;
	String strAccount;

	TextView textBankName;
	TextView textDepositName;
	TextView textAccount;
	EditText editCash;
	Button button1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet_bank_apply);

		strId = getIntent().getStringExtra("id");
		strBankName = getIntent().getStringExtra("bank_name");
		strDepositName = getIntent().getStringExtra("deposit_name");
		strAccount = getIntent().getStringExtra("account");

		ViewInit();
	}

	private void ViewInit()
	{
		text_title = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);

		textBankName = (TextView) findViewById(R.id.text_bank_name);
		textDepositName = (TextView) findViewById(R.id.text_bank_name2);
		textAccount = (TextView) findViewById(R.id.text_bank_number);
		editCash = (EditText) findViewById(R.id.edit_bank_cash);
		button1 = (Button) findViewById(R.id.button1);

		text_title.setText("银行卡绑定");
		textBankName.setText(strBankName);
		textDepositName.setText(strDepositName);
		textAccount.setText(strAccount);

		imgBack.setOnClickListener(listener);
		button1.setOnClickListener(listener);

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
			case R.id.button1:
				if (StrFormat.formatStr(editCash.getText().toString()))
				{
					String str[] = BankDP.verifyBankAuthCash(editCash.getText()
							.toString(), strId, UserWalletBankAuth.this);

					// 对返回的数据进行判断
					if (str[0].equals("1000"))
					{
						Toast.makeText(UserWalletBankAuth.this, "认证成功",
								Toast.LENGTH_SHORT).show();

						TestEvent event = new TestEvent();
						event.setBankAdd(true);
						EventBus.getDefault().post(event);

						finish();
					}
					else
					{
						Toast.makeText(UserWalletBankAuth.this, str[1],
								Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(UserWalletBankAuth.this, "请输入验证金额", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				break;
			}

		}
	};
}
