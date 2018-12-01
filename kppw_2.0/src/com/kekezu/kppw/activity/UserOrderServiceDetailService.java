package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.LazyFragment;
import com.kekezu.kppw.control.RoundTransform;
import com.kekezu.kppw.dataprocess.HireDP;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.utils.StrFormat;

//服务订单详情
public class UserOrderServiceDetailService extends LazyFragment
{
	View view;
	TextView tvTitle;
	TextView tvContent;
	String strId;
	Map<String, String> resMap;
	private boolean isInit = false;

	LinearLayout layFujian;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.user_order_service_detail_service, container,
				false);
		strId = getActivity().getIntent().getStringExtra("id");

		initView();

		isInit = true;
		LazyLoad();
		return view;
	}

	private void initView()
	{
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvContent = (TextView) view.findViewById(R.id.tv_content);
		layFujian = (LinearLayout) view.findViewById(R.id.lay_fujian);

	}

	private void getAttachment()
	{
		final ArrayList<HashMap<String, Object>> attachmentList = TaskDP
				.getAttachment(resMap.get("employ_att"));
		Log.e("attachmentList", "" + attachmentList);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		float density = displayMetrics.density;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int) (48 * density), (int) (48 * density));
		params.rightMargin = 36;

		for (int i = 0; i < attachmentList.size(); i++)
		{
			ImageView newImg = new ImageView(getActivity());
			Glide.with(this).load(attachmentList.get(i).get("url"))
					.error(R.drawable.ic_fujian)
					.transform(new RoundTransform(getActivity(), 3)).into(newImg);
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
						imgDialogShow(getActivity(), attachmentList.get(finalI)
								.get("url").toString());
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

	@Override
	public void LazyLoad()
	{
		if (isInit && isVisible)
		{
			resMap = HireDP.employServiceDetail(strId, getActivity());

			tvTitle.setText(resMap.get("title"));
			tvContent.setText(Html.fromHtml(resMap.get("desc")));
			getAttachment();
			isInit = false; // 数据仅加载一次
			Log.i("haha", "load data");
		}

	}
}