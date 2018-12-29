package com.kedong.newenergyapp.bussiness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

import com.kedong.newenergyapp.R;

public class LoadingActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.loading);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelogin); 
			
		new Handler().postDelayed(new Runnable(){
			public void run(){
				Intent intent = new Intent (LoadingActivity.this,MainActivity.class);
				startActivity(intent);
				LoadingActivity.this.finish();
				Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
			}
		}, 200);
   }
}
