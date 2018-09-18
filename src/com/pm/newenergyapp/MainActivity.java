package com.pm.newenergyapp;
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
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {
    protected static final String TAG = "MainActivity";
    private View currentButton;
    int pageNO  = 4;
    int currentPageIndex = 0;
    List<Fragment_Two> fragList = new ArrayList<Fragment_Two>();
    List<View>btnLayoutList = new ArrayList<View>();
    List<String>btnText = new ArrayList<String>();
    List<Integer>btnImg = new ArrayList<Integer>();
    List<String>webViewUrl = new ArrayList<String>();
    String []urlArray ={"https://www.lnsdxny.top/NEApp/windOutline",
            "https://www.lnsdxny.top/NEApp/lightOutline",
            "https://www.lnsdxny.top/NEApp/dayReportOutline",
            "https://www.lnsdxny.top/NEApp/dayReportOutline"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int phoneWidth = dm.widthPixels;
        int phoneHeight = dm.heightPixels;

        for(int i=0;i<pageNO;i++){
            fragList.add(new Fragment_Two());
            fragList.get(i).setIndex(i);
            fragList.get(i).setUrl(urlArray[i]);

            LinearLayout btnLayout = (LinearLayout)findViewById(R.id.buttom_bar_group);
            RelativeLayout rl = (RelativeLayout)View.inflate(MainActivity.this, R.layout.bottom_button, null);
            btnLayoutList.add(rl);
            ImageButton imgBtn = rl.findViewById(R.id.buttom_two);
            imgBtn.setTag(i);
            btnLayout.addView(rl);
            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            int heightll = (int)(60 * scale + 0.5f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(phoneWidth/pageNO,heightll);
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
        for(currentPageIndex=0;currentPageIndex<pageNO;currentPageIndex++){
            ImageButton imgBtn = btnLayoutList.get(currentPageIndex).findViewById(R.id.buttom_two);
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int btnIndex = (Integer)((ImageButton)v).getTag();
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
        v.setEnabled(false);
        int btnIndex = (Integer)v.getTag();
        for(int i=0;i<pageNO;i++){
            if(btnIndex!=i){
                btnLayoutList.get(i).findViewById(R.id.buttom_two).setEnabled(true);
            }
        }
        currentButton = v;
    }


}
