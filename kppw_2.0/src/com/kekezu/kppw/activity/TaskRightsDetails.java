package com.kekezu.kppw.activity;

import com.kekezu.kppw.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 维权详情
 * 
 * @author cm
 * 
 */
public class TaskRightsDetails extends Activity
{
	ImageView imgBack;
	TextView textTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_rights_details);

		ViewInit();

		// EventBus.getDefault().register(this);
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);

		textTitle.setText("维权详情");
		imgBack.setOnClickListener(listener);

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
			default:
				break;
			}
		}
	};
}