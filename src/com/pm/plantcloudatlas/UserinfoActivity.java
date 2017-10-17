package com.pm.plantcloudatlas;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class UserinfoActivity extends Activity {
	static String userid = "";
	private TextView tv_yh, tv_ssjt, tv_ssgs, tv_ssdc;
	private String str_yh = "";
	private String str_ssjt = "";
	private String str_ssgs = "";
	private String str_ssdc = "";
	String url = "http://192.168.1.105:8080/PlantCloudAtlasAppWebpub/UserinfoServlet?yhid=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.userinfo);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelogin); 

		tv_yh = (TextView) findViewById(R.id.yh);
		tv_ssjt = (TextView) findViewById(R.id.ssjt);
		tv_ssgs = (TextView) findViewById(R.id.ssgs);
		tv_ssdc = (TextView) findViewById(R.id.ssdc);

		setData();
	}


	public void setData() {
		getInfo();

		int s = 0;
		while (true) {
			if (s >= 20) {
				break;
			}
			if (str_yh.length() > 0 && str_ssjt.length() > 0 && str_ssgs.length() > 0 && str_ssdc.length() > 0) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s ++;
		}

		tv_yh.setText(str_yh);
		tv_ssjt.setText(str_ssjt);
		tv_ssgs.setText(str_ssgs);
		tv_ssdc.setText(str_ssdc);
	}




	public void getInfo() {
		new Thread() {
			public void run() {
				Looper.prepare(); 
				try {
					//调用servlet的doget方法
					HttpGet request = new HttpGet(url + userid);

					//在这里执行请求,访问url，并获取响应
					HttpResponse response = new DefaultHttpClient().execute(request); 

					//获取返回码,等于200即表示连接成功,并获得响应
					if(response.getStatusLine().getStatusCode() == 200) {
						//获取响应中的数据
						String result= EntityUtils.toString(response.getEntity());
						str_yh = result.split(",")[0];
						str_ssjt = result.split(",")[1];
						str_ssgs = result.split(",")[2];
						str_ssdc = result.split(",")[3];
					}else {
						Dialog.showDialog("系统提示", "数据连接失败，\n请设置网络状态！", UserinfoActivity.this);
					}
				} catch (Exception e) {}
				Looper.loop(); 
			}
		}.start();	
	}




	public static String getUserid() {
		return userid;
	}


	public static void setUserid(String userid) {
		UserinfoActivity.userid = userid;
	}
}
