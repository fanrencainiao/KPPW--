package com.kekezu.kppw.activity;

import java.util.HashMap;

import com.kekezu.kppw.R;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.tools.StatusBarUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 个人资料
 * 
 * @author cm
 * 
 */
public class UserShopManage extends Activity
{
	ImageView imgBack;
	TextView textTitle;
	LinearLayout layoutInfo;
	LinearLayout layoutCase;
	Intent intent;
	
	HashMap<String, String> map;
	@SuppressWarnings({ "unchecked"})
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_shop_manage);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		map = (HashMap<String, String>) getIntent().getSerializableExtra("info");
		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);

		textTitle.setText("店铺设置");

		layoutInfo = (LinearLayout) findViewById(R.id.layout_user_information);
		layoutCase = (LinearLayout) findViewById(R.id.layout_user_case);

		if (TestData.getuserType(this) == 0)
		{
			layoutCase.setVisibility(View.GONE);
		}
		else
		{
			layoutCase.setVisibility(View.VISIBLE);
		}

		imgBack.setOnClickListener(listener);
		layoutInfo.setOnClickListener(listener);
		layoutCase.setOnClickListener(listener);
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
			case R.id.layout_user_information:
				intent = new Intent(UserShopManage.this, UserShopSet.class);
				startActivity(intent);
				break;
			case R.id.layout_user_case:
				intent = new Intent(UserShopManage.this, UserShopCase.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};
}
