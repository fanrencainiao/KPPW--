package com.kekezu.kppw.activity;

import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import net.tsz.afinal.FinalDb;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.IYWConversationUnreadChangeListener;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.bean.UserBean;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.dataprocess.UserDP;
import com.kekezu.kppw.imcustom.LoginSampleHelper;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener
{
	FragmentIndex fragmentIndex;
	FragmentMsg fragmentMsg;
	FragmentUser fragmentUser;
	FragmentTask fragmentTaskHall;
	FragmentMall fragmentMall;

	FragmentManager fManager;
	FrameLayout flayout;

	LinearLayout indexLayout;
	LinearLayout taskLayout;
	RelativeLayout msgLayout;
	LinearLayout userLayout;
	LinearLayout layout5;

	ImageView imgIndex;
	ImageView imgTask;
	ImageView imgMsg;
	ImageView imgUser;
	TextView tv1, tv2, tv3, tv4;

	ImageView imgTaskRelease;
	PopupWindow popWindow;
	View popView;
	LinearLayout layZhao, layXuan;
	ImageView imgTaskNull;

	FinalDb db;
	List<UserBean> users;
	Intent intent;
	
	TextView tvCount;
	YWIMKit mIMKit;
	IYWConversationService mConversationService;
	private IYWConversationUnreadChangeListener mConversationUnreadChangeListener;
	private Handler mHandler = new Handler(Looper.getMainLooper());

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
		setContentView(R.layout.activity_main222);
		// App.initImageLoader(this);
		
		mIMKit = LoginSampleHelper.getInstance().getIMKit();
		mConversationService = mIMKit.getConversationService();
		initConversationServiceAndListener();

		fManager = getSupportFragmentManager();
		db = FinalDb.create(this, getResources().getString(R.string.db_name));
		UserDP.ReLogin(this);
		initViews();

		EventBus.getDefault().register(this);
		setChioceItem(0);
		// CityDP.getCity(this);
	}

	public void initViews()
	{
		indexLayout = (LinearLayout) findViewById(R.id.tab_1);
		taskLayout = (LinearLayout) findViewById(R.id.tab_2);
		msgLayout = (RelativeLayout) findViewById(R.id.tab_3);
		userLayout = (LinearLayout) findViewById(R.id.tab_4);
		tvCount = (TextView) findViewById(R.id.unread);
		layout5 = (LinearLayout) findViewById(R.id.tab_5);

		imgIndex = (ImageView) findViewById(R.id.image_index);
		imgTask = (ImageView) findViewById(R.id.Image_task);
		imgMsg = (ImageView) findViewById(R.id.Image_msg);
		imgUser = (ImageView) findViewById(R.id.Image_user);
		tv1 = (TextView) findViewById(R.id.tv_1);
		tv2 = (TextView) findViewById(R.id.tv_2);
		tv3 = (TextView) findViewById(R.id.tv_3);
		tv4 = (TextView) findViewById(R.id.tv_4);

		imgTaskRelease = (ImageView) findViewById(R.id.img_task_release);

		indexLayout.setOnClickListener(this);
		taskLayout.setOnClickListener(this);
		msgLayout.setOnClickListener(this);
		userLayout.setOnClickListener(this);
		imgTaskRelease.setOnClickListener(this);

		Tab2Change();
	}

	// 定义一个选中一个item后的处理
	public void setChioceItem(int index)
	{
		// 重置选项+隐藏所有Fragment
		FragmentTransaction transaction = fManager.beginTransaction();
		// clearChioce();
		hideFragments(transaction);
		switch (index)
		{
		case 0:

			if (fragmentIndex == null)
			{
				// 如果fg1为空，则创建一个并添加到界面上
				fragmentIndex = new FragmentIndex();
				transaction.add(R.id.center_boby, fragmentIndex);
			}
			else
			{
				// 如果fg1存在，则删除之前的，重新创建一个并添加到界面上
				// transaction.remove(fragmentIndex);
				// fragmentIndex = new FragmentIndex();
				// transaction.add(R.id.center_boby, fragmentIndex);
				transaction.show(fragmentIndex);
				StatusBarUtil.StatusBarDarkMode(getWindow());
			}

			break;

		case 1:
			if (TestData.getuserType(MainActivity.this) == 0)
			{
				imgTaskRelease.setVisibility(View.VISIBLE);
				layout5.setVisibility(View.VISIBLE);
				if (fragmentMall == null)
				{
					// 如果fg1为空，则创建一个并添加到界面上
					fragmentMall = new FragmentMall();
					transaction.add(R.id.center_boby, fragmentMall);
					StatusBarUtil.setStatusBarLightMode(getWindow());
				}
				else
				{
					// 如果MessageFragment不为空，则直接将它显示出来
					transaction.show(fragmentMall);
					StatusBarUtil.setStatusBarLightMode(getWindow());
				}
			}
			else
			{
				imgTaskRelease.setVisibility(View.GONE);
				layout5.setVisibility(View.GONE);
				if (fragmentTaskHall == null)
				{
					fragmentTaskHall = new FragmentTask();
					transaction.add(R.id.center_boby, fragmentTaskHall);
					StatusBarUtil.setStatusBarLightMode(getWindow());
				}
				else
				{
					// 如果MessageFragment不为空，则直接将它显示出来
					transaction.show(fragmentTaskHall);
					StatusBarUtil.setStatusBarLightMode(getWindow());
				}
			}

			break;

		case 2:
			if (fragmentMsg == null)
			{
				// 如果fg1为空，则创建一个并添加到界面上
				fragmentMsg = new FragmentMsg();
				transaction.add(R.id.center_boby, fragmentMsg);
				StatusBarUtil.setStatusBarLightMode(getWindow());
			}
			else
			{
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(fragmentMsg);
				StatusBarUtil.setStatusBarLightMode(getWindow());
			}
			break;
		case 3:
			if (fragmentUser == null)
			{
				// 如果fg1为空，则创建一个并添加到界面上
				fragmentUser = new FragmentUser();
				transaction.add(R.id.center_boby, fragmentUser);
			}
			else
			{
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(fragmentUser);
			}

			break;

		default:
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	// 隐藏所有的Fragment,避免fragment混乱
	private void hideFragments(FragmentTransaction transaction)
	{
		if (fragmentIndex != null)
		{
			transaction.hide(fragmentIndex);
		}
		if (TestData.getuserType(this) == 0)
		{
			imgTaskRelease.setVisibility(View.VISIBLE);
			layout5.setVisibility(View.VISIBLE);
			if (fragmentMall != null)
			{
				transaction.hide(fragmentMall);
			}
		}
		else
		{
			imgTaskRelease.setVisibility(View.GONE);
			layout5.setVisibility(View.GONE);
			if (fragmentTaskHall != null)
			{
				transaction.hide(fragmentTaskHall);
			}
		}

		if (fragmentMsg != null)
		{
			transaction.hide(fragmentMsg);
		}
		if (fragmentUser != null)
		{
			transaction.hide(fragmentUser);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tab_1:
			setChioceItem(0);
			setTabColor(0);
			break;
		case R.id.tab_2:
			setChioceItem(1);
			setTabColor(1);
			break;
		case R.id.tab_3:

			users = db.findAll(UserBean.class);
			if (users.size() > 0)
			{
				setChioceItem(2);
				setTabColor(2);
			}
			else
			{
				new AlertDialog.Builder(MainActivity.this)
						.setMessage("你还没有登录，是否去登录")
						// 设置显示的内容
						.setPositiveButton("确定", new DialogInterface.OnClickListener()
						{// 添加确定按钮
									@Override
									public void onClick(DialogInterface dialog, int which)
									{// 确定按钮的响应事件
										Intent intent = new Intent(MainActivity.this,
												Login.class);
										startActivity(intent);
									}
								})
						.setNegativeButton("取消", new DialogInterface.OnClickListener()
						{// 添加返回按钮
									@Override
									public void onClick(DialogInterface dialog, int which)
									{// 响应事件

									}

								}).show();// 在按键响应事件中显示此对话框
			}

			break;
		case R.id.tab_4:
			users = db.findAll(UserBean.class);
			if (users.size() == 0)
			{
				new AlertDialog.Builder(MainActivity.this)
						.setMessage("你还没有登录，是否去登录")
						// 设置显示的内容
						.setPositiveButton("确定", new DialogInterface.OnClickListener()
						{// 添加确定按钮
									@Override
									public void onClick(DialogInterface dialog, int which)
									{// 确定按钮的响应事件
										Intent intent = new Intent(MainActivity.this,
												Login.class);

										startActivityForResult(intent, 88);
									}
								})
						.setNegativeButton("取消", new DialogInterface.OnClickListener()
						{// 添加返回按钮
									@Override
									public void onClick(DialogInterface dialog, int which)
									{// 响应事件

									}

								}).show();// 在按键响应事件中显示此对话框
			}
			else
			{
				setChioceItem(3);
				setTabColor(3);
			}
			break;
		case R.id.img_task_release:
			users = db.findAll(UserBean.class);
			if (users.size() == 0)
			{
				new AlertDialog.Builder(MainActivity.this)
						.setMessage("你还没有登录，是否去登录")
						// 设置显示的内容
						.setPositiveButton("确定", new DialogInterface.OnClickListener()
						{// 添加确定按钮
									@Override
									public void onClick(DialogInterface dialog, int which)
									{// 确定按钮的响应事件
										Intent intent = new Intent(MainActivity.this,
												Login.class);
										startActivity(intent);
									}
								})
						.setNegativeButton("取消", new DialogInterface.OnClickListener()
						{// 添加返回按钮
									@Override
									public void onClick(DialogInterface dialog, int which)
									{// 响应事件

									}

								}).show();// 在按键响应事件中显示此对话框
			}
			else
			{
				showPopupWindow();
			}
			break;
		case R.id.img_release:
			popWindow.dismiss();
			popWindow = null;
			break;
		case R.id.lay_zhaobiao:
			intent = new Intent(MainActivity.this, TaskRelease.class);
			intent.putExtra("taskType", TestData.taskType[1]);
			intent.putExtra("op", "add");
			startActivity(intent);
			popWindow.dismiss();
			break;
		case R.id.lay_xuanshang:
			intent = new Intent(MainActivity.this, TaskRelease.class);
			intent.putExtra("taskType", TestData.taskType[0]);
			intent.putExtra("op", "add");
			startActivity(intent);
			popWindow.dismiss();
			break;
		default:
			break;
		}
	}

	// 切换选项卡的ui变化
	@SuppressWarnings("deprecation")
	private void setTabColor(int index)
	{
		switch (index)
		{
		case 0:
			imgIndex.setImageDrawable(getResources().getDrawable(R.drawable.tab_1_p));
			Tab2Change();
			imgMsg.setImageDrawable(getResources().getDrawable(R.drawable.tab_3_n));
			imgUser.setImageDrawable(getResources().getDrawable(R.drawable.tab_4_n));

			tv1.setTextColor(getResources().getColor(R.color.header_bg));
			tv2.setTextColor(getResources().getColor(R.color.tab_light));
			tv3.setTextColor(getResources().getColor(R.color.tab_light));
			tv4.setTextColor(getResources().getColor(R.color.tab_light));
			break;
		case 1:
			imgIndex.setImageDrawable(getResources().getDrawable(R.drawable.tab_1_n));
			if (TestData.getuserType(MainActivity.this) == 0)
			{
				imgTask.setImageDrawable(getResources().getDrawable(R.drawable.tab_2_p));
				tv2.setText("找人才");
			}
			else
			{
				imgTask.setImageDrawable(getResources().getDrawable(R.drawable.tab_5_p));
				tv2.setText("任务大厅");
			}
			imgMsg.setImageDrawable(getResources().getDrawable(R.drawable.tab_3_n));
			imgUser.setImageDrawable(getResources().getDrawable(R.drawable.tab_4_n));

			tv1.setTextColor(getResources().getColor(R.color.tab_light));
			tv2.setTextColor(getResources().getColor(R.color.header_bg));
			tv3.setTextColor(getResources().getColor(R.color.tab_light));
			tv4.setTextColor(getResources().getColor(R.color.tab_light));
			break;
		case 2:
			imgIndex.setImageDrawable(getResources().getDrawable(R.drawable.tab_1_n));
			Tab2Change();
			imgMsg.setImageDrawable(getResources().getDrawable(R.drawable.tab_3_p));
			imgUser.setImageDrawable(getResources().getDrawable(R.drawable.tab_4_n));

			tv1.setTextColor(getResources().getColor(R.color.tab_light));
			tv2.setTextColor(getResources().getColor(R.color.tab_light));
			tv3.setTextColor(getResources().getColor(R.color.header_bg));
			tv4.setTextColor(getResources().getColor(R.color.tab_light));
			break;
		case 3:
			imgIndex.setImageDrawable(getResources().getDrawable(R.drawable.tab_1_n));
			Tab2Change();
			imgMsg.setImageDrawable(getResources().getDrawable(R.drawable.tab_3_n));
			imgUser.setImageDrawable(getResources().getDrawable(R.drawable.tab_4_p));

			tv1.setTextColor(getResources().getColor(R.color.tab_light));
			tv2.setTextColor(getResources().getColor(R.color.tab_light));
			tv3.setTextColor(getResources().getColor(R.color.tab_light));
			tv4.setTextColor(getResources().getColor(R.color.header_bg));
			break;
		default:
			break;
		}
	}

	// 身份切换的选项卡ui变化
	@SuppressWarnings("deprecation")
	public void Tab2Change()
	{
		if (TestData.getuserType(this) == 0)
		{
			imgTask.setImageDrawable(getResources().getDrawable(R.drawable.tab_2_n));
			layout5.setVisibility(View.VISIBLE);
			tv2.setText("找人才");
		}
		else
		{
			imgTask.setImageDrawable(getResources().getDrawable(R.drawable.tab_5_n));
			layout5.setVisibility(View.GONE);
			tv2.setText("任务大厅");
		}
	}

	/*
	 * 发布商品
	 */
	private void showPopupWindow()
	{
		if (popWindow == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			popView = layoutInflater.inflate(R.layout.task_release_pop, null);

			popWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
		}

		popWindow.setAnimationStyle(android.R.style.Animation);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);

		StrFormat.fitPopupWindowOverStatusBar(popWindow, true);

		// 实例化一个ColorDrawable颜色白色,设置SelectPicPopupWindow弹出窗体的背景
		popWindow.setBackgroundDrawable(new ColorDrawable(0xc0000000));

		popWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow.showAtLocation(popView, 0, 0, 0);

		layZhao = (LinearLayout) popView.findViewById(R.id.lay_zhaobiao);
		layXuan = (LinearLayout) popView.findViewById(R.id.lay_xuanshang);
		imgTaskNull = (ImageView) popView.findViewById(R.id.img_release);

		layZhao.setOnClickListener(this);
		layXuan.setOnClickListener(this);
		imgTaskNull.setOnClickListener(this);
	}

	// 登录后的跳转
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.e("11111111111", "" + requestCode);
		try
		{
			switch (resultCode)
			{
			case 88:
				setChioceItem(0);
				setTabColor(0);
				break;
			default:
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Subscribe
	public void onEvent(TestEvent event)
	{
		// 切换身份的刷新
		if (event.isRefesh())
		{
			setChioceItem(3);
			setTabColor(3);
		}

		if (event.isGetMore())
		{
			setChioceItem(1);
			setTabColor(1);
		}

		// 退出登录的刷新
		if (event.isLogin_out())
		{
			setChioceItem(0);
			setTabColor(0);
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	private void initConversationServiceAndListener()
	{
		mConversationUnreadChangeListener = new IYWConversationUnreadChangeListener()
		{
			// 当未读数发生变化时会回调该方法，开发者可以在该方法中更新未读数
			@Override
			public void onUnreadChange()
			{
				mHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						mConversationService = mIMKit.getConversationService();
						// 获取当前登录用户的所有未读数
						int unReadCount = mConversationService
								.getAllUnreadCount();
						// 设置桌面角标的未读数
						// mIMKit.setShortcutBadger(unReadCount);
						if (unReadCount > 0)
						{
							tvCount.setVisibility(View.VISIBLE);
							if (unReadCount < 100)
							{
								tvCount.setText(unReadCount + "");
							}
							else
							{
								tvCount.setText("99+");
							}
						}
						else
						{
							tvCount.setVisibility(View.GONE);
						}
					}
				});
			}
		};
		mConversationService
				.addTotalUnreadChangeListener(mConversationUnreadChangeListener);
	}

	// 退出时的时间
	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			if ((System.currentTimeMillis() - mExitTime) > 2000)
			{
				Toast.makeText(MainActivity.this, "再按一次退出", 1800).show();
				mExitTime = System.currentTimeMillis();
			}
			else
			{
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
