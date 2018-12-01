package com.kekezu.kppw.control;

import com.kekezu.kppw.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressView extends View
{
	@SuppressWarnings("unused")
	private static final String TAG = "CircleProgressView";

	// 需要执行动画的参数名
	@SuppressWarnings("unused")
	private static final String PROGRESS_PROPERTY = "progress";

	private int mMaxProgress = 100;

	private int mProgress = 0;

	// 动画位置百分比进度
	private int mCurPercent;

	private final int mCircleLineStrokeWidth = 8;

	private final int mTxtStrokeWidth = 2;

	// 画圆所在的距形区域
	private final RectF mRectF;

	private final Paint mPaint;

	@SuppressWarnings("unused")
	private final Context mContext;

	private String mTxtHint1;

	private String mTxtHint2;

	public CircleProgressView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		mContext = context;
		mRectF = new RectF();
		mPaint = new Paint();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();

		if (width != height)
		{
			int min = Math.min(width, height);
			width = min;
			height = min;
		}

		// 设置画笔相关属性
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
		canvas.drawColor(Color.TRANSPARENT);
		mPaint.setStrokeWidth(mCircleLineStrokeWidth);
		mPaint.setStyle(Style.STROKE);
		// 位置
		mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
		mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
		mRectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
		mRectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y

		// 绘制圆圈，进度条背景
		canvas.drawArc(mRectF, -90, 360, false, mPaint);
		mPaint.setColor(getResources().getColor(R.color.header_bg));
		canvas.drawArc(mRectF, -90, (float) (mCurPercent * 3.6), false, mPaint);

		// 绘制进度文案显示
		mPaint.setStrokeWidth(mTxtStrokeWidth);
		String text = mCurPercent + "%";
		int textHeight = height / 4;
		mPaint.setTextSize(textHeight);
		mPaint.setColor(getResources().getColor(R.color.black));
		int textWidth = (int) mPaint.measureText(text, 0, text.length());
		mPaint.setStyle(Style.FILL);
		canvas.drawText(text, width / 2 - textWidth / 2, 2 * textHeight, mPaint);

		mPaint.setStrokeWidth(mTxtStrokeWidth);
		String text1 = "好评率";
		int textHeight1 = height / 8;
		mPaint.setTextSize(textHeight1);
		mPaint.setColor(getResources().getColor(R.color.light_gray));
		int textWidth1 = (int) mPaint.measureText(text1, 0, text1.length());
		mPaint.setStyle(Style.FILL);
		canvas.drawText(text1, width / 2 - textWidth1 / 2, height / 2 + 2
				* textHeight1, mPaint);

		if (!TextUtils.isEmpty(mTxtHint1))
		{
			mPaint.setStrokeWidth(mTxtStrokeWidth);
			text = mTxtHint1;
			textHeight = height / 8;
			mPaint.setTextSize(textHeight);
			textWidth = (int) mPaint.measureText(text, 0, text.length());
			mPaint.setStyle(Style.FILL);
			canvas.drawText(text, width / 2 - textWidth / 2, height / 4
					+ textHeight / 2, mPaint);
		}

		if (!TextUtils.isEmpty(mTxtHint2))
		{
			mPaint.setStrokeWidth(mTxtStrokeWidth);
			text = mTxtHint2;
			textHeight = height / 8;
			mPaint.setTextSize(textHeight);
			textWidth = (int) mPaint.measureText(text, 0, text.length());
			mPaint.setStyle(Style.FILL);
			canvas.drawText(text, width / 2 - textWidth / 2, 3 * height / 4
					+ textHeight / 2, mPaint);
		}
	}

	public int getMaxProgress()
	{
		return mMaxProgress;
	}

	public void setMaxProgress(int maxProgress)
	{
		this.mMaxProgress = maxProgress;
	}

	public void setProgress(int progress)
	{
		// this.mProgress = progress;
		// this.invalidate();

		setCurPercent(progress);
	}

	// 内部设置百分比 用于动画效果
	private void setCurPercent(int percent)
	{
		mProgress = percent;

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				int sleepTime = 10;
				for (int i = 0; i <= mProgress; i++)
				{
					if (i % 20 == 0)
					{
						sleepTime += 2;
					}
					try
					{
						Thread.sleep(sleepTime);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					mCurPercent = i;
					CircleProgressView.this.postInvalidate();
				}
			}

		}).start();

	}

	public void setProgressNotInUiThread(int progress)
	{
		this.mProgress = progress;
		this.postInvalidate();
	}

	public String getmTxtHint1()
	{
		return mTxtHint1;
	}

	public void setmTxtHint1(String mTxtHint1)
	{
		this.mTxtHint1 = mTxtHint1;
	}

	public String getmTxtHint2()
	{
		return mTxtHint2;
	}

	public void setmTxtHint2(String mTxtHint2)
	{
		this.mTxtHint2 = mTxtHint2;
	}
}
