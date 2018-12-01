package com.kekezu.kppw.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.CateAdapter;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.ImageClip;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.CaseDP;
import com.kekezu.kppw.dataprocess.IndustryDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * 添加案例
 * 
 * @author cm
 * 
 */
public class UserShopCaseAdd extends Activity
{
	TextView text_title;
	ImageView imgBack;
	EditText editTitle;
	TextView textCate;
	EditText editDesc;
	ImageView imageView;
	TextView button1;
	private PopupWindow popWindow;
	View popView;

	LinearLayout hotLayout;
	TextView textLine;
	TextView textHotCate;
	ListView listView;
	CateAdapter listAdapter;
	ListView gridView;
	CateAdapter listAdapter2;

	private PopupWindow popWindow222;
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

	ArrayList<HashMap<String, Object>> bigList;
	ArrayList<HashMap<String, Object>> smallList;

	String strCateId;
	String strFile;
	String strBigCateName;
	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_shop_case_add);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		ViewInit();
	}

	private void ViewInit()
	{
		text_title = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);
		editTitle = (EditText) findViewById(R.id.edit_case_title);
		textCate = (TextView) findViewById(R.id.text_case_cate);
		editDesc = (EditText) findViewById(R.id.edit_case_desc);
		imageView = (ImageView) findViewById(R.id.image_case);
		button1 = (TextView) findViewById(R.id.button1);

		text_title.setText("新增案例");

		imgBack.setOnClickListener(listener);
		textCate.setOnClickListener(listener);
		imageView.setOnClickListener(listener);
		button1.setOnClickListener(listener);

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
			case R.id.text_case_cate:
				progressDialog = new LoadingDialog(UserShopCaseAdd.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread1);
				t.start();
				break;
			case R.id.image_case:
				showPopupWindow222(imageView);
				break;
			case R.id.button1:
				addUserCase();
				break;
			default:
				break;
			}

		}
	};

	Runnable newTread1 = new Runnable()
	{
		@Override
		public void run()
		{
			bigList = IndustryDP.getBigCate(UserShopCaseAdd.this);
			smallList = IndustryDP.getCate((bigList.get(0).get("children_task"))
					.toString());
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
				showPopupWindow();
				progressDialog.dismiss();
				break;
			}
		}
	};

	protected void addUserCase()
	{
		if (StrFormat.formatStr(editTitle.getText().toString()))
		{
			if (StrFormat.formatStr(editDesc.getText().toString()))
			{
				if (StrFormat.formatStr(strCateId))
				{
					if (StrFormat.formatStr(strFile))
					{
						String str[] = CaseDP.addCase(editTitle.getText().toString(),
								strCateId, editDesc.getText().toString(), strFile, this);

						// 对返回的数据进行判断
						if (str[0].equals("1000"))
						{
							TestEvent event = new TestEvent();
							event.setCaseAdd(true);
							EventBus.getDefault().post(event);

							finish();

							Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						Toast.makeText(UserShopCaseAdd.this, "请上传封面图片",
								Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(UserShopCaseAdd.this, "请选择分类", Toast.LENGTH_SHORT)
							.show();
				}
			}
			else
			{
				Toast.makeText(UserShopCaseAdd.this, "请填写描述详情", Toast.LENGTH_SHORT)
						.show();
			}
		}
		else
		{
			Toast.makeText(UserShopCaseAdd.this, "请填写案例标题", Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * 行业
	 */
	private void showPopupWindow()
	{
		popWindow = null;
		if (popWindow == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			popView = layoutInflater.inflate(R.layout.industry2, null);

			popWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
		}

		popWindow.setAnimationStyle(android.R.style.Animation);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);

		// 实例化一个ColorDrawable颜色白色,设置SelectPicPopupWindow弹出窗体的背景
		popWindow.setBackgroundDrawable(new ColorDrawable(0xc0000000));
		popWindow.showAsDropDown(text_title);
		popWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow.showAtLocation(popView, 0, 0, 0);

		hotLayout = (LinearLayout) popView.findViewById(R.id.layout_hot_cate);
		hotLayout.setVisibility(View.GONE);
		listView = (ListView) popView.findViewById(R.id.listView1);
		listAdapter = new CateAdapter(this, bigList, 1);
		listView.setAdapter(listAdapter);
		listAdapter.setSelectItem(0);

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				listAdapter.setSelectItem(position);
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);

				strBigCateName = item.get("name").toString() + "-";
				smallList.clear();
				smallList.addAll(IndustryDP.getCate((bigList.get(position)
						.get("children_task")).toString()));
				listAdapter.notifyDataSetChanged();
				listAdapter2.notifyDataSetChanged();
			}
		});

		gridView = (ListView) popView.findViewById(R.id.listView2);
		listAdapter2 = new CateAdapter(this, smallList, 2);
		gridView.setAdapter(listAdapter2);

		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);

				if (StrFormat.formatStr(strBigCateName))
				{
					textCate.setText(strBigCateName + item.get("name").toString());
				}
				else
				{
					textCate.setText(item.get("name").toString());
				}

				popWindow.dismiss();
				popWindow = null;

				strCateId = item.get("id").toString();
				Log.e("aaaaaaaaaaa", "" + item.get("id"));
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void showPopupWindow222(View parent)
	{
		if (popWindow222 == null)
		{
			layoutInflater = LayoutInflater.from(this);
			View view = layoutInflater.inflate(R.layout.pop_select_photo, null);
			popWindow222 = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
			initPop(view);
		}
		popWindow222.setAnimationStyle(android.R.style.Animation_InputMethod);
		popWindow222.setFocusable(true);
		popWindow222.setOutsideTouchable(true);
		StrFormat.fitPopupWindowOverStatusBar(popWindow, true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow222
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow222.showAtLocation(parent, Gravity.CENTER, 0, 0);
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
				popWindow222.dismiss();
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
				popWindow222.dismiss();
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
				popWindow222.dismiss();
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
			path = StrFormat.getRealFilePath(UserShopCaseAdd.this, uri);
			Intent intent3 = new Intent(UserShopCaseAdd.this, ImageClip.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(UserShopCaseAdd.this, ImageClip.class);
			intent2.putExtra("path", path);
			startActivityForResult(intent2, IMAGE_COMPLETE);
			break;
		case IMAGE_COMPLETE:
			strFile = data.getStringExtra("path");
			Glide.with(this).load(strFile).into(imageView);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
