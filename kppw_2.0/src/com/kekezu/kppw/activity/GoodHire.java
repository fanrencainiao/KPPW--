package com.kekezu.kppw.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.ImageClip;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.HireDP;
import com.kekezu.kppw.dataprocess.OtherDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 雇佣
 * 
 * @author cm
 * 
 */
public class GoodHire extends Activity
{
	ImageView imgBack;
	TextView tvTitle;
	TextView tvConserve;
	TextView tvName;
	EditText etTitle;
	EditText etContent;
	EditText etPhone;
	EditText etCash;
	LinearLayout lyTime;
	TextView tvTime;

	String strUid;
	String strUserName;
	String strTitle;
	String strContent;
	String strNow;// 当前时间
	String strBeginAt;// 开始时间
	Map<String, String> resMap;
	Map<String, String> PayMap;

	Intent intent;

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

	private String FrontSavePath;
	String strFile = "";
	String strSreviceId;

	ImageView imgAdd;
	LinearLayout layout;
	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.good_hire);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		strUid = getIntent().getStringExtra("uid");
		strUserName = getIntent().getStringExtra("username");
		strSreviceId = getIntent().getStringExtra("srevice_id");
		strTitle = getIntent().getStringExtra("title");
		strContent = getIntent().getStringExtra("content");
		initView();
	}

	private void initView()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.header_title);
		tvConserve = (TextView) findViewById(R.id.text_conserve);
		tvName = (TextView) findViewById(R.id.tv_name);
		etTitle = (EditText) findViewById(R.id.et_title);
		etContent = (EditText) findViewById(R.id.et_content);
		etPhone = (EditText) findViewById(R.id.et_phone);
		etCash = (EditText) findViewById(R.id.et_cash);
		lyTime = (LinearLayout) findViewById(R.id.ly_time);
		tvTime = (TextView) findViewById(R.id.tv_time);
		imgAdd = (ImageView) findViewById(R.id.img_task_file);
		layout = (LinearLayout) findViewById(R.id.lay_imglist);

		tvTitle.setText("雇佣");
		tvName.setText("您将雇佣" + strUserName + "为你服务");

		etTitle.setText(strTitle);
		etContent.setText(Html.fromHtml(strContent));

		// 获取当前时间
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		strNow = String.format("%d-%02d-%02d", year, month, day + 1);

		// 图片缓存
		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		File file = new File(Environment.getExternalStorageDirectory(), "kppw/cache/");
		if (!file.exists())
			file.mkdirs();
		photoSavePath = Environment.getExternalStorageDirectory() + "/kppw/cache/";
		photoSaveName = System.currentTimeMillis() + ".png";

		lyTime.setOnClickListener(listener);
		imgBack.setOnClickListener(listener);
		tvConserve.setOnClickListener(listener);

		imgAdd.setOnClickListener(listener);

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
			case R.id.img_task_file:
				showPopupWindow(imgAdd);
				break;
			case R.id.text_conserve:
				formConserve();
				break;
			case R.id.ly_time:
				setEndTime();
				break;
			default:
				break;
			}
		}
	};

	/*
	 * 截稿日期
	 */
	protected void setEndTime()
	{
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		new DatePickerDialog(GoodHire.this, new DatePickerDialog.OnDateSetListener()
		{
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth)
			{
				if (strNow.compareTo(String.format("%d-%02d-%02d", year, monthOfYear + 1,
						dayOfMonth)) <= 0)
				{
					strBeginAt = String.format("%d-%02d-%02d", year, monthOfYear + 1,
							dayOfMonth);

					tvTime.setText(strBeginAt);
				}
				else
				{
					Toast.makeText(GoodHire.this, "截稿时间不得小于1天", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}, year, month, day).show();
	}

	// 雇佣数据提交验证
	protected void formConserve()
	{
		if (StrFormat.formatStr(etTitle.getText().toString()))
		{
			if (StrFormat.formatStr(etContent.getText().toString()))
			{
				if (StrFormat.isMobileNO(etPhone.getText().toString()))
				{
					if (StrFormat.formatStr(etCash.getText().toString()))
					{
						if (!tvTime.getText().toString().equals("请选择"))
						{
							progressDialog = new LoadingDialog(this);
							progressDialog.show();
							progressDialog.setCancelable(false);

							Thread t = new Thread(newTread1);
							t.start();
						}
						else
						{
							Toast.makeText(this, "请选择截止时间", 2000).show();
						}
					}
					else
					{
						Toast.makeText(this, "请填写雇佣预算", 2000).show();
					}
				}
				else
				{
					Toast.makeText(this, "请填写正确的联系方式", 2000).show();
				}
			}
			else
			{
				Toast.makeText(this, "请填写雇佣详情", 2000).show();
			}
		}
		else
		{
			Toast.makeText(this, "请填写雇佣标题", 2000).show();
		}
	}

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					strFile = strFile + OtherDP.uploadImg(list.get(i), GoodHire.this)
							+ ",";
				}
				strFile = strFile.substring(0, strFile.length() - 1);
			}

			resMap = HireDP.createEmploy(etTitle.getText().toString(), etContent
					.getText().toString(), etPhone.getText().toString(), tvTime.getText()
					.toString(), etCash.getText().toString(), strUid, strFile,
					strSreviceId, GoodHire.this);
			Message msg = tHandler.obtainMessage(1);
			msg.sendToTarget();
		}
	};

	private Handler tHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case 1:

				if (resMap.get("code").equals("1000"))
				{
					intent = new Intent(GoodHire.this, GoodHirePay.class);
					intent.putExtra("title", etTitle.getText().toString());
					intent.putExtra("cash", etCash.getText().toString());
					intent.putExtra("order_id", resMap.get("order_id"));
					intent.putExtra("employ_id", resMap.get("employ_id"));
					startActivity(intent);
					finish();
				}
				progressDialog.dismiss();
				Toast.makeText(GoodHire.this, resMap.get("msg"), 2000).show();
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	private void showPopupWindow(View parent)
	{
		if (popWindow == null)
		{
			View view = layoutInflater.inflate(R.layout.pop_select_photo, null);
			popWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
			initPop(view);
		}
		popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
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
			path = StrFormat.getRealFilePath(GoodHire.this, uri);
			Intent intent3 = new Intent(GoodHire.this, ImageClip.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(GoodHire.this, ImageClip.class);
			intent2.putExtra("path", path);
			startActivityForResult(intent2, IMAGE_COMPLETE);
			break;
		case IMAGE_COMPLETE:
			FrontSavePath = data.getStringExtra("path");
			createImageItem(FrontSavePath);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	ArrayList<String> list = new ArrayList<String>();

	protected void createImageItem(String strPath)
	{
		final View imgView = View.inflate(GoodHire.this, R.layout.img_item, null);
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
					imgAdd.setVisibility(View.VISIBLE);
				}
			}
		});

		layout.addView(imgView);

		if (layout.getChildCount() == 3)
		{
			imgAdd.setVisibility(View.GONE);
		}
	}
}
