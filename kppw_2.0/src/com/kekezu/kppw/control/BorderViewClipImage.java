package com.kekezu.kppw.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

//上传图片，绘制方形边框
public class BorderViewClipImage extends View
{
	/**
	 * 水平方向与View的边距
	 */
	@SuppressWarnings("unused")
	private int mHorizontalPadding;
	/**
	 * 边框的宽度 单位dp
	 */
	private int mBorderWidth = 2;

	private Paint mPaint;

	@SuppressWarnings("unused")
	private int mVerticalPadding;

	public BorderViewClipImage(Context context)
	{
		this(context, null);
	}

	public BorderViewClipImage(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public BorderViewClipImage(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		mBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
						.getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// 绘制边框
		mPaint.setColor(Color.parseColor("#FFFFFF"));
		mPaint.setStrokeWidth(mBorderWidth);
		mPaint.setStyle(Style.STROKE);
		// 方形边框
		canvas.drawRect(0, getHeight() / 5, getWidth(), getHeight()
				- getHeight() / 5, mPaint);
		// 圆形边框
		// canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2
		// - mHorizontalPadding, mPaint);

	}

	public void setHorizontalPadding(int mHorizontalPadding)
	{
		this.mHorizontalPadding = mHorizontalPadding;

	}

}
