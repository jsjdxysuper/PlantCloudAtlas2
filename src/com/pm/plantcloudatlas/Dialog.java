package com.pm.plantcloudatlas;

import android.app.Activity;
import android.app.AlertDialog;

public class Dialog {
	public static void showDialog(String title, String msg, Activity activity) {
		new AlertDialog.Builder(activity)
		.setIcon(activity.getResources().getDrawable(R.drawable.login_error_icon))
		.setTitle(title)
		.setMessage(msg)
		.setPositiveButton("确定", null) 
		.create().show();
	}
}
