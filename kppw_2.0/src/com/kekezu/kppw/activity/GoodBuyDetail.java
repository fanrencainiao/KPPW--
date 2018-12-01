package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.alibaba.mobileim.YWIMKit;
import com.bumptech.glide.Glide;
import com.kekezu.kppw.MyApp;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.CircleTransform;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.control.RoundTransform;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.dataprocess.WorkDP;
import com.kekezu.kppw.imcustom.LoginSampleHelper;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 购买的商品详情
 * 
 * @author cm
 * 
 */
public class GoodBuyDetail extends Activity
{
	ImageView imgBack;
	TextView tvTitle;
	ImageView imgPic;
	TextView tvName;
	TextView tvCash;
	TextView tvSale;
	TextView tvCity;
	LinearLayout lyShop;
	LinearLayout lyLianxi;
	LinearLayout layPingJia;
	Button btnTougao;
	LinearLayout layFujian;

	ImageView imgEvlPic, imgGood;
	TextView tvEvlName;
	TextView tvDesc;
	TextView tv1, tv2, tv3, tv11, tv22, tv33;
	RatingBar ratingBar1, ratingBar2, ratingBar3;

	String strId;
	Map<String, String> goodMap;
	Map<String, String> evlMap;
	ArrayList<HashMap<String, Object>> attachment;
	Intent intent;
	YWIMKit mIMKit;

	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buy_good_detail);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		strId = getIntent().getStringExtra("id");
		initView();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();

		EventBus.getDefault().register(this);
	}

	private void initView()
	{
		mIMKit = LoginSampleHelper.getInstance().getIMKit();

		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.header_title);
		imgPic = (ImageView) findViewById(R.id.img_pic);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvCash = (TextView) findViewById(R.id.tv_cash);
		tvSale = (TextView) findViewById(R.id.tv_sales);
		tvCity = (TextView) findViewById(R.id.tv_city);
		lyShop = (LinearLayout) findViewById(R.id.ly_goshop);
		lyLianxi = (LinearLayout) findViewById(R.id.ly_lianxi);
		btnTougao = (Button) findViewById(R.id.btn_task_tougao);
		layFujian = (LinearLayout) findViewById(R.id.lay_fujian);
		layPingJia = (LinearLayout) findViewById(R.id.lay_pingjia);

		imgEvlPic = (ImageView) findViewById(R.id.img_evl_pic);
		imgGood = (ImageView) findViewById(R.id.img_good);
		tvEvlName = (TextView) findViewById(R.id.tv_evl_name);
		tvDesc = (TextView) findViewById(R.id.tv_content);

		tv1 = (TextView) findViewById(R.id.tv_1);
		tv2 = (TextView) findViewById(R.id.tv_2);
		tv3 = (TextView) findViewById(R.id.tv_3);
		tv11 = (TextView) findViewById(R.id.tv_1_1);
		tv22 = (TextView) findViewById(R.id.tv_2_2);
		tv33 = (TextView) findViewById(R.id.tv_3_3);
		ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
		ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
		ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);

		tvTitle.setText("作品订单详情");
		imgBack.setOnClickListener(listener);
		btnTougao.setOnClickListener(listener);
		lyShop.setOnClickListener(listener);
		lyLianxi.setOnClickListener(listener);

	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			goodMap = WorkDP.buyGoodsDetail(strId, GoodBuyDetail.this);
			evlMap = WorkDP.getComment(strId, GoodBuyDetail.this);
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
				setEnable();
				getAttachment();
				progressDialog.dismiss();
				break;
			}
		}
	};

	private void getAttachment()
	{
		// 商品封面
		Glide.with(this).load(goodMap.get("cover")).placeholder(R.drawable.erha)
				.error(R.drawable.erha).into(imgPic);
		tvName.setText(goodMap.get("title"));// 商品标题
		tvCash.setText(goodMap.get("cash"));// 商品金额
		tvSale.setText("销量" + goodMap.get("sales_num") + "笔");// 商品销售量
		tvCity.setText(goodMap.get("address"));// 商品所在地
		btnTougao.setText(goodMap.get("button_status"));// 商品购买后状态

		setTaskAttachment();

		if (!evlMap.isEmpty())
		{
			layPingJia.setVisibility(View.VISIBLE);
			setGoodEvl();
		}
	}

	private void setGoodEvl()
	{
		Glide.with(this).load(evlMap.get("avatar").toString())
				.placeholder(R.drawable.erha).error(R.drawable.erha)
				.transform(new CircleTransform(this)).into(imgEvlPic);

		if (evlMap.get("type").equals("1"))
		{
			Glide.with(this).load(R.drawable.evl_good).into(imgGood);
		}
		else if (evlMap.get("type").equals("2"))
		{
			Glide.with(this).load(R.drawable.evl_mid).into(imgGood);
		}
		else if (evlMap.get("type").equals("3"))
		{
			Glide.with(this).load(R.drawable.evl_bad).into(imgGood);
		}

		tvEvlName.setText(evlMap.get("name").toString());
		tvDesc.setText(evlMap.get("comment_desc").toString());
		tv11.setText(evlMap.get("speed_score").toString());
		ratingBar1.setRating(Float.valueOf(evlMap.get("speed_score").toString()));
		tv22.setText(evlMap.get("quality_score").toString());
		ratingBar2.setRating(Float.valueOf(evlMap.get("quality_score").toString()));
		tv33.setText(evlMap.get("attitude_score").toString());
		ratingBar3.setRating(Float.valueOf(evlMap.get("attitude_score").toString()));
	}

	private void setEnable()
	{
		if (goodMap.get("status").equals("3"))
		{
			btnTougao.setBackgroundResource(R.color.light_gray7);
		}
	}

	protected void setNextStep()
	{
		if (goodMap.get("status").equals("1"))
		{
			AcceptofRights();
		}
		else if (goodMap.get("status").equals("2"))
		{
			Intent intent = new Intent(GoodBuyDetail.this, Evaluate.class);
			intent.putExtra("type", "good");
			intent.putExtra("id", strId);
			intent.putExtra("pic", goodMap.get("avatar"));
			intent.putExtra("name", goodMap.get("username"));
			startActivity(intent);
		}
		else if (goodMap.get("status").equals("4"))
		{
			// Intent intent = new Intent(BuyGoodDetail.this,
			// BuyEvaluateShow.class);
			// intent.putExtra("id", strId);
			// intent.putExtra("order", goodMap.get("title"));
			// startActivity(intent);
		}
	}

	// 验收or维权
	private void AcceptofRights()
	{
		new AlertDialog.Builder(GoodBuyDetail.this).setTitle("提示")
				.setMessage("请确认相关作品内容")
				// 设置显示的内容
				.setPositiveButton("验收", new DialogInterface.OnClickListener()
				{// 添加确定按钮
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (WorkDP.confirmGoods(strId, GoodBuyDetail.this)
										.equals("1000"))
								{
									TestEvent event = new TestEvent();
									event.setOrder_ok(true);
									EventBus.getDefault().post(event);
								}
								else
								{
									Toast.makeText(
											GoodBuyDetail.this,
											WorkDP.confirmGoods(strId, GoodBuyDetail.this),
											2000).show();
								}
							}
						}).setNegativeButton("维权", new DialogInterface.OnClickListener()
				{// 添加返回按钮

							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								Intent intent = new Intent(GoodBuyDetail.this,
										TaskRights.class);
								intent.putExtra("id", strId);
								intent.putExtra("aaa", "1");
								startActivity(intent);
							}
						}).show();// 在按键响应事件中显示此对话框

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
			case R.id.ly_goshop:
				intent = new Intent(GoodBuyDetail.this, ShopDetail.class);
				intent.putExtra("shopId", goodMap.get("shop_id"));
				startActivity(intent);
				break;
			case R.id.ly_lianxi:
				Intent intent = mIMKit.getChattingActivityIntent(goodMap.get("uid"),
						MyApp.APP_KEY);
				startActivity(intent);
				break;
			case R.id.btn_task_tougao:
				setNextStep();
				break;
			default:
				break;
			}
		}
	};

	private void setTaskAttachment()
	{
		final ArrayList<HashMap<String, Object>> attachmentList = TaskDP
				.getAttachment(goodMap.get("attachment"));
		Log.e("attachmentList", "" + attachmentList);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		float density = displayMetrics.density;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int) (48 * density), (int) (48 * density));
		params.rightMargin = 36;

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
						imgDialogShow(GoodBuyDetail.this,
								attachmentList.get(finalI).get("url").toString());
					}
					else
					{
						new AlertDialog.Builder(GoodBuyDetail.this)
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
														GoodBuyDetail.this,
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
		if (event.isOrder_ok())
		{
			goodMap = WorkDP.buyGoodsDetail(strId, this);
			getAttachment();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

}
