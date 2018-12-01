package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.dataprocess.BankDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 添加银行卡绑定
 * 
 * @author cm
 * 
 */
public class UserWalletBankAdd extends Activity
{
	ImageView imgBack;
	TextView text_title;
	EditText etBankName;
	EditText editBankNumber1;
	EditText editBankNumber2;
	Button button1;
	Intent intent;
	String strApply;
	List<String> banknamelist;
	private Spinner spinner;
	private ArrayAdapter<String> arr_adapter;

	String strBankName;

	ArrayList<HashMap<String, Object>> cityList;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet_bank_add);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		banknamelist = BankDP.getBankName(this);

		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		text_title = (TextView) findViewById(R.id.header_title);
		etBankName = (EditText) findViewById(R.id.bank_name);
		editBankNumber1 = (EditText) findViewById(R.id.edit_bank_number);
		editBankNumber2 = (EditText) findViewById(R.id.edit_bank_number2);
		button1 = (Button) findViewById(R.id.button1);

		text_title.setText("银行卡绑定");

		imgBack.setOnClickListener(listener);
		button1.setOnClickListener(listener);
		spinner = (Spinner) findViewById(R.id.spinner1);
		// 适配器
		arr_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, banknamelist);

		strBankName = banknamelist.get(0);

		// 加载适配器
		spinner.setAdapter(arr_adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id)
			{
				strBankName = banknamelist.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

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
			case R.id.button1:
				addBank();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 添加银行卡前的数据判断
	 */
	protected void addBank()
	{
		if (StrFormat.formatStr(etBankName.getText().toString()))
		{
			if (StrFormat.formatStr(editBankNumber1.getText().toString()))
			{
				if (StrFormat.formatStr(editBankNumber2.getText().toString()))
				{
					if (editBankNumber1.getText().toString()
							.equals(editBankNumber1.getText().toString()))
					{
						// 请求接口
						String str[] = BankDP.addBank(etBankName.getText().toString(),
								strBankName, editBankNumber1.getText().toString(),
								editBankNumber2.getText().toString(),
								UserWalletBankAdd.this);

						// 对返回的数据进行判断
						if (str[0].equals("1000"))
						{
							Toast.makeText(this, "申请成功", Toast.LENGTH_SHORT).show();

							TestEvent event = new TestEvent();
							event.setBankAdd(true);
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
						Toast.makeText(UserWalletBankAdd.this, "确认账号和账号不同",
								Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(UserWalletBankAdd.this, "请填写确认账号", Toast.LENGTH_SHORT)
							.show();
				}
			}
			else
			{
				Toast.makeText(UserWalletBankAdd.this, "请填写银行账号", Toast.LENGTH_SHORT)
						.show();
			}
		}
		else
		{
			Toast.makeText(UserWalletBankAdd.this, "请填支行名称", Toast.LENGTH_SHORT).show();
		}
	}
}
