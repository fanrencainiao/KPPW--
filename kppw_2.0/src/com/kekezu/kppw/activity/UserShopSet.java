package com.kekezu.kppw.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.adapter.CateAdapter;
import com.kekezu.kppw.control.ImageClip;
import com.kekezu.kppw.control.LoadingDialog;
import com.kekezu.kppw.dataprocess.CityDP;
import com.kekezu.kppw.dataprocess.MyShopDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.kekezu.kppw.adapter.UserShopTagAdapter;
import com.kekezu.kppw.bean.TestEvent;

/**
 * 店铺设置
 * 
 * @author cm
 * 
 */
public class UserShopSet extends Activity
{
	TextView tvTitle;
	ImageView imgBack;

	TextView tv1;
	TextView tv2;
	TextView tv3;
	EditText etName;
	TextView tvCate;
	LinearLayout lyCate;
	TextView tvCity;
	LinearLayout lyCity;
	EditText etDesc;
	ImageView imgPic;
	TextView btnShop;

	String type;
	Map<String, String> map;

	private PopupWindow popWindow3;
	TextView tvCateTitle3;
	ImageView imgCateBack3;
	TextView textView3;
	ArrayList<HashMap<String, Object>> allCate;
	GridView gridView3;

	String strID;
	String strTags = "";
	String strTagName = "";
	String strProvince = "北京市"; // 省份，默认北京市
	String strProvinceID = "1";// 省份id
	String strCity;
	String strCityID;// 市id

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
	LoadingDialog progressDialog;

	private PopupWindow popWindow2;
	ArrayList<HashMap<String, Object>> cityList;

	PopupWindow popWindow;
	View popView;
	ArrayList<HashMap<String, Object>> smallList = new ArrayList<>();
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

