package com.kekezu.kppw.activity;

import org.greenrobot.eventbus.EventBus;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.HireDP;
import com.kekezu.kppw.dataprocess.ManuscriptDP;
import com.kekezu.kppw.dataprocess.WorkDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * 任务，服务，商品评价
 * 
 * @author cm
 * 
 */
public class Evaluate extends Activity
{
	ImageView imgBack;
	TextView textTitle;
	LoadingDialog progressDialog;
	ImageView imgPic, imgGood, imgMid, imgBad;
	TextView tvName;
	EditText etDesc;
	TextView tv1, tv2, tv3, tv11, tv22, tv33;
	RatingBar ratingBar1, ratingBar2, ratingBar3;
	LinearLayout layHide;
	TextView tvSubmit;

	float rating1, rating2, rating3;
	String strRole, strTaskid, strWordId, strName, strPic;
	String strEvlType = "1";
	String type;
	String stridentity;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_evaluate);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		type = getIntent().getStringExtra("type");
		if (type.equals("task"))
		{
			strRole = getIntent().getStringExtra("role");
			strTaskid = getIntent().getStringExtra("task_id");
			strWordId = getIntent().getStringExtra("work_id");
			strName = getIntent().getStringExtra("name");
			strPic = getIntent().getStringExtra("pic");
		}
		else if (type.equals("good"))
		{
			strTaskid = getIntent().getStringExtra("id");// 商品id
			strName = getIntent().getStringExtra("name");// 被评价者名字
			strPic = getIntent().getStringExtra("pic");// 被评论者头像
		}
		else if (type.equals("service"))
		{
			strTaskid = getIntent().getStringExtra("id");// 商品id
			strName = getIntent().getStringExtra("name");// 被评价者名字
			strPic = getIntent().getStringExtra("pic");// 被评论者头像
			stridentity = getIntent().getStringExtra("identity");
		}

		ViewInit();
	}

	private void ViewInit()
	{
		textTitle = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);

		imgPic = (ImageView) findViewById(R.id.img_pic);
		imgGood = (ImageView) findViewById(R.id.img_good);
		imgMid = (ImageView) findViewById(R.id.img_mid);
		imgBad = (ImageView) findViewById(R.id.img_bad);
		tvName = (TextView) findViewById(R.id.tv_name);
		etDesc = (EditText) findViewById(R.id.et_desc);

		tv1 = (TextView) findViewById(R.id.tv_1);
		tv2 = (TextView) findViewById(R.id.tv_2);
		tv3 = (TextView) findViewById(R.id.tv_3);
		tv11 = (TextView) findViewById(R.id.tv_1_1);
		tv22 = (TextView) findViewById(R.id.tv_2_2);
		tv33 = (TextView) findViewById(R.id.tv_3_3);
		ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
		ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
		ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
		layHide = (LinearLayout) findViewById(R.id.lay_hide);
		tvSubmit = (TextView) findViewById(R.id.tv_submit);

		textTitle.setText("评价");
		imgBack.setOnClickListener(listener);
		imgGood.setOnClickListener(listener);
		imgMid.setOnClickListener(listener);
		imgBad.setOnClickListener(listener);
		tvSubmit.setOnClickListener(listener);

		Glide.with(this).load(strPic).placeholder(R.drawable.erha).error(R.drawable.erha)
				.into(imgPic);
		tvName.setText(strName);

		if (type.equals("task"))
		{
			if (strRole.equals("employee"))
			{
				layHide.setVisibility(View.GONE);
				tv1.setText("付款及时性：");
				tv2.setText("合作愉快：");
			}
		}
		else if (type.equals("good"))
		{

		}
		else if (type.equals("service"))
		{
			if (stridentity.equals("2"))
			{
				layHide.setVisibility(View.GONE);
				tv1.setText("付款及时性：");
				tv2.setText("合作愉快：");
			}
		}

		changeRatingBar();
	}

	private void changeRatingBar()
	{
		rating1 = ratingBar1.getRating();
		ratingBar1.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
		{
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser)
			{
				if (fromUser)
				{
					rating1 = rating;
					tv11.setText("" + rating);
				}
			}
		});

		rating2 = ratingBar2.getRating();
		ratingBar2.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
		{
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser)
			{
				if (fromUser)
				{
					rating2 = rating;
					tv22.setText("" + rating);
				}
			}
		});

		rating3 = ratingBar3.getRating();
		ratingBar3.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
		{
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser)
			{
				if (fromUser)
				{
					rating3 = rating;
					tv33.setText("" + rating);
				}
			}
		});
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
			case R.id.img_good:
				strEvlType = "1";
				Glide.with(Evaluate.this).load(R.drawable.evl_good).into(imgGood);
				Glide.with(Evaluate.this).load(R.drawable.evl_mid_null).into(imgMid);
				Glide.with(Evaluate.this).load(R.drawable.evl_bad_null).into(imgBad);
				break;
			case R.id.img_mid:
				strEvlType = "2";
				Glide.with(Evaluate.this).load(R.drawable.evl_good_null).into(imgGood);
				Glide.with(Evaluate.this).load(R.drawable.evl_mid).into(imgMid);
				Glide.with(Evaluate.this).load(R.drawable.evl_bad_null).into(imgBad);
				break;
			case R.id.img_bad:
				strEvlType = "3";
				Glide.with(Evaluate.this).load(R.drawable.evl_good_null).into(imgGood);
				Glide.with(Evaluate.this).load(R.drawable.evl_mid_null).into(imgMid);
				Glide.with(Evaluate.this).load(R.drawable.evl_bad).into(imgBad);
				break;
			case R.id.tv_submit:
				toUserEvaluate();
				break;
			default:
				break;
			}
		}
	};

	protected void toUserEvaluate()
	{
		if (StrFormat.formatStr(etDesc.getText().toString()))
		{
			progressDialog = new LoadingDialog(this);
			progressDialog.show();
			progressDialog.setCancelable(false);

			Thread t = new Thread(newTread1);
			t.start();
		}
		else
		{
			Toast.makeText(this, "请输入评价信息", Toast.LENGTH_SHORT).show();
		}
	}

	String[] strRes;
	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			if (type.equals("task"))
			{
				if (strRole.equals("employee"))
				{
					strRes = ManuscriptDP.putEvaluate(strTaskid, etDesc.getText()
							.toString(), rating1, rating2, 0, strEvlType, strWordId,
							Evaluate.this);
				}
				else
				{
					strRes = ManuscriptDP.putEvaluate(strTaskid, etDesc.getText()
							.toString(), rating1, rating2, rating3, strEvlType,
							strWordId, Evaluate.this);
				}
			}
			else if (type.equals("good"))
			{
				strRes = WorkDP.commentGoods(strTaskid, strEvlType, etDesc.getText()
						.toString(), rating1, rating2, rating3, Evaluate.this);
			}
			else if (type.equals("service"))
			{
				if (stridentity.equals("1"))
				{
					strRes = HireDP.employEvaluate(strTaskid, stridentity, etDesc
							.getText().toString(), rating1, rating2, rating3,
							Evaluate.this);
				}
			}

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
				if (strRes[0].equals("1000"))
				{
					progressDialog.dismiss();
					Toast.makeText(Evaluate.this, "感谢评价", 2000).show();
					if (type.equals("task"))
					{
						TestEvent event = new TestEvent();
						event.setManu_add(true);
						EventBus.getDefault().post(event);
					}
					else if (type.equals("good"))
					{
						TestEvent event = new TestEvent();
						event.setOrder_ok(true);
						EventBus.getDefault().post(event);
					}
					else if (type.equals("service"))
					{
						TestEvent event = new TestEvent();
						event.setOrderDetail(true);
						EventBus.getDefault().post(event);
					}
					finish();
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(Evaluate.this, strRes[1], 2000).show();
				}
				break;
			}
		}
	};
}
