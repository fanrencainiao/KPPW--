package com.kekezu.kppw.adapter;

import java.util.List;
import net.tsz.afinal.FinalDb;
import com.kekezu.kppw.R;
import com.kekezu.kppw.activity.MainActivity;
import com.kekezu.kppw.bean.UserType;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.CityDP;
import com.kekezu.kppw.utils.ConfigInc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 *     class desc: 引导页面适配器
 * 
 * 
 * 
 */
public class ViewPagerAdapter extends PagerAdapter
{
	// 界面列表
	private List<View> views;
	private Activity activity;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	LoadingDialog progressDialog;

	public ViewPagerAdapter(List<View> views, Activity activity)
	{
		this.views = views;
		this.activity = activity;
	}

	// 销毁arg1位置的界面
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2)
	{
		((ViewPager) arg0).removeView(views.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0)
	{
	}

	// 获得当前界面数
	@Override
	public int getCount()
	{
		if (views != null)
		{
			return views.size();
		}
		return 0;
	}

	// 初始化arg1位置的界面
	@Override
	public Object instantiateItem(View arg0, int arg1)
	{
		((ViewPager) arg0).addView(views.get(arg1), 0);
		if (arg1 == views.size() - 1)
		{
			TextView text_guzhu = (TextView) arg0.findViewById(R.id.text_guzhu);

			TextView text_witkey = (TextView) arg0.findViewById(R.id.text_witkey);

			final FinalDb db = ConfigInc.getCreateDB(activity);
			final UserType testBean = new UserType();

			text_guzhu.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// 设置已经引导
					setGuided();
					testBean.setUserType(0);
					db.save(testBean);

					progressDialog = new LoadingDialog(activity);
					progressDialog.show();
					progressDialog.setCancelable(false);
					Thread t = new Thread(newTread1);
					t.start();

				}
			});

			text_witkey.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// 设置已经引导
					setGuided();
					testBean.setUserType(1);
					db.save(testBean);

					progressDialog = new LoadingDialog(activity);
					progressDialog.setMessage("正在加载数据，请稍等");
					progressDialog.show();
					progressDialog.setCancelable(false);
					Thread t = new Thread(newTread1);
					t.start();
				}
			});
		}
		return views.get(arg1);
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			CityDP.getCity(activity);
			Message msg = mHandler.obtainMessage(1);
			msg.sendToTarget();
		}
	};

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case 1:
				progressDialog.dismiss();
				goHome();
				break;
			}
		}
	};

	private void goHome()
	{
		// 跳转
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 
	 * method desc：设置已经引导过了，下次启动不用再次引导
	 */
	private void setGuided()
	{
		SharedPreferences preferences = activity.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// 存入数据
		editor.putBoolean("isFirstIn", false);
		// 提交修改
		editor.commit();
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1)
	{
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	@Override
	public void startUpdate(View arg0)
	{
	}

}
