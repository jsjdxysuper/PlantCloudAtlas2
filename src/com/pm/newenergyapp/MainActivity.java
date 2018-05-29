package com.pm.newenergyapp;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
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

import com.kedong.utils.SessionUtil;


public class MainActivity extends Activity {

	static MainActivity instance;
	
	Context context = null;
	LocalActivityManager manager = null;
	ViewPager pager = null;
	TabHost tabHost = null;
	TextView t1,t2,t3;

	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private ImageView cursor;// 动画图片


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

//		CookieSyncManager.createInstance(this);
//		CookieManager cookieManager = CookieManager.getInstance();
//		String cookieString = SessionUtil.cookie.getName()+"="+SessionUtil.cookie.getValue()+
//				";domain="+SessionUtil.cookie.getDomain();
//		cookieManager.setCookie(FgsActivity.url, cookieString);
//		CookieSyncManager.getInstance().sync();
		//监控网络状态服务启动
//		Intent i = new Intent(context, NetworkStateService.class);
//		startService(i);

		InitImageView();
		initTextView();
		initPagerViewer();

		new Thread() {
			public void run() {
				try {
					FgsActivity.loadurl();
					Thread.sleep(1000);
					FgsActivity.loadurl();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					DcActivity.loadurl();
					Thread.sleep(1000);
					DcActivity.loadurl();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					JzActivity.loadurl();
					Thread.sleep(1000);
					JzActivity.loadurl();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
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
		Intent intent = new Intent(context, FgsActivity.class);
		list.add(getView("A", intent));
		Intent intent2 = new Intent(context, DcActivity.class);
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
				FgsActivity.loadurl();
			if (currIndex == 1)
				DcActivity.loadurl();
			if (currIndex == 2)
				JzActivity.loadurl();
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