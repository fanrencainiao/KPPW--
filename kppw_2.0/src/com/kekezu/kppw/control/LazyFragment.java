package com.kekezu.kppw.control;

import android.support.v4.app.Fragment;

public abstract class LazyFragment extends Fragment
{
	protected boolean isVisible = false;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint())
		{
			isVisible = true;
			onVisible();
		}
		else
		{
			isVisible = false;
			onInVisible();
		}
	}

	private void onInVisible()
	{

	}

	private void onVisible()
	{
		LazyLoad();
	}

	public abstract void LazyLoad();
}
