package com.kekezu.kppw.wxapi;

import com.kekezu.kppw.MyApp;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler
{
	@SuppressWarnings("unused")
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(com.kekezu.kppw.R.layout.pay_result);

		api = MyApp.api;
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req)
	{

	}

	@Override
	public void onResp(BaseResp resp)
	{
		// Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		//
		// if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX)
		// {
		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.app_tip);
		// builder.setMessage(getString(R.string.pay_result_callback_msg,
		// String.valueOf(resp.errCode)));
		// builder.show();
		// }

		if (resp.errCode == 0)
		{
			finish();
			Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
		}
		else if (resp.errCode == -2)
		{
			Toast.makeText(WXPayEntryActivity.this, "取消支付", Toast.LENGTH_SHORT).show();
		}
	}
}