package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.TaskSectionAdapter;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 确认报价阶段
 * 
 * @author cm
 * 
 */
public class TaskPriceDetails extends Activity
{
	ImageView imgBack;
	TextView textTitle;

	TextView tvCash, tvPayType, tv1, tv2;
	Spinner spinner;
	ListView listView;

	Map<String, String> resMap;
	ArrayList<HashMap<String, Object>> sectionList;
	Intent intent;
	String strTaskId;
	LoadingDialog progressDialog;
	TaskSectionAdapter adapter;

	private List<String> data_list;
	private ArrayAdapter<String> arr_adapter;
	int intType = 1;
	ViewGroup.LayoutParams params;
	String[] string = { "一", "二", "三", "四", "五" };
	String strAppend = "";
	String again = " ";
	Dialog dialog1;
	Thread t;

	String str;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_price_details);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		strTaskId = getIntent().getStringExtra("taskid");
		ViewInit();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		tvCash = (TextView) findViewById(R.id.tv_cash);
		tvPayType = (TextView) findViewById(R.id.tv_paytype);
		spinner = (Spinner) findViewById(R.id.spinner1);
		listView = (ListView) findViewById(R.id.listView1);
		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);

		textTitle.setText("确认付款阶段");
		imgBack.setOnClickListener(listener);
		tv1.setOnClickListener(listener);
		tv2.setOnClickListener(listener);
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
			case R.id.tv1:
				str = "2";
				progressDialog = new LoadingDialog(TaskPriceDetails.this);
				progressDialog.show();
				progressDialog.setCancelable(false);
				t = new Thread(newTread3);
				t.start();
				break;
			case R.id.tv2:
				if (resMap.get("role").equals("employer"))
				{
					if (resMap.get("pay_type_status").equals("0"))
					{
						progressDialog = new LoadingDialog(TaskPriceDetails.this);
						progressDialog.show();
						progressDialog.setCancelable(false);

						t = new Thread(newTread2);
						t.start();
					}
					else if (resMap.get("pay_type_status").equals("2"))
					{
						progressDialog = new LoadingDialog(TaskPriceDetails.this);
						progressDialog.show();
						progressDialog.setCancelable(false);

						t = new Thread(newTread2);
						t.start();
					}
				}
				else
				{
					str = "1";
					progressDialog = new LoadingDialog(TaskPriceDetails.this);
					progressDialog.show();
					progressDialog.setCancelable(false);
					t = new Thread(newTread3);
					t.start();
				}
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
			resMap = TaskDP.paySection(strTaskId, TaskPriceDetails.this);
			sectionList = TaskDP.getPaySection(resMap.get("pay_section"));
			Message msg = tHandler.obtainMessage(1);
			msg.sendToTarget();
		}
	};

	String[] strRes;
	Runnable newTread2 = new Runnable()
	{
		@Override
		public void run()
		{
			String strDesc = "";
			for (int i = 0; i < sectionList.size(); i++)
			{
				View view = listView.getChildAt(i);
				EditText etDesc = (EditText) view.findViewById(R.id.editText1);
				strDesc = strDesc + etDesc.getText().toString() + ":";
			}

			strRes = TaskDP.postPayType(strTaskId, intType,
					strDesc.substring(0, strDesc.length() - 1), strAppend, again,
					TaskPriceDetails.this);
			Message msg = tHandler.obtainMessage(2);
			msg.sendToTarget();
		}
	};

	Runnable newTread3 = new Runnable()
	{
		@Override
		public void run()
		{
			strRes = TaskDP.dealPayType(strTaskId, str, TaskPriceDetails.this);
			Message msg = tHandler.obtainMessage(2);
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
				setViewValue();
				progressDialog.dismiss();
				break;
			case 2:
				if (strRes[0].equals("1000"))
				{
					progressDialog.dismiss();
					TestEvent event = new TestEvent();
					event.setManu_add(true);
					EventBus.getDefault().post(event);
					finish();
					Toast.makeText(TaskPriceDetails.this, "提交成功", 2000).show();
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(TaskPriceDetails.this, strRes[1], 2000).show();
				}
				break;
			}
		}
	};

	private void setViewValue()
	{
		tvCash.setText(resMap.get("task_bounty") + "元");
		// 数据
		data_list = new ArrayList<String>();
		data_list.add("一次性");
		data_list.add("50:50");
		data_list.add("50:30:20");
		data_list.add("自定义");

		// 适配器
		arr_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, data_list);
		// 加载适配器
		spinner.setAdapter(arr_adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id)
			{
				if (position == 0)
				{
					intType = position + 1;
					sectionList.clear();
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("name", "第一阶段");
					map.put("percent", "100");
					map.put("price", resMap.get("task_bounty"));
					sectionList.add(map);

					adapter.notifyDataSetChanged();
					params = StrFormat.getListViewParams(listView);
					listView.setLayoutParams(params);
				}
				else if (position == 1)
				{
					intType = position + 1;
					sectionList.clear();
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("name", "第一阶段");
					map.put("percent", "50");
					map.put("price", Float.valueOf(resMap.get("task_bounty")) * 0.5);
					sectionList.add(map);

					HashMap<String, Object> map1 = new HashMap<String, Object>();
					map1.put("name", "第二阶段");
					map1.put("percent", "50");
					map1.put("price", Float.valueOf(resMap.get("task_bounty")) * 0.5);
					sectionList.add(map1);

					adapter.notifyDataSetChanged();
					params = StrFormat.getListViewParams(listView);
					listView.setLayoutParams(params);
				}
				else if (position == 2)
				{
					intType = position + 1;
					sectionList.clear();
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("name", "第一阶段");
					map.put("percent", "50");
					map.put("price", Float.valueOf(resMap.get("task_bounty")) * 0.5);
					sectionList.add(map);

					HashMap<String, Object> map1 = new HashMap<String, Object>();
					map1.put("name", "第二阶段");
					map1.put("percent", "30");
					map1.put("price", Float.valueOf(resMap.get("task_bounty")) * 0.3);
					sectionList.add(map1);

					HashMap<String, Object> map2 = new HashMap<String, Object>();
					map2.put("name", "第三阶段");
					map2.put("percent", "20");
					map2.put("price", Float.valueOf(resMap.get("task_bounty")) * 0.2);
					sectionList.add(map2);

					adapter.notifyDataSetChanged();
					params = StrFormat.getListViewParams(listView);
					listView.setLayoutParams(params);
				}
				else if (position == 3)
				{
					customSection();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		if (resMap.get("pay_type").equals("1"))
		{
			tvPayType.setText(data_list.get(0));
		}
		else if (resMap.get("pay_type").equals("2"))
		{
			tvPayType.setText(data_list.get(1));
		}
		else if (resMap.get("pay_type").equals("3"))
		{
			tvPayType.setText(data_list.get(2));
		}
		else if (resMap.get("pay_type").equals("4"))
		{
			tvPayType.setText(resMap.get("pay_type_append"));
		}

		if (resMap.get("role").equals("employer"))
		{
			if (resMap.get("pay_type_status").equals("0"))
			{
				spinner.setSelection(0);
			}
			else if (resMap.get("pay_type_status").equals("1"))
			{
				tv2.setVisibility(View.GONE);
			}
			else if (resMap.get("pay_type_status").equals("2"))
			{
				spinner.setSelection(Integer.valueOf(resMap.get("pay_type")) - 1);
				again = "1";
				tv2.setText("对方已拒绝，重新设置提交");
				tvPayType.setVisibility(View.GONE);
			}
			else if (resMap.get("pay_type_status").equals("3"))
			{
				tv2.setVisibility(View.GONE);
				spinner.setVisibility(View.GONE);
				tvPayType.setVisibility(View.VISIBLE);
				tvPayType.setText("等待对方同意");
			}
		}
		else
		{
			if (resMap.get("pay_type_status").equals("0"))
			{
				spinner.setVisibility(View.GONE);
				tv2.setVisibility(View.GONE);
				tvPayType.setText("等待确认付款方式");
				tvPayType.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			}
			else if (resMap.get("pay_type_status").equals("1"))
			{
				tv2.setVisibility(View.GONE);
			}
			else if (resMap.get("pay_type_status").equals("2"))
			{
				tv2.setVisibility(View.GONE);
				tvPayType.setVisibility(View.VISIBLE);
				tvPayType.setText("等待重新确认付款方式");
				spinner.setVisibility(View.GONE);
			}
			else if (resMap.get("pay_type_status").equals("3"))
			{
				spinner.setVisibility(View.GONE);
				tvPayType.setVisibility(View.VISIBLE);
				tv1.setVisibility(View.VISIBLE);
			}
		}

		Log.e("sectionList", "" + sectionList);
		adapter = new TaskSectionAdapter(TaskPriceDetails.this, sectionList,
				resMap.get("pay_type_status"), resMap.get("role"));
		listView.setAdapter(adapter);

		// 动态算出ListView的LayoutParams，并设置到ListView中
		params = StrFormat.getListViewParams(listView);
		listView.setLayoutParams(params);
	}

	TextView tvOk;
	TextView tvCancel;
	EditText editText;

	protected void customSection()
	{
		LayoutInflater inflater1 = LayoutInflater.from(this);
		LinearLayout layout1 = (LinearLayout) inflater1.inflate(
				R.layout.task_custom_section_dialog, null);

		dialog1 = new Dialog(TaskPriceDetails.this);
		dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog1.setContentView(layout1);
		dialog1.show();

		tvOk = (TextView) layout1.findViewById(R.id.tv_ok);
		tvCancel = (TextView) layout1.findViewById(R.id.tv_cancel);
		editText = (EditText) layout1.findViewById(R.id.editText1);

		tvCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				spinner.setSelection(intType - 1);
				dialog1.cancel();
			}
		});

		tvOk.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int all = 0;
				if (StrFormat.formatStr(editText.getText().toString()))
				{
					if (editText.getText().toString().contains(":"))
					{
						String[] strs = editText.getText().toString().split(":");
						if (strs.length <= 5)
						{
							for (int i = 0; i < strs.length; i++)
							{
								all = all + Integer.valueOf(strs[i]);
							}
							if (all == 100)
							{
								sectionList.clear();
								for (int i = 0; i < strs.length; i++)
								{
									HashMap<String, Object> map = new HashMap<String, Object>();
									map.put("name", "第" + string[i] + "阶段");
									map.put("percent", strs[i]);
									map.put("price",
											Float.valueOf(resMap.get("task_bounty"))
													* Integer.valueOf(strs[i]) / 100);
									sectionList.add(map);
								}
								intType = 4;
								adapter.notifyDataSetChanged();
								params = StrFormat.getListViewParams(listView);
								listView.setLayoutParams(params);

								strAppend = editText.getText().toString();
								dialog1.cancel();
							}
							else
							{
								Toast.makeText(TaskPriceDetails.this, "输入的阶段比例有误", 2000)
										.show();
							}
						}
						else
						{
							Toast.makeText(TaskPriceDetails.this, "输入的阶段有误,最多5个阶段", 2000)
									.show();
						}
					}
					else
					{
						if (Integer.valueOf(editText.getText().toString()) == 100)
						{
							sectionList.clear();
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("name", "第一阶段");
							map.put("percent", "100");
							map.put("price", resMap.get("task_bounty"));
							sectionList.add(map);
							intType = 4;
							adapter.notifyDataSetChanged();
							params = StrFormat.getListViewParams(listView);
							listView.setLayoutParams(params);

							strAppend = editText.getText().toString();
							dialog1.cancel();
						}
						else
						{
							Toast.makeText(TaskPriceDetails.this, "输入的阶段比例有误", 2000)
									.show();
						}
					}
				}
				else
				{
					Toast.makeText(TaskPriceDetails.this, "请输入正确的付款比例", 2000).show();
				}
			}
		});

	}
}
