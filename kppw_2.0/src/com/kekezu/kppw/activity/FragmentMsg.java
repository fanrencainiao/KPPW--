package com.kekezu.kppw.activity;

import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWCustomConversationUpdateModel;
import com.alibaba.mobileim.login.IYWConnectionListener;
import com.alibaba.mobileim.login.YWLoginCode;
import com.alibaba.mobileim.login.YWLoginState;
import com.alibaba.mobileim.utility.IMPrefsTools;
import com.kekezu.kppw.MyApp;
import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.UserBean;
import com.kekezu.kppw.imcustom.LoginSampleHelper;
import com.kekezu.kppw.imcustom.NotificationInitSampleHelper;
import com.kekezu.kppw.utils.ConfigInc;

/**
 * 消息模块主页
 * 
 * @author cm
 * 
 */
public class FragmentMsg extends Fragment
{

	View view;

	YWIMKit mIMKit;
	IYWConversationService mConversationService;
	IYWConnectionListener mConnectionListener;

	Fragment videoFragment;
	FragmentTransaction transaction;

	private static final String USER_ID = "userId";
	private static final String PASSWORD = "password";
	private static final String APPKEY = "appkey";

	public static final String SYSTEM_TRIBE_CONVERSATION = "sysTribe";
	public static final String SYSTEM_FRIEND_REQ_CONVERSATION = "sysfrdreq";
	public static final String RELATED_ACCOUNT = "relatedAccount";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fg_msg, container, false);
		initView();
		addConnectionListener();
		return view;
	}

	public void initView()
	{
		FinalDb db = ConfigInc.getCreateDB(getActivity());
		List<UserBean> users = db.findAll(UserBean.class);

		// "testpro919", "taobao1234"

		mIMKit = LoginSampleHelper.getInstance().getIMKit();
		mConversationService = mIMKit.getConversationService();
		LoginSampleHelper.getInstance().login_Sample(users.get(0).getUid(),
				users.get(0).getImPass(), MyApp.APP_KEY, new IWxCallback()
				{
					@Override
					public void onSuccess(Object... arg0)
					{
						videoFragment = mIMKit.getConversationFragment();
						transaction = getChildFragmentManager().beginTransaction();
						transaction.add(R.id.frame1, videoFragment).commit();

						showListItemView("1", 1, new YWCustomConversationUpdateModel());
						showListItemView("1", 1, new YWCustomConversationUpdateModel());
						showListItemView("0", 0, new YWCustomConversationUpdateModel());
						showListItemView("0", 0, new YWCustomConversationUpdateModel());

					}

					@Override
					public void onProgress(int arg0)
					{
					}

					@Override
					public void onError(int errorCode, String errorMessage)
					{
						Log.e("错误码:" + errorCode, errorMessage);
					}
				});
		// 通知栏相关的初始化
		NotificationInitSampleHelper.init();
	}

	/**
	 * 保存登录的用户名密码到本地
	 * 
	 * @param userId
	 * @param password
	 */
	@SuppressWarnings("unused")
	private void saveLoginInfoToLocal(String userId, String password, String appkey)
	{
		IMPrefsTools.setStringPrefs(getActivity(), USER_ID, userId);
		IMPrefsTools.setStringPrefs(getActivity(), PASSWORD, password);
		IMPrefsTools.setStringPrefs(getActivity(), APPKEY, appkey);
	}

	private void showListItemView(String strId, int type,
			YWCustomConversationUpdateModel cu)
	{
		cu = new YWCustomConversationUpdateModel();
		cu.setIdentity(strId);
		cu.setContent(strId);
		cu.setSubType(type);
		cu.setLastestTime(new Date().getTime());

		mConversationService.updateOrCreateCustomConversation(cu);
	}

	// 设置连接状态的监听
	private void addConnectionListener()
	{
		mConnectionListener = new IYWConnectionListener()
		{
			@Override
			public void onDisconnect(int code, String info)
			{
				if (code == YWLoginCode.LOGON_FAIL_KICKOFF)
				{
					// 在其它终端登录，当前用户被踢下线
					Toast.makeText(MyApp.getContext(), "被踢下线", Toast.LENGTH_LONG).show();
					YWLog.i("LoginSampleHelper", "被踢下线");
					LoginSampleHelper.getInstance().setAutoLoginState(YWLoginState.idle);
					initView();
				}
			}

			@Override
			public void onReConnecting()
			{

			}

			@Override
			public void onReConnected()
			{

			}
		};
		mIMKit.getIMCore().addConnectionListener(mConnectionListener);
	}
}
