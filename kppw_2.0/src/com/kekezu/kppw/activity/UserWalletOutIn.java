package com.kekezu.kppw.activity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.kekezu.kppw.MyApp;
import com.kekezu.kppw.R;
import com.kekezu.kppw.alipay.PayResult;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.dataprocess.UserDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserWalletOutIn extends Activity
{
	ImageView imgBack;
	TextView textTitle;
	LinearLayout layoutWeiXin;
	LinearLayout layoutBank;
	LinearLayout layoutAlipay;
	TextView tvCheckWx, tvCheckBank, tvCheckAlipay;
	TextView tvAilpay, tvTag, tvSubmit;
	TextView tvBankName, tvAlipayName;
	EditText etCash;

	LoadingDialog progressDialog;

	Intent intent;
	String strViewType;
	String strCash;
	String strType;
	IWXAPI api;
	String strAccount;
	String strOutType = "2";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet_out_in);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		api = MyApp.api;
		strViewType = getIntent().getStringExtra("type");
		strCash = getIntent().getStringExtra("cash");

		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		layoutWeiXin = (LinearLayout) findViewById(R.id.lay_wallet_wx);
		layoutBank = (LinearLayout) findViewById(R.id.lay_wallet_bank);
		layoutAlipay = (LinearLayout) findViewById(R.id.lay_wallet_alipay);
		tvCheckWx = (TextView) findViewById(R.id.tv_check_wx);
		tvCheckBank = (TextView) findViewById(R.id.tv_check_bank);
		tvCheckAlipay = (TextView) findViewById(R.id.tv_check_alipay);
		tvAilpay = (TextView) findViewById(R.id.tv_wallet_alipay);
		tvTag = (TextView) findViewById(R.id.tv_tag);
		etCash = (EditText) findViewById(R.id.et_cash);
		tvSubmit = (TextView) findViewById(R.id.tv_submit);
		tvBankName = (TextView) findViewById(R.id.tv_bank_id);
		tvAlipayName = (TextView) findViewById(R.id.tv_alipay_id);

		imgBack.setOnClickListener(listener);
		tvCheckWx.setOnClickListener(listener);
		tvCheckBank.setOnClickListener(listener);
		tvCheckAlipay.setOnClickListener(listener);
		tvBankName.setOnClickListener(listener);
		tvAlipayName.setOnClickListener(listener);
		tvSubmit.setOnClickListener(listener);
		setViewType();
	}

	@SuppressWarnings("deprecation")
	private void setViewType()
	{
		if (strViewType.equals("in"))
		{
			textTitle.setText("充值");
			tvAilpay.setText("支付宝充值");
			tvCheckWx.setBackground(getResources().getDrawable(R.drawable.radio_check));
			layoutBank.setVisibility(View.GONE);
			tvTag.setText("2小时内到账，请耐心等待");
		}
		else
		{
			textTitle.setText("提现");
			tvAilpay.setText("支付宝提现");
			tvCheckBank.setBackground(getResources().getDrawable(R.drawable.radio_check));
			layoutWeiXin.setVisibility(View.GONE);
			tvTag.setText("账户余额" + strCash + "元，每日最高可提现金额10,000元");
		}
	}

	OnClickListener listener = new OnClickListener()
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.img_back:
				finish();
				break;
			case R.id.tv_check_wx:
				strType = "wechat";
				tvCheckWx.setBackground(getResources()
						.getDrawable(R.drawable.radio_check));
				tvCheckAlipay.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));
				break;
			case R.id.tv_check_bank:
				tvCheckBank.setBackground(getResources().getDrawable(
						R.drawable.radio_check));
				tvCheckAlipay.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));

				tvAlipayName.setVisibility(View.GONE);
				tvBankName.setVisibility(View.VISIBLE);
				tvBankName.setText("请选择");
				strAccount = "";
				strOutType = "2";
				break;
			case R.id.tv_check_alipay:
				strType = "alipay";
				tvCheckAlipay.setBackground(getResources().getDrawable(
						R.drawable.radio_check));
				tvCheckWx.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));
				tvCheckBank.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));
				if (strViewType.equals("out"))
				{
					tvAlipayName.setVisibility(View.VISIBLE);
					tvBankName.setVisibility(View.GONE);
					strAccount = "";
					tvAlipayName.setText("请选择");
					strOutType = "1";
				}

				break;
			case R.id.tv_bank_id:
				intent = new Intent(UserWalletOutIn.this, UserWalletBankList.class);
				intent.putExtra("type", "1");
				startActivityForResult(intent, 333);
				break;
			case R.id.tv_alipay_id:
				intent = new Intent(UserWalletOutIn.this, UserWalletAlipayList.class);
				intent.putExtra("type", "1");
				startActivityForResult(intent, 444);
				break;
			case R.id.tv_submit:
				postWalletIn();
				break;
			case R.id.button_quxiao:
				dialog1.cancel();
				break;
			case R.id.button_queding:
				if (StrFormat.formatStr(mPetPwd.getText().toString()))
				{
					dialog1.cancel();
					progressDialog = new LoadingDialog(UserWalletOutIn.this);
					progressDialog.setCancelable(false);
					progressDialog.show();

					Thread t = new Thread(newTread);
					t.start();
				}
				else
				{
					Toast.makeText(UserWalletOutIn.this, "请输入支付密码", 2000).show();
				}
				break;
			default:
				break;
			}
		}
	};

	protected void postWalletIn()
	{
		if (StrFormat.formatStr(etCash.getText().toString()))
		{
			if (strViewType.equals("in"))
			{
				if (strType.equals("wechat"))
				{
					wechatPay();
				}
				else if (strType.equals("alipay"))
				{
					alipayPay();
				}
			}
			else
			{
				if (StrFormat.formatStr(strAccount))
				{
					payType1();
				}
				else
				{
					Toast.makeText(UserWalletOutIn.this, "请选择提现收款的账号", 2000).show();
				}
			}
		}
		else
		{
			Toast.makeText(UserWalletOutIn.this, "请输入金额", 2000).show();
		}
	}

	Runnable newTread = new Runnable()
	{
		String strRe[];

		@Override
		public void run()
		{
			strRe = UserDP.postCashOut(etCash.getText().toString(), strType, strAccount,
					mPetPwd.getText().toString(), UserWalletOutIn.this);

			Message msg = tHandler.obtainMessage(1, strRe);
			msg.sendToTarget();
		}
	};

	private Handler tHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case 1:
				String str[] = (String[]) msg.obj;
				if (str[0].equals("1000"))
				{
					TestEvent event = new TestEvent();
					event.setCash(true);
					EventBus.getDefault().post(event);
					finish();
				}
				progressDialog.dismiss();
				Toast.makeText(UserWalletOutIn.this, str[1], 2000).show();
				break;
			}
		}
	};

	Dialog dialog1;
	EditText mPetPwd;
	Button quxiao;
	Button queding;

	protected void payType1()
	{
		LayoutInflater inflater1 = LayoutInflater.from(this);
		LinearLayout layout1 = (LinearLayout) inflater1.inflate(R.layout.pwd_editview,
				null);

		dialog1 = new Dialog(UserWalletOutIn.this);
		dialog1.setContentView(layout1);
		dialog1.setTitle("支付密码");
		dialog1.show();

		mPetPwd = (EditText) layout1.findViewById(R.id.pet_pwd);
		mPetPwd.setFocusable(true);
		quxiao = (Button) layout1.findViewById(R.id.button_quxiao);
		queding = (Button) layout1.findViewById(R.id.button_queding);

		quxiao.setOnClickListener(listener);
		queding.setOnClickListener(listener);

	}

	// 微信支付
	protected void wechatPay()
	{
		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = TaskDP.postCash("平台充值", etCash.getText().toString(),
				strType, "", this);

		Toast.makeText(UserWalletOutIn.this, "获取订单中...", Toast.LENGTH_SHORT).show();
		try
		{
			if (payInfo != null && payInfo.length() > 0)
			{
				String content = new String(payInfo);
				Log.e("get server pay params:", content);
				JSONObject json = new JSONObject(content);
				if (null != json && !json.has("retcode"))
				{
					PayReq request = new PayReq();
					request.appId = json.getString("appid");
					request.partnerId = json.getString("partnerid");
					request.prepayId = json.getString("prepayid");
					request.nonceStr = json.getString("noncestr");
					request.timeStamp = json.getString("timestamp");
					request.packageValue = json.getString("package");
					request.sign = json.getString("sign");
					request.extData = "app data";
					api.sendReq(request);
				}
				else
				{
					Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
					Toast.makeText(UserWalletOutIn.this,
							"返回错误" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Log.d("PAY_GET", "服务器请求错误");
				Toast.makeText(UserWalletOutIn.this, "服务器请求错误", Toast.LENGTH_SHORT)
						.show();
			}
		}
		catch (Exception e)
		{
			Log.e("PAY_GET", "异常：" + e.getMessage());
			Toast.makeText(UserWalletOutIn.this, "异常：" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	// 支付宝支付
	protected void alipayPay()
	{
		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = TaskDP.postCash("平台充值", etCash.getText().toString(),
				strType, "", this);
		Runnable payRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				// 构造PayTask 对象
				PayTask alipay = new PayTask(UserWalletOutIn.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	// 支付宝同步返回的结果必须放置到服务端进行验证
	private static final int SDK_PAY_FLAG = 1;
	private Handler mHandler = new Handler()
	{
		@SuppressWarnings("unused")
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case SDK_PAY_FLAG:
			{
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000"))
				{
					Toast.makeText(UserWalletOutIn.this, "充值成功", Toast.LENGTH_SHORT)
							.show();
					finish();
					TestEvent event = new TestEvent();
					event.setCashIn(true);
					EventBus.getDefault().post(event);
				}
				else
				{
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000"))
					{
						Toast.makeText(UserWalletOutIn.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();
					}
					else
					{
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(UserWalletOutIn.this, "支付失败", Toast.LENGTH_SHORT)
								.show();
					}
				}
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (data != null)
		{
			if (requestCode == 333)
			{
				String result = data.getExtras().getString("bank");// 得到新Activity关闭后返回的数据
				strAccount = data.getExtras().getString("account");
				tvBankName.setText(result);
			}
			if (requestCode == 444)
			{
				String result = data.getExtras().getString("alipay");// 得到新Activity关闭后返回的数据
				strAccount = data.getExtras().getString("account");
				tvAlipayName.setText(result);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
