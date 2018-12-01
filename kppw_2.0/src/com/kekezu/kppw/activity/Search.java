package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalDb;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.SearchKey;
import com.kekezu.kppw.dataprocess.TestData;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.ConfigInc;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class Search extends Activity
{
	EditText edit_keyWord;
	TextView text_search;
	TextView textView2;
	ListView listView;
	SimpleAdapter listAdapter;

	private Spinner spinner;
	private List<String> data_list;
	private ArrayAdapter<String> arr_adapter;
	int intType = 0;

	FinalDb db;
	List<SearchKey> sList;
	List<SearchKey> sList2;
	SearchKey searchKey;

	ArrayList<HashMap<String, Object>> keyList = new ArrayList<>();
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		db = ConfigInc.getCreateDB(Search.this);
		sList = db.findAll(SearchKey.class);
		intType = getIntent().getIntExtra("type", 0);
		searchKey = new SearchKey();

		viewInit();
	}

	public void viewInit()
	{
		edit_keyWord = (EditText) findViewById(R.id.edit_search_keyword);
		text_search = (TextView) findViewById(R.id.text_search_search);
		textView2 = (TextView) findViewById(R.id.textView2);
		listView = (ListView) findViewById(R.id.listView1);

		spinner = (Spinner) findViewById(R.id.spinner1);

		if (TestData.getuserType(this) == 0)
		{
			spinner.setVisibility(View.VISIBLE);
		}
		else
		{
			spinner.setVisibility(View.GONE);
		}

		// 数据
		data_list = new ArrayList<String>();
		data_list.add("找人才");
		data_list.add("找服务");
		data_list.add("找作品");

		// 适配器
		arr_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, data_list);
		// 加载适配器
		spinner.setAdapter(arr_adapter);
		spinner.setSelection(intType);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position,
					long id)
			{
				intType = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});

		textView2.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (sList.size() > 0)
				{
					db.deleteByWhere(SearchKey.class, "1=1");
					keyList.clear();
					listAdapter.notifyDataSetChanged();
				}
			}
		});

		text_search.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (StrFormat.formatStr(edit_keyWord.getText().toString()))
				{
					if (sList.size() >= 0)
					{
						sList2 = db.findAllByWhere(SearchKey.class, "name=" + "'"
								+ edit_keyWord.getText().toString() + "'");
						if (sList2.size() > 0)
						{

						}
						else
						{
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("name", edit_keyWord.getText().toString());
							searchKey.setName(edit_keyWord.getText().toString());
							db.save(searchKey);
							keyList.add(map);
							listAdapter.notifyDataSetChanged();
						}

					}
					else
					{
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name", edit_keyWord.getText().toString());
						searchKey.setName(edit_keyWord.getText().toString());
						db.save(searchKey);
						keyList.add(map);
						listAdapter.notifyDataSetChanged();
					}

					if (TestData.getuserType(Search.this) == 1)
					{
						intent = new Intent(Search.this, SearchTaskResult.class);
					}
					else
					{
						intent = new Intent(Search.this, SearchResult.class);
						intent.putExtra("type", intType);
					}
					intent.putExtra("keychat", edit_keyWord.getText().toString());
					intent.putExtra("search_type", "search");
					startActivity(intent);
					edit_keyWord.setText("");
				}
				else
				{
					Toast.makeText(Search.this, "请输入查询关键字", 2000).show();
				}
			}
		});

		if (sList.size() >= 0)
		{
			for (int i = 0; i < sList.size(); i++)
			{
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", sList.get(i).getName());
				keyList.add(map);
			}
		}

		listAdapter = new SimpleAdapter(this, keyList, R.layout.search_list_item,
				new String[] { "name" }, new int[] { R.id.textView1 });
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);

				if (TestData.getuserType(Search.this) == 1)
				{
					intent = new Intent(Search.this, SearchTaskResult.class);
				}
				else
				{
					intent = new Intent(Search.this, SearchResult.class);
					intent.putExtra("type", intType);
				}
				intent.putExtra("search_type", "search");
				intent.putExtra("keychat", item.get("name").toString());
				startActivity(intent);
			}
		});
	}
}
