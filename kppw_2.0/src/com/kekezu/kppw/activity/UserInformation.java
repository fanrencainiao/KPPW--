package com.kekezu.kppw.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.CircleTransform;
import com.kekezu.kppw.control.ImageClip;
import com.kekezu.kppw.dataprocess.UserDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
 * 个人资料编辑
 * 
 * @author cm
 * 
 */
public class UserInformation extends Activity
{
	TextView text_conserve;
	ImageView img_back;
	ImageView imgUserImage;

	TextView textInfoName;
	EditText editInfoQQ;
	EditText editInfoWx;
	LinearLayout layoutATag;
	TextView tvTag;

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
	private String SavePath;

	Map<String, String> map = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_information);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		ViewInit();
		initData();
	}

	private void ViewInit()
	{
		img_back = (ImageView) findViewById(R.id.img_back);
		text_conserve = (TextView) findViewById(R.id.text_conserve);
		imgUserImage = (ImageView) findViewById(R.id.img_user_image);

		textInfoName = (TextView) findViewById(R.id.info_name);
		editInfoQQ = (EditText) findViewById(R.id.edit_info_qq);
		editInfoWx = (EditText) findViewById(R.id.edit_info_wx);
		layoutATag = (LinearLayout) findViewById(R.id.layout_user_tag);
		tvTag = (TextView) findViewById(R.id.tv_user_tag);

		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		File file = new File(Environment.getExternalStorageDirectory(), "kppw/cache/");
		if (!file.exists())
			file.mkdirs();
		photoSavePath = Environment.getExternalStorageDirectory() + "/kppw/cache/";
		photoSaveName = System.currentTimeMillis() + ".png";

		img_back.setOnClickListener(listener);
		text_conserve.setOnClickListener(listener);
		imgUserImage.setOnClickListener(listener);
		layoutATag.setOnClickListener(listener);
	}

	private void initData()
	{
		Glide.with(UserInformation.this).load(getIntent().getStringExtra("avatar"))
				.placeholder(R.drawable.ic_me_personage_head)
				.error(R.drawable.ic_me_personage_head)
				.transform(new CircleTransform(UserInformation.this)).into(imgUserImage);

		textInfoName.setText(getIntent().getStringExtra("nickname"));
		editInfoQQ.setText(getIntent().getStringExtra("qq"));
		editInfoWx.setText(getIntent().getStringExtra("wechat"));
		tvTag.setText(getShopCate(getIntent().getStringExtra("tag")));
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
			case R.id.text_conserve:
				UserDP.updateUserInfo(editInfoQQ.getText().toString(), editInfoWx
						.getText().toString(), SavePath, UserInformation.this);

				TestEvent event = new TestEvent();
				event.setF_user(true);
				EventBus.getDefault().post(event);

				// updateInfo();
				break;
			case R.id.img_user_image:
				showPopupWindow(imgUserImage);
				break;
			case R.id.layout_user_tag:
				intent = new Intent(UserInformation.this, UserInfoTag.class);
				startActivityForResult(intent, 777);
				break;
			default:
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
				Log.e("path", "" + imageUri);

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
			path = StrFormat.getRealFilePath(UserInformation.this, uri);
			Intent intent3 = new Intent(UserInformation.this, ImageClip.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(UserInformation.this, ImageClip.class);
			intent2.putExtra("path", path);
			startActivityForResult(intent2, IMAGE_COMPLETE);
			break;
		case IMAGE_COMPLETE:
			SavePath = data.getStringExtra("path");
			Glide.with(this).load(SavePath).placeholder(R.drawable.erha)
					.transform(new CircleTransform(this)).into(imgUserImage);
			break;
		case 777:
			if (StrFormat.formatStr(data.getStringExtra("tag")))
			{
				tvTag.setText(getShopCate(data.getStringExtra("tag")));
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public String getShopCate(String string)
	{
		String str = "";
		try
		{
			JSONArray jArray = new JSONArray(string);

			if (jArray.length() > 0)
			{
				for (int i = 0; i < jArray.length(); i++)
				{
					str = str + jArray.getString(i) + ",";
				}
				str = str.substring(0, str.length() - 1);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
}
