package com.kekezu.kppw.control;

import com.kekezu.kppw.R;

import android.R.color;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

public class LoadingDialog extends Dialog
{
	private TextView tv_text;

	public LoadingDialog(Context context)
	{
		super(context);
		/** 设置对话框背景透明 */
		getWindow().setBackgroundDrawable(new ColorDrawable(color.black));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog);
		tv_text = (TextView) findViewById(R.id.id_tv_loadingmsg);
		setCanceledOnTouchOutside(false);
		
		// 去除遮罩
		getWindow().setDimAmount(0f);
	}

	/**
	 * 为加载进度个对话框设置不同的提示消息
	 * 
	 * @param message
	 *            给用户展示的提示信息
	 * @return build模式设计，可以链式调用
	 */
	public LoadingDialog setMessage(String message)
	{
		tv_text.setText(message);
		return this;
	}
}