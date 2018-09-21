package com.pm.newenergyapp;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ezy.boost.update.IUpdateParser;
import ezy.boost.update.UpdateInfo;
import ezy.boost.update.UpdateManager;


public class MainActivity extends FragmentActivity {
    protected static final String TAG = "MainActivity";
    public static List<UserPage> listUserPage;
    private View currentButton;

    int currentPageIndex = 0;
    List<Fragment_Two> fragList = new ArrayList<Fragment_Two>();
    List<View>btnLayoutList = new ArrayList<View>();
    List<String>btnText = new ArrayList<String>();
    List<Integer>btnImg = new ArrayList<Integer>();
    List<String>webViewUrl = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

        Button btnFresh = (Button)findViewById(R.id.btnFresh);
        btnFresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressWebView progressWebView = (ProgressWebView) fragList.get(currentPageIndex).getView().findViewById(R.id.webview);
                progressWebView.reload();
            }
        });
        Button btnSet = (Button)findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateManager.create(view.getContext()).setUrl( getApplication().getString(R.string.update_check_url)).setParser(new IUpdateParser() {
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

                        if(Utility.getVersionCode(view.getContext())<info.versionCode){
                            info.hasUpdate = true;
                            info.isForce = jo.getInt("isforce")==1?true:false;
                            info.isSilent = jo.getInt("issilent")==1?true:false;
                            info.isAutoInstall = jo.getInt("isautoinstall")==1?true:false;
                            info.isIgnorable = jo.getInt("isignorable")==1?true:false;
                            info.maxTimes = jo.getInt("maxtimes");
                        }

                        return info;
                    }
                }).check();
            }
        });
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int phoneWidth = dm.widthPixels;
        int phoneHeight = dm.heightPixels;

        for(int i=0;i<listUserPage.size();i++){
            fragList.add(new Fragment_Two());
            fragList.get(i).setIndex(i);
            fragList.get(i).setUrl(listUserPage.get(i).getPageUrl());

            LinearLayout btnLayout = (LinearLayout)findViewById(R.id.buttom_bar_group);
            RelativeLayout rl = (RelativeLayout)View.inflate(MainActivity.this, R.layout.bottom_button, null);
            btnLayoutList.add(rl);
            Button imgBtn = (Button)rl.findViewById(R.id.buttom_two);
            imgBtn.setTag(i);
            imgBtn.setText(listUserPage.get(i).getPagename());
            btnLayout.addView(rl);
            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            int heightll = (int)(60 * scale + 0.5f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(phoneWidth/listUserPage.size(),heightll);
            rl.setLayoutParams(params);
            btnText.add("按钮"+(i+1));

            btnImg.add(R.drawable.bar_news);

            webViewUrl.add("我是URL"+(i+1));
        }
        initComponents();

    }

    /**
     * 已经放入fragmentList中的fragment已经显示的是否有现在要传入的fragment
     * @param fragment_two
     * @return
     */
    public boolean hasThisFragment(Fragment_Two fragment_two){
        FragmentManager fm = getSupportFragmentManager();

        List<Fragment> frList = fm.getFragments();
        int fragIndexThis = fragment_two.getIndex();
        if(frList==null)return false;
        for(int i=0;i<frList.size();i++) {
            int fragIndexTemp = ((Fragment_Two)frList.get(i)).getIndex();
            if (fragIndexTemp==fragIndexThis)
                return true;
        }
        return false;
    }

    /**
     * 隐藏所有显示过的fragment
     * @param ft
     * @param frList
     */
    public void hideFragment(FragmentTransaction ft, List<Fragment> frList) {
        //如果不为空，就先隐藏起来ft.
        if(frList!=null) {
            for (int i = 0; i < frList.size(); i++) {
                ft.hide(frList.get(i));
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        for(currentPageIndex=0;currentPageIndex<listUserPage.size();currentPageIndex++){
            Button imgBtn = (Button)btnLayoutList.get(currentPageIndex).findViewById(R.id.buttom_two);
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View v) {
                    int btnIndex = (Integer)((Button)v).getTag();
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    hideFragment(ft,fm.getFragments());
                    if(!hasThisFragment(fragList.get(btnIndex))){
                        ft.add(R.id.fl_content, fragList.get(btnIndex));
                    }else{
                        ft.show(fragList.get(btnIndex));
                    }
//                    ft.replace(R.id.fl_content, fragList.get(btnIndex), MainActivity.TAG);
//                    ft.commit();
                    ft.commitAllowingStateLoss();
                    fm.executePendingTransactions();

                    setButton(v);
                }
            });
        }
        /**
         * 默认第一个按钮点击
         */
        btnLayoutList.get(0).findViewById(R.id.buttom_two).performClick();
    }
    private void initComponents() {
    }

    /**
     * 设置按钮的背景图片
     *
     * @param v
     */
    private void setButton(View v) {
//        v.setEnabled(false);
        v.setSelected(true);
        int btnIndex = (Integer)v.getTag();
        for(int i=0;i<listUserPage.size();i++){
            if(btnIndex!=i){
                btnLayoutList.get(i).findViewById(R.id.buttom_two).setSelected(false);
            }
        }
        currentButton = v;
        currentPageIndex = btnIndex;
    }


}
