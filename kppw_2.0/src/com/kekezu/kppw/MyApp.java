package com.kekezu.kppw;

import com.alibaba.wxlib.util.SysUtil;
import com.kekezu.kppw.imcustom.InitHelper;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application
{
	// 微信支付的key
	public static IWXAPI api;
	// OpenIM应用APPKEY，这个APPKEY是申请应用时获取的
	public static String APP_KEY = "24809505";
	// 云旺OpenIM的DEMO用到的Application上下文实例
	private static Context sContext;

	public static Context getContext()
	{
		return sContext;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		if (mustRunFirstInsideApplicationOnCreate())
		{
			// todo 如果在":TCMSSevice"进程中，无需进行openIM和app业务的初始化，以节省内存
			return;
		}
		// YWAPI.init(this, APP_KEY);
		// 初始化云旺SDK
		InitHelper.initYWSDK(this);

		// 初始化微信支付api
		api = WXAPIFactory.createWXAPI(this, "wxb07ae674e6ff929b");
	}

	private boolean mustRunFirstInsideApplicationOnCreate()
	{
		// 必须的初始化
		SysUtil.setApplication(this);
		sContext = getApplicationContext();
		return SysUtil.isTCMSServiceProcess(sContext);
	}
}
