package com.kekezu.kppw.pagerecyclerview;

import com.kekezu.kppw.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by zhuguohui on 2016/8/21 0021.
 */
public class MyPageIndicator extends LinearLayout implements PageGridView.PageIndicator
{
	public MyPageIndicator(Context context)
	{
		this(context, null);
	}

	public MyPageIndicator(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public void InitIndicatorItems(int itemsNumber)
	{
		removeAllViews();
		for (int i = 0; i < itemsNumber; i++)
		{
			ImageView imageView = new ImageView(getContext());

			LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(15, 15);
			layout.setMargins(5, 0, 5, 0);
			imageView.setLayoutParams(layout);
			imageView.setImageResource(R.drawable.ic_evaluate_button_normal);
			// imageView.setPadding(8, 0, 8, 0);
			addView(imageView);
		}
	}

	@Override
	public void onPageSelected(int pageIndex)
	{
		ImageView imageView = (ImageView) getChildAt(pageIndex);
		if (imageView != null)
		{
			imageView.setImageResource(R.drawable.ic_evaluate_button_pressed);
		}
	}

	@Override
	public void onPageUnSelected(int pageIndex)
	{
		ImageView imageView = (ImageView) getChildAt(pageIndex);
		if (imageView != null)
		{
			imageView.setImageResource(R.drawable.ic_evaluate_button_normal);
		}
	}
}