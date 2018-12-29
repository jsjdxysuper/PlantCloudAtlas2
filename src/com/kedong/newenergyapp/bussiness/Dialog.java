package com.kedong.newenergyapp.bussiness;

import android.app.Activity;
import android.app.AlertDialog;

import com.kedong.newenergyapp.R;

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
