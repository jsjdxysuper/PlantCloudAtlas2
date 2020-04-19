package com.kedong.newenergyapp.bussiness;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Toast;
import com.kedong.app.BaseActivity;
import com.kedong.newenergyapp.rsa.RSAUtils;
import com.kedong.newenergyapp.service.CheckNewsIntentService;
import com.kedong.utils.DESUtil;
import com.kedong.utils.SessionUtil;
import com.kedong.utils.WholenessCheck;
import com.kedong.newenergyapp.R;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

import ezy.boost.update.IUpdateParser;
import ezy.boost.update.UpdateInfo;
import ezy.boost.update.UpdateManager;

public class LoginActivity extends BaseActivity {

	private EditText mUser; // 帐号编辑框
	private EditText mPassword; // 密码编辑框
	private EditText checkCodeET;
	private String loginServletURL = "";

    private String hardwareId;
	private int loginCheckResult = 0;
	private CheckBox rem_pw;
	private CheckBox auto_login;
	private SharedPreferences sp;
	private String userid = "";

	private ProgressDialog progressDialog;
	private String FGS_URL;
	private String DC_URL;
	private String JZ_URL;
	private String RSA_URL;
	private Handler webHandler;

	private ImageView imageCheckControl;

	private CookieStore cookieStore = null;

    private Handler handler=new Handler();
    private Runnable imageCheckRunnable;
	private List<UserPage>userPageList;
	int notification_id=1;

	public void checkUpdate(){
		UpdateManager.create(this).setUrl( getApplication().getString(R.string.update_check_url)).setParser(new IUpdateParser() {
			@Override
			public UpdateInfo parse(String source) throws Exception {
				JSONObject jo = new JSONObject(source);
				UpdateInfo info = new UpdateInfo();// todo
				info.md5 = jo.getString("md5");
				info.size = jo.getInt("size");
				info.updateContent = jo.getString("updatecontent");
				info.url = jo.getString("url");
				info.versionCode = jo.getInt("versioncode");
				info.versionName = jo.getString("versionname");

				if(Utility.getVersionCode(getApplicationContext())<info.versionCode){
					info.hasUpdate = true;
					info.isForce = jo.getInt("isforce")==1?true:false;
					info.isSilent = jo.getInt("issilent")==1?true:false;
					info.isAutoInstall = jo.getInt("isautoinstall")==1?true:false;
					info.isIgnorable = jo.getInt("isignorable")==1?true:false;
					info.maxTimes = jo.getInt("maxtimes");
				}

				return info;
			}
		}).setWifiOnly(false).check();
	}

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




        imageCheckRunnable=new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情
                webGetCheckImage(null);
                handler.postDelayed(this, 1000*60);
            }
        };



		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelogin);

		FGS_URL = getApplication().getString(R.string.page1_url);
		DC_URL = getApplication().getString(R.string.page2_url);
		JZ_URL = getApplication().getString(R.string.page3_url);
		hardwareId = Utility.getUnicId(this);
		//获得实例对象
		sp = this.getSharedPreferences(getString(R.string.projectStore), Context.MODE_WORLD_READABLE);
		mUser = (EditText)findViewById(R.id.login_user_edit);
		checkCodeET = (EditText)findViewById(R.id.image_check_input);
		mPassword = (EditText)findViewById(R.id.login_passwd_edit);

		rem_pw = (CheckBox) findViewById(R.id.isjz);
		auto_login = (CheckBox) findViewById(R.id.iszd);

		imageCheckControl = (ImageView)findViewById(R.id.check_image_bitmap);
		webHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what == 0x123){//RSA
					int loginState = (int)msg.obj;
					progressDialog.dismiss();
					if(loginState==1) {
						Toast.makeText(LoginActivity.this,("获取加密组件成功\n"), Toast.LENGTH_SHORT).show();
						handler.postDelayed(imageCheckRunnable, 100);
                        // 判断自动登陆多选框状态
                        if (sp.getBoolean("AUTO_ISCHECK", false)) {
                            // 设置默认是自动登录状态
                            auto_login.setChecked(true);
//                            // 跳转界面
                            login_mainweixin(null);
                        }
					}
					else
						Dialog.showDialog("", "获取加密组件失败\n请检查网络连接", LoginActivity.this);
				}else if(msg.what == 0x124){//login
					progressDialog.dismiss();
					int loginState = (int)msg.obj;
					login_state(loginState);
				}
				else if(msg.what == 0x129){//login
					progressDialog.dismiss();
					Bitmap bitmap = (Bitmap)msg.obj;
					imageCheckControl.setImageBitmap(bitmap);
				}else if(msg.what == 0x130){//login
					progressDialog.dismiss();
					//Toast.makeText(LoginActivity.this,("通讯数据可能被篡改，请重新登录\n"), Toast.LENGTH_SHORT).show();
				}

			}
		};

		// 判断记住密码多选框的状态
		if (sp.getBoolean("ISCHECK", false)) {
			// 设置默认是记录密码状态
			rem_pw.setChecked(true);
			mUser.setText(sp.getString("USER_NAME", ""));
			mPassword.setText(sp.getString("PASSWORD", ""));
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
		//获取RSA加密的公钥
		getRSAPublic();
        checkUpdate();
	}


	private void sendNotification() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification.Builder builder = new Notification.Builder(this);
		Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.csdn.net/xiangzhihong8"));
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
		builder.setContentIntent(pendingIntent);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		builder.setAutoCancel(true);
		builder.setContentTitle(getApplication().getString(R.string.title_activity_title));
		builder.setContentText("水新处关于风电场整改的通知");
		nm.notify(0, builder.build());
	}

	public void good(){
		Dialog.showDialog("修改失败", "原密码不正确，\n请检查后重新输入！", LoginActivity.this);
	}
	public void webGetCheckImage(View v){
		new Thread(){
			public void run(){
				Bitmap bitmap = null;
				try {
					String imageCheckUrl = getApplication().getString(R.string.image_check_url);
					HttpPost httpPost = new HttpPost(imageCheckUrl);
					//在这里执行请求,访问url，并获取响应
//					HttpClient httpClient = new DefaultHttpClient();
					HttpClient httpClient = CertificateValidationIgnored.getNoCertificateHttpClient("");
					((AbstractHttpClient) httpClient).setCookieStore(SessionUtil.cookieStore);//写cookie
					// 请求超时  10s
					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000 ) ;
					// 读取超时  10s
					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000 );
					HttpResponse response = httpClient.execute( httpPost );

					//获取返回码,等于200即表示连接成功,并获得响应
					if(response.getStatusLine().getStatusCode() == 200) {
						//获取响应中的数据
						HttpEntity httpEntity = response.getEntity();
						String result= EntityUtils.toString(response.getEntity());
						JSONObject jo = new JSONObject(result);
						String checkCode = (String)jo.get("checkCode");
						DESUtil des = new DESUtil();
						checkCode = des.decrypt(checkCode);
						SessionUtil.checkCode = checkCode;
						String imageDataStr = (String)jo.get("imageData");
						String wholeCheck = (String)jo.get("wholeCheck");
						String localWholeCheck = WholenessCheck.justCode(checkCode,null);
						if(wholeCheck.compareTo(localWholeCheck)!=0)
						{
							Message msg = new Message();
							msg.what = 0x130;
							msg.obj = "";
							webHandler.sendMessage(msg);
						}


						if ("".compareTo(checkCode)==0||null==checkCode||
								"".compareTo(imageDataStr)==0||null==imageDataStr) {
							loginCheckResult = 2;
						} else {
							byte[]imageData = Base64.decode(imageDataStr,Base64.DEFAULT);
							bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
							loginCheckResult = 1;
						}
					}else {
						loginCheckResult = 3;
					}
				} catch (Exception e) {
					e.printStackTrace();
					loginCheckResult = 4;
				}

				Message msg = new Message();
				msg.what = 0x129;
				msg.obj = bitmap;
				webHandler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 点击登录后的，处理返回数据
	 */
	public void login_state(int loginState){
		if (loginState == 1) {
			if (rem_pw.isChecked()) {
				// 记住用户名、密码、
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("USER_NAME", mUser.getText().toString());
				editor.putString("PASSWORD", mPassword.getText().toString());
				editor.commit();
			}

			userid = mUser.getText().toString();


			UserinfoActivity.setUserid(userid);
			ChangepwActivity.setUserid(userid);

            handler.removeCallbacks(imageCheckRunnable);
			//startTimer();//登录超时自动跳到登录界面
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this,MainActivity.class);
			startActivity(intent);
			this.finish();
		} else if (loginState == 2) {
			Dialog.showDialog("登录失败", "用户或者密码不正确或者设备未注册，\n请检查后重新输入，或联系管理员！", LoginActivity.this);
            webGetCheckImage(null);
		} else if (loginState == 3) {
			Dialog.showDialog("系统提示", "数据连接失败，\n请检查网络状态！", LoginActivity.this);
		} else if (loginState == 4) {
			Dialog.showDialog("系统提示", "登录异常，\n请检查网络状态！", LoginActivity.this);
		} else if (loginState == 5) {
			Dialog.showDialog("系统提示", "登录超时，\n请检查网络状态！", LoginActivity.this);
		} else if (loginState == 6) {
			Dialog.showDialog("系统提示", "密码或验证码输入次数超过3次，\n请等待20分钟后再试！", LoginActivity.this);
		}else if (loginState == 7) {
			Dialog.showDialog("系统提示", "返回结果遭到恶意用户篡改，\n请重新登录！", LoginActivity.this);
		}else if (loginState == 9) {
			Dialog.showDialog("登录错误", "验证码错误或为空，\n请输入后再登录！", LoginActivity.this);
            webGetCheckImage(null);
		}
	}
	public void login_mainweixin(View v) {
		String pageName[] = {"风电","光伏","日报"};
		String pageU[] = {"https://www.lnsdxny.top/NEApp/windOutline/init","https://www.lnsdxny.top/NEApp/lightOutline/init","https://www.lnsdxny.top/NEApp/dayReportOutline/init"};
		List<UserPage> listUserPage = new ArrayList<UserPage>();
		for(int i=0;i<3;i++){
			UserPage temp = new UserPage();

			temp.setId("sxc");
			temp.setOrder_int(i);
			temp.setPagename(pageName[i]);
			temp.setPageUrl(pageU[i]);
			listUserPage.add(temp);
		}
		MainActivity.listUserPage = listUserPage;
		//login_state(1);
        if (isConnectInternet() == true) {
			if ("".equals(mUser.getText().toString()) || "".equals(mPassword.getText().toString())) {
				Dialog.showDialog("登录错误", "用户或者密码不能为空，\n请输入后再登录！", LoginActivity.this);
                webGetCheckImage(null);
			} else {
				checkLogin();
			}
		} else{
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

	/**
	 * RSA1024获取加密公钥
	 */
	public void getRSAPublic(){
		RSA_URL = getApplication().getString(R.string.RSA_url);
		progressDialog=ProgressDialog.show(LoginActivity.this, "启动", "获取加密组件，请稍后……");
		new Thread(){
			public void run(){
				try {
//					HttpClient httpClient = new DefaultHttpClient() ;
					HttpClient httpClient = CertificateValidationIgnored.getNoCertificateHttpClient("");
					HttpPost httpPost = new HttpPost(RSA_URL);
					HttpResponse response = httpClient.execute( httpPost);
					if(response.getStatusLine().getStatusCode() == 200) {
						//获取响应中的数据
						String result= EntityUtils.toString(response.getEntity());
						JSONObject jo = new JSONObject(result);
						setCookieStore(response);

						RSAUtils.RSA_publicExponent = jo.getString("public_exponent");
						RSAUtils.RSA_modulus = jo.getString("modulus");
						String remoteCheckCode = (String)jo.getString("MD5Code");
						String localCheckCode = WholenessCheck.justCode(RSAUtils.RSA_modulus+RSAUtils.RSA_publicExponent,null);
						if(remoteCheckCode.compareTo(localCheckCode)!=0){
							Message msg = new Message();
							msg.what = 0x130;
							msg.obj = "";
							webHandler.sendMessage(msg);
						}
						loginCheckResult = 1;
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
				msg.what = 0x123;
				msg.obj = loginCheckResult;
				webHandler.sendMessage(msg);
			}
		}.start();
	}
	public void checkLogin() {
		loginCheckResult = 0;
		loginServletURL = getApplication().getString(R.string.login_url);


//		if("".equals(checkCode)||!checkCode.equals(SessionUtil.checkCode)){
//			Dialog.showDialog("登录错误", "验证码错误或为空，\n请输入后再登录！", LoginActivity.this);
//		}
//		else
//			loginServletURL += "?userId=" + userid + "&pwd=" + password+"&hardwareId="+hardwareId;
		// 设置HTTP POST请求参数必须用NameValuePair对象
		progressDialog=ProgressDialog.show(LoginActivity.this, "登录", "验证中，请稍后……");
		new Thread() {
			public void run() {
				try {
//					HttpClient httpClient = new DefaultHttpClient();
					HttpClient httpClient = CertificateValidationIgnored.getNoCertificateHttpClient("");
					((AbstractHttpClient) httpClient).setCookieStore(SessionUtil.cookieStore);//写cookie
					//调用servlet的doget方法
					HttpPost httpPost = new HttpPost(loginServletURL);
					String userid = mUser.getText().toString();
					String password = mPassword.getText().toString();
					String checkCode = checkCodeET.getText().toString();
					DESUtil du = new DESUtil();
                    String des_password = du.encrypt(password);
					//使用模和指数生成公钥和私钥
					RSAPublicKey pubKey = RSAUtils.getPublicKey(RSAUtils.RSA_modulus, RSAUtils.RSA_publicExponent);

					//加密后的密文
					String secret_userid = RSAUtils.encryptByPublicKey(userid, pubKey);
					String secret_pwd = RSAUtils.encryptByPublicKey(des_password, pubKey);
					// 设置httpPost请求参数
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userId",secret_userid));
					params.add(new BasicNameValuePair("pwd", secret_pwd));

					params.add(new BasicNameValuePair("hardwareId",hardwareId));
					params.add(new BasicNameValuePair("checkCode",checkCode));

					String MD5Code = WholenessCheck.justCode(secret_userid+secret_pwd+hardwareId+checkCode,null);
					params.add(new BasicNameValuePair("MD5Code",MD5Code));
					httpPost.setEntity(new UrlEncodedFormEntity( params , HTTP.UTF_8 ));
					//在这里执行请求,访问url，并获取响应

					// 请求超时  10s
					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000 ) ;
					// 读取超时  10s
					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000 );
//                    HttpContext context = new BasicHttpContext();
//                    CookieStore cookieStore = new BasicCookieStore();
//                    context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

					HttpResponse response = httpClient.execute( httpPost) ;
					int resCode = response.getStatusLine().getStatusCode();
					//获取返回码,等于200即表示连接成功,并获得响应
					if(resCode == 200) {
						//获取响应中的数据
						String result= EntityUtils.toString(response.getEntity());
						JSONObject jo = new JSONObject(result);

						String checkCodeRemote = (String )jo.remove("MD5Code");

						String joStr = WholenessCheck.decode(jo.toString(), RSAUtils.RSA_modulus);
						JSONObject joTemp = new JSONObject(joStr);
//						String checkCodeLocal = (String )joTemp.remove("MD5Code");
//						if(checkCodeRemote.compareTo(checkCodeLocal)!=0){
//							Message msg = new Message();
//							msg.what = 0x130;
//							msg.obj = "";
//							webHandler.sendMessage(msg);
//						}
//						else
						if (Integer.parseInt(jo.get("code").toString()) == 0||Integer.parseInt(jo.get("code").toString()) == 4) {//登陆成功,0成功，4硬件id以前为空
							//setCookieStore(response);
								String msgStr = jo.get("msg").toString();
								JSONArray ja = new JSONArray(msgStr);
//							List<UserPage> list = (List)JSONArray.toList(ja, UserPage.class);
							List<UserPage> listUserPage = new ArrayList<UserPage>();
							for(int i=0;i<ja.length();i++){
								UserPage temp = new UserPage();
								JSONObject joTempUserpage = ja.getJSONObject(i);
								temp.setId(joTempUserpage.get("id").toString());
								temp.setOrder_int(Integer.parseInt(joTempUserpage.get("order_int").toString()));
								temp.setPagename(joTempUserpage.get("pagename").toString());
								temp.setPageUrl(joTempUserpage.get("pageUrl").toString());
								listUserPage.add(temp);
							}
							MainActivity.listUserPage = listUserPage;
							loginCheckResult = 1;
						} else if (Integer.parseInt(jo.get("code").toString()) == 1||Integer.parseInt(jo.get("code").toString()) == 2||
									Integer.parseInt(jo.get("code").toString()) == 3) {//密码或者用户名错误，或者登陆手机不是注册过的id
							loginCheckResult = 2;
						}else if (Integer.parseInt(jo.get("code").toString()) == 6) {//输入密码错误次数超过3次
							loginCheckResult = 6;
						}else if (Integer.parseInt(jo.get("code").toString()) == 7) {//验证码输入错误
							loginCheckResult = 9;
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
				msg.what = 0x124;
				msg.obj = loginCheckResult;
				webHandler.sendMessage(msg);
			}
		}.start();

	}

	/**
	 * 将cookie保存到静态变量中供后续调用
	 * @param httpResponse
	 */
	public void setCookieStore(HttpResponse httpResponse) {
		System.out.println("----setCookieStore");
		cookieStore = new BasicCookieStore();
		// JSESSIONID
		String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
		String JSESSIONID = setCookie.substring("JSESSIONID=".length(),
				setCookie.indexOf(";"));
		System.out.println("JSESSIONID:" + JSESSIONID);
		// 新建一个Cookie
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID",JSESSIONID);
		cookie.setVersion(0);
		cookie.setDomain(getApplication().getString(R.string.domain));
		cookie.setPath("/"+getApplication().getString(R.string.projectPath));
		cookieStore.addCookie(cookie);
		SessionUtil.cookieStore = cookieStore;
	}
}
