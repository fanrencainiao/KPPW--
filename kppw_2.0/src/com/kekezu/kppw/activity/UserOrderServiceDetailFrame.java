package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kekezu.kppw.R;

/**
 * 服务订单容器
 * 
 * @author cm
 * 
 */
public class UserOrderServiceDetailFrame extends Fragment implements OnClickListener
{
	private ViewPager viewPager;

	private List<Fragment> fragmentsList = new ArrayList<Fragment>();

	UserOrderServiceDetailService orderServiceDetailService;
	UserOrderServiceDetailWork orderServiceDetailWork;
	UserOrderServiceDetailEvaluate orderServiceDetailEvaluate;

	LinearLayout layout1;
	LinearLayout layout2;
	LinearLayout layout3;

	ImageView imageView1;
	ImageView imageView2;
	ImageView imageView3;

	TextView textView1;
	TextView textView2;
	TextView textView3;

	private FragmentPagerAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.user_order_service_detail_frame, container,
				false);

		InitTextView(view);
		InitViewPager(view);
		setTextViewColor(1);
		return view;
	}

	private void InitTextView(View parentView)
	{
		layout1 = (LinearLayout) parentView.findViewById(R.id.layout_1);
		layout2 = (LinearLayout) parentView.findViewById(R.id.layout_2);
		layout3 = (LinearLayout) parentView.findViewById(R.id.layout_3);

		imageView1 = (ImageView) parentView.findViewById(R.id.imageView1);
		imageView2 = (ImageView) parentView.findViewById(R.id.imageView2);
		imageView3 = (ImageView) parentView.findViewById(R.id.imageView3);

		textView1 = (TextView) parentView.findViewById(R.id.textView1);
		textView2 = (TextView) parentView.findViewById(R.id.textView3);
		textView3 = (TextView) parentView.findViewById(R.id.textView5);

		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		layout3.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	private void InitViewPager(View parentView)
	{
		viewPager = (ViewPager) parentView.findViewById(R.id.pager);
		fragmentsList = new ArrayList<Fragment>();

		orderServiceDetailService = new UserOrderServiceDetailService();
		orderServiceDetailWork = new UserOrderServiceDetailWork();
		orderServiceDetailEvaluate = new UserOrderServiceDetailEvaluate();

		fragmentsList.add(orderServiceDetailService);
		fragmentsList.add(orderServiceDetailWork);
		fragmentsList.add(orderServiceDetailEvaluate);

		mAdapter = new FragmentPagerAdapter(getChildFragmentManager())
		{
			@Override
			public int getCount()
			{
				return fragmentsList.size();
			}

			@Override
			public Fragment getItem(int arg0)
			{
				return fragmentsList.get(arg0);
			}
		};

		viewPager.setAdapter(mAdapter);

		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				switch (position)
				{
				case 0:
					setTextViewColor(1);

					break;
				case 1:
					setTextViewColor(2);
					break;
				case 2:
					setTextViewColor(3);
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{

			}
		});
		viewPager.setCurrentItem(0);

	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.layout_1:
			setTextViewColor(1);
			viewPager.setCurrentItem(0);
			break;
		case R.id.layout_2:
			setTextViewColor(2);
			viewPager.setCurrentItem(1);
			break;
		case R.id.layout_3:
			setTextViewColor(3);
			viewPager.setCurrentItem(2);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void setTextViewColor(int index)
	{
		textViewImage();
		switch (index)
		{
		case 1:
			textView1.setTextColor(getResources().getColor(R.color.header_bg));
			imageView1.setVisibility(View.VISIBLE);
			break;
		case 2:
			textView2.setTextColor(getResources().getColor(R.color.header_bg));
			imageView2.setVisibility(View.VISIBLE);
			break;
		case 3:
			textView3.setTextColor(getResources().getColor(R.color.header_bg));
			imageView3.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}

	}

	@SuppressWarnings("deprecation")
	private void textViewImage()
	{
		textView1.setTextColor(getResources().getColor(R.color.third_text_color));
		textView2.setTextColor(getResources().getColor(R.color.third_text_color));
		textView3.setTextColor(getResources().getColor(R.color.third_text_color));

		imageView1.setVisibility(View.GONE);
		imageView2.setVisibility(View.GONE);
		imageView3.setVisibility(View.GONE);

	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter
	{
		public List<Activity> mListViews;

		public MyPagerAdapter(List<Activity> mListViews)
		{
			this.mListViews = mListViews;
		}

		@Override
		public void finishUpdate(View arg0)
		{
		}

		@Override
		public int getCount()
		{
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
		}

		@Override
		public Parcelable saveState()
		{
			return null;
		}

		@Override
		public void startUpdate(View arg0)
		{
		}
	}
}
