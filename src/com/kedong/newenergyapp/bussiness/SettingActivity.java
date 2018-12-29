package com.kedong.newenergyapp.bussiness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import com.kedong.app.BaseActivity;
import com.kedong.newenergyapp.R;

public class SettingActivity extends BaseActivity {
	private RelativeLayout rl_userinfo;
	private RelativeLayout btn_changepw;
	private RelativeLayout btn_about;
	private SharedPreferences sp;
	static SettingActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.setting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelogin); 

		instance = this;
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);

//		rl_userinfo = (RelativeLayout) findViewById(R.id.btn_userinfo);
//		rl_userinfo.setClickable(true);
//		rl_userinfo.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				btnuserinfoClick();
//			}
//		});

		btn_changepw = (RelativeLayout) findViewById(R.id.btn_changepw);
		btn_changepw.setClickable(true);
		btn_changepw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnchangepwClick();
			}
		});
		
//		btn_about = (RelativeLayout) findViewById(R.id.btn_about);
//		btn_about.setClickable(true);
//		btn_about.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				btnaboutClick();
//			}
//		});
	}


	public void btnuserinfoClick() {
		if (isConnectInternet() == true) {
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this,UserinfoActivity.class);
			startActivity(intent);
		} else {
			Dialog.showDialog("系统提示", "没有可用网络连接，\n请设置网络状态！", SettingActivity.this);
		}
	}

	public void btnchangepwClick() {
		if (isConnectInternet() == true) {
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this,ChangepwActivity.class);
			startActivity(intent);
		} else {
			Dialog.showDialog("系统提示", "没有可用网络连接，\n请设置网络状态！", SettingActivity.this);
		}
	}

	public void btnaboutClick() {
		Intent intent = new Intent();
		intent.setClass(SettingActivity.this,AboutActivity.class);
		startActivity(intent);
	}

	public void btn_logout(View source) {
		Editor editor = sp.edit();
		editor.putString("USER_NAME", null);
		editor.putString("PASSWORD", null);
		editor.putBoolean("ISCHECK", false);
		editor.putBoolean("AUTO_ISCHECK", false);
		editor.commit();

		Intent intent = new Intent();
		intent.setClass(SettingActivity.this,LoginActivity.class);
		startActivity(intent);

		SettingActivity.this.finish();
//		MainActivity.getInstance().finish();
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


	public static SettingActivity getInstance() {
		return instance;
	}


	public static void setInstance(SettingActivity instance) {
		SettingActivity.instance = instance;
	}
}
