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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 支付宝认证金额验证
 * 
 * @author cm
 * 
 */
public class UserWalletAlipayAuth extends Activity
{
	TextView textTitle;
	ImageView imgBack;
	TextView textName;
	EditText editCash;
	Button btnCash;

	String strName;
	String strId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet_alipay_authcash);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		strName = getIntent().getStringExtra("alipay_name");
		strId = getIntent().getStringExtra("id");
		ViewInit();
	}

	private void ViewInit()
	{
		textTitle = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);
		textName = (TextView) findViewById(R.id.text_alipay_name);
		editCash = (EditText) findViewById(R.id.edit_alipay_cash);
		btnCash = (Button) findViewById(R.id.btn_alipay_cash);

		textTitle.setText("支付宝认证");
		textName.setText(strName);

		imgBack.setOnClickListener(listener);
		btnCash.setOnClickListener(listener);
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
			case R.id.btn_alipay_cash:
				if (StrFormat.formatStr(editCash.getText().toString()))
				{
					// 请求验证支付宝认证金额接口
					String str[] = AuthDP.verifyAlipayAuthCash(editCash.getText()
							.toString(), strId, UserWalletAlipayAuth.this);

					// 对返回的数据进行判断
					if (str[0].equals("1000"))
					{
						Toast.makeText(UserWalletAlipayAuth.this, "认证成功",
								Toast.LENGTH_SHORT).show();

						TestEvent event = new TestEvent();
						event.setAliAdd(true);
						EventBus.getDefault().post(event);

						finish();
					}
					else if (str[0].equals("1036"))
					{
						Toast.makeText(UserWalletAlipayAuth.this, str[1],
								Toast.LENGTH_SHORT).show();
						TestEvent event = new TestEvent();
						event.setAliAdd(true);
						EventBus.getDefault().post(event);

						finish();
					}
					else
					{
						Toast.makeText(UserWalletAlipayAuth.this, str[1],
								Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(UserWalletAlipayAuth.this, "请输入验证金额",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	};

}
