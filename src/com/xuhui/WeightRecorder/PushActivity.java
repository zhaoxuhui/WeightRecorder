package com.xuhui.WeightRecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zhaox_000 on 2016-06-23.
 */
public class PushActivity extends Activity {
    private TextView tv_content;
    private TextView tv_title;
    private Button btn_do;
    private LinearLayout pa;
    private String ButtonText;
    private String WebSite;
    private int Open = 0;
    private final int ADD_RECORD = 1;
    private final int STANDARD_WEIGHT = 2;
    private final int HELP = 3;
    private final int SHARE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_activity);
        tv_content = (TextView) findViewById(R.id.content);
        tv_title = (TextView) findViewById(R.id.title);
        btn_do = (Button) findViewById(R.id.btn_do);
        pa = (LinearLayout) findViewById(R.id.PA);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams layoutParams = pa.getLayoutParams();
        layoutParams.width = (int) (0.8 * width);
        pa.setLayoutParams(layoutParams);
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            tv_title.setText(title);
            tv_content.setText(content);
            try {
                JSONObject jsonObject = new JSONObject(extras);
                ButtonText = jsonObject.optString("Text", "好的");
                btn_do.setText(ButtonText);
                WebSite = jsonObject.optString("Web", "");
                Open = jsonObject.optInt("Open", -1);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    public void GoTo(View view) {
        if (WebSite.isEmpty() && Open == -1) {
            this.finish();
        } else if ((!WebSite.isEmpty()) && Open == -1) {
            Intent intent = new Intent(this, com.xuhui.WeightRecorder.OpenWebSite.class);
            intent.putExtra("Web", WebSite);
            startActivity(intent);
            this.finish();
        } else if (WebSite.isEmpty() && (Open != -1)) {
            Intent intent;
            switch (Open) {
                case ADD_RECORD:
                    intent = new Intent(this, com.xuhui.WeightRecorder.Add.class);
                    startActivityForResult(intent, 1);
                    break;
                case STANDARD_WEIGHT:
                    intent = new Intent(this, com.xuhui.WeightRecorder.BMI.class);
                    intent.putExtra("Flag", 2);
                    startActivity(intent);
                    break;
                case HELP:
                    intent = new Intent(this, com.xuhui.WeightRecorder.Help.class);
                    startActivity(intent);
                    break;
                case SHARE:
                    ShareSDK.initSDK(this);
                    OnekeyShare oks = new OnekeyShare();
                    //关闭sso授权
                    oks.disableSSOWhenAuthorize();

                    // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
                    //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
                    // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                    oks.setTitle("体重记录器");
                    // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                    oks.setTitleUrl("http://qr06.cn/EOvHhG");
                    // text是分享文本，所有平台都需要这个字段
                    oks.setText("方便实用的体重记录、追踪应用，帮助管理您的健康。下载地址:http://qr06.cn/EOvHhG");
                    // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                    //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
                    // url仅在微信（包括好友和朋友圈）中使用
                    oks.setUrl("http://qr06.cn/EOvHhG");
                    oks.setImageUrl("http://r.photo.store.qq.com/psb?/5ca68c41-c4b8-4f34-ae29-00a8851cc3cf/zs9UrBp3aclEAoR1KpHav1I758RJziJROavMAnyhOy8!/r/dNoAAAAAAAAA");
                    // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                    oks.setComment("方便实用的体重记录、追踪应用。下载地址:http://qr06.cn/EOvHhG");
                    // site是分享此内容的网站名称，仅在QQ空间使用
                    oks.setSite(getString(R.string.app_name));
                    // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                    oks.setSiteUrl("http://qr06.cn/EOvHhG");

                    // 启动分享GUI
                    oks.show(this);
                    break;
            }
        } else if ((!WebSite.isEmpty()) && (Open != -1)) {
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                String time = data.getStringExtra("Time");
                double weight = data.getDoubleExtra("Weight", 0);
                String ps = data.getStringExtra("PS");
                Weight tempW = new Weight(time, weight, ps);
                MyActivity.weightList.add(tempW);
                try {
                    String Results = "";
                    for (int i = 0; i < MyActivity.weightList.size(); i++) {
                        Results += MyActivity.weightList.get(i).time + " " + MyActivity.weightList.get(i).weight + " " + MyActivity.weightList.get(i).ps + "\n";
                    }

                    File filedir = new File(MyActivity.path);
                    if (!filedir.exists()) {
                        filedir.mkdir();
                    }
                    File file = new File(MyActivity.path, "体重记录" + ".txt");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(Results.replace("\n", "\r\n").getBytes());
                    fos.close();
                    this.finish();
                    Toast.makeText(this, "保存成功,快去看看体重变化吧~", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
