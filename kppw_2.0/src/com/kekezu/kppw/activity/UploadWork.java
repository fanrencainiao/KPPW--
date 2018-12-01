package com.kekezu.kppw.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.kekezu.kppw.R;
import com.kekezu.kppw.control.ImageClip;
import com.kekezu.kppw.dataprocess.HireDP;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 威客上次完成的订单作品
 * @author cm
 *
 */
public class UploadWork extends Activity
{
	ImageView imgBack;
	TextView tvTitle;
	EditText etContent;
	ImageView img1;
	ImageView img2;
	ImageView img3;
	TextView tvConserve;

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
	private String BackSavePath;
	private String Validation;

	private int check;// 点击上传的 哪个图片

	String strId;
	String strFile;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_work);

		strId = getIntent().getStringExtra("id");

		initView();
	}

	private void initView()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		tvTitle = (TextView) findViewById(R.id.header_title);
		etContent = (EditText) findViewById(R.id.et_content);
		img1 = (ImageView) findViewById(R.id.img_1);
		img2 = (ImageView) findViewById(R.id.img_2);
		img3 = (ImageView) findViewById(R.id.img_3);
		tvConserve = (TextView) findViewById(R.id.text_conserve);

		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		File file = new File(Environment.getExternalStorageDirectory(),
				"kppw/cache/");
		if (!file.exists())
			file.mkdirs();
		photoSavePath = Environment.getExternalStorageDirectory()
				+ "/kppw/cache/";
		photoSaveName = System.currentTimeMillis() + ".png";

		tvTitle.setText("上传作品");
		
		imgBack.setOnClickListener(listener);
		tvConserve.setOnClickListener(listener);
		img1.setOnClickListener(listener);
		img2.setOnClickListener(listener);
		img3.setOnClickListener(listener);
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
			case R.id.img_1:
				check = 1;
				showPopupWindow(img1);
				break;
			case R.id.img_2:
				check = 2;
				showPopupWindow(img2);
				break;
			case R.id.img_3:
				check = 3;
				showPopupWindow(img3);
				break;
			case R.id.text_conserve:
				conserveWork();
				break;
			default:
				break;
			}
		}
	};

	protected void conserveWork()
	{
		if (StrFormat.formatStr(etContent.getText().toString()))
		{
			if (StrFormat.formatStr(FrontSavePath))
			{
				if (StrFormat.formatStr(BackSavePath))
				{
					if (StrFormat.formatStr(Validation))
					{
						strFile = HireDP.fileUpload(FrontSavePath, this)
								+ ","
								+ HireDP.fileUpload(BackSavePath, this)
								+ ","
								+ HireDP.fileUpload(Validation, this);
					}
					else
					{
						strFile = HireDP.fileUpload(FrontSavePath, this)
								+ ","
								+ HireDP.fileUpload(BackSavePath, this);
					}
				}
				else
				{
					if (StrFormat.formatStr(Validation))
					{
						strFile = HireDP.fileUpload(FrontSavePath, this)
								+ ","
								+ HireDP.fileUpload(Validation, this);
					}
					else
					{
						strFile = HireDP.fileUpload(FrontSavePath, this);
					}
				}
			}
			else
			{
				if (StrFormat.formatStr(BackSavePath))
				{
					if (StrFormat.formatStr(Validation))
					{
						strFile = HireDP.fileUpload(BackSavePath, this)
								+ ","
								+ HireDP.fileUpload(Validation, this);
					}
					else
					{
						strFile = HireDP.fileUpload(BackSavePath, this);
					}
				}
				else
				{
					if (StrFormat.formatStr(Validation))
					{
						strFile = HireDP.fileUpload(Validation, this);
					}
				}
			}

			HireDP.workEmployCreate(strId, etContent.getText().toString(),
					strFile, this);
		}
	}

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
		StrFormat.fitPopupWindowOverStatusBar(popWindow, true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
				photoSaveName = String.valueOf(System.currentTimeMillis())
						+ ".png";
				Uri imageUri = null;
				Intent openCameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
				openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION,
						0);
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
				openAlbumIntent
						.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
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
			path = StrFormat.getRealFilePath(UploadWork.this, uri);
			Intent intent3 = new Intent(UploadWork.this, ImageClip.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(UploadWork.this, ImageClip.class);
			intent2.putExtra("path", path);
			startActivityForResult(intent2, IMAGE_COMPLETE);
			break;
		case IMAGE_COMPLETE:
			if (check == 1)
			{
				FrontSavePath = data.getStringExtra("path");
				img1.setImageBitmap(getLoacalBitmap(FrontSavePath));
			}
			else if (check == 2)
			{
				BackSavePath = data.getStringExtra("path");
				img2.setImageBitmap(getLoacalBitmap(BackSavePath));
			}
			else if (check == 3)
			{
				Validation = data.getStringExtra("path");
				img3.setImageBitmap(getLoacalBitmap(Validation));
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url)
	{
		try
		{
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public byte[] bitmap2Bytes(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

}
