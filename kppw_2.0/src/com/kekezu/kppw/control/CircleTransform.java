package com.kekezu.kppw.control;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Glide加载圆形图片
 * 
 * @author cm
 * 
 */
public class CircleTransform extends BitmapTransformation
{
	private Paint mBorderPaint;
	private float mBorderWidth;

	public CircleTransform(Context context)
	{
		super(context);
	}

	/**
	 * 圆形带边框
	 * 
	 * @param context
	 * @param borderWidth
	 *            边框款
	 * @param borderColor
	 *            边框颜色
	 */
	public CircleTransform(Context context, int borderWidth)
	{
		super(context);
		mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;
		mBorderPaint = new Paint();

		mBorderPaint.setARGB(234, 250, 255, 255); // *
													// 设置绘制的颜色，a代表透明度，r，g，b代表颜色值。
		mBorderPaint.setDither(true);
		mBorderPaint.setAntiAlias(true);
		// mBorderPaint.setColor(borderColor);
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setStrokeWidth(mBorderWidth);

		// mBorderPaint = new Paint();
		// mBorderPaint.setStyle(Paint.Style.STROKE);
		// mBorderPaint.setStrokeWidth(15);
		// mBorderPaint.setColor(0x7f080081);
		// mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
		// mBorderPaint.setAntiAlias(true);

	}

	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth,
			int outHeight)
	{
		return circleCrop(pool, toTransform);
	}

	private Bitmap circleCrop(BitmapPool pool, Bitmap source)
	{
		if (source == null)
			return null;

		int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
		int x = (source.getWidth() - size) / 2;
		int y = (source.getHeight() - size) / 2;

		Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
		Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
		if (result == null)
		{
			result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		}
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP,
				BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);

		float r = size / 2f;
		canvas.drawCircle(r, r, r, paint);

		if (mBorderPaint != null)
		{
			float borderRadius = r - mBorderWidth / 2;
			canvas.drawCircle(r, r, borderRadius, mBorderPaint);
		}
		return result;
	}

	@Override
	public String getId()
	{
		return getClass().getName();
	}
}