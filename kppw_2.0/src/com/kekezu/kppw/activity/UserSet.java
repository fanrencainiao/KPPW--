package com.kekezu.kppw.activity;

import java.io.File;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.dataprocess.UserDP;
import com.kekezu.kppw.tools.HttpDownloader;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.ConfigInc;
import com.kekezu.kppw.utils.DataCleanManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 设置
 * @author cm
 *
 */
public class UserSet extends Activity
{
	TextView text_title;
	ImageView imgBack;

	LinearLayout layout_clear_cache;
	LinearLayout layout_about;
	LinearLayout layout_update;

	TextView textOut;

	TextView tx;

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_set);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		text_title = (TextView) findViewById(R.id.header_title);

		text_title.setText("设置");

		layout_clear_cache = (LinearLayout) findViewById(R.id.layout_clear_cache);

		layout_about = (LinearLayout) findViewById(R.id.layout_about_us);
		textOut = (TextView) findViewById(R.id.text_out);
		tx = (TextView) findViewById(R.id.textView26);
		layout_update = (LinearLayout) findViewById(R.id.layout_check_updates);

		try
		{
			tx.setText(DataCleanManager.getTotalCacheSize(this));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		imgBack.setOnClickListener(listener);
		layout_clear_cache.setOnClickListener(listener);
		layout_about.setOnClickListener(listener);
		layout_update.setOnClickListener(listener);
		textOut.setOnClickListener(listener);

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
			case R.id.layout_clear_cache:
				DataCleanManager.clearAllCache(UserSet.this);
				Toast.makeText(UserSet.this, "清除完成", 2000).show();
				tx.setText("0.0 B");
				break;
			case R.id.layout_about_us:
				intent = new Intent(UserSet.this, UserSetAbout.class);
				startActivity(intent);
				break;
			case R.id.layout_check_updates:
				// 服务器版本
				String serviceVersion = ConfigInc
						.getServiceVersion(UserSet.this);
				// 客户端版本
				String clientVersion = ConfigInc.getVersion(UserSet.this);

				if (!clientVersion.equals(serviceVersion))
				{
					dialog();
				}
				else
				{
					Toast.makeText(UserSet.this, "当前版本为最新", 2000).show();
				}

				break;
			case R.id.text_out:
				UserDP.userLoginOut(UserSet.this);

				TestEvent event = new TestEvent();
				event.setLogin_out(true);
				EventBus.getDefault().post(event);

				finish();
				break;
			default:
				break;
			}

		}
	};

	public Dialog dialog2;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog dialog = null;
	public Bundle bundle;

	/**
	 * showdialog提示框
	 */
	protected void dialog()
	{
		AlertDialog.Builder builder = new Builder(UserSet.this);
		builder.setMessage(R.string.check_new_version); // 检测到有新版本，是否更新?
		builder.setTitle(R.string.tips); // 提示
		builder.setPositiveButton(R.string.update, // 更新
				new android.content.DialogInterface.OnClickListener()
				{
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int which)
					{
						File oldFile = new File(Environment
								.getExternalStorageDirectory()
								+ "/kppw/kppw_android.apk");
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
						Intent intent = new Intent(UserSet.this,
								MainActivity.class);
						UserSet.this.startActivity(intent);
						UserSet.this.finish();
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
					Toast.makeText(UserSet.this, error, 1).show();
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
					+ "/kppw/kppw_android.apk");
			if (file.exists())
			{
				file.delete();
			}
			HttpDownloader httpDownloader = new HttpDownloader();
			int result = httpDownloader.download(UserSet.this, "/kppw/",
					"kppw3_android.apk");

			if (result == 0)
			{
				Uri fileuri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory()
						+ "/kppw/kppw3_android.apk")); // 这里是APK路径
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(fileuri,
						"application/vnd.android.package-archive");
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

}
