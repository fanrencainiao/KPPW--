package com.kekezu.kppw.activity;

import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import android.os.Handler.Callback;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.dataprocess.UserDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener, Callback,
		PlatformActionListener
{
	EditText editName;
	EditText editPassWord;

	Button btn_login;
	TextView text_register;
	TextView text_retrieve_pwd;
	TextView text_tourist;

	ImageView imgWeiBo;
	ImageView imgQQ;
	ImageView imgWinXin;

	Platform platform;

	private Handler handler;
	@SuppressWarnings("unused")
	private static final int MSG_SMSSDK_CALLBACK = 1;
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR = 3;
	private static final int MSG_AUTH_COMPLETE = 4;

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// sdk2.3以后再主线程里面是是建议访问网络的，所以要更改activity的启动模式。
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
		handler = new Handler(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		ShareSDK.initSDK(this);

		viewInit();
	}

	private void viewInit()
	{
		editName = (EditText) findViewById(R.id.edit_name);
		editPassWord = (EditText) findViewById(R.id.edit_password);

		btn_login = (Button) findViewById(R.id.btn_login);
		text_register = (TextView) findViewById(R.id.text_register);
		text_retrieve_pwd = (TextView) findViewById(R.id.text_retrieve_pwd);
		text_tourist = (TextView) findViewById(R.id.text_tourist);

		imgWeiBo = (ImageView) findViewById(R.id.img_weibo);
		imgQQ = (ImageView) findViewById(R.id.img_qq);
		imgWinXin = (ImageView) findViewById(R.id.img_weixin);

		btn_login.setOnClickListener(listener);
		text_register.setOnClickListener(listener);
		text_retrieve_pwd.setOnClickListener(listener);
		text_tourist.setOnClickListener(listener);
		imgWeiBo.setOnClickListener(listener);
		imgQQ.setOnClickListener(listener);
		imgWinXin.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.btn_login:
				toLogin();
				break;
			case R.id.text_register:
				intent = new Intent(Login.this, Register.class);
				startActivity(intent);
				break;
			case R.id.text_retrieve_pwd:
				intent = new Intent(Login.this, RetrievePassword1.class);
				startActivity(intent);
				break;
			case R.id.text_tourist:
				finish();
				break;
			case R.id.img_weibo:
				// 新浪微博
				Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
				authorize(sina);
				break;
			case R.id.img_qq:
				Platform qq = ShareSDK.getPlatform(QQ.NAME);
				authorize(qq);
				break;
			case R.id.img_weixin:
				Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
				authorize(wechat);
				break;
			default:
				break;
			}
		}
	};

	// 用户名登录
	protected void toLogin()
	{
		if (StrFormat.formatStr(editName.getText().toString())
				&& StrFormat.formatStr(editPassWord.getText().toString()))
		{
			if (editPassWord.getText().length() >= 6)
			{
				String str[] = UserDP.userLogin(editName.getText().toString(),
						editPassWord.getText().toString(), Login.this);

				// 对返回的数据进行判断
				if (str[0].equals("1000"))
				{
					Intent intent = new Intent();
					setResult(88, intent);

					TestEvent event = new TestEvent();
					event.setF_user(true);
					EventBus.getDefault().post(event);

					Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
					finish();
				}
				else
				{
					Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(Login.this, "密码至少6位", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			if (StrFormat.formatStr(editName.getText().toString()) == false)
			{
				Toast.makeText(Login.this, "请输入账号", Toast.LENGTH_SHORT).show();
			}
			else if (StrFormat.formatStr(editPassWord.getText().toString()) == false)
			{
				Toast.makeText(Login.this, "请输入密码", Toast.LENGTH_SHORT).show();
			}
		}

	}

	// 执行授权,获取用户信息
	// 文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
	private void authorize(Platform plat)
	{
		if (plat == null)
		{
			return;
		}
		plat.setPlatformActionListener(this);
		// 关闭SSO授权
		plat.SSOSetting(false);
		plat.showUser(null);
	}

	@Override
	public void onCancel(Platform arg0, int arg1)
	{
		if (arg1 == Platform.ACTION_USER_INFOR)
		{
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}

	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2)
	{
		if (arg1 == Platform.ACTION_USER_INFOR)
		{
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] { arg0.getName(), arg2 };
			handler.sendMessage(msg);
		}

	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2)
	{
		if (arg1 == Platform.ACTION_USER_INFOR)
		{
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		arg2.printStackTrace();

	}

	@Override
	public void onClick(View v)
	{

	}

	// 第三方登录
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public boolean handleMessage(Message msg)
	{
		switch (msg.what)
		{
		case MSG_AUTH_CANCEL:
			// 取消授权
			Toast.makeText(this, R.string.ssdk_oks_cancel, Toast.LENGTH_SHORT).show();
			break;
		case MSG_AUTH_ERROR:
			// 授权失败
			Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
			break;
		case MSG_AUTH_COMPLETE:
			// 授权成功
			Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
			Object[] objs = (Object[]) msg.obj;
			String platform = (String) objs[0];
			HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
			Platform aaaa = ShareSDK.getPlatform(platform);
			aaaa.getDb().getUserId();

			// Log.d("id", "" + aaaa.getDb().getUserId());
			// Log.d("name", "" + aaaa.getDb().getUserName());
			// Log.d("sex", "" + aaaa.getDb().getUserGender());

			String str[] = UserDP.oauthLogin(platform, aaaa.getDb().getUserId(), aaaa
					.getDb().getUserName(), aaaa.getDb().getUserGender(), this);

			// 对返回的数据进行判断
			if (str[0].equals("1000"))
			{
				Intent intent = new Intent();
				setResult(88, intent);

				TestEvent event = new TestEvent();
				event.setF_user(true);
				EventBus.getDefault().post(event);
				finish();
			}
			else
			{
				Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return false;
	}
}
