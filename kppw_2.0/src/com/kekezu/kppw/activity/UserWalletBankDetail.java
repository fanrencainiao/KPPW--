package com.kekezu.kppw.activity;

import java.util.List;

import net.tsz.afinal.FinalDb;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.CityBean;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 认证成功的银行卡详细页
 * 
 * @author cm
 * 
 */
public class UserWalletBankDetail extends Activity
{
	TextView text_title;
	ImageView imgBack;

	TextView tx1;
	TextView tx2;
	TextView tx4;
	TextView tx5;

	List<CityBean> cityBean;
	String strCity;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_wallet_bank_detail);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		ViewInit();
	}

	private void ViewInit()
	{
		text_title = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);

		tx1 = (TextView) findViewById(R.id.textView1);
		tx2 = (TextView) findViewById(R.id.textView2);
		tx4 = (TextView) findViewById(R.id.textView7);
		tx5 = (TextView) findViewById(R.id.textView8);

		Log.e("2222", getIntent().getStringExtra("deposit_area"));

		String[] StrArray = getIntent().getStringExtra("deposit_area").split(",");

		FinalDb db = FinalDb.create(this, getResources().getString(R.string.db_name));
		cityBean = db.findAllByWhere(CityBean.class, "cid=" + StrArray[0]);
		strCity = cityBean.get(0).getName();
		cityBean = db.findAllByWhere(CityBean.class, "cid=" + StrArray[1]);
		strCity = strCity + "-" + cityBean.get(0).getName();

		tx1.setText(getIntent().getStringExtra("bank_name"));
		tx2.setText(strCity);
		tx4.setText(getIntent().getStringExtra("deposit_name"));
		tx5.setText(StrFormat.getStarString(getIntent().getStringExtra("account"), 0, 4));

		text_title.setText("银行卡认证");

		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
}
