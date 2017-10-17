package com.pm.plantcloudatlas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class WelcomeActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 2000; //延迟三秒

	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.welcome); 


		new Handler().postDelayed(new Runnable(){ 

			@Override
			public void run() { 
				Intent mainIntent = new Intent(WelcomeActivity.this, LoginActivity.class); 
				WelcomeActivity.this.startActivity(mainIntent); 
				WelcomeActivity.this.finish(); 
			} 

		}, SPLASH_DISPLAY_LENGHT); 
	} 
}
