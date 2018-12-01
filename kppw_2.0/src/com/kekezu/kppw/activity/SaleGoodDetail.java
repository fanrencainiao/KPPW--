package com.kekezu.kppw.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.CircleTransform;
import com.kekezu.kppw.dataprocess.WorkDP;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class SaleGoodDetail extends Activity
{
	ImageView imgBack;
	TextView tvTitle;
	ImageView imgPic;
	TextView tvName;
	TextView tvCash;
	TextView tvSale;
	TextView tvCity;

	ImageView imgAvatar;
	TextView tvBuyName;

	LinearLayout lyBg;
	LinearLayout lyPingJia;
	ImageView imgGood;
	TextView tvGood;
	TextView tvContent;
	RatingBar rating1;
	RatingBar rating2;
	RatingBar rating3;

	Map<String, String> resMap;
	Map<String, String> comment;
	String strId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sale_good_detail);
		strId = getIntent().getStringExtra("id");

		resMap = WorkDP.saleGoodsDetail(strId, this);
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.header_title);
		imgPic = (ImageView) findViewById(R.id.img_work_pic);
		tvName = (TextView) findViewById(R.id.tv_work_name);
		tvCash = (TextView) findViewById(R.id.tv_work_cash);
		tvSale = (TextView) findViewById(R.id.tv_work_sales);
		tvCity = (TextView) findViewById(R.id.tv_work_city);

		imgAvatar = (ImageView) findViewById(R.id.img_avatar);
		tvBuyName = (TextView) findViewById(R.id.buy_name);

		lyBg = (LinearLayout) findViewById(R.id.ly_bg);
		lyPingJia = (LinearLayout) findViewById(R.id.ly_pingjia);
		imgGood = (ImageView) findViewById(R.id.img_he_good);
		tvGood = (TextView) findViewById(R.id.tv_he_good);
		tvContent = (TextView) findViewById(R.id.tv_he_content);

		rating1 = (RatingBar) findViewById(R.id.he_RatingBar_1);
		rating2 = (RatingBar) findViewById(R.id.he_RatingBar_2);
		rating3 = (RatingBar) findViewById(R.id.he_RatingBar_3);

		tvTitle.setText("作品订单详情");
		Glide.with(this).load(resMap.get("cover")).placeholder(R.drawable.erha)
				.error(R.drawable.erha).into(imgPic);
		
		tvName.setText(resMap.get("title"));
		tvCash.setText(resMap.get("cash"));
		tvSale.setText("销量" + resMap.get("sales_num") + "笔");
		tvCity.setText(resMap.get("address"));

		Glide.with(this).load(resMap.get("avatar"))
				.placeholder(R.drawable.erha)
				.transform(new CircleTransform(this)).error(R.drawable.erha)
				.into(imgAvatar);
		
		tvBuyName.setText(resMap.get("username"));

		if (!resMap.get("comment").equals("[]"))
		{
			comment = getcomment(resMap.get("comment"));
			if (comment.get("type").equals("1"))
			{
				imgGood.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_evaluate_good));
				tvGood.setText("好评");
			}
			else if (comment.get("type").equals("2"))
			{
				imgGood.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_evaluate_ordinary));
				tvGood.setText("中评");
			}
			else if (comment.get("type").equals("3"))
			{
				imgGood.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_evaluate_negative));
				tvGood.setText("差评");
			}

			tvContent.setText(comment.get("comment_desc"));

			rating1.setRating(Float.valueOf(comment.get("speed_score")));
			rating2.setRating(Float.valueOf(comment.get("quality_score")));
			rating3.setRating(Float.valueOf(comment.get("attitude_score")));
		}
		else
		{
			lyPingJia.setVisibility(View.GONE);
		}

		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

	}

	private Map<String, String> getcomment(String string)
	{
		Map<String, String> res = new HashMap<String, String>();
		try
		{
			JSONObject jsonObject = new JSONObject(string);

			res.put("id", jsonObject.getString("id"));
			res.put("goods_id", jsonObject.getString("goods_id"));
			res.put("uid", jsonObject.getString("uid"));
			res.put("comment_desc", jsonObject.getString("comment_desc"));
			res.put("comment_by", jsonObject.getString("comment_by"));
			res.put("speed_score", jsonObject.getString("speed_score"));
			res.put("quality_score", jsonObject.getString("quality_score"));
			res.put("attitude_score", jsonObject.getString("attitude_score"));
			res.put("type", jsonObject.getString("type"));
			res.put("created_at", jsonObject.getString("created_at"));

		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return res;
	}
}
