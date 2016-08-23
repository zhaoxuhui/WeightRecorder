package com.xuhui.WeightRecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.autoupdatesdk.*;

import java.text.DecimalFormat;

/**
 * Created by zhaox_000 on 2016-05-25.
 */
public class DownLoad extends Activity {

    private TextView AppName;
    private TextView AppVersionOld;
    private TextView AppVersionNew;
    private TextView AppLog;
    private TextView Percent;
    private TextView Content;
    private TextView tv_arrow;
    private TextView tv_new;
    private TextView tv_log;
    private ProgressBar progressBar;
    private AppUpdateInfo Info;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_download);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        AppName = (TextView) findViewById(R.id.AppName);
        AppVersionOld = (TextView) findViewById(R.id.AppVersionOld);
        AppVersionNew = (TextView) findViewById(R.id.AppVersionNew);
        AppLog = (TextView) findViewById(R.id.AppLog);
        Percent = (TextView) findViewById(R.id.percent);
        Content = (TextView) findViewById(R.id.content);
        tv_arrow = (TextView) findViewById(R.id.tv_arrow);
        tv_new = (TextView) findViewById(R.id.tv_new);
        tv_log = (TextView) findViewById(R.id.tv_log);
        decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
        decimalFormat.applyPattern("0.00");
        AppVersionOld.setText(getVersionName(this));
        BDAutoUpdateSDK.cpUpdateCheck(this, new MyCPCheckUpdateCallback());
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

    public void Update(View view) {
        if (Info != null) {
            progressBar.setVisibility(View.VISIBLE);
            Percent.setVisibility(View.VISIBLE);
            Content.setVisibility(View.VISIBLE);
            tv_arrow.setVisibility(View.VISIBLE);
            tv_new.setVisibility(View.VISIBLE);
            tv_log.setVisibility(View.VISIBLE);
            AppLog.setVisibility(View.VISIBLE);
            BDAutoUpdateSDK.cpUpdateDownload(DownLoad.this, Info, new UpdateDownloadCallback());
        } else {
            progressBar.setVisibility(View.GONE);
            Percent.setVisibility(View.GONE);
            Content.setVisibility(View.GONE);
            tv_arrow.setVisibility(View.GONE);
            tv_new.setVisibility(View.GONE);
            tv_log.setVisibility(View.GONE);
            AppLog.setVisibility(View.GONE);
            Toast.makeText(this, "当前为最新版本，无需更新。欢迎关注开发者其它应用。", Toast.LENGTH_LONG).show();
        }
    }

    public void Cancel(View view) {
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

    private class MyCPCheckUpdateCallback implements CPCheckUpdateCallback {

        @Override
        public void onCheckUpdateCallback(AppUpdateInfo info, AppUpdateInfoForInstall infoForInstall) {
            if (infoForInstall != null && !TextUtils.isEmpty(infoForInstall.getInstallPath())) {
                tv_arrow.setVisibility(View.GONE);
                tv_new.setVisibility(View.GONE);
                tv_log.setVisibility(View.GONE);
                AppLog.setVisibility(View.GONE);
                BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), infoForInstall.getInstallPath());
                Toast.makeText(DownLoad.this, "当前为最新版本！", Toast.LENGTH_LONG).show();
            } else if (info != null) {
                Info = info;
                AppName.setText(Info.getAppSname());
                AppVersionNew.setText(Info.getAppVersionName());
                AppLog.setVisibility(View.VISIBLE);
                AppLog.setText(Info.getAppChangeLog().replace("<br>", "\n"));
                tv_arrow.setVisibility(View.VISIBLE);
                tv_new.setVisibility(View.VISIBLE);
                tv_log.setVisibility(View.VISIBLE);
                Toast.makeText(DownLoad.this, "有新版本可更新！", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(DownLoad.this, "当前为最新版本", Toast.LENGTH_LONG).show();
                tv_arrow.setVisibility(View.GONE);
                tv_new.setVisibility(View.GONE);
                tv_log.setVisibility(View.GONE);
                AppLog.setVisibility(View.GONE);
            }
        }

    }


    private class UpdateDownloadCallback implements CPUpdateDownloadCallback {

        @Override
        public void onDownloadComplete(String apkPath) {
            try {
                Toast.makeText(DownLoad.this, "下载完成,安装包已下载到\n" + apkPath, Toast.LENGTH_LONG).show();
                BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), apkPath);
            } catch (Exception e) {
                new AlertDialog.Builder(DownLoad.this).setTitle("提示").setMessage("更新失败，点击“转至商店”打开应用下载链接手动更新。").setPositiveButton("转至商店", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse("http://qr06.cn/DJFSke");
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                }).setNegativeButton("暂时不了", null).show();
            }
        }

        @Override
        public void onStart() {
        }

        @Override
        public void onPercent(int percent, long rcvLen, long fileSize) {
            Percent.setText(percent + "%");
            progressBar.setProgress(percent);
            Content.setText(decimalFormat.format(rcvLen / 1024.0 / 1024.0) + "/" + decimalFormat.format(fileSize / 1024.0 / 1024.0) + " MB");
        }

        @Override
        public void onFail(Throwable error, String content) {
        }

        @Override
        public void onStop() {
        }

    }
}
