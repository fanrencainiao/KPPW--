package com.kekezu.kppw.activity;

import java.io.File;
import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.ImageClip;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.ManuscriptDP;
import com.kekezu.kppw.dataprocess.OtherDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 任务交稿
 * 
 * @author cm
 * 
 */
public class TaskManuscriptDelivery extends Activity
{
	ImageView imgBack;
	TextView textTitle;
	EditText editManuDesc;
	TextView btnManuSubmit;

	LinearLayout layout;
	ImageView imgAdd1;

	private PopupWindow popWindow;
	private LayoutInflater layoutInflater;
	private TextView photograph, albums;
	private LinearLayout cancel;

	public static final int PHOTOZOOM = 0; // 相册/拍照
	public static final int PHOTOTAKE = 1; // 相册/拍照
	public static final int IMAGE_COMPLETE = 2; // 结果
	public static final int CROPREQCODE = 3; // 截取
	private String photoSavePath;// 保存路径
	private String photoSaveName;// 图pian名
	private String path;// 图片全路径
	String strPath1;

	String strTaskId;
	String strTaskType;
	LoadingDialog progressDialog;
	LinearLayout layBaojia;
	EditText etCash;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_manuscript_delivery);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		strTaskType = getIntent().getStringExtra("taskType");
		strTaskId = getIntent().getStringExtra("task_id");
		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		editManuDesc = (EditText) findViewById(R.id.edit_manu_desc);
		btnManuSubmit = (TextView) findViewById(R.id.btn_manu_submit);
		imgAdd1 = (ImageView) findViewById(R.id.img_manuAdd_1);
		layout = (LinearLayout) findViewById(R.id.lay_imglist);
		textTitle.setText("稿件上传");
		layBaojia = (LinearLayout) findViewById(R.id.lay_baojia);
		etCash = (EditText) findViewById(R.id.et_cash);

		imgBack.setOnClickListener(listener);
		imgAdd1.setOnClickListener(listener);
		btnManuSubmit.setOnClickListener(listener);

		if (strTaskType.equals("zhaobiao"))
		{
			layBaojia.setVisibility(View.VISIBLE);
		}

		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		File file = new File(Environment.getExternalStorageDirectory(), "kppw/cache/");
		if (!file.exists())
			file.mkdirs();
		photoSavePath = Environment.getExternalStorageDirectory() + "/kppw/cache/";
		photoSaveName = System.currentTimeMillis() + ".png";
	}

	OnClickListener listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.img_back:
				finish();
				break;
			case R.id.btn_manu_submit:
				submitManuscript();
				break;
			case R.id.img_manuAdd_1:
				showPopupWindow(imgAdd1);
				break;
			default:
				break;
			}

		}
	};

	protected void submitManuscript()
	{
		if (StrFormat.formatStr(editManuDesc.getText().toString()))
		{
			if (strTaskType.equals("zhaobiao"))
			{
				if (StrFormat.formatStr(etCash.getText().toString()))
				{
					progressDialog = new LoadingDialog(this);
					progressDialog.setMessage("数据提交中");
					progressDialog.show();
					progressDialog.setCancelable(false);
					Thread t = new Thread(newTread1);
					t.start();
				}
				else
				{
					Toast.makeText(this, "请输入报价金额", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				progressDialog = new LoadingDialog(this);
				progressDialog.setMessage("数据提交中");
				progressDialog.show();
				progressDialog.setCancelable(false);
				Thread t = new Thread(newTread1);
				t.start();
			}
		}
		else
		{
			Toast.makeText(this, "请输入稿件详情", Toast.LENGTH_SHORT).show();
		}
	}

	String[] strManRes;
	Runnable newTread1 = new Runnable()
	{
		String strRe = "";

		@Override
		public void run()
		{
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					strRe = strRe
							+ OtherDP.uploadImg(list.get(i), TaskManuscriptDelivery.this)
							+ ",";
				}
				strRe = strRe.substring(0, strRe.length() - 1);
			}

			strManRes = ManuscriptDP.createWinBid(editManuDesc.getText().toString(),
					strTaskId, strRe, etCash.getText().toString(),
					TaskManuscriptDelivery.this);

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
				if (strManRes[0].equals("1000"))
				{
					progressDialog.dismiss();
					Toast.makeText(TaskManuscriptDelivery.this, "交稿成功，等待雇主选稿", 2000)
							.show();
					TestEvent event = new TestEvent();
					event.setManu_add(true);
					EventBus.getDefault().post(event);
					finish();
					
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(TaskManuscriptDelivery.this, strManRes[1], 2000)
							.show();
				}
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	private void showPopupWindow(View parent)
	{
		if (popWindow == null)
		{
			layoutInflater = LayoutInflater.from(this);
			View view = layoutInflater.inflate(R.layout.pop_select_photo, null);
			popWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
			initPop(view);
		}
		popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		StrFormat.fitPopupWindowOverStatusBar(popWindow, true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
	}

	public void initPop(View view)
	{
		photograph = (TextView) view.findViewById(R.id.photograph);// 拍照
		albums = (TextView) view.findViewById(R.id.albums);// 相册
		cancel = (LinearLayout) view.findViewById(R.id.cancel);// 取消
		photograph.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				popWindow.dismiss();
				photoSaveName = String.valueOf(System.currentTimeMillis()) + ".png";
				Uri imageUri = null;
				Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
				openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent, PHOTOTAKE);
			}
		});
		albums.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				popWindow.dismiss();
				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				openAlbumIntent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(openAlbumIntent, PHOTOZOOM);
			}
		});
		cancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				popWindow.dismiss();
			}
		});
	}

	/**
	 * 图片选择及拍照结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != RESULT_OK)
		{
			return;
		}
		Uri uri = null;
		switch (requestCode)
		{
		case PHOTOZOOM:// 相册
			uri = data.getData();
			path = StrFormat.getRealFilePath(TaskManuscriptDelivery.this, uri);
			Intent intent3 = new Intent(TaskManuscriptDelivery.this, ImageClip.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(TaskManuscriptDelivery.this, ImageClip.class);
			intent2.putExtra("path", path);
			startActivityForResult(intent2, IMAGE_COMPLETE);
			break;
		case IMAGE_COMPLETE:
			strPath1 = data.getStringExtra("path");
			createImageItem(strPath1);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	ArrayList<String> list = new ArrayList<String>();

	protected void createImageItem(String strPath)
	{
		final View imgView = View.inflate(TaskManuscriptDelivery.this, R.layout.img_item,
				null);
		ImageView imgDel = (ImageView) imgView.findViewById(R.id.img_del);
		ImageView imgPic = (ImageView) imgView.findViewById(R.id.img_pic);

		Glide.with(this).load(strPath).into(imgPic);

		list.add(strPath);
		imgView.setTag(strPath);
		imgDel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (null != imgView)
				{
					ViewGroup parent = (ViewGroup) imgView.getParent();
					list.remove(imgView.getTag());
					parent.removeView(imgView);
					imgAdd1.setVisibility(View.VISIBLE);
				}
			}
		});

		layout.addView(imgView);

		if (layout.getChildCount() == 3)
		{
			imgAdd1.setVisibility(View.GONE);
		}
	}

}
