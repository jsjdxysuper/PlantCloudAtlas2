package com.pm.plantcloudatlas;

import java.util.Timer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FgsActivity extends Activity {
	
	static ProgressWebView wv;
	static String url;
	private long timeout = 10000;
    private Handler mHandler = new Handler();
    private Timer timer;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fgs);
		
		wv = (ProgressWebView) findViewById(R.id.webView1);
		
		//自动适应屏幕
		wv.getSettings().setUseWideViewPort(true);
		wv.getSettings().setLoadWithOverviewMode(true);

		//支持 Javascript
		wv.getSettings().setJavaScriptEnabled(true);  
		
		//不使用缓存
		wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		//得到焦点
		wv.requestFocus();
		
		
		
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl); 
				FgsActivity.loadoptionurl("file:///android_asset/nonet.html");
			}
		});
	}

	public static void loadoptionurl(String optionurl) {
		wv.loadUrl(optionurl);
	}
	
	public static void loadurl() {
		wv.loadUrl(url);
	}
	
	public static void stoploadurl() {
		wv.stopLoading();
	}
	
	public static void goback() {
		wv.goBack();
	}
	
	public static int getprogress() {
		return wv.getProgress();
	}
	
	public static void setUrl(String inputurl) {
		url = inputurl; 
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			// 创建退出对话框
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// 设置对话框标题
			isExit.setTitle("系统提示");
			// 设置对话框消息
			isExit.setMessage("确定要退出吗");
			// 添加选择按钮并注册监听
			isExit.setButton("确定", listener);
			isExit.setButton2("取消", listener);
			// 显示对话框
			isExit.show();

		}

		return false;

	}
	/**监听对话框里面的button点击事件*/
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};	
}
