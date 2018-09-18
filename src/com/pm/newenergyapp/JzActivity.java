package com.pm.newenergyapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.kedong.app.BaseActivity;

public class JzActivity extends BaseActivity {

	public static List<UserPage> urlList = new ArrayList<UserPage>();
	private ProgressWebView wv;
	private String url;
	private long timeout = 10000;
    private Handler mHandler = new Handler();
    private Timer timer;
	public static int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jz);

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
				loadoptionurl("file:///android_asset/nonet.html");
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				String url1 =getApplication().getString(R.string.page1_url);
				String url2 = getApplication().getString(R.string.page2_url);
				String url3 = getApplication().getString(R.string.page3_url);
				if (url.contains(url1)||url.contains(url2)||url.contains(url3)) {
					return super.shouldInterceptRequest(view, url);//正常加载
				}else{

					flag = 1;
					return super.shouldInterceptRequest(view, url);//正常加载
				}
			}
		});
	}

	
	public void loadoptionurl(String optionurl) {
		wv.loadUrl(optionurl);
	}

	public void loadurl() {
		wv.loadUrl(url);
	}
	
	public void stoploadurl() {
		wv.stopLoading();
	}
	
	public void goback() {
		wv.goBack();
	}
	
	public int getprogress() {
		return wv.getProgress();
	}
	
	public void setUrl(String inputurl) {
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

	public ProgressWebView getWv() {
		return wv;
	}
	public String getUrl() {
		return url;
	}
	public void setWv(ProgressWebView wv) {
		this.wv = wv;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public static List<UserPage> getUrlList() {
		return urlList;
	}
	public static void setUrlList(List<UserPage> urlList) {
		JzActivity.urlList = urlList;
	}
}
