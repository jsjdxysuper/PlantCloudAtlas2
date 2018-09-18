package com.pm.newenergyapp;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.pm.PackageInstaller;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.Settings;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.kedong.app.BaseActivity;
import com.kedong.newenergyapp.rsa.RSAUtils;
import com.kedong.utils.DESUtil;
import com.kedong.utils.SessionUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class MainActivity extends BaseActivity {

	static MainActivity instance;
	private Handler webHandler;
	Context context = null;
	LocalActivityManager manager = null;
	ViewPager pager = null;
	TabHost tabHost = null;
	TextView t1,t2,t3;

	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private ImageView cursor;// 动画图片
	private String deleteSessionUrl;
	private int pageNo;
	private List<JzActivity>allPageAct;
	@Override
	public void onDestroy(){

		super.onDestroy();
		new Thread() {
			int loginCheckResult = 0;
			public void run() {
				try {

//					HttpClient httpClient = new DefaultHttpClient();
					HttpClient httpClient = CertificateValidationIgnored.getNoCertificateHttpClient("");
					((AbstractHttpClient) httpClient).setCookieStore(SessionUtil.cookieStore);//写cookie
					//调用servlet的doget方法
					HttpPost httpPost = new HttpPost(deleteSessionUrl);


					// 请求超时  10s
					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000 ) ;
					// 读取超时  10s
					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000 );


					HttpResponse response = httpClient.execute( httpPost) ;
					//获取返回码,等于200即表示连接成功,并获得响应
					if(response.getStatusLine().getStatusCode() == 200) {
						//获取响应中的数据
						String result= EntityUtils.toString(response.getEntity());
						JSONObject jo = new JSONObject(result);
						if (Integer.parseInt(jo.get("code").toString()) == 0) {//登陆成功
							//setCookieStore(response);
							loginCheckResult = 1;
						} else{
							loginCheckResult = 2;
						}
					}else {
						loginCheckResult = 3;
					}
				}catch(ConnectTimeoutException e){
					loginCheckResult = 5;
				}
				catch (Exception e) {
					loginCheckResult = 4;
				}
				Message msg = new Message();
				msg.what = 0x127;
				msg.obj = loginCheckResult;
				webHandler.sendMessage(msg);
			}
		}.start();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		deleteSessionUrl = getApplication().getString(R.string.deleteSession_url);
		webHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what == 0x127){//RSA
					int loginState = (int)msg.obj;
					if(loginState==1)
						Toast.makeText(MainActivity.this,("销毁登陆信息成功\n"), Toast.LENGTH_SHORT).show();
				}
			}
		};
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		instance = this;
		context = MainActivity.this;
		manager = new LocalActivityManager(this , true);
		manager.dispatchCreate(savedInstanceState);
		String brand = Utility.getDeviceBrand();
		String androidId = Utility.getAndroidId(context);
		//String imbi = Utility.getIMEI(context);
		String serialNO = Utility.getSerialNO();
		String sysVersion = Utility.getSystemVersion();

        //可以让所有的webview共享此cookie
		CookieSyncManager.createInstance(getApplication());
		CookieManager cookieManager = CookieManager.getInstance();
        Cookie cookie = SessionUtil.cookieStore.getCookies().get(0);
		String cookieString = cookie.getName()+"="+cookie.getValue()+
				";domain="+cookie.getDomain();
		cookieManager.setCookie(FgsActivity.url, cookieString);
		CookieSyncManager.getInstance().sync();
		//监控网络状态服务启动
//		Intent i = new Intent(context, NetworkStateService.class);
//		startService(i);

		InitImageView();
		initTextView();
		initPagerViewer();

//		new Thread() {
//			public void run() {
//				try {
//					FgsActivity.loadurl();
//					Thread.sleep(1000);
//					FgsActivity.loadurl();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}.start();
//
//		new Thread() {
//			public void run() {
//				try {
//					DcActivity.loadurl();
//					Thread.sleep(1000);
//					DcActivity.loadurl();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}.start();
		allPageAct = new ArrayList<JzActivity>();
		for(pageNo = 0;pageNo<JzActivity.getUrlList().size();pageNo++){
			new Thread() {
				public void run() {
					try {
						JzActivity jzAc = new JzActivity();
						allPageAct.add(jzAc);
						jzAc.setUrl(JzActivity.getUrlList().get(pageNo).getPageUrl());
						jzAc.loadurl();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}

	}
	/**
	 * 初始化标题
	 */
	private void initTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);


		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));

	}
	/**
	 * 初始化PageViewer
	 */
	private void initPagerViewer() {
		pager = (ViewPager) findViewById(R.id.vPager);
		final ArrayList<View> list = new ArrayList<View>();
		Intent intent = new Intent(context, JzActivity.class);
		list.add(getView("A", intent));
		Intent intent2 = new Intent(context, JzActivity.class);
		list.add(getView("B", intent2));
		Intent intent3 = new Intent(context, JzActivity.class);
		list.add(getView("C", intent3));


		pager.setAdapter(new MyPagerAdapter(list));
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
//		cursor.setAlpha(255);// 设置透明度
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.tab_bg).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
//		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}


	/**
	 * 通过activity获取视图
	 * @param id
	 * @param intent
	 * @return
	 */
	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}


	/**
	 * Pager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter{
		List<View> list =  new ArrayList<View>();
		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}


		@Override
		public void destroyItem(ViewGroup container, int position,
				Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}


		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}


		@Override
		public int getCount() {
			return list.size();
		}
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}


		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {


		}


		@Override
		public Parcelable saveState() {
			return null;
		}


		@Override
		public void startUpdate(View arg0) {
		}
	}
	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {


		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量


		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);    
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}


		@Override
		public void onPageScrollStateChanged(int arg0) {

		}


		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}
	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;


		public MyOnClickListener(int i) {
			index = i;
		}


		@Override
		public void onClick(View v) {
			pager.setCurrentItem(index);
		}
	};

	public void btn_flash(View source) {
		if (isConnectInternet() == true) {
			if (currIndex == 0)
				allPageAct.get(0).loadurl();

			if (currIndex == 1)
				allPageAct.get(1).loadurl();
			if (currIndex == 2)
				allPageAct.get(2).loadurl();

//			if(FgsActivity.flag==1) {
//				//Toast.makeText(getApplicationContext(), ("用户访问内容被劫持，请重新刷新\n"), Toast.LENGTH_SHORT).show();
//				Dialog.showDialog("", "用户访问内容被劫持，请重新登录", MainActivity.this);
//				FgsActivity.flag=0;
//			}
//			if(DcActivity.flag==1) {
//				//Toast.makeText(getApplicationContext(), ("用户访问内容被劫持，请重新刷新\n"), Toast.LENGTH_SHORT).show();
//				Dialog.showDialog("", "用户访问内容被劫持，请重新登录", MainActivity.this);
//				DcActivity.flag=0;
//			}
//			if(JzActivity.flag==1) {
//				//Toast.makeText(getApplicationContext(), ("用户访问内容被劫持，请重新刷新\n"), Toast.LENGTH_SHORT).show();
//				Dialog.showDialog("", "用户访问内容被劫持，请重新登录", MainActivity.this);
//				JzActivity.flag=0;
//			}
		} else {
			Dialog.showDialog("系统提示", "没有可用网络连接，\n请设置网络状态！", MainActivity.this);
		}
	}
	
	public void btn_setting(View source) {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this,SettingActivity.class);
		startActivity(intent);
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
	
	public static MainActivity getInstance() {
		return instance;
	}
	public static void setInstance(MainActivity instance) {
		MainActivity.instance = instance;
	}

}