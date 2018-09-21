package com.kedong.utils;

//import net.sf.json.JSONObject;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;


public class WholenessCheck {
	public static String keyData = "jsjdxysuper@126.com";
	public static String decode(String jsonStr,String pwd){
		if(pwd==null)
			pwd=keyData;

		JSONObject jo = null;
		try {
			jo = new JSONObject(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String beforeMD5 = jsonStr.replaceAll("'", "");
		beforeMD5 = beforeMD5.replaceAll("\"", "");
		String afterMD5 =null;
		try {
			afterMD5 = MD5Util.md5Password(beforeMD5+pwd);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			jo.put("MD5Code", afterMD5);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo.toString();
	}

	public static String justCode(String content,String pwd){
		if(pwd==null)
			pwd=keyData;
		String afterMD5="";
		try {
			afterMD5 = MD5Util.md5Password(content+pwd);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return afterMD5;
	}
}
