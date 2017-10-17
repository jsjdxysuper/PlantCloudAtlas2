package com.pm.plantcloudatlas;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.about);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlelogin); 
	}
}
