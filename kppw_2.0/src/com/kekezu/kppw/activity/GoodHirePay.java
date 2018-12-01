package com.kekezu.kppw.activity;

import java.util.Map;

import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.kekezu.kppw.MyApp;
import com.kekezu.kppw.R;
import com.kekezu.kppw.alipay.PayResult;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.HireDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import android.app.Activity;
import android.app.Dialog;
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

/**
 * 雇佣付款
 * 
 * @author cm
 * 
 */
public class GoodHirePay extends Activity
{
	TextView textTitle;
	ImageView imgBack;
	TextView tvTitle, tvMoney, tvInfo, tvBalance;
	LinearLayout layoutWeiXin;
	LinearLayout layoutBank;
	LinearLayout layoutAlipay;
	TextView tvCheckWx, tvCheckBank, tvCheckAlipay;
	TextView tvPay;

	String strId;
	String strTitle;
	String strCash;
	String strEmployid;
	String strType = "station";
	IWXAPI api;
	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_release_pay);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		api = MyApp.api;
		ViewInit();

		setViewValue();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvMoney = (TextView) findViewById(R.id.tv_money);
		tvInfo = (TextView) findViewById(R.id.tv_info);
		tvBalance = (TextView) findViewById(R.id.tv_balance);
		layoutWeiXin = (LinearLayout) findViewById(R.id.lay_wallet_wx);
		layoutBank = (LinearLayout) findViewById(R.id.lay_wallet_bank);
		layoutAlipay = (LinearLayout) findViewById(R.id.lay_wallet_alipay);
		tvCheckWx = (TextView) findViewById(R.id.tv_check_wx);
		tvCheckBank = (TextView) findViewById(R.id.tv_check_bank);
		tvCheckAlipay = (TextView) findViewById(R.id.tv_check_alipay);
		tvPay = (TextView) findViewById(R.id.tv_submit_pay);

		tvInfo.setVisibility(View.GONE);
		tvBalance.setVisibility(View.GONE);
		textTitle.setText("在线支付");
		imgBack.setOnClickListener(listener);
		tvInfo.setOnClickListener(listener);
		layoutWeiXin.setOnClickListener(listener);
		layoutBank.setOnClickListener(listener);
		layoutAlipay.setOnClickListener(listener);
		tvPay.setOnClickListener(listener);
	}

	private void setViewValue()
	{
		strEmployid = getIntent().getStringExtra("employ_id");
		strId = getIntent().getStringExtra("order_id");
		strTitle = getIntent().getStringExtra("title");
		strCash = getIntent().getStringExtra("cash");
		tvTitle.setText(strTitle);
		tvMoney.setText(strCash);
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
				onBackPressed();
				break;
			case R.id.tv_info:

				break;
			case R.id.lay_wallet_wx:
				strType = "wechat";
				tvCheckWx.setBackground(getResources()
						.getDrawable(R.drawable.radio_check));
				tvCheckAlipay.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));
				tvCheckBank.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));
				break;
			case R.id.lay_wallet_bank:
				strType = "station";
				tvCheckBank.setBackground(getResources().getDrawable(
						R.drawable.radio_check));
				tvCheckAlipay.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));
				tvCheckWx.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));
				break;
			case R.id.lay_wallet_alipay:
				strType = "alipay";
				tvCheckAlipay.setBackground(getResources().getDrawable(
						R.drawable.radio_check));
				tvCheckWx.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));
				tvCheckBank.setBackground(getResources().getDrawable(
						R.drawable.radio_checknull));
				break;
			case R.id.tv_submit_pay:
				if (strType.equals("station"))
				{
					payType1();
				}
				else if (strType.equals("alipay"))
				{
					payType2();
				}
				else
				{
					payType3();
				}
				break;
			case R.id.button_quxiao:
				dialog1.cancel();
				break;
			case R.id.button_queding:
				if (StrFormat.formatStr(mPetPwd.getText().toString()))
				{
					dialog1.cancel();
					progressDialog = new LoadingDialog(GoodHirePay.this);
					progressDialog.setCancelable(false);
					progressDialog.show();

					Thread t = new Thread(newTread);
					t.start();
				}
				else
				{
					Toast.makeText(GoodHirePay.this, "请输入支付密码", 2000).show();
				}
				break;
			default:
				break;
			}
		}
	};

	Map<String, String> strRe;
	Runnable newTread = new Runnable()
	{
		@Override
		public void run()
		{
			strRe = HireDP.cashPayEmploy(strId, strEmployid, 0, mPetPwd.getText()
					.toString(), GoodHirePay.this);

			Message msg = tHandler.obtainMessage(1);
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
				if (strRe.get("code").equals("1000"))
				{
					finish();
				}
				progressDialog.dismiss();
				Toast.makeText(GoodHirePay.this, strRe.get("msg"), 2000).show();
				break;
			}
		}
	};

	Dialog dialog1;
	EditText mPetPwd;
	Button quxiao;
	Button queding;

	// 支付方式1 站内支付
	protected void payType1()
	{
		LayoutInflater inflater1 = LayoutInflater.from(this);
		LinearLayout layout1 = (LinearLayout) inflater1.inflate(R.layout.pwd_editview,
				null);

		dialog1 = new Dialog(GoodHirePay.this);
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

	// 支付宝支付
	protected void payType2()
	{
		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = HireDP.ThirdCashEmployPay(strId, strEmployid, "alipay",
				this);
		// final String payInfo =
		// "_input_charset=utf-8&body=%E5%85%85%E5%80%BC100%E5%85%83&notify_url=http%3A%2F%2Fdev.kppw.cn%2Fapi%2Falipay%2Fnotify&out_trade_no=20160829101210000000152&partner=2088301857503158&payment_type=1&seller_id=pay%40kekezu.com&service=mobile.securitypay.pay&subject=%E5%85%85%E5%80%BC%E6%94%AF%E4%BB%98&total_fee=100&sign=TBCbF9wptlwvonQ%2FMxW3UWmNz6SqTo1LYCP8PfVJ%2FOiOIWIdSb1B%2B%2BlGpG2sf0yjsNLX7ClkKKk4mx5WjHpsDqscF0kfZXsIazd%2FltHOss0TK29R4yhDFJEIjhTJOsDY1WimXDMK%2BxiNRczFAbtUS%2F5CC1exvZsrg7WVUqMlL20%3D&sign_type=RSA";

		Runnable payRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				// 构造PayTask 对象
				PayTask alipay = new PayTask(GoodHirePay.this);
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
					Toast.makeText(GoodHirePay.this, "支付成功", Toast.LENGTH_SHORT).show();

					finish();
				}
				else
				{
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000"))
					{
						Toast.makeText(GoodHirePay.this, "支付结果确认中", Toast.LENGTH_SHORT)
								.show();
					}
					else
					{
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(GoodHirePay.this, "支付失败", Toast.LENGTH_SHORT)
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

	// 微信支付
	protected void payType3()
	{
		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = HireDP.ThirdCashEmployPay(strId, strEmployid, "wechat",
				this);

		Toast.makeText(GoodHirePay.this, "获取订单中...", Toast.LENGTH_SHORT).show();
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
					// req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
					// req.appId = "wxab43f78a83a5dd98";
					// req.partnerId = "1380675202";
					// req.prepayId = "wx201608251811488c766fdd810799803733";
					// req.nonceStr = "d8ded839e0cf8482481c4120d2bce04c";
					// req.timeStamp = "1472119909";
					// req.packageValue = "Sign=WXPay";
					// req.sign = "9A9C3B80609EFF182749F285C73EC953";

					request.appId = json.getString("appid");
					request.partnerId = json.getString("partnerid");
					request.prepayId = json.getString("prepayid");
					request.nonceStr = json.getString("noncestr");
					request.timeStamp = json.getString("timestamp");
					request.packageValue = json.getString("package");
					request.sign = json.getString("sign");
					request.extData = "app data"; // optional
					// Toast.makeText(TaskBill.this, "正常调起支付",
					// Toast.LENGTH_SHORT)
					// .show();
					// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
					api.sendReq(request);
				}
				else
				{
					Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
					Toast.makeText(GoodHirePay.this, "返回错误" + json.getString("retmsg"),
							Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Log.d("PAY_GET", "服务器请求错误");
				Toast.makeText(GoodHirePay.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
			}
		}
		catch (Exception e)
		{
			Log.e("PAY_GET", "异常：" + e.getMessage());
			Toast.makeText(GoodHirePay.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}
	}
}
