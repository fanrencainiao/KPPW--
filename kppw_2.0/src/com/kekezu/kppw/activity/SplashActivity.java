package com.kekezu.kppw.activity;

import java.io.File;
import java.math.BigDecimal;

import com.kekezu.kppw.R;
import com.kekezu.kppw.tools.HttpDownloader;
import com.kekezu.kppw.utils.ConfigInc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Toast;

/**
 * 
 *     class desc: 启动画面 (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 *     (2)是，则进入GuideActivity；否，则进入MainActivity (3)3s后执行(2)操作
 * 
 * 
 */
public class SplashActivity extends Activity
{
	boolean isFirstIn = false;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 2000;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	public Dialog dialog2;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog dialog = null;
	public Bundle bundle;

	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// sdk2.3以后再主线程里面是是建议访问网络的，所以要更改activity的启动模式。
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		init();
	}

	private void init()
	{
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME,
				MODE_PRIVATE);

		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (!isFirstIn)
		{
			// 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		}
		else
		{
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, 0);
		}
	}

	private void goHome()
	{
		// 检测版本
		if (ConfigInc.checkVersion(this))
		{
			// 有更新---》更新
			dialog();
		}
		else
		{
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			SplashActivity.this.startActivity(intent);
			SplashActivity.this.finish();
		}
	}

	private void goGuide()
	{
		Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	/**
	 * showdialog提示框
	 */
	protected void dialog()
	{
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		builder.setMessage(R.string.check_new_version); // 检测到有新版本，是否更新?
		builder.setTitle(R.string.tips); // 提示
		builder.setPositiveButton(R.string.update, // 更新
				new android.content.DialogInterface.OnClickListener()
				{
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int which)
					{
						File oldFile = new File(Environment.getExternalStorageDirectory()
								+ "/kppw/kppw3_android.apk");
						if (oldFile.exists())
						{
							oldFile.delete();
						}
						dialog.dismiss();
						showDialog(DIALOG_DOWNLOAD_PROGRESS);
						Thread t = new Thread(runnable);
						t.start();
					}
				});
		builder.setNegativeButton(R.string.not_update, // 暂不更新
				new android.content.DialogInterface.OnClickListener()
				{

					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						Intent intent = new Intent(SplashActivity.this,
								MainActivity.class);
						SplashActivity.this.startActivity(intent);
						SplashActivity.this.finish();
					}
				});
		builder.create().show();
	}

	// 进度条
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
		case DIALOG_DOWNLOAD_PROGRESS:
			dialog = new ProgressDialog(this);
			dialog.setMessage("正在下载…");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setCancelable(false);
			dialog.show();
			return dialog;
		default:
			return null;
		}
	}

	/**
	 * 控制进度条
	 */
	private Handler handler = new Handler()
	{

		public void handleMessage(Message msg)
		{
			if (!Thread.currentThread().isInterrupted())
			{
				switch (msg.what)
				{
				case 0:
					dialog.setMax(msg.arg1);
					break;
				case 1:
					dialog.setProgress(msg.arg1);
					break;
				case 2:
					dialog.dismiss();
					break;
				case -1:
					String error = msg.getData().getString("error");
					Toast.makeText(SplashActivity.this, error, 1).show();
					break;

				}
			}
			super.handleMessage(msg);
		}
	};
	// 下载新的版本
	Runnable runnable = new Runnable()
	{
		public void run()
		{
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/kppw/kppw3_android.apk");
			if (file.exists())
			{
				file.delete();
			}
			HttpDownloader httpDownloader = new HttpDownloader();
			int result = httpDownloader.download(SplashActivity.this, "/kppw/",
					"kppw3_android.apk");

			if (result == 0)
			{
				Uri fileuri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory() + "/kppw/kppw3_android.apk")); // 这里是APK路径
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(fileuri, "application/vnd.android.package-archive");
				startActivity(intent);
			}
		}

	};

	public void sendMsg(int flag, int value)
	{
		Message message = new Message();
		message.what = flag;
		message.arg1 = value;
		handler.sendMessage(message);
	}

	/**
	 * 格式化单位
	 * 
	 * @param size
	 * @return
	 */
	public static double getFormatSize(double size)
	{
		double kiloByte = size / 1024;
		if (kiloByte < 1)
		{
			return size;
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1)
		{
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return Double.valueOf(result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString());
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1)
		{
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return Double.valueOf(result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString());
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1)
		{
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return Double.valueOf(result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString());
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return Double.valueOf(result4.setScale(2, BigDecimal.ROUND_HALF_UP)
				.toPlainString());
	}

}
