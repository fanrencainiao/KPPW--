package com.kekezu.kppw.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.CircleTransform;
import com.kekezu.kppw.control.LazyFragment;
import com.kekezu.kppw.dataprocess.HireDP;
import com.kekezu.kppw.utils.StrFormat;

/**
 * 订单详情页的服务评价
 * 
 * @author cm
 * 
 */
public class UserOrderServiceDetailEvaluate extends LazyFragment
{
	View view;

	LinearLayout lyMe;
	ImageView imgMePic;
	TextView tvMeName;
	ImageView imgMeGood;
	TextView tvMeGood;
	TextView tvMeContent;
	RatingBar rating1;
	RatingBar rating2;
	RatingBar rating6;

	TextView tvLine;

	LinearLayout lyHe;
	ImageView imgHePic;
	TextView tvHeName;
	ImageView imgHeGood;
	TextView tvHeGood;
	TextView tvHeContent;
	RatingBar rating3;
	RatingBar rating4;
	RatingBar rating5;

	TextView tv_1;
	TextView tv_2;
	TextView tv_3;
	TextView tv_4;
	TextView tv_5;
	TextView tv_6;

	String strId;
	Map<String, String> resMap;
	Map<String, String> comment;
	private boolean isInit = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.user_order_service_detail_evaluate, container,
				false);

		strId = getActivity().getIntent().getStringExtra("id");

		initView();

		isInit = true;
		LazyLoad();

		return view;
	}

	private void initView()
	{
		lyMe = (LinearLayout) view.findViewById(R.id.ly_me);
		imgMePic = (ImageView) view.findViewById(R.id.img_me_pic);
		tvMeName = (TextView) view.findViewById(R.id.tv_me_name);
		imgMeGood = (ImageView) view.findViewById(R.id.img_me_good);
		tvMeGood = (TextView) view.findViewById(R.id.tv_me_good);
		tvMeContent = (TextView) view.findViewById(R.id.tv_me_content);
		rating1 = (RatingBar) view.findViewById(R.id.me_RatingBar_1);
		rating2 = (RatingBar) view.findViewById(R.id.me_RatingBar_2);
		rating6 = (RatingBar) view.findViewById(R.id.me_RatingBar_6);

		tvLine = (TextView) view.findViewById(R.id.tv_line);

		lyHe = (LinearLayout) view.findViewById(R.id.ly_he);
		imgHePic = (ImageView) view.findViewById(R.id.img_he_pic);
		tvHeName = (TextView) view.findViewById(R.id.tv_he_name);
		imgHeGood = (ImageView) view.findViewById(R.id.img_he_good);
		tvHeGood = (TextView) view.findViewById(R.id.tv_he_good);
		tvHeContent = (TextView) view.findViewById(R.id.tv_he_content);
		rating3 = (RatingBar) view.findViewById(R.id.he_RatingBar_1);
		rating4 = (RatingBar) view.findViewById(R.id.he_RatingBar_2);
		rating5 = (RatingBar) view.findViewById(R.id.he_RatingBar_3);

		tv_1 = (TextView) view.findViewById(R.id.tv_1);
		tv_2 = (TextView) view.findViewById(R.id.tv_2);
		tv_3 = (TextView) view.findViewById(R.id.tv_3);
		tv_4 = (TextView) view.findViewById(R.id.tv_4);
		tv_5 = (TextView) view.findViewById(R.id.tv_5);
		tv_6 = (TextView) view.findViewById(R.id.tv_6);

		lyMe.setVisibility(View.GONE);
		lyHe.setVisibility(View.GONE);
		tvLine.setVisibility(View.GONE);
	}

	@Override
	public void LazyLoad()
	{
		// 根据接口返回谁的评价进行展示
		if (isInit && isVisible)
		{
			resMap = HireDP.employCommentDetail(strId, getActivity());

			if (resMap.get("type").equals("1"))
			{
				tv_6.setVisibility(View.GONE);
				rating6.setVisibility(View.GONE);

				tv_1.setText("付款及时：");
				tv_2.setText("合作愉快：");
				tv_3.setText("工作速度：");
				tv_4.setText("工作质量：");
				tv_5.setText("工作态度：");
			}
			else
			{
				tv_5.setVisibility(View.GONE);
				rating5.setVisibility(View.GONE);

				tv_1.setText("工作速度：");
				tv_2.setText("工作质量：");
				tv_3.setText("合作愉快：");
				tv_4.setText("付款及时：");
				tv_6.setText("工作态度：");
			}

			if (StrFormat.formatStr(resMap.get("comment_to_me")))
			{
				lyMe.setVisibility(View.VISIBLE);
				comment = getcomment(resMap.get("comment_to_me"));

				Glide.with(getActivity()).load(comment.get("avatar"))
						.placeholder(R.drawable.erha).error(R.drawable.erha)
						.transform(new CircleTransform(getActivity())).into(imgMePic);

				tvMeName.setText(comment.get("username"));

				if (comment.get("type").equals("1"))
				{
					Glide.with(this).load(R.drawable.evl_good).into(imgMeGood);
					tvMeGood.setText("好评");
				}
				else if (comment.get("type").equals("2"))
				{
					Glide.with(this).load(R.drawable.evl_mid).into(imgMeGood);
					tvMeGood.setText("中评");
				}
				else if (comment.get("type").equals("3"))
				{
					Glide.with(this).load(R.drawable.evl_bad).into(imgMeGood);
					tvMeGood.setText("差评");
				}

				tvMeContent.setText(comment.get("comment"));

				rating1.setRating(Float.valueOf(comment.get("speed_score")));
				rating2.setRating(Float.valueOf(comment.get("quality_score")));
				rating6.setRating(Float.valueOf(comment.get("attitude_score")));

			}

			if (StrFormat.formatStr(resMap.get("comment_to_he")))
			{
				if (StrFormat.formatStr(resMap.get("comment_to_me")))
				{
					tvLine.setVisibility(View.VISIBLE);
				}

				lyHe.setVisibility(View.VISIBLE);

				comment = getcomment(resMap.get("comment_to_he"));
				Glide.with(getActivity()).load(comment.get("avatar"))
						.placeholder(R.drawable.erha).error(R.drawable.erha)
						.transform(new CircleTransform(getActivity())).into(imgHePic);

				tvHeName.setText(comment.get("username"));

				if (comment.get("type").equals("1"))
				{
					Glide.with(this).load(R.drawable.evl_good).into(imgMeGood);
					tvHeGood.setText("好评");
				}
				else if (comment.get("type").equals("2"))
				{
					Glide.with(this).load(R.drawable.evl_mid).into(imgMeGood);
					tvHeGood.setText("中评");
				}
				else if (comment.get("type").equals("3"))
				{
					Glide.with(this).load(R.drawable.evl_bad).into(imgMeGood);
					tvHeGood.setText("差评");
				}

				tvHeContent.setText(comment.get("comment"));
				rating3.setRating(Float.valueOf(comment.get("speed_score")));
				rating4.setRating(Float.valueOf(comment.get("quality_score")));
				rating5.setRating(Float.valueOf(comment.get("attitude_score")));
			}

			isInit = false; // 数据仅加载一次
			Log.i("haha", "load data");
		}

	}

	private Map<String, String> getcomment(String string)
	{
		Map<String, String> res = new HashMap<String, String>();
		try
		{
			JSONObject jsonObject = new JSONObject(string);
			res.put("id", jsonObject.getString("id"));
			res.put("employ_id", jsonObject.getString("employ_id"));
			res.put("from_uid", jsonObject.getString("from_uid"));
			res.put("to_uid", jsonObject.getString("to_uid"));
			res.put("comment", jsonObject.getString("comment"));
			res.put("comment_by", jsonObject.getString("comment_by"));
			res.put("speed_score", jsonObject.getString("speed_score"));
			res.put("quality_score", jsonObject.getString("quality_score"));
			res.put("attitude_score", jsonObject.getString("attitude_score"));
			res.put("type", jsonObject.getString("type"));
			res.put("created_at", jsonObject.getString("created_at"));
			res.put("username", jsonObject.getString("username"));
			res.put("avatar", jsonObject.getString("avatar"));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return res;
	}

}