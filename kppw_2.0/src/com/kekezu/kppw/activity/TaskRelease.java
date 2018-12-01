package com.kekezu.kppw.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.CateAdapter;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.ImageClip;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.CityDP;
import com.kekezu.kppw.dataprocess.IndustryDP;
import com.kekezu.kppw.dataprocess.TaskDP;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * 任务发布
 * 
 * @author cm
 * 
 */
public class TaskRelease extends Activity
{
	TextView textTitle;
	ImageView imgBack;
	TextView tvQieHuan;

	EditText editTitle;
	EditText editDesc;
	ImageView imgAdd;
	LinearLayout layout;
	TextView textCate;
	TextView textCity;
	EditText editPhone;
	TextView btnTaskSubmit;

	private PopupWindow popWindow2;
	ArrayList<HashMap<String, Object>> cityList;

	PopupWindow popWindow;
	View popView;
	ArrayList<HashMap<String, Object>> bigList;// 一级分类集合
	ArrayList<HashMap<String, Object>> smallList;// 二级分类集合
	LinearLayout hotLayout;
	TextView textHotCate;
	TextView textLine;
	ListView listView;
	CateAdapter listAdapter;
	ListView gridView;
	CateAdapter listAdapter2;
	RelativeLayout layoutCateHeaher;
	TextView tvCateTitle;
	ImageView imgCateBack;

	String strTaskId;
	Intent intent;
	String strTaskType;
	String strBigCateName;
	String strCateId; // 行业
	String strStatus;// 是否现在发布 发布是0，不发布存草稿箱是1

	String strProvince = "北京市"; // 省份，默认北京市
	String strProvinceID = "1";// 省份id
	String strCityID;// 市id