	String strBigCateName;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_set);
		StatusBarUtil.setStatusBarLightMode(getWindow());

		initView();

		progressDialog = new LoadingDialog(this);
		progressDialog.show();
		progressDialog.setCancelable(false);

		Thread t = new Thread(newTread1);
		t.start();
	}

	private void initView()
	{
		tvTitle = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);

		tv1 = (TextView) findViewById(R.id.tv_1);
		tv2 = (TextView) findViewById(R.id.tv_2);
		tv3 = (TextView) findViewById(R.id.tv_3);
		etName = (EditText) findViewById(R.id.et_shop_name);
		tvCate = (TextView) findViewById(R.id.tv_shop_cate);
		lyCate = (LinearLayout) findViewById(R.id.ly_shop_cate);
		tvCity = (TextView) findViewById(R.id.tv_shop_city);
		lyCity = (LinearLayout) findViewById(R.id.ly_shop_city);
		etDesc = (EditText) findViewById(R.id.et_shop_desc);
		imgPic = (ImageView) findViewById(R.id.img_pic);
		btnShop = (TextView) findViewById(R.id.btn_shop_submit);

		tvTitle.setText("店铺设置");

		layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		File file = new File(Environment.getExternalStorageDirectory(), "kppw/cache/");
		if (!file.exists())
			file.mkdirs();
		photoSavePath = Environment.getExternalStorageDirectory() + "/kppw/cache/";
		photoSaveName = System.currentTimeMillis() + ".png";

		imgBack.setOnClickListener(listener);
		tv2.setOnClickListener(listener);
		lyCate.setOnClickListener(listener);
		lyCity.setOnClickListener(listener);
		imgPic.setOnClickListener(listener);
		btnShop.setOnClickListener(listener);

		HashMap<String, Object> map = new HashMap<>();
		map.put("key", "0");
		map.put("value", "+ 添加");
		smallList.add(map);
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
			case R.id.tv_2:
				Intent intent = new Intent(UserShopSet.this, UserShopUpgrade.class);
				startActivity(intent);
				break;
			case R.id.ly_shop_cate:
				allCate = MyShopDP.getShopSkill(UserShopSet.this);
				shopAllCate2();
				break;
			case R.id.ly_shop_city:
				cityList = CityDP.getCityByPid("1", UserShopSet.this);
				showPopupCity();
				break;
			case R.id.img_pic:
				showPop(imgPic);
				break;
			case R.id.btn_shop_submit:
				setShopInfo();
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
			map = MyShopDP.getShopInfo(UserShopSet.this);
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
				if (!map.isEmpty())
				{
					initData();
				}
				progressDialog.dismiss();
				break;
			}
		}
	};

	protected void initData()
	{
		strID = map.get("id");
		// strPic = map.get("shop_pic");
		// strTagName = map.get("cate_name");
		strProvince = map.get("province");
		tv3.setText(map.get("type"));
		etName.setText(map.get("shop_name"));
		tvCity.setText(map.get("city_name"));
		strCity = map.get("city");
		strBigCateName = map.get("city_name");
		etDesc.setText(map.get("shop_desc"));
		if (!map.get("cate_name").equals("[]"))
		{
			try
			{
				JSONArray jsonArray = new JSONArray(map.get("cate_name").toString());

				if (jsonArray.length() == 1)
				{
					tvCate.setText(jsonArray.get(0).toString());
				}
				else if (jsonArray.length() == 2)
				{
					tvCate.setText(jsonArray.get(0).toString() + "/"
							+ jsonArray.get(1).toString());
				}
				else if (jsonArray.length() == 3)
				{
					tvCate.setText(jsonArray.get(0).toString() + "/"
							+ jsonArray.get(1).toString() + "/"
							+ jsonArray.get(2).toString());
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

		Glide.with(this).load(map.get("shop_pic")).placeholder(R.drawable.erha)
				.error(R.drawable.erha).into(imgPic);

		if (map.get("is_company_auth").equals("0"))
		{
			tv1.setText("可升级为企业店铺，提高店铺信誉度");
			tv2.setText("立即升级");
		}
		else if (map.get("is_company_auth").equals("1"))
		{
			tv1.setText("已完成企业认证");
			tv2.setVisibility(View.GONE);
		}
		else if (map.get("is_company_auth").equals("2"))
		{
			tv1.setText("企业店铺正在认证中");
			tv2.setVisibility(View.GONE);
		}
		else if (map.get("is_company_auth").equals("3"))
		{
			tv1.setText("企业店铺认证失败");
			tv2.setText("立即升级");
		}
	}

	private void setShopInfo()
	{
		if (StrFormat.formatStr(etName.getText().toString()))
		{
			if (StrFormat.formatStr(etDesc.getText().toString()))
			{
				String str[] = MyShopDP.postShopInfo(strID, etName.getText().toString(),
						etDesc.getText().toString(), FrontSavePath, strTags,
						strProvinceID, strCityID, UserShopSet.this);

				// 对返回的数据进行判断
				if (str[0].equals("1000"))
				{
					TestEvent event = new TestEvent();
					event.setShopset(true);
					EventBus.getDefault().post(event);
					Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(this, str[1], Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(UserShopSet.this, "请填写店铺介绍信息", 2000).show();
			}
		}
		else
		{
			Toast.makeText(UserShopSet.this, "请填写店铺名", 2000).show();
		}
	}

	private RecyclerView mRecyclerView;
	private UserShopTagAdapter mAdapter;

	private void shopAllCate2()
	{
		if (popWindow3 == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			popView = layoutInflater.inflate(R.layout.user_shop_tag, null);

			popWindow3 = new PopupWindow(popView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
		}

		popWindow3.setAnimationStyle(android.R.style.Animation);
		popWindow3.setFocusable(true);
		popWindow3.setOutsideTouchable(true);

		StrFormat.fitPopupWindowOverStatusBar(popWindow3, true);
		// 实例化一个ColorDrawable颜色白色,设置SelectPicPopupWindow弹出窗体的背景
		popWindow3.setBackgroundDrawable(new ColorDrawable(0xc0000000));

		popWindow3.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		popWindow3.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popWindow3.showAtLocation(popView, 0, 0, 0);

		tvCateTitle3 = (TextView) popView.findViewById(R.id.header_title);
		imgCateBack3 = (ImageView) popView.findViewById(R.id.img_back);
		textView3 = (TextView) popView.findViewById(R.id.textView1);

		tvCateTitle3.setText("店铺标签选择");
		mRecyclerView = (RecyclerView) popView.findViewById(R.id.id_recyclerview);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

		mAdapter = new UserShopTagAdapter(this, allCate);
		mRecyclerView.setAdapter(mAdapter);

		imgCateBack3.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				popWindow3.dismiss();
			}
		});

		textView3.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				strTags = "";
				strTagName = "";
				if (mAdapter.getClickList().size() > 0)
				{
					for (int i = 0; i < mAdapter.getClickList().size(); i++)
					{
						strTags = strTags
								+ mAdapter.getClickList().get(i).get("id").toString()
								+ ",";
						strTagName = strTagName
								+ mAdapter.getClickList().get(i).get("value").toString()
								+ ",";
					}
					strTags = strTags.substring(0, strTags.length() - 1);
					strTagName = strTagName.substring(0, strTagName.length() - 1);
					tvCate.setText(strTagName);
					Log.e("111111111111111", strTags);

					// if (mAdapter.getClickList().size() == 1)
					// {
					// strTags =
					// mAdapter.getClickList().get(0).get("key").toString();
					// tvCate.setText(mAdapter.getClickList().get(0).get("value")
					// .toString());
					// }
					// else if (mAdapter.getClickList().size() == 2)
					// {
					// strTags =
					// mAdapter.getClickList().get(0).get("key").toString()
					// + ","
					// + mAdapter.getClickList().get(1).get("key").toString();
					// tvCate.setText(mAdapter.getClickList().get(0).get("value")
					// .toString()
					// + "/"
					// +
					// mAdapter.getClickList().get(1).get("value").toString());
					// }
					// else if (mAdapter.getClickList().size() == 3)
					// {
					// strTags =
					// mAdapter.getClickList().get(0).get("key").toString()
					// + ","
					// + mAdapter.getClickList().get(1).get("key").toString()
					// + ","
					// + mAdapter.getClickList().get(2).get("key").toString();
					// tvCate.setText(mAdapter.getClickList().get(0).get("value")
					// .toString()
					// + "/"
					// + mAdapter.getClickList().get(1).get("value").toString()
					// + "/"
					// +
					// mAdapter.getClickList().get(2).get("value").toString());
					// }
				}
				else
				{

				}

				popWindow3.dismiss();
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
						UserShopSet.this));
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

	@Subscribe
	public void onEvent(TestEvent event)
	{
		if (event.isRefesh())
		{
			map = MyShopDP.getShopInfo(UserShopSet.this);
		}
	}

	@Override
	public void onDestroy()
	{
		EventBus.getDefault().unregister(this);
		super.onDestroy();
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
			path = StrFormat.getRealFilePath(UserShopSet.this, uri);
			Intent intent3 = new Intent(UserShopSet.this, ImageClip.class);
			intent3.putExtra("path", path);
			startActivityForResult(intent3, IMAGE_COMPLETE);
			break;
		case PHOTOTAKE:// 拍照
			path = photoSavePath + photoSaveName;
			uri = Uri.fromFile(new File(path));
			Intent intent2 = new Intent(UserShopSet.this, ImageClip.class);
			intent2.putExtra("path", path);
			startActivityForResult(intent2, IMAGE_COMPLETE);
			break;
		case IMAGE_COMPLETE:
			FrontSavePath = data.getStringExtra("path");
			imgPic.setImageBitmap(getLoacalBitmap(FrontSavePath));

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
