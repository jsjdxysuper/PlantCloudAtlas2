package com.kedong.newenergyapp.bussiness;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.kedong.newenergyapp.rsa.RSAUtils;
import com.kedong.utils.DESUtil;
import com.kedong.utils.SessionUtil;
import com.kedong.utils.WholenessCheck;
import com.kedong.newenergyapp.R;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

public class ChangepwActivity extends Activity {
	private static String userid = "";
	private EditText et_ymm, et_xmm, et_xmmqr;
	private String loginServletURL = "";
	private int loginCheckResult = 0;
	private SharedPreferences sp;
	private Handler webHandler;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.changepw);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelogin); 
		
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		
		et_ymm = (EditText) findViewById(R.id.ymm);
		et_xmm = (EditText) findViewById(R.id.xmm);
		et_xmmqr = (EditText) findViewById(R.id.xmmqr);

		webHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what == 0x125){//RSA
					int loginState = (int)msg.obj;
					progressDialog.dismiss();
					webRetDeal(loginState);
				}else if(msg.what == 0x130){//login
					progressDialog.dismiss();
					//Toast.makeText(ChangepwActivity.this,("通讯数据可能被篡改，请重新登录\n"), Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	public void webRetDeal(int loginState){
		if (loginState == 1) {
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			isExit.setTitle("修改成功");
			isExit.setMessage("建议注销重新登录！\n是否要注销？");
			isExit.setButton("注销", listener);
			isExit.setButton2("否", listener);
			isExit.show();
		} else if (loginState == 2) {
			Dialog.showDialog("修改失败", "原密码不正确，\n请检查后重新输入！", ChangepwActivity.this);
		} else if (loginState == 3) {
			Dialog.showDialog("系统提示", "服务器没有相应，\n请检查网络状态！", ChangepwActivity.this);
		} else if (loginState == 4) {
			Dialog.showDialog("系统提示", "登录异常，\n请检查网络状态！", ChangepwActivity.this);
		} else if (loginState == 5) {
			Dialog.showDialog("系统提示", "登录超时，\n请检查网络状态！", ChangepwActivity.this);
		} else if (loginState == 6) {
			Dialog.showDialog("系统提示", "密码必须包含数字、特殊字符(~!@#$%^&*()_+[]{}|\\;:'\",./<>?)和字母中的2种，请重新输入！", ChangepwActivity.this);
		}
	}
	public boolean judgePwdQua(String newPwd){
//		1) 密码控制只能输入字母、数字、特殊符号(~!@#$%^&*()_+[]{}|\;:'",./<>?)
//		2) 长度 6-16 位，必须包括字母、数字、特殊符号中的2种
//		3) 密码不能包含用户名信息
//		判断密码是否包含数字：包含返回1，不包含返回0
		int i = newPwd.matches(".*\\d+.*") ? 1 : 0;

//		判断密码是否包含字母：包含返回1，不包含返回0
		int j = newPwd.matches(".*[a-zA-Z]+.*") ? 1 : 0;

//		判断密码是否包含特殊符号(~!@#$%^&*()_+|<>,.?/:;'[]{}\)：包含返回1，不包含返回0
		int k = newPwd.matches(".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*") ? 1 : 0;

//		判断密码长度是否在6-16位
		int l = newPwd.length();

//		判断密码中是否包含用户名
//		boolean contains = newPwd.contains(userId);

		if (i + j + k < 2 || l < 6 || l > 16 ) {
			return false;
		}else
			return true;//正确
	}
	public void changepassword(View source) {
		String ymm = et_ymm.getText().toString();
		String xmm = et_xmm.getText().toString();
		String xmmqr = et_xmmqr.getText().toString();
		if(!judgePwdQua(xmm)){
			Dialog.showDialog("错误", "密码必须包含数字、特殊字符(~!@#$%^&*()_+[]{}|\\;:'\",./<>?)和字母中的2种，\n请重新输入！", ChangepwActivity.this);
		}
		else if ("".equals(et_ymm.getText().toString()) || "".equals(et_xmm.getText().toString()) || "".equals(et_xmmqr.getText().toString())) {
			Dialog.showDialog("错误", "各输入框不能为空，\n请重新输入！", ChangepwActivity.this);
		} else if (et_xmm.getText().toString().equals(et_xmmqr.getText().toString()) == false){
			Dialog.showDialog("错误", "两次填写的密码不一致，\n请正确输入！", ChangepwActivity.this);
		} else {
			changePwdThreadFun();
		}
	}
	

	public void changePwdThreadFun() {
		loginCheckResult = 0;
		loginServletURL =getApplication().getString(R.string.change_pwd_url);
		progressDialog=ProgressDialog.show(ChangepwActivity.this, "启动", "正在验证原密码、提交修改后密码，请稍后……");
		new Thread() {
			public void run() {
				try {


//					String userid = mUser.getText().toString();
//					String password = mPassword.getText().toString();
					// 设置httpPost请求参数
					List<NameValuePair> params = new ArrayList<NameValuePair>();

					String ymm = et_ymm.getText().toString();
					String xmm = et_xmm.getText().toString();
					DESUtil du = new DESUtil();
					ymm = du.encrypt(ymm);
					xmm = du.encrypt(xmm);

					//使用模和指数生成公钥和私钥
					RSAPublicKey pubKey = RSAUtils.getPublicKey(RSAUtils.RSA_modulus, RSAUtils.RSA_publicExponent);
					//加密后的密文
					String secret_userid = RSAUtils.encryptByPublicKey(userid, pubKey);
					String secret_pwd = RSAUtils.encryptByPublicKey(xmm, pubKey);
					String secret_origin_pwd = RSAUtils.encryptByPublicKey(ymm, pubKey);
					params.add(new BasicNameValuePair("userId",secret_userid));
					params.add(new BasicNameValuePair("origin_pwd", secret_origin_pwd));
					params.add(new BasicNameValuePair("pwd",secret_pwd));
                    // 创建HttpClientBuilder
					String MD5Code = WholenessCheck.justCode(secret_userid+secret_origin_pwd+secret_pwd,null);
					params.add(new BasicNameValuePair("MD5Code",MD5Code));
                    HttpPost httpPost = new HttpPost(loginServletURL);
					httpPost.setEntity(new UrlEncodedFormEntity( params , HTTP.UTF_8 ));
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
						String result= EntityUtils.toString(response.getEntity());
                        JSONObject jo = new JSONObject(result);
						String checkCodeRemote = (String )jo.remove("MD5Code");

						String joStr = WholenessCheck.decode(jo.toString(), RSAUtils.RSA_modulus);
						JSONObject joTemp = new JSONObject(joStr);
						String checkCodeLocal = (String )joTemp.remove("MD5Code");
						if(checkCodeRemote.compareTo(checkCodeLocal)!=0){
							Message msg = new Message();
							msg.what = 0x130;
							msg.obj = "";
							webHandler.sendMessage(msg);
						}
						else if (Integer.parseInt(jo.get("code").toString()) == 0) {
							loginCheckResult = 1;
						} else if (Integer.parseInt(jo.get("code").toString()) == 1||
                                Integer.parseInt(jo.get("code").toString()) == 2) {
							loginCheckResult = 2;
						} else if (Integer.parseInt(jo.get("code").toString()) == 3) {
							loginCheckResult = 6;//密码太简单，不符合要求
						}else if (Integer.parseInt(jo.get("code").toString()) == 4) {
							loginCheckResult = 7;//完整性校验没通过
							Message msg = new Message();
							msg.what = 0x130;
							msg.obj = "";
							webHandler.sendMessage(msg);
						}
					}else {
						loginCheckResult = 3;
					}
				} catch (Exception e) {
					loginCheckResult = 4;
				}

				Message msg = new Message();
				msg.what = 0x125;
				msg.obj = loginCheckResult;
				webHandler.sendMessage(msg);
			}
		}.start();

	}
	
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				Editor editor = sp.edit();
				editor.putString("USER_NAME", null);
				editor.putString("PASSWORD", null);
				editor.putBoolean("ISCHECK", false);
				editor.putBoolean("AUTO_ISCHECK", false);
				editor.commit();

				Intent intent = new Intent();
				intent.setClass(ChangepwActivity.this,LoginActivity.class);
				startActivity(intent);

//				MainActivity.getInstance().finish();
				SettingActivity.getInstance().finish();
				ChangepwActivity.this.finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				ChangepwActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};	
	
	public static String getUserid() {
		return userid;
	}


	public static void setUserid(String userid) {
		ChangepwActivity.userid = userid;
	}
}