	LoadingDialog progressDialog;
	Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_release);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		strTaskType = getIntent().getStringExtra("taskType");

		ViewInit();
		EventBus.getDefault().register(this);

		if (getIntent().getStringExtra("op").equals("update"))
		{
			strTaskId = getIntent().getStringExtra("task_id");

			progressDialog = new LoadingDialog(this);
			progressDialog.show();
			progressDialog.setCancelable(false);

			Thread t = new Thread(newTread1);
			t.start();
		}
	}

	private void ViewInit()
	{
		imgBack = (ImageView) findViewById(R.id.img_back);
		textTitle = (TextView) findViewById(R.id.header_title);
		tvQieHuan = (TextView) findViewById(R.id.tv_qiehuan);

		editTitle = (EditText) findViewById(R.id.edit_task_title);
		editDesc = (EditText) findViewById(R.id.edit_task_desc);
		imgAdd = (ImageView) findViewById(R.id.img_task_file);
		layout = (LinearLayout) findViewById(R.id.lay_imglist);
		textCate = (TextView) findViewById(R.id.text_task_cate);
		textCity = (TextView) findViewById(R.id.text_task_city);
		btnTaskSubmit = (TextView) findViewById(R.id.btn_task_submit);
		editPhone = (EditText) findViewById(R.id.edit_task_phone);
		textTitle.setText(R.string.task_releaes_title);

		// layoutInflater = (LayoutInflater)
		// getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// File file = new File(Environment.getExternalStorageDirectory(),
		// "kppw/cache/");
		// if (!file.exists())
		// file.mkdirs();
		// photoSavePath = Environment.getExternalStorageDirectory() +
		// "/kppw/cache/";
		// photoSaveName = System.currentTimeMillis() + ".png";

		imgBack.setOnClickListener(listener);
		textCate.setOnClickListener(listener);
		imgAdd.setOnClickListener(listener);
		textCity.setOnClickListener(listener);
		btnTaskSubmit.setOnClickListener(listener);
		tvQieHuan.setOnClickListener(listener);

		if (strTaskType.equals("xuanshang"))
		{
			tvQieHuan.setText("发布招标");
		}
		else
		{
			tvQieHuan.setText("发布悬赏");
		}
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
			case R.id.tv_qiehuan:
				if (strTaskType.equals("xuanshang"))
				{
					intent = new Intent(TaskRelease.this, TaskRelease.class);
					intent.putExtra("taskType", TestData.taskType[1]);
					intent.putExtra("op", "add");
					startActivity(intent);
					finish();
				}
				else
				{
					intent = new Intent(TaskRelease.this, TaskRelease.class);
					intent.putExtra("taskType", TestData.taskType[0]);
					intent.putExtra("op", "add");
					startActivity(intent);
					finish();
				}
				break;
			case R.id.img_task_file:
				showPopupWindow(imgAdd);
				break;
			case R.id.text_task_cate:
				progressDialog = new LoadingDialog(TaskRelease.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread2);
				t.start();
				break;
			case R.id.text_task_city:
				cityList = CityDP.getCityByPid("1", TaskRelease.this);
				showPopupCity();
				break;
			case R.id.btn_task_submit:
				createTaskOne();
				// intent = new Intent(TaskRelease.this, TaskReleasePay.class);
				// startActivity(intent);
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
			map = TaskDP.editTask(strTaskId, TaskRelease.this);
			Message msg = mHandler.obtainMessage(1);
			msg.sendToTarget();
		}
	};

	Runnable newTread2 = new Runnable()
	{
		@Override
		public void run()
		{
			bigList = IndustryDP.getBigCate(TaskRelease.this);
			smallList = IndustryDP.getCate((bigList.get(0).get("children_task"))
					.toString());
			Message msg = mHandler.obtainMessage(2);
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
				if (map.get("code").equals("1000"))
				{
					progressDialog.dismiss();
					setViewValues();
				}
				else
				{
					progressDialog.dismiss();
					Toast.makeText(TaskRelease.this, map.get("message"), 2000).show();
					finish();
				}
				break;
			case 2:
				showPopupWindow();
				progressDialog.dismiss();
				break;
			}
		}
	};

	protected void setViewValues()
	{
		editTitle.setText(map.get("title"));
		editDesc.setText(Html.fromHtml(map.get("desc")));

		strBigCateName = map.get("");
		textCate.setText(map.get("cate_name"));
		strCateId = map.get("cate_id");

		textCity.setText(map.get("province_name") + "-" + map.get("city_name"));
		strProvince = map.get("");
		strProvinceID = map.get("province");
		strCityID = map.get("city");

		editPhone.setText(map.get("phone"));
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
		popWindow.showAsDropDown(textTitle);
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

	/*
	 * 地区
	 */
	private void showPopupCity()
	{
		popWindow = null;
		if (popWindow2 == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			popView = layoutInflater.inflate(R.layout.industry2, null);

			popWindow2 = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
		}

		popWindow2.setAnimationStyle(android.R.style.Animation);
		popWindow2.setFocusable(true);
		popWindow2.setOutsideTouchable(true);

		// 实例化一个ColorDrawable颜色白色,设置SelectPicPopupWindow弹出窗体的背景
		popWindow2.setBackgroundDrawable(new ColorDrawable(0xc0000000));
		popWindow2.showAsDropDown(textTitle);

		popWindow2.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸

		popWindow2.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow2.showAtLocation(popView, 0, 0, 0);

		hotLayout = (LinearLayout) popView.findViewById(R.id.layout_hot_cate);

		textLine = (TextView) popView.findViewById(R.id.text_line);
		textHotCate = (TextView) popView.findViewById(R.id.text_name_name);

		hotLayout.setVisibility(View.GONE);

		listView = (ListView) popView.findViewById(R.id.listView1);
		listAdapter = new CateAdapter(this, CityDP.getCityByPid("0", this), 1);
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

				strProvince = item.get("name").toString();
				strProvinceID = item.get("cid").toString();
				cityList.clear();

				cityList.addAll(CityDP.getCityByPid(item.get("cid").toString(),
						TaskRelease.this));
				listAdapter.notifyDataSetChanged();
				listAdapter2.notifyDataSetChanged();
			}
		});

		gridView = (ListView) popView.findViewById(R.id.listView2);
		listAdapter2 = new CateAdapter(this, cityList, 1);
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

				strCityID = item.get("cid").toString();

				textCity.setText(strProvince + "-" + item.get("name").toString());
				popWindow2.dismiss();
				popWindow2 = null;

				Log.e("aaaaaaaaaaa", "" + item.get("id"));
			}
		});
	}

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
	// private int check;// 点击上传的 哪个图片
	String strPath1 = "";

	@SuppressWarnings("deprecation")
	private void showPopupWindow(View parent)
	{
		popWindow = null;
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
				Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
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
			path = StrFormat.getRealFilePath(TaskRelease.this, uri);
			Intent intent3 = new Intent(TaskRelease.this, ImageClip.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(TaskRelease.this, ImageClip.class);
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
		final View imgView = View.inflate(TaskRelease.this, R.layout.img_item, null);
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

	// 判断创建任务是需要的参数
	protected void createTaskOne()
	{
		if (StrFormat.formatStr(editTitle.getText().toString()))
		{
			if (StrFormat.formatStr(editDesc.getText().toString()))
			{
				if (StrFormat.formatStr(strCateId))
				{
					if (StrFormat.formatStr(strProvinceID))
					{
						if (StrFormat.formatStr(strCityID))
						{
							if (StrFormat.formatStr(editPhone.getText().toString()))
							{
								if (StrFormat.isMobileNO(editPhone.getText().toString()))
								{
									intent = new Intent(TaskRelease.this,
											TaskReleaseNext.class);
									intent.putExtra("taskType", strTaskType);
									intent.putExtra("title", editTitle.getText()
											.toString());
									intent.putExtra("desc", editDesc.getText().toString());
									intent.putExtra("phone", editPhone.getText()
											.toString());
									intent.putExtra("cateid", strCateId);
									intent.putExtra("provinceid", strProvinceID);
									intent.putExtra("cityid", strCityID);
									intent.putStringArrayListExtra("fileList", list);
									intent.putExtra("task_id", strTaskId);
									intent.putExtra("op", "update");

									startActivity(intent);
								}
								else
								{
									Toast.makeText(this, "请填入正确的联系方式", Toast.LENGTH_SHORT)
											.show();
								}
							}
							else
							{
								Toast.makeText(this, "请填入联系方式", Toast.LENGTH_SHORT)
										.show();
							}
						}
						else
						{
							Toast.makeText(this, "请选择所在城市", Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						Toast.makeText(this, "请选择所在省份", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(this, "请选择行业类型", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(this, "请填入任务详情描述", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(this, "请填入任务名称", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(TaskRelease.this)

		.setMessage("你还没有完成任务提交，请问是否离开")
		// 设置显示的内容

				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{// 添加确定按钮

							@Override
							public void onClick(DialogInterface dialog, int which)
							{// 确定按钮的响应事件
								// createTask("0");
								finish();
							}

						}).setNegativeButton("取消", new DialogInterface.OnClickListener()
				{// 添加返回按钮

							@Override
							public void onClick(DialogInterface dialog, int which)
							{// 响应事件
								Log.i("alertdialog", " 请保存数据！");
							}

						}).show();// 在按键响应事件中显示此对话框

		// super.onBackPressed();
	}

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.istaskrelease())
		{
			finish();
		}
	}

	@Override
	protected void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

}
