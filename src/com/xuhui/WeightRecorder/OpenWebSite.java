package com.xuhui.WeightRecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by zhaox_000 on 2016-05-26.
 */
public class OpenWebSite extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_moreapp);
        webView = (WebView) findViewById(R.id.WebView);
        Intent intent = getIntent();
        String Web = intent.getStringExtra("Web");
        if (!Web.contains("http://")) {
            Web = "http://" + Web;
        }
        Toast.makeText(this, "正在调起浏览器打开...", Toast.LENGTH_LONG).show();
        webView.loadUrl(Web);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
