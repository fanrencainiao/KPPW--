package com.kekezu.kppw.imcustom;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMCore;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMConversationListUI;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWCustomConversationBody;
import com.alibaba.mobileim.conversation.YWTribeConversationBody;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeType;
import com.alibaba.mobileim.kit.contact.YWContactHeadLoadHelper;
import com.alibaba.mobileim.lib.presenter.conversation.CustomConversation;
import com.alibaba.mobileim.lib.presenter.conversation.CustomViewConversation;
import com.alibaba.mobileim.ui.IYWConversationFragment;
import com.alibaba.mobileim.utility.IMSmilyCache;
import com.alibaba.mobileim.utility.IMUtil;
import com.bumptech.glide.Glide;
import com.kekezu.kppw.R;
import com.kekezu.kppw.tools.StatusBarUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 最近会话界面的定制点(根据需要实现相应的接口来达到自定义会话列表界面)，不设置则使用openIM默认的实现 调用方设置的回调，必须继承BaseAdvice
 * 根据不同的需求实现 不同的 开放的 Advice
 * com.alibaba.mobileim.aop.pointcuts包下开放了不同的Advice.通过实现多个接口，组合成对不同的ui界面的定制
 * 这里设置了自定义会话的定制 1.CustomConversationAdvice 实现自定义会话的ui定制
 * 2.CustomConversationTitleBarAdvice 实现自定义会话列表的标题的ui定制
 * <p/>
 * 另外需要在application中将这个Advice绑定。设置以下代码
 * AdviceBinder.bindAdvice(PointCutEnum.CONVERSATION_FRAGMENT_POINTCUT,
 * CustomChattingAdviceDemo.class);
 * 
 * @author jing.huai
 */
public class ConversationListUICustomSample extends IMConversationListUI
{
	private static final String TAG = "ConversationListUICustomSample";

	public ConversationListUICustomSample(Pointcut pointcut)
	{
		super(pointcut);
	}

	/**
	 * 返回会话列表的自定义标题
	 * 
	 * @param fragment
	 * @param context
	 * @param inflater
	 * @return
	 */
	@Override
	public View getCustomConversationListTitle(final Fragment fragment,
			final Context context, LayoutInflater inflater)
	{
		// TODO 重要：必须以该形式初始化customView---［inflate(R.layout.**, new
		// RelativeLayout(context),false)］------，以让inflater知道父布局的类型，否则布局xml**中定义的高度和宽度无效，均被默认的wrap_content替代
		RelativeLayout customView = (RelativeLayout) inflater.inflate(
				R.layout.demo_custom_conversation_title_bar, new RelativeLayout(context),
				false);
		// customView.setBackgroundColor(Color.parseColor("#00b4ff"));
		TextView title = (TextView) customView.findViewById(R.id.title_txt);
		final YWIMKit mIMKit = LoginSampleHelper.getInstance().getIMKit();

		title.setText("消息");
		title.setTextColor(Color.BLACK);
		final String loginUserId = LoginSampleHelper.getInstance().getIMKit().getIMCore()
				.getLoginUserId();
		final String appKey = LoginSampleHelper.getInstance().getIMKit().getIMCore()
				.getAppKey();
		if (TextUtils.isEmpty(loginUserId) || TextUtils.isEmpty(appKey))
		{
			title.setText("未登录");
		}
		TextView backButton = (TextView) customView.findViewById(R.id.left_button);
		backButton.setVisibility(View.GONE);

		TextView rightButton = (TextView) customView.findViewById(R.id.right_button);

		rightButton.setVisibility(View.GONE);
		// rightButton.setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// mIMKit.getConversationService().markAllReaded();
		// }
		// });
		return customView;
	}

	/**
	 * 高级功能，通知调用方 消息漫游接收的状态 （可选 ） 可以通过 fragment.getActivity() 拿到Context
	 * 
	 * @param mCustomTitleView
	 *            用户设置的自定义标题 View
	 * @param isVisible
	 *            是否显示“正在接收消息中” true:调用方需要去显示“消息接收中的菊花” false:调方用需要隐藏“消息接收中的菊花”
	 */
	@Override
	public void setCustomTitleProgressBar(Fragment fragment, View mCustomTitleView,
			boolean isVisible)
	{

	}

	@Override
	public boolean needHideTitleView(Fragment fragment)
	{
		return false;
	}

	@Override
	public boolean needHideNullNetWarn(Fragment fragment)
	{
		return false;
	}

	/**
	 * 是否支持下拉刷新
	 */
	@Override
	public boolean getPullToRefreshEnabled()
	{
		return true;
	}

	/**
	 * 返回自定义置顶回话的背景色(16进制字符串形式)
	 * 
	 * @return
	 */
	@Override
	public String getCustomTopConversationColor()
	{
		return "#ffffff";
	}

	@Override
	public boolean enableSearchConversations(Fragment fragment)
	{
		return false;
	}

	/**
	 * {@link ConversationListUICustomSample#getCustomView(Context, YWConversation, View, ViewGroup)}
	 * 中使用到的ViewHolder示例
	 */
	static class ViewHolder
	{
		TextView conversationName;
		TextView conversationContent;
	}

	/**
	 * 会话列表onDestroy事件
	 * 
	 * @param fragment
	 */
	@Override
	public void onDestroy(Fragment fragment)
	{
		super.onDestroy(fragment);
	}

	/**
	 * 会话列表onResume事件
	 * 
	 * @param fragment
	 */
	@Override
	public void onResume(Fragment fragment)
	{
		super.onResume(fragment);
	}

	private IYWConversationFragment mConversationFragment;

	/**
	 * 会话列表初始化完成回调
	 * 
	 * @param fragment
	 *            会话列表Fragment
	 */
	@Override
	public void onInitFinished(IYWConversationFragment fragment)
	{
		// //TODO 为了防止内存泄露这里请使用弱引用方式
		WeakReference<IYWConversationFragment> reference = new WeakReference<IYWConversationFragment>(
				fragment);
		// 获取IYWConversationFragment实例，开发者可以通过该实例主动调用该接口内的方法
		mConversationFragment = reference.get();
		// //TODO 由于是弱引用，所以conversationFragment可能为null，因此使用时一定要判空
		if (mConversationFragment != null)
		{
			// 刷新adapter
			mConversationFragment.refreshAdapter();
		}
	}

	/**
	 * 该方法可以构造一个会话列表为空时的展示View
	 * 
	 * @return empty view
	 */
	@Override
	public View getCustomEmptyViewInConversationUI(Context context)
	{
		/** 以下为示例代码，开发者可以按需返回任何view */
		TextView textView = new TextView(context);
		textView.setText("还没有会话哦，快去找人聊聊吧!");
		textView.setGravity(Gravity.CENTER);
		textView.setTextSize(18);
		return textView;
	}

	/**
	 * 返回设置最近联系人界面背景的资源Id,返回0则使用默认值
	 * 
	 * @return 资源Id
	 */
	@Override
	public int getCustomBackgroundResId()
	{
		return 0;
	}

	/*********** 以下是定制会话item view的示例代码 ***********/
	// 有几种自定义，数组元素就需要几个，数组元素值从0开始
	// private final int[] viewTypeArray = {0,1,2,3}，这样就有4种自定义View
	private final int[] viewTypeArray = { 0, 1 };

	/**
	 * 自定义item view的种类数
	 * 
	 * @return 种类数
	 */
	@Override
	public int getCustomItemViewTypeCount()
	{
		return viewTypeArray.length;
	}

	/**
	 * 自定义item的viewType
	 * 
	 * @param conversation
	 * @return
	 */
	@Override
	public int getCustomItemViewType(YWConversation conversation)
	{
		// todo 若修改 YWConversationType.Tribe为自己type，SDK认为您要在｛@link
		// #getCustomItemView｝中完全自定义，针对群的自定义，如getTribeConversationHead会失效。
		// todo 该原则同样适用于 YWConversationType.P2P等其它内部类型，请知晓！
		if (conversation.getConversationType() == YWConversationType.Custom)
		{
			CustomConversation customConversation = (CustomConversation) conversation;
			if (customConversation.getConversationBody() instanceof YWCustomConversationBody)
			{
				YWCustomConversationBody conversationBody = (YWCustomConversationBody) customConversation
						.getConversationBody();

				if (!customConversation.isTop())
				{
					LoginSampleHelper.getInstance().getIMKit().getConversationService()
							.setTopConversation(customConversation);
				}

				if (conversationBody.getSubType() == 0)
				{
					Log.e("0", "000000000");
					return viewTypeArray[0];
				}
				else if (conversationBody.getSubType() == 1)
				{
					Log.e("1", "111111111");
					return viewTypeArray[1];
				}
			}
		}
		// 这里必须调用基类方法返回！！
		return super.getCustomItemViewType(conversation);
	}

	/**
	 * 根据viewType自定义item的view
	 * 
	 * @param fragment
	 * @param conversation
	 *            当前item对应的会话
	 * @param convertView
	 *            convertView
	 * @param viewType
	 *            当前itemView的viewType
	 * @param headLoadHelper
	 *            加载头像管理器，用户可以使用该管理器设置头像
	 * @param parent
	 *            getView中的ViewGroup参数
	 * @return
	 */
	@Override
	public View getCustomItemView(Fragment fragment, YWConversation conversation,
			View convertView, int viewType, YWContactHeadLoadHelper headLoadHelper,
			ViewGroup parent)
	{
		ViewHolder3 holder = null;
		if (convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(fragment.getActivity());
			holder = new ViewHolder3();
			convertView = inflater.inflate(R.layout.custom_list_addview11, parent, false);
			holder.imgPic = (ImageView) convertView.findViewById(R.id.imageView6);
			holder.tvTitleName = (TextView) convertView.findViewById(R.id.textView29);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder3) convertView.getTag();
		}
		if (viewType == viewTypeArray[0])
		{
			Glide.with(fragment.getActivity()).load("").placeholder(R.drawable.ic_im_msg)
					.into(holder.imgPic);
			holder.tvTitleName.setText("交易动态");
			return convertView;
		}
		else if (viewType == viewTypeArray[1])
		{
			Glide.with(fragment.getActivity()).load("")
					.placeholder(R.drawable.ic_im_noti).into(holder.imgPic);
			holder.tvTitleName.setText("系统通知");
			return convertView;
		}
		return super.getCustomItemView(fragment, conversation, convertView, viewType,
				headLoadHelper, parent);
	}

	public class ViewHolder1
	{
		ImageView head;
		TextView name;
		TextView unread;
	}

	public class ViewHolder3
	{
		ImageView imgPic;
		TextView tvTitleName;
	}

	/*********** 以上是定制会话item view的示例代码 ***********/

	/**
	 * 自定义会话列表item中name view
	 * 
	 * @param fragment
	 *            会话列表fragment
	 * @param conversation
	 *            当前item的会话对象
	 * @param convertView
	 *            自定义name view
	 * @param defaultView
	 *            SDK中默认的name view，开发者可以从该textView中获取text、textColor、textSize等信息
	 * @return 自定义name view
	 */
	@Override
	public View getCustomConversationTitleView(Fragment fragment,
			YWConversation conversation, View convertView, TextView defaultView)
	{
		return null;
	}

	/**
	 * 自定义会话列表搜索框样式
	 * 
	 * @param fragment
	 *            会话列表fragment
	 * @param onClickListener
	 *            SDK中默认的搜索框点击事件，开发者可以使用该onClickListener，也可以自定义点击事件
	 * @return 自定义搜索框view
	 */
	// @Override
	// public View getCustomSearchView(Fragment fragment,
	// OnClickListener onClickListener)
	// {
	// // View view = View.inflate(fragment.getActivity(),
	// // R.layout.demo_custom_search_view, null);
	// // //TODO 开发者必须自己设置点击事件，对于自定义搜索框SDK内部不会设置点击事件
	// // view.setOnClickListener(onClickListener);
	// // return view;
	// return null;
	// }
}
