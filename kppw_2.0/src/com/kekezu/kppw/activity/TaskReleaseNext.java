package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.TaskSerAdapter;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.OtherDP;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 任务发布
 * 
 * @author cm
 * 
 */
public class TaskReleaseNext extends Activity
{
	TextView textTitle;
	ImageView imgBack;
	EditText editBounty;
	EditText editWorker;
	TextView textBeginAt;
	TextView textDelivery;
	TextView tvSer;
	TextView tvCreate;
	TextView tvDraft;

	PopupWindow popWindow;
	View popView;
	TextView tvQU, tvQue;
	ListView listView;
	TaskSerAdapter listAdapter;
	ArrayList<HashMap<String, String>> alist;
	String strSmallNameaaa;
	String strSmallIdaaa = "";
	LinearLayout layWorker;

	String strTaskType;
	String strTitle, strDesc, strPhone, strCate, strP, strC;
	ArrayList<String> list;

	String strTaskId;
	String strNow;// 当前时间
	String strBeginAt;// 开始时间
	String strDelivery;// 截稿时间
	String strStatus;
	Intent intent;
	String string[];

	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_release_next);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		strTaskType = getIntent().getStringExtra("taskType");
		strTitle = getIntent().getStringExtra("title");
		strDesc = getIntent().getStringExtra("desc");
		strPhone = getIntent().getStringExtra("phone");
		strCate = getIntent().getStringExtra("cateid");
		strP = getIntent().getStringExtra("provinceid");
		strC = getIntent().getStringExtra("cityid");
		list = getIntent().getStringArrayListExtra("fileList");

		if (getIntent().getStringExtra("op").equals("update"))
		{
			strTaskId = getIntent().getStringExtra("task_id");
		}

		ViewInit();

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);

		layWorker = (LinearLayout) findViewById(R.id.lay_worker);
		editBounty = (EditText) findViewById(R.id.edit_task_bounty);
		editWorker = (EditText) findViewById(R.id.edit_task_worker);
		textBeginAt = (TextView) findViewById(R.id.text_task_begin_at);
		textDelivery = (TextView) findViewById(R.id.text_task_delivery);
		tvSer = (TextView) findViewById(R.id.tv_ser);
		tvCreate = (TextView) findViewById(R.id.tv_createtask);
		tvDraft = (TextView) findViewById(R.id.tv_drafttask);

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		strNow = String.format("%d-%02d-%02d", year, month, day);

		if (strTaskType.equals(TestData.taskType[1]))
		{
			editWorker.setText("1");
			layWorker.setVisibility(View.GONE);
		}

		textTitle.setText(R.string.task_releaes_title);
		imgBack.setOnClickListener(listener);
		textBeginAt.setOnClickListener(listener);
		textDelivery.setOnClickListener(listener);
		tvSer.setOnClickListener(listener);
		tvCreate.setOnClickListener(listener);
		tvDraft.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.img_back:
				onBackPressed();
				break;
			case R.id.text_task_begin_at:
				taskStartTime();
				break;
			case R.id.text_task_delivery:
				if (StrFormat.formatStr(strBeginAt))
				{
					deadlineForSubmission();
				}
				else
				{
					Toast.makeText(TaskReleaseNext.this, "您还未选择开始时间", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case R.id.tv_ser:
				showPopupWindow();
				break;
			case R.id.tv_createtask:
				strStatus = "1";
				createTask();
				break;
			case R.id.tv_drafttask:
				strStatus = "0";
				createTask();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 任务开始日期
	 */
	protected void taskStartTime()
	{
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		new DatePickerDialog(TaskReleaseNext.this,
				new DatePickerDialog.OnDateSetListener()
				{
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth)
					{
						if (strNow.compareTo(String.format("%d-%02d-%02d", year,
								monthOfYear + 1, dayOfMonth)) <= 0)
						{
							strBeginAt = String.format("%d-%02d-%02d", year,
									monthOfYear + 1, dayOfMonth);

							textBeginAt.setText(strBeginAt);
						}
						else
						{
							Toast.makeText(TaskReleaseNext.this, "开始时间不能小于当前时间",
									Toast.LENGTH_SHORT).show();
							strBeginAt = "";
							textBeginAt.setText(strBeginAt);
						}
					}
				}, year, month, day).show();
	}

	/**
	 * 截稿日期
	 */
	protected void deadlineForSubmission()
	{
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		new DatePickerDialog(TaskReleaseNext.this,
				new DatePickerDialog.OnDateSetListener()
				{
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth)
					{
						if (strBeginAt.compareTo(String.format("%d-%02d-%02d", year,
								monthOfYear + 1, dayOfMonth)) < 0)
						{
							strDelivery = String.format("%d-%02d-%02d", year,
									monthOfYear + 1, dayOfMonth);
							textDelivery.setText(strDelivery);
						}
						else
						{
							Toast.makeText(TaskReleaseNext.this, "截稿时间不能小于开始时间",
									Toast.LENGTH_SHORT).show();
							strDelivery = "";
							textDelivery.setText(strDelivery);
						}
					}
				}, year, month, day).show();
	}

	/*
	 * 增值服务
	 */
	private void showPopupWindow()
	{
		if (popWindow == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			popView = layoutInflater.inflate(R.layout.task_ser_list_pop, null);
			popWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
		}

		popWindow.setAnimationStyle(android.R.style.Animation);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);

		StrFormat.fitPopupWindowOverStatusBar(popWindow, true);

		popWindow.setBackgroundDrawable(new ColorDrawable(0xc0000000)); // 设置SelectPicPopupWindow弹出窗体的背景
		popWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow.showAtLocation(popView, 0, 0, 0);

		tvQU = (TextView) popView.findViewById(R.id.textView1);
		tvQue = (TextView) popView.findViewById(R.id.textView2);
		listView = (ListView) popView.findViewById(R.id.listView1);
		listAdapter = new TaskSerAdapter(TaskReleaseNext.this, alist, strSmallIdaaa);
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				listAdapter.choiceState(position);
			}
		});

		tvQue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				strSmallIdaaa = listAdapter.selectCheckId();
				strSmallNameaaa = listAdapter.selectCheckName();
				tvSer.setText(strSmallNameaaa);
				popWindow.dismiss();
			}
		});
		tvQU.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				popWindow.dismiss();
			}
		});

	}

	protected void createTask()
	{
		if (StrFormat.formatStr(editWorker.getText().toString())
				&& !editWorker.getText().toString().equals("0"))
		{
			if (StrFormat.formatStr(strBeginAt))
			{
				if (StrFormat.formatStr(strDelivery))
				{
					if (strTaskType.equals(TestData.taskType[0]))
					{
						if (StrFormat.formatStr(editBounty.getText().toString())
								&& !editBounty.getText().toString().equals("0"))
						{
							progressDialog = new LoadingDialog(TaskReleaseNext.this);
							progressDialog.setMessage("保存数据中。。。");
							progressDialog.setCancelable(false);
							progressDialog.show();

							Thread t = new Thread(newTread);
							t.start();
						}
						else
						{
							Toast.makeText(this, "请填入任务赏金", Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						progressDialog = new LoadingDialog(TaskReleaseNext.this);
						progressDialog.setMessage("保存数据中。。。");
						progressDialog.setCancelable(false);
						progressDialog.show();

						Thread t = new Thread(newTread);
						t.start();
					}
				}
				else
				{
					Toast.makeText(this, "请选择截止时间", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, "请填入中标人数", Toast.LENGTH_SHORT).show();
		}
	}

	Runnable newTread = new Runnable()
	{
		String strRe = "";

		@Override
		public void run()
		{
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					strRe = strRe + OtherDP.uploadImg(list.get(i), TaskReleaseNext.this)
							+ ",";
				}
				strRe = strRe.substring(0, strRe.length() - 1);
			}

			string = TaskDP.creationTask(strTaskId, strTitle, strDesc, strCate,
					editBounty.getText().toString(), editWorker.getText().toString(),
					strP, strC, strDelivery, strStatus, strRe, strBeginAt, strPhone,
					strTaskType, strSmallIdaaa, TaskReleaseNext.this);

			Message msg = mHandler.obtainMessage(1, string);
			msg.sendToTarget();
		}
	};

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			alist = TaskDP.getTaskService(TaskReleaseNext.this);
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
				String str[] = (String[]) msg.obj;
				if (str[0].equals("1000"))
				{
					if (strStatus.equals("1"))
					{
						if (getIntent().getStringExtra("op").equals("update"))
						{
							if (strTaskType.equals(TestData.taskType[1]))
							{
								if (StrFormat.formatStr(strSmallIdaaa))
								{
									intent = new Intent(TaskReleaseNext.this,
											TaskReleasePay.class);
									intent.putExtra("strResult", str[2]);
									startActivity(intent);
								}
								TestEvent event = new TestEvent();
								event.setIstaskrelease(true);
								event.setTaskDraft(true);
								EventBus.getDefault().post(event);
								finish();
							}
							else
							{
								intent = new Intent(TaskReleaseNext.this,
										TaskReleasePay.class);
								intent.putExtra("strResult", str[2]);
								startActivity(intent);
								TestEvent event = new TestEvent();
								event.setIstaskrelease(true);
								event.setTaskDraft(true);
								EventBus.getDefault().post(event);
								finish();
							}
						}
						else
						{
							if (strTaskType.equals(TestData.taskType[1]))
							{
								if (StrFormat.formatStr(strSmallIdaaa))
								{
									intent = new Intent(TaskReleaseNext.this,
											TaskReleasePay.class);
									intent.putExtra("strResult", str[2]);
									startActivity(intent);
								}
								TestEvent event = new TestEvent();
								event.setIstaskrelease(true);
								EventBus.getDefault().post(event);
								finish();
							}
							else
							{
								intent = new Intent(TaskReleaseNext.this,
										TaskReleasePay.class);
								intent.putExtra("strResult", str[2]);
								startActivity(intent);
								TestEvent event = new TestEvent();
								event.setIstaskrelease(true);
								EventBus.getDefault().post(event);
								finish();
							}
						}
					}
					else
					{
						TestEvent event = new TestEvent();
						event.setIstaskrelease(true);
						EventBus.getDefault().post(event);
						finish();
					}
				}
				progressDialog.dismiss();
				Toast.makeText(TaskReleaseNext.this, str[1], 2000).show();
				break;
			}
		}
	};

}
