package com.kekezu.kppw.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.greenrobot.eventbus.EventBus;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.CateAdapter;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.control.ImageClip;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.CityDP;
import com.kekezu.kppw.dataprocess.IndustryDP;
import com.kekezu.kppw.dataprocess.ManuscriptDP;
import com.kekezu.kppw.dataprocess.MyShopDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 店铺升级
 * 
 * @author cm
 * 
 */
public class UserShopUpgrade extends Activity
{
	TextView tvTitle;
	ImageView imgBack;

	ImageView img1;
	ImageView img2;
	ImageView img3;

	EditText etName;
	LinearLayout lyCate;
	TextView textCate;
	EditText etNum;
	EditText etLicense;
	TextView tvBegin;
	EditText etWeb;
	LinearLayout lyCity;
	TextView tvCity;
	EditText etAddress;
	TextView btnSubmit;

	private PopupWindow popWindow;
	RelativeLayout layoutCateHeaher;
	TextView tvCateTitle;
	ImageView imgCateBack;
	ListView listView;
	CateAdapter listAdapter;
	ListView gridView;
	CateAdapter listAdapter2;

	ArrayList<HashMap<String, Object>> bigList;
	ArrayList<HashMap<String, Object>> smallList;

	private PopupWindow popWindow2;
	View popView;
	LinearLayout hotLayout;
	TextView textLine;
	TextView textHotCate;
	ArrayList<HashMap<String, Object>> cityList;
	RelativeLayout layoutHeaher;
	TextView tvCityTitle;
	ImageView imgCityBack;

	String strProvince = "北京市"; // 省份，默认北京市;
	String strProvinceID = "1";// 省份id
	String strCityID;// 市id
	String strCity;
	String strBeginAt;
	String strFile;
	String strBigCateName;
	String strCateId; // 行业
	String strNow;// 当前时间

	Intent intent;

