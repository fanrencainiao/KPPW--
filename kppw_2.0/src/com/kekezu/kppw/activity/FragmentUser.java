package com.kekezu.kppw.activity;

import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.CircleTransform;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.dataprocess.UserDP;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我的
 * 
 * @author cm
 * 
 */
public class FragmentUser extends Fragment
{
	View view;

	RelativeLayout layout_mywallet;
	RelativeLayout layout_mall;
	RelativeLayout layout_user_order;
	RelativeLayout layout_user_task;
	LinearLayout layout_information;
	LinearLayout layout_security;
	LinearLayout layout_taskcoll;
	LinearLayout layout_help;
	LinearLayout layout_set;

	ImageView imgUserImg;
	TextView text_set;
	TextView text_balance;
	TextView text_name;
	TextView text_switch;

	Intent intent;
	Map<String, Object> userInfo;
	Thread t;
	LoadingDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fg_user, container, false);
		ViewInit();

		progressDialog = new LoadingDialog(getActivity());
		progressDialog.show();
		progressDialog.setCancelable(false);

		t = new Thread(newTread1);
		t.start();

		EventBus.getDefault().register(this);
		return view;
	}

	private void ViewInit()
	{
		layout_mall = (RelativeLayout) view.findViewById(R.id.layout_user_mall);
		layout_mywallet = (RelativeLayout) view.findViewById(R.id.layout_user_my_wallet);
		layout_information = (LinearLayout) view
				.findViewById(R.id.layout_user_information);
		layout_security = (LinearLayout) view.findViewById(R.id.layout_user_security);
		layout_taskcoll = (LinearLayout) view.findViewById(R.id.layout_user_taskcoll);

		layout_help = (LinearLayout) view.findViewById(R.id.layout_user_help);
		layout_set = (LinearLayout) view.findViewById(R.id.layout_user_set);

		layout_user_task = (RelativeLayout) view.findViewById(R.id.layout_user_task);
		layout_user_order = (RelativeLayout) view.findViewById(R.id.layout_user_order);
		imgUserImg = (ImageView) view.findViewById(R.id.img_fg_user);
		text_name = (TextView) view.findViewById(R.id.text_user_name);
		text_switch = (TextView) view.findViewById(R.id.text_user_switch);

		layout_mall.setOnClickListener(listener);
		layout_mywallet.setOnClickListener(listener);
		layout_information.setOnClickListener(listener);
		layout_security.setOnClickListener(listener);
		layout_taskcoll.setOnClickListener(listener);
		layout_help.setOnClickListener(listener);
		layout_user_task.setOnClickListener(listener);
		layout_user_order.setOnClickListener(listener);
		layout_set.setOnClickListener(listener);
		text_switch.setOnClickListener(listener);

		if (TestData.getuserType(getActivity()) == 0)
		{
			text_switch.setText("点击切换威客");
			layout_mall.setVisibility(View.GONE);
		}
		else
		{
			text_switch.setText("点击切换雇主");
			layout_mall.setVisibility(View.VISIBLE);
		}

	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			userInfo = UserDP.getUserInfo(getActivity());

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
				text_name.setText(userInfo.get("nickname").toString());
				Glide.with(getActivity()).load(userInfo.get("avatar").toString())
						.placeholder(R.drawable.erha).error(R.drawable.erha)
						.transform(new CircleTransform(getActivity(), 3))
						.into(imgUserImg);
				progressDialog.dismiss();
				break;
			}
		}
	};

	OnClickListener listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.layout_user_mall:
				// 我的钱包
				intent = new Intent(getActivity(), UserShop.class);
				startActivity(intent);
				break;
			case R.id.layout_user_my_wallet:
				// 我的店铺
				intent = new Intent(getActivity(), UserWallet.class);
				startActivity(intent);
				break;
			case R.id.layout_user_order:
				// 我的订单
				intent = new Intent(getActivity(), UserOrder.class);
				startActivity(intent);
				break;
			case R.id.layout_user_task:
				// 我的任务
				intent = new Intent(getActivity(), UserTask.class);
				startActivity(intent);
				break;
			case R.id.layout_user_information:
				// 个人信息
				intent = new Intent(getActivity(), UserInformation.class);
				intent.putExtra("avatar", userInfo.get("avatar").toString());
				intent.putExtra("nickname", userInfo.get("nickname").toString());
				intent.putExtra("qq", userInfo.get("qq").toString());
				intent.putExtra("wechat", userInfo.get("wechat").toString());
				intent.putExtra("tag", userInfo.get("tag").toString());
				startActivity(intent);
				break;
			case R.id.layout_user_security:
				// 账号信息
				intent = new Intent(getActivity(), UserSecurity.class);
				startActivity(intent);
				break;
			case R.id.layout_user_taskcoll:
				// 收藏
				intent = new Intent(getActivity(), UserCollection.class);
				startActivity(intent);
				break;
			case R.id.layout_user_help:
				// 帮助
				intent = new Intent(getActivity(), UserHelp.class);
				startActivity(intent);
				break;
			case R.id.layout_user_set:
				// 设置
				intent = new Intent(getActivity(), UserSet.class);
				startActivity(intent);
				break;
			case R.id.text_user_switch:
				// 身份切换
				if (TestData.getuserType(getActivity()) == 0)
				{
					TestData.setUserType(getActivity(), 1);
					text_switch.setText("点击切换雇主");
					layout_mall.setVisibility(View.VISIBLE);

					TestEvent event = new TestEvent();
					event.setRefesh(true);
					event.setF_user(true);
					EventBus.getDefault().post(event);
				}
				else
				{
					TestData.setUserType(getActivity(), 0);
					text_switch.setText("点击切换威客");
					layout_mall.setVisibility(View.GONE);

					TestEvent event = new TestEvent();
					event.setRefesh(true);
					event.setF_user(true);
					EventBus.getDefault().post(event);
				}
				break;

			default:
				break;
			}
		}
	};

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isRefesh())
		{
			// t = new Thread(newTread1);
			// t.start();
		}
		if (event.isF_user())
		{
			t = new Thread(newTread1);
			t.start();
		}
	}

	@Override
	public void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
