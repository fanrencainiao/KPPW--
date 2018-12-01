package com.kekezu.kppw.activity;

import java.util.List;

import net.tsz.afinal.FinalDb;

import org.greenrobot.eventbus.EventBus;

import com.kekezu.kppw.R;
import com.kekezu.kppw.bean.TestEvent;
import com.kekezu.kppw.bean.UserBean;
import com.kekezu.kppw.dataprocess.RegisterDP;
import com.kekezu.kppw.tools.StatusBarUtil;
import com.kekezu.kppw.utils.ConfigInc;
import com.kekezu.kppw.utils.StrFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 修改登录密码
 * @author cm
 *
 */
public class UserSecurityChangePassword extends Activity
{
	TextView text_title;
	ImageView imgBack;
	EditText editOldPwd;
	EditText editPassWord;
	EditText editRePassWord;
	Button btnLoginPwd;
	
	FinalDb db;
	List<UserBean> users;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// sdk2.3以后再主线程里面是是建议访问网络的，所以要更改activity的启动模式。
				StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
						.detectDiskReads().detectDiskWrites().detectNetwork()
						.penaltyLog().build());
				StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
						.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
						.build());
				
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_security_change_password);
		StatusBarUtil.setStatusBarLightMode(getWindow());
		ViewInit();
	}

	private void ViewInit()
	{
		db = ConfigInc.getCreateDB(this);
		users = db.findAll(UserBean.class);
		
		text_title = (TextView) findViewById(R.id.header_title);
		imgBack = (ImageView) findViewById(R.id.img_back);
		editOldPwd = (EditText) findViewById(R.id.edit_oldpwd);
		editPassWord = (EditText) findViewById(R.id.edit_password);
		editRePassWord = (EditText) findViewById(R.id.edit_repassword);
		btnLoginPwd = (Button) findViewById(R.id.btn_loginpwd);

		text_title.setText("登录密码修改");

		imgBack.setOnClickListener(listener);
		btnLoginPwd.setOnClickListener(listener);

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
			case R.id.btn_loginpwd:
				changePwd();
				break;
			default:
				break;
			}
		}
	};

	protected void changePwd()
	{
		if (StrFormat.formatStr(editOldPwd.getText().toString())
				&& StrFormat.formatStr(editPassWord.getText().toString())
				&& StrFormat.formatStr(editRePassWord.getText().toString()))
		{
			if (editPassWord.getText().toString()
					.equals(editRePassWord.getText().toString()))
			{
				String str[]=RegisterDP.userChangePwd(editOldPwd.getText().toString(),
						editPassWord.getText().toString(), editRePassWord
								.getText().toString(), UserSecurityChangePassword.this);
				
				// 对返回的数据进行判断
				if (str[0].equals("1000"))
				{
					finish();
					db.deleteById(UserBean.class, users.get(0).getId());
					TestEvent event = new TestEvent();
					event.setLogin_out(true);
					EventBus.getDefault().post(event);
					Toast.makeText(this, "修改成功，请用新密码登录", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(this, str[1],
							Toast.LENGTH_SHORT).show();
				}
				

			}
			else
			{
				Toast.makeText(UserSecurityChangePassword.this, "确认密码和新密码不一致",
						Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			if (StrFormat.formatStr(editOldPwd.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePassword.this, "请输入原始密码",
						Toast.LENGTH_SHORT).show();
			}
			else if (StrFormat.formatStr(editPassWord.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePassword.this, "请输入新密码",
						Toast.LENGTH_SHORT).show();
			}
			else if (StrFormat.formatStr(editRePassWord.getText().toString()) == false)
			{
				Toast.makeText(UserSecurityChangePassword.this, "请输入确认密码",
						Toast.LENGTH_SHORT).show();
			}
		}

	}
}