	private PopupWindow popP;
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
	LoadingDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_shop_upgrade);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		initView();
	}

	private void initView()
	{
		tvTitle = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);
		etName = (EditText) findViewById(R.id.et_shop_name);
		lyCate = (LinearLayout) findViewById(R.id.ly_shop_cate);
		textCate = (TextView) findViewById(R.id.tv_shop_cate);
		etNum = (EditText) findViewById(R.id.et_shop_num);
		etLicense = (EditText) findViewById(R.id.et_shop_license);
		tvBegin = (TextView) findViewById(R.id.tv_shop_beginat);
		etWeb = (EditText) findViewById(R.id.et_shop_website);
		lyCity = (LinearLayout) findViewById(R.id.ly_shop_city);
		tvCity = (TextView) findViewById(R.id.tv_shop_city);
		etAddress = (EditText) findViewById(R.id.et_shop_address);
		btnSubmit = (TextView) findViewById(R.id.btn_shop_submit);

		img1 = (ImageView) findViewById(R.id.img_11);
		img2 = (ImageView) findViewById(R.id.img_22);
		img3 = (ImageView) findViewById(R.id.img_33);

		tvTitle.setText("升级店铺");

		imgBack.setOnClickListener(listener);
		lyCate.setOnClickListener(listener);
		tvBegin.setOnClickListener(listener);
		lyCity.setOnClickListener(listener);
		btnSubmit.setOnClickListener(listener);

		img1.setOnClickListener(listener);
		img2.setOnClickListener(listener);
		img3.setOnClickListener(listener);

		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		File file = new File(Environment.getExternalStorageDirectory(), "kppw/cache/");
		if (!file.exists())
			file.mkdirs();
		photoSavePath = Environment.getExternalStorageDirectory() + "/kppw/cache/";
		photoSaveName = System.currentTimeMillis() + ".png";

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);

		strNow = String.format("%d-%02d-%02d", year, month, day);

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
			case R.id.ly_shop_cate:
				progressDialog = new LoadingDialog(UserShopUpgrade.this);
				progressDialog.show();
				progressDialog.setCancelable(false);

				Thread t = new Thread(newTread1);
				t.start();
				break;
			case R.id.tv_shop_beginat:
				taskStartTime();
				break;
			case R.id.ly_shop_city:
				cityList = CityDP.getCityByPid("1", UserShopUpgrade.this);
				showPopupCity();
				break;
			case R.id.btn_shop_submit:
				setPriseAuth();
				break;
			case R.id.img_11:
				check = 1;
				showPop(img1);
				break;
			case R.id.img_22:
				check = 2;
				showPop(img2);
				break;
			case R.id.img_33:
				check = 3;
				showPop(img3);
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
			bigList = IndustryDP.getBigCate(UserShopUpgrade.this);
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

	protected void setPriseAuth()
	{
		if (StrFormat.formatStr(etName.getText().toString()))
		{
			if (StrFormat.formatStr(strCateId))
			{
				if (StrFormat.formatStr(etNum.getText().toString()))
				{
					if (StrFormat.formatStr(etLicense.getText().toString()))
					{
						if (StrFormat.formatStr(strBeginAt))
						{
							if (StrFormat.formatStr(strProvinceID))
							{
								if (StrFormat.formatStr(strCityID))
								{
									if (StrFormat.formatStr(etAddress.getText()
											.toString()))
									{
										conserveWork();
										if (StrFormat.formatStr(strFile))
										{
											String str[] = MyShopDP.postPriseAuth(etName
													.getText().toString(), strCateId,
													etNum.getText().toString(), etLicense
															.getText().toString(),
													strBeginAt, strProvinceID, strCityID,
													etAddress.getText().toString(),
													strFile, etWeb.getText().toString(),
													UserShopUpgrade.this);

											// 对返回的数据进行判断
											if (str[0].equals("1000"))
											{
												Toast.makeText(this, "企业认证已提交，等待后台审核",
														Toast.LENGTH_SHORT).show();
												finish();

												TestEvent event = new TestEvent();
												event.setRefesh(true);
												EventBus.getDefault().post(event);
											}
											else
											{
												Toast.makeText(this, str[1],
														Toast.LENGTH_SHORT).show();
											}
										}
										else
										{
											Toast.makeText(UserShopUpgrade.this,
													"请上传相关资质!", 2000).show();
										}
									}
									else
									{
										Toast.makeText(UserShopUpgrade.this, "请填写经营地址！",
												2000).show();
									}
								}
								else
								{
									Toast.makeText(UserShopUpgrade.this, "请选择经营地址！", 2000)
											.show();
								}
							}
							else
							{
								Toast.makeText(UserShopUpgrade.this, "请选择经营地址！", 2000)
										.show();
							}
						}
						else
						{
							Toast.makeText(UserShopUpgrade.this, "请填写开始经营时间！", 2000)
									.show();
						}
					}
					else
					{
						Toast.makeText(UserShopUpgrade.this, "请填写营业执照！", 2000).show();
					}
				}
				else
				{
					Toast.makeText(UserShopUpgrade.this, "请填写员工人数！", 2000).show();
				}
			}
			else
			{
				Toast.makeText(UserShopUpgrade.this, "请选择行业归类！", 2000).show();
			}
		}
		else
		{
			Toast.makeText(UserShopUpgrade.this, "请填写公司名称！", 2000).show();
		}
	}

	protected void conserveWork()
	{
		if (StrFormat.formatStr(FrontSavePath))
		{
			if (StrFormat.formatStr(BackSavePath))
			{
				if (StrFormat.formatStr(Validation))
				{
					strFile = ManuscriptDP.fileUpload(FrontSavePath, this) + ","
							+ ManuscriptDP.fileUpload(BackSavePath, this) + ","
							+ ManuscriptDP.fileUpload(Validation, this);
				}
				else
				{
					strFile = ManuscriptDP.fileUpload(FrontSavePath, this) + ","
							+ ManuscriptDP.fileUpload(BackSavePath, this);
				}
			}
			else
			{
				if (StrFormat.formatStr(Validation))
				{
					strFile = ManuscriptDP.fileUpload(FrontSavePath, this) + ","
							+ ManuscriptDP.fileUpload(Validation, this);
				}
				else
				{
					strFile = ManuscriptDP.fileUpload(FrontSavePath, this);
				}
			}
		}
		else
		{
			if (StrFormat.formatStr(BackSavePath))
			{
				if (StrFormat.formatStr(Validation))
				{
					strFile = ManuscriptDP.fileUpload(BackSavePath, this) + ","
							+ ManuscriptDP.fileUpload(Validation, this);
				}
				else
				{
					strFile = ManuscriptDP.fileUpload(BackSavePath, this);
				}
			}
			else
			{
				if (StrFormat.formatStr(Validation))
				{
					strFile = ManuscriptDP.fileUpload(Validation, this);
				}
			}
		}
	}

	/*
	 * 任务开始日期
	 */
	protected void taskStartTime()
	{
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		new DatePickerDialog(UserShopUpgrade.this,
				new DatePickerDialog.OnDateSetListener()
				{
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth)
					{
						if (strNow.compareTo(String.format("%d-%02d-%02d", year,
								monthOfYear + 1, dayOfMonth)) >= 0)
						{
							strBeginAt = String.format("%d-%02d-%02d", year,
									monthOfYear + 1, dayOfMonth);

							tvBegin.setText(strBeginAt);
						}
						else
						{
							Toast.makeText(UserShopUpgrade.this, "经营时间不得大于当前时间",
									Toast.LENGTH_SHORT).show();
							strBeginAt = "";
							tvBegin.setText(strBeginAt);
						}
					}
				}, year, month, day).show();
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
		popWindow.showAsDropDown(tvTitle);
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
		popWindow2.showAsDropDown(tvTitle);

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
						UserShopUpgrade.this));
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

				tvCity.setText(strProvince + "-" + item.get("name").toString());
				popWindow2.dismiss();
				popWindow2 = null;

				Log.e("aaaaaaaaaaa", "" + item.get("id"));
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void showPop(View parent)
	{
		if (popP == null)
		{
			View view = layoutInflater.inflate(R.layout.pop_select_photo, null);
			popP = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
			initPop(view);
		}
		popP.setAnimationStyle(android.R.style.Animation_InputMethod);
		popP.setFocusable(true);
		popP.setOutsideTouchable(true);
		popP.setBackgroundDrawable(new BitmapDrawable());
		popP.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popP.showAtLocation(parent, Gravity.CENTER, 0, 0);
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
				popP.dismiss();
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
				popP.dismiss();
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
				popP.dismiss();
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
			path = StrFormat.getRealFilePath(UserShopUpgrade.this, uri);
			Intent intent3 = new Intent(UserShopUpgrade.this, ImageClip.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(UserShopUpgrade.this, ImageClip.class);
			intent2.putExtra("path", path);
			startActivityForResult(intent2, IMAGE_COMPLETE);
			break;
		case IMAGE_COMPLETE:
			if (check == 1)
			{
				FrontSavePath = data.getStringExtra("path");
				Glide.with(this).load(FrontSavePath).into(img1);
			}
			else if (check == 2)
			{
				BackSavePath = data.getStringExtra("path");
				Glide.with(this).load(BackSavePath).into(img2);
			}
			else if (check == 3)
			{
				Validation = data.getStringExtra("path");
				Glide.with(this).load(Validation).into(img3);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
