package com.pm.newenergyapp;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.kobjects.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框
	private String loginServletURL = "";

    private String hardwareId;
	private int loginCheckResult = 0;
	private CheckBox rem_pw;
	private CheckBox auto_login;
	private SharedPreferences sp;
	private String userid = ""; 

	private String FGS_URL;
	private String DC_URL;
	private String JZ_URL;
	private String RSA_URL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelogin);

		FGS_URL = getApplication().getString(R.string.page1_url)+"?yhid=";
		DC_URL = getApplication().getString(R.string.page2_url)+"?yhid=";
		JZ_URL = getApplication().getString(R.string.page3_url)+"?yhid=";

		hardwareId = Utility.getUnicId(this);
		//获得实例对象
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		mUser = (EditText)findViewById(R.id.login_user_edit);
		mPassword = (EditText)findViewById(R.id.login_passwd_edit);
		rem_pw = (CheckBox) findViewById(R.id.isjz);
		auto_login = (CheckBox) findViewById(R.id.iszd);





		// 判断记住密码多选框的状态
		if (sp.getBoolean("ISCHECK", false)) {
			// 设置默认是记录密码状态
			rem_pw.setChecked(true);
			mUser.setText(sp.getString("USER_NAME", ""));
			mPassword.setText(sp.getString("PASSWORD", ""));
			// 判断自动登陆多选框状态
			if (sp.getBoolean("AUTO_ISCHECK", false)) {
				// 设置默认是自动登录状态
				auto_login.setChecked(true);
				// 跳转界面
				login_mainweixin(null);
			}
		}



		//监听记住密码多选框按钮事件
		rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (rem_pw.isChecked()) {
					System.out.println("记住密码已选中");
					sp.edit().putBoolean("ISCHECK", true).commit();
				}else {
					System.out.println("记住密码没有选中");
					sp.edit().putBoolean("ISCHECK", false).commit();
					auto_login.setChecked(false);
					sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				}

			}
		});

		//监听自动登录多选框事件
		auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (auto_login.isChecked()) {
					System.out.println("自动登录已选中");
					sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
					rem_pw.setChecked(true);
					sp.edit().putBoolean("ISCHECK", true).commit();

				} else {
					System.out.println("自动登录没有选中");
					sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				}
			}
		});
	}
	public void login_mainweixin(View v) {
		if (isConnectInternet() == true) {
			if ("".equals(mUser.getText().toString()) || "".equals(mPassword.getText().toString())) {
				Dialog.showDialog("登录错误", "用户或者密码不能为空，\n请输入后再登录！", LoginActivity.this);
			} else {
				int loginState = checkLogin();

				if (loginState == 1) {
					if (rem_pw.isChecked()) {
						// 记住用户名、密码、
						Editor editor = sp.edit();
						editor.putString("USER_NAME", mUser.getText().toString());
						editor.putString("PASSWORD", mPassword.getText().toString());
						editor.commit();
					}

					userid = mUser.getText().toString();
					
					
					UserinfoActivity.setUserid(userid);
					ChangepwActivity.setUserid(userid);
					
					
					FgsActivity.setUrl(FGS_URL + userid);
					DcActivity.setUrl(DC_URL + userid);
					JzActivity.setUrl(JZ_URL + userid);


					Intent intent = new Intent();
					intent.setClass(LoginActivity.this,LoadingActivity.class);
					startActivity(intent);
					this.finish();
				} else if (loginState == 2) {
					Dialog.showDialog("登录失败", "用户或者密码不正确或者设备未注册，\n请检查后重新输入！", LoginActivity.this);
				} else if (loginState == 3) {
					Dialog.showDialog("系统提示", "数据连接失败，\n请检查网络状态！", LoginActivity.this);
				} else if (loginState == 4) {
					Dialog.showDialog("系统提示", "登录异常，\n请检查网络状态！", LoginActivity.this);
				} else if (loginState == 5) {
					Dialog.showDialog("系统提示", "登录超时，\n请检查网络状态！", LoginActivity.this);
				}
			}
		} else {
			Dialog.showDialog("系统提示", "没有可用网络连接，\n请检查网络状态！", LoginActivity.this);
		}
	}

	public boolean isConnectInternet() {
		boolean netSataus = false;
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			netSataus = networkInfo.isAvailable();
		}
		return netSataus;
	}

	public int checkLogin() {
		loginCheckResult = 0;
		loginServletURL = getApplication().getString(R.string.login_url);
		RSA_URL = getApplication().getString(R.string.RSA_url);
		//loginServletURL += "?userId=" + userid + "&pwd=" + password+"&hardwareId="+hardwareId;
		// 设置HTTP POST请求参数必须用NameValuePair对象

		new Thread() {
			public void run() {
				try {
					//调用servlet的doget方法
					HttpPost httpPost = new HttpPost(loginServletURL);
					String userid = mUser.getText().toString();
					String password = mPassword.getText().toString();
					// 设置httpPost请求参数
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userId",userid));
					params.add(new BasicNameValuePair("pwd", password));

					params.add(new BasicNameValuePair("hardwareId",hardwareId));

					httpPost.setEntity(new UrlEncodedFormEntity( params , HTTP.UTF_8 ));
					//在这里执行请求,访问url，并获取响应
					HttpClient httpClient = new DefaultHttpClient() ;
					// 请求超时  10s
					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000 ) ;
					// 读取超时  10s
					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000 );
					HttpResponse response = httpClient.execute( httpPost ) ;
					//获取返回码,等于200即表示连接成功,并获得响应
					if(response.getStatusLine().getStatusCode() == 200) {
						//获取响应中的数据
						String result= EntityUtils.toString(response.getEntity());
						JSONObject jo = new JSONObject(result);
						if (Integer.parseInt(jo.get("code").toString()) == 0||Integer.parseInt(jo.get("code").toString()) == 4) {
							loginCheckResult = 1;
						} else if (Integer.parseInt(jo.get("code").toString()) == 1||Integer.parseInt(jo.get("code").toString()) == 2||
									Integer.parseInt(jo.get("code").toString()) == 3) {
							loginCheckResult = 2;
						}
					}else {
						loginCheckResult = 3;
					}
				} catch (Exception e) {
					loginCheckResult = 4;
				}
			}
		}.start();

		int s = 0;
		while (true) {
			if (s >= 20) {
				loginCheckResult = 5;
				break;
			}
			if (loginCheckResult != 0)
				break;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			s ++;
		}
		return loginCheckResult;

	}
}
