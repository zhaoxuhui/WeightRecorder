package com.xuhui.WeightRecorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by zhaox_000 on 2016-07-06.
 */
public class Help extends Activity {
    TextView tv_more;
    TextView tv_version;
    LinearLayout ll_help;
    TextView tv_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        ll_help = (LinearLayout) findViewById(R.id.ll_help);
        tv_check = (TextView) findViewById(R.id.btn_check);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText(getVersionName(getApplicationContext()));
        SpannableString spannableString = new SpannableString("检查更新");
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_check.setText(spannableString);
        ViewGroup.LayoutParams layoutParams = ll_help.getLayoutParams();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        layoutParams.width = (int) (0.8 * width);
        tv_more = (TextView) findViewById(R.id.tv_more);
        SpannableString spanText = new SpannableString("更多应用");
        spanText.setSpan(new URLSpan("http://qr06.cn/DK8mgO"), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_more.setText(spanText);
        tv_more.setMovementMethod(new LinkMovementMethod());
    }

    public String getVersionName(Context context)//获取版本号
    {
        try {
            PackageInfo pi = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Unknown";
        }
    }

    public void OK(View view) {
        this.finish();
    }

    public void Check(View view) {
        Intent intent = new Intent(this, DownLoad.class);
        startActivity(intent);
    }
}
