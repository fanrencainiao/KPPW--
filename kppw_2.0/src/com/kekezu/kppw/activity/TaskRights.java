package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.dataprocess.HireDP;
import com.kekezu.kppw.dataprocess.ManuscriptDP;
import com.kekezu.kppw.dataprocess.WorkDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 任务，服务，商品雇佣维权
 * 
 * @author cm
 * 
 */
public class TaskRights extends Activity
{
	String strId;
	String strTaskId;
	int intType = 0;
	String aaa;

	ImageView imgBack;
	TextView textTitle;
	Spinner spinner;
	EditText editDesc;
	TextView button;

	private List<String> data_list;
	private ArrayAdapter<String> arr_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_rights);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		strId = getIntent().getStringExtra("id");
		aaa = getIntent().getStringExtra("aaa");
		if (aaa.equals("3"))
		{
			strTaskId = getIntent().getStringExtra("task_id");
		}

		ViewInit();

		// EventBus.getDefault().register(this);
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		spinner = (Spinner) findViewById(R.id.spinner1);
		editDesc = (EditText) findViewById(R.id.edit_desc);
		button = (TextView) findViewById(R.id.button1);

		textTitle.setText("申请维权");

		// 数据
		data_list = new ArrayList<String>();
		data_list.add("违规信息");
		data_list.add("虚假交付");
		data_list.add("涉嫌抄袭");
		data_list.add("其他");

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
				intType = position + 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		imgBack.setOnClickListener(listener);
		button.setOnClickListener(listener);

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
			case R.id.button1:
				submitRights();
				break;
			default:
				break;
			}
		}
	};

	protected void submitRights()
	{
		if (intType != 0)
		{
			if (StrFormat.formatStr(editDesc.getText().toString()))
			{
				if (aaa.equals("1"))
				{
					if (WorkDP.rightGoods(strId, intType, editDesc.getText().toString(),
							this).equals("1000"))
					{
						TestEvent event = new TestEvent();
						event.setOrder_ok(true);
						EventBus.getDefault().post(event);

						finish();
					}
					else
					{
						Toast.makeText(
								this,
								WorkDP.rightGoods(strId, intType, editDesc.getText()
										.toString(), this), 2000).show();
					}
				}
				else if (aaa.equals("2"))
				{
					if (HireDP.employRights(strId, intType,
							editDesc.getText().toString(), this).equals("1000"))
					{
						TestEvent event = new TestEvent();
						event.setOrderDetail(true);
						EventBus.getDefault().post(event);
						Toast.makeText(this, "提交成功", 2000).show();
						finish();
					}
					else
					{
						Toast.makeText(
								this,
								HireDP.employRights(strId, intType, editDesc.getText()
										.toString(), this), 2000).show();
					}
				}
				else if (aaa.equals("3"))
				{
					String str[] = ManuscriptDP.deliveryRight(strId, intType, editDesc
							.getText().toString(), strTaskId, this);
					// 对返回的数据进行判断
					if (str[0].equals("1000"))
					{
						TestEvent event = new TestEvent();
						event.setManu_add(true);
						EventBus.getDefault().post(event);

						finish();
						Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
					}
				}

				finish();
			}
			else
			{
				Toast.makeText(this, "请描述具体情况", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, "请选择维权理由", Toast.LENGTH_SHORT).show();
		}
	}
}