package com.kekezu.kppw.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.control.BaseFragment;
import com.kekezu.kppw.control.MyGridView;
import com.kekezu.kppw.dataprocess.ShopDetailDP;

/**
 * 店铺的案例
 * 
 * @author cm
 * 
 */
public class ShopDetailCase extends BaseFragment
{
	MyGridView gridView;
	ProgressBar moreProgressBar;
	View Lastview;
	ArrayList<HashMap<String, Object>> caseList;
	int page = 1; // 页码

	String strShopId;

	public ShopDetailCase(String strShopId2)
	{
		strShopId=strShopId2;
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.shop_detail_case;
	}

	@Override
	protected void initView()
	{
		gridView = (MyGridView) findView(R.id.gridView1);
		
		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				HashMap<String, Object> item = (HashMap<String, Object>) parent
						.getItemAtPosition(position);

				Intent intent = new Intent(getActivity(), UserShopCaseDetails.class);
				intent.putExtra("id", item.get("id").toString());
				startActivity(intent);

			}
		});
	}

	@Override
	protected void initData()
	{
		caseList = ShopDetailDP.getShopCase(strShopId, page, getActivity());
		gridView.setAdapter(new MyListAdapter(caseList));
	}

	private class MyListAdapter extends BaseAdapter
	{
		private ViewHolder mHolder;
		ArrayList<HashMap<String, Object>> alist;

		MyListAdapter(ArrayList<HashMap<String, Object>> list)
		{
			alist = list;
		}

		@Override
		public int getCount()
		{
			return alist.size();
		}

		@Override
		public Object getItem(int position)
		{
			return alist.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// 只有当convertView不存在的时候才去inflate子元素
			if (convertView == null)
			{
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.user_shop_case_list_item, parent, false);
				mHolder = new ViewHolder();

				mHolder.textTitle = (TextView) convertView.findViewById(R.id.textView1);
				mHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);

				convertView.setTag(mHolder);
			}
			else
			{
				mHolder = (ViewHolder) convertView.getTag();
			}

			//mHolder.textTitle.setBackgroundColor(Color.argb(255, 0, 255, 0));
			mHolder.textTitle.setText((alist.get(position).get("title")).toString());

			Glide.with(getActivity()).load(alist.get(position).get("pic").toString())
					.placeholder(R.drawable.ic_alipay_bg).error(R.drawable.ic_alipay_bg)
					.into(mHolder.imageView);

			return convertView;
		}

		private class ViewHolder
		{
			ImageView imageView;
			TextView textTitle;
		}
	}
}
