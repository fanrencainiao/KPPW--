package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RoundTransform;
import com.kekezu.kppw.dataprocess.ManuscriptDP;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 稿件详情
 * 
 * @author cm
 * 
 */
public class TaskManuscriptDetails extends Activity
{
	ImageView imgBack;
	TextView textTitle;
	ImageView imgAvatar;
	TextView tvName, tvRate, tvComplete, tvDesc;
	ImageView img1, img2, img3, img4;
	TextView tvSubmit;
	TextView tvWeiQuan;
	TextView tvJujue;
	Intent intent;

	Map<String, String> workMap;
	String strWordId;
	String strTaskType;
	String strTaskId;
	String status = "";

	LoadingDialog progressDialog;
	LinearLayout layFujian;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_manuscript_details);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		strWordId = getIntent().getStringExtra("work_id");
		strTaskId = getIntent().getStringExtra("task_id");
		strTaskType = getIntent().getStringExtra("task_type");
		ViewInit();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();

		EventBus.getDefault().register(this);
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		imgAvatar = (ImageView) findViewById(R.id.img_avatar);
		tvName = (TextView) findViewById(R.id.text_name);
		tvRate = (TextView) findViewById(R.id.text_applauseRate);
		tvComplete = (TextView) findViewById(R.id.text_complete);
		tvDesc = (TextView) findViewById(R.id.text_desc);
		img1 = (ImageView) findViewById(R.id.imageView3);
		img2 = (ImageView) findViewById(R.id.imageView4);
		img3 = (ImageView) findViewById(R.id.imageView5);
		img4 = (ImageView) findViewById(R.id.imageView6);
		tvSubmit = (TextView) findViewById(R.id.btn_task_tougao);
		tvWeiQuan = (TextView) findViewById(R.id.tv_weiquan);
		tvJujue = (TextView) findViewById(R.id.tv_jujue);
		layFujian = (LinearLayout) findViewById(R.id.lay_fujian);

		textTitle.setText("稿件详情");
		imgBack.setOnClickListener(listener);
		tvSubmit.setOnClickListener(listener);
		tvWeiQuan.setOnClickListener(listener);
		tvJujue.setOnClickListener(listener);
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
			case R.id.tv_weiquan:
				// 维权
				intent = new Intent(TaskManuscriptDetails.this, TaskRights.class);
				intent.putExtra("id", strWordId);
				intent.putExtra("task_id", strTaskId);
				intent.putExtra("aaa", "3");
				startActivity(intent);
				break;
			case R.id.tv_jujue:
				status = "2";
				caozuo();
				break;
			case R.id.btn_task_tougao:
				status = "1";
				caozuo();
				break;
			default:
				break;
			}
		}
	};

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			workMap = ManuscriptDP.getWorkDetails(strWordId, TaskManuscriptDetails.this);
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
				if (workMap.get("code").equals("1000"))
				{
					progressDialog.dismiss();
					setViewValue();
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(TaskManuscriptDetails.this, workMap.get("message"),
							2000).show();
					finish();
				}
				break;
			}
		}
	};

	private void setViewValue()
	{
		Glide.with(this).load(workMap.get("avatar")).placeholder(R.drawable.erha)
				.error(R.drawable.erha).into(imgAvatar);
		tvName.setText(workMap.get("nickname"));
		tvRate.setText(workMap.get("applauseRate"));
		tvComplete.setText(workMap.get("complete"));
		tvDesc.setText(Html.fromHtml(workMap.get("desc")));

		setTaskAttachment();

		if (workMap.get("realname").toString().equals("1"))
		{
			Glide.with(this).load(R.drawable.cert).into(img1);
		}
		if (workMap.get("email").toString().equals("1"))
		{
			Glide.with(this).load(R.drawable.cert2_gray).into(img2);
		}
		if (workMap.get("alipay").toString().equals("1"))
		{

		}
		if (workMap.get("isEnterprise").toString().equals("1"))
		{
			Glide.with(this).load(R.drawable.cert4_gray).into(img4);
		}

		if (workMap.get("button_status").equals("0"))
		{
			tvSubmit.setVisibility(View.GONE);
		}
		else if (workMap.get("button_status").equals("1"))
		{

		}
		else if (workMap.get("button_status").equals("2"))
		{
			tvWeiQuan.setVisibility(View.VISIBLE);
			tvSubmit.setText("验收");
			if (strTaskType.equals("zhaobiao"))
			{
				tvJujue.setVisibility(View.VISIBLE);
			}
		}
		else if (workMap.get("button_status").equals("3"))
		{
			tvSubmit.setText("评价");
		}
	}

	protected void caozuo()
	{
		if (workMap.get("button_status").equals("1"))
		{
			String str[] = ManuscriptDP.putWinBid(strWordId, this);

			// 对返回的数据进行判断
			if (str[0].equals("1000"))
			{
				Toast.makeText(this, "等待对方交付", Toast.LENGTH_SHORT).show();
				TestEvent event = new TestEvent();
				event.setManu_add(true);
				EventBus.getDefault().post(event);
				finish();
			}
			else
			{
				Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
			}
		}
		else if (workMap.get("button_status").equals("2"))
		{
			if (strTaskType.equals("zhaobiao"))
			{
				String str[] = ManuscriptDP.deliveryAgree(strWordId, status, this);

				// 对返回的数据进行判断
				if (str[0].equals("1000"))
				{
					TestEvent event = new TestEvent();
					event.setManu_add(true);
					EventBus.getDefault().post(event);
					finish();

					if (StrFormat.formatStr(status))
					{
						if (status.equals("1"))
						{
							Toast.makeText(this, "当前阶段已验收", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Toast.makeText(this, "提交成功，等待对方重新提交", Toast.LENGTH_SHORT)
									.show();
						}
					}
					else
					{
						Toast.makeText(this, "验收成功", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				String str[] = ManuscriptDP.deliveryAgree(strWordId, "", this);

				// 对返回的数据进行判断
				if (str[0].equals("1000"))
				{
					TestEvent event = new TestEvent();
					event.setManu_add(true);
					EventBus.getDefault().post(event);

					finish();
				}
				else
				{
					Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
				}

			}
		}
		else if (workMap.get("button_status").equals("3"))
		{
			if (strTaskType.equals("xuanshang"))
			{
				intent = new Intent(TaskManuscriptDetails.this, Evaluate.class);
				intent.putExtra("type", "task");
				intent.putExtra("role", "employer");
				intent.putExtra("task_id", workMap.get("task_id"));
				intent.putExtra("work_id", workMap.get("id"));
				intent.putExtra("name", workMap.get("nickname"));
				intent.putExtra("pic", workMap.get("avatar"));
				startActivity(intent);
				finish();
			}
			else
			{
				Toast.makeText(TaskManuscriptDetails.this, "当前任务模式需到交付详情页评价", 2000);
			}
		}
	}

	private void setTaskAttachment()
	{
		final ArrayList<HashMap<String, Object>> attachmentList = TaskDP
				.getAttachment(workMap.get("attachment"));
		Log.e("attachmentList", "" + attachmentList);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		float density = displayMetrics.density;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int) (48 * density), (int) (48 * density));
		params.leftMargin = 36;

		for (int i = 0; i < attachmentList.size(); i++)
		{
			ImageView newImg = new ImageView(this);
			Glide.with(this).load(attachmentList.get(i).get("url"))
					.error(R.drawable.ic_fujian).transform(new RoundTransform(this, 3))
					.into(newImg);
			layFujian.addView(newImg, params);

			final int finalI = i;

			newImg.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (attachmentList.get(finalI).get("type").equals("png")
							|| attachmentList.get(finalI).get("type").equals("gif")
							|| attachmentList.get(finalI).get("type").equals("jpg")
							|| attachmentList.get(finalI).get("type").equals("jepg"))
					{
						imgDialogShow(TaskManuscriptDetails.this,
								attachmentList.get(finalI).get("url").toString());
					}
					else
					{
						new AlertDialog.Builder(TaskManuscriptDetails.this)
								.setMessage("是否确认下载")
								// 设置显示的内容
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener()
										{// 添加确定按钮
											@Override
											public void onClick(DialogInterface dialog,
													int which)
											{// 确定按钮的响应事件
												StrFormat.getDownFile(
														TaskManuscriptDetails.this,
														attachmentList.get(finalI)
																.get("url").toString(),
														attachmentList.get(finalI)
																.get("name").toString());
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener()
										{// 添加返回按钮
											@Override
											public void onClick(DialogInterface dialog,
													int which)
											{// 响应事件

											}

										}).show();// 在按键响应事件中显示此对话框
					}
				}
			});
		}
	}

	private PopupWindow popImg;
	View popView;
	ImageView imgView;

	protected void imgDialogShow(Context context, String string)
	{
		if (popImg == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			popView = layoutInflater.inflate(R.layout.image_dialog, null);
			popImg = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
		}

		popImg.setAnimationStyle(android.R.style.Animation);
		popImg.setFocusable(true);
		popImg.setOutsideTouchable(true);
		StrFormat.fitPopupWindowOverStatusBar(popImg, true);
		// 实例化一个ColorDrawable颜色白色,设置SelectPicPopupWindow弹出窗体的背景
		popImg.setBackgroundDrawable(new ColorDrawable(0xb0000000));
		popImg.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popImg.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popImg.showAtLocation(popView, 0, 0, 0);

		imgView = (ImageView) popView.findViewById(R.id.imageView1);
		Glide.with(context).load(string).error(R.drawable.ic_fujian).into(imgView);
		imgView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				popImg.dismiss();
			}
		});
	}

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isManu_add())
		{
			finish();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
