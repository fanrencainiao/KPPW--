

package com.kekezu.kppw.ruihui;


import java.util.ArrayList;
import java.util.List;

import com.kekezu.kppw.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class NavigationBar extends FragmentActivity {
	private ViewPager mVp;
	private PagerTabStrip mTabStrip;
	private List<Fragment> mList;
	private String[] tabNames;
	private String[] tabUrls;
	private ImageView img_back;
	private TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.out_link);
		
		 mVp=(ViewPager)findViewById(R.id.vp);
		 tv=(TextView) findViewById(R.id.header_title);
		 img_back = (ImageView) findViewById(R.id.img_back);
		 img_back.setOnClickListener(listener);
		 mTabStrip=(PagerTabStrip) findViewById(R.id.tab_navigation);
		 mTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		 mTabStrip.setTextColor(Color.DKGRAY);
		 mTabStrip.setTabIndicatorColor(Color.GREEN);
		 tabNames=getResources().getStringArray(R.array.outtabs);
		 tabUrls=getResources().getStringArray(R.array.outlinks);
		 mList= new ArrayList<Fragment>();
		 for(int i=0;i<tabUrls.length;i++){
			 ContentFragment fragment=ContentFragment.newInstance(tabUrls[i]);
			 mList.add(fragment);
		 }
		 FragmentManager manager = getSupportFragmentManager();
		 Mypager adapter=new Mypager(manager);
		 mVp.setAdapter(adapter);
	}
	OnClickListener listener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.img_back:
				finish();
				break;

			default:
				break;
			}
			
		}
	};
	
	class Mypager extends FragmentPagerAdapter{

		public Mypager(FragmentManager fm) {
			super(fm);
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return tabNames[position];
		}
		@Override
		public Fragment getItem(int position) {
			return mList.get(position);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList!=null?mList.size():0;
		}
		
	}

}
