package com.kekezu.kppw.activity;

import java.io.File;

import org.greenrobot.eventbus.EventBus;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.ImageClip;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.AuthDP;
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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class UserSecurityIdentity extends Activity
{
	LoadingDialog progressDialog;
	ImageView imgBack;
	TextView text_title;

	String strType = "1"; // 证件类型，1是身份证，2是营业执照
	EditText editName; // 真是名字或公司名
	EditText editCardNumber; // 身份证号或营业执照号
	ImageView imgFrontSide; // 身份证正面或营业执照正面
	ImageView imgBackDside; // 身份证反面或营业执照反面;
	ImageView imgValidation; // 手持身份证
	TextView btnSubmit;

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

	TextView textView7;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_security_identity);

		ViewInit();
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		text_title = (TextView) findViewById(R.id.header_title);
		text_title.setText("实名认证");

		editName = (EditText) findViewById(R.id.edit_name);
		editCardNumber = (EditText) findViewById(R.id.edit_card_number);
		imgFrontSide = (ImageView) findViewById(R.id.img_front_side);
		imgBackDside = (ImageView) findViewById(R.id.img_back_dside);
		imgValidation = (ImageView) findViewById(R.id.img_validation);
		btnSubmit = (TextView) findViewById(R.id.btn_submit);

		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		File file = new File(Environment.getExternalStorageDirectory(), "kppw/cache/");
		if (!file.exists())
			file.mkdirs();
		photoSavePath = Environment.getExternalStorageDirectory() + "/kppw/cache/";
		photoSaveName = System.currentTimeMillis() + ".png";

		textView7 = (TextView) findViewById(R.id.textView7);

		imgBack.setOnClickListener(listener);
		imgFrontSide.setOnClickListener(listener);
		imgBackDside.setOnClickListener(listener);
		imgValidation.setOnClickListener(listener);
		btnSubmit.setOnClickListener(listener);
	}

	OnClickListener listener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.img_back:
				onBackPressed();
				break;
			case R.id.img_front_side:
				check = 1;
				showPopupWindow(imgFrontSide);
				break;
			case R.id.img_back_dside:
				check = 2;
				showPopupWindow(imgFrontSide);
				break;
			case R.id.img_validation:
				check = 3;
				showPopupWindow(imgFrontSide);
				break;
			case R.id.btn_submit:
				authSubmit();
				break;
			default:
				break;
			}
		}
	};

	protected void authSubmit()
	{
		if (StrFormat.formatStr(editName.getText().toString())
				&& StrFormat.formatStr(editCardNumber.getText().toString())
				&& StrFormat.formatStr(FrontSavePath)
				&& StrFormat.formatStr(BackSavePath))
		{
			if (strType.equals("1"))
			{
				if (StrFormat.formatStr(Validation))
				{

					progressDialog = new LoadingDialog(this);
					progressDialog.show();
					progressDialog.setCancelable(false);

					Thread t = new Thread(newTread1);
					t.start();
				}
				else
				{
					Toast.makeText(UserSecurityIdentity.this, "请上传手持证件照",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		else
		{
			if (StrFormat.formatStr(editName.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityIdentity.this, "请输入真实名字", Toast.LENGTH_SHORT)
						.show();
			}
			else if (StrFormat.formatStr(editCardNumber.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityIdentity.this, "请输入对应的证件号", Toast.LENGTH_SHORT)
						.show();
			}
			else if (StrFormat.formatStr(FrontSavePath) == false)
			{
				Toast.makeText(UserSecurityIdentity.this, "请上传证件正面", Toast.LENGTH_SHORT)
						.show();
			}
			else if (StrFormat.formatStr(BackSavePath) == false)
			{
				Toast.makeText(UserSecurityIdentity.this, "请上传证件反面", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	String str[];
	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			str = AuthDP.realNameAuth(editName.getText().toString(), editCardNumber
					.getText().toString(), FrontSavePath, BackSavePath, Validation,
					strType, UserSecurityIdentity.this);
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
				if (str[0].equals("1000"))
				{
					TestEvent event = new TestEvent();
					event.setAuth(true);
					EventBus.getDefault().post(event);
					finish();
					Toast.makeText(UserSecurityIdentity.this, "已提交，等待审核",
							Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(UserSecurityIdentity.this, str[1], Toast.LENGTH_SHORT)
							.show();
				}
				progressDialog.dismiss();
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
			path = StrFormat.getRealFilePath(UserSecurityIdentity.this, uri);
			Intent intent3 = new Intent(UserSecurityIdentity.this, ImageClip.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(UserSecurityIdentity.this, ImageClip.class);
			intent2.putExtra("path", path);
			startActivityForResult(intent2, IMAGE_COMPLETE);
			break;
		case IMAGE_COMPLETE:
			if (check == 1)
			{
				FrontSavePath = data.getStringExtra("path");
				Glide.with(UserSecurityIdentity.this).load(FrontSavePath)
						.into(imgFrontSide);
			}
			else if (check == 2)
			{
				BackSavePath = data.getStringExtra("path");
				Glide.with(UserSecurityIdentity.this).load(BackSavePath)
						.into(imgBackDside);
			}
			else if (check == 3)
			{
				Validation = data.getStringExtra("path");
				Glide.with(UserSecurityIdentity.this).load(Validation)
						.into(imgValidation);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
