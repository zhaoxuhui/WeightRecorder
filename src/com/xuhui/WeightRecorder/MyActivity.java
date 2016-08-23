package com.xuhui.WeightRecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyActivity extends InstrumentedActivity {

    static List<Weight> weightList = new ArrayList<>();
    ListView lv_weights;
    LinearLayout ll_btns;
    String FileName;
    static String path;
    private AppUpdateInfo Info;
    int Width;
    int Length;
    double Max = 0;
    public int Position;
    double Target;
    SharedPreferences sharedPreferences;
    DecimalFormat decimalFormatS;
    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        JPushInterface.init(this);

        BDAutoUpdateSDK.cpUpdateCheck(this, new MyCPCheckUpdateCallback());
        decimalFormatS = (DecimalFormat) DecimalFormat.getInstance();
        decimalFormatS.applyPattern("0.0");
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Width = windowManager.getDefaultDisplay().getWidth();
        Length = windowManager.getDefaultDisplay().getHeight();
        lv_weights = (ListView) findViewById(R.id.lv_weights);
        ll_btns = (LinearLayout) findViewById(R.id.ll_buttons);
        ViewGroup.LayoutParams layoutParams = lv_weights.getLayoutParams();
        layoutParams.height = (int) (0.7 * Length);
        lv_weights.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams1 = ll_btns.getLayoutParams();
        layoutParams1.height = (int) (0.3 * Length);
        ll_btns.setLayoutParams(layoutParams1);
        path = context.getFilesDir() + "/";
        File filedir = new File(path);
        if (!filedir.exists()) {
            filedir.mkdir();
        }

        FileName = path + "体重记录" + ".txt";
        File file = new File(FileName);
        try {
            weightList.clear();
            InputStream inputStream = null;
            inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String temp;
            try {
                while ((temp = bufferedReader.readLine()) != null) {
                    String[] numbers = temp.split(" ");
                    Weight weightTemp = new Weight(numbers[0], Double.parseDouble(numbers[1]), numbers[2]);
                    weightList.add(weightTemp);
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (weightList.size() == 0) {
            Max = 0;
        } else if (weightList.size() == 1) {
            Max = weightList.get(weightList.size() - 1).weight;
        } else {
            for (int i = 0; i < weightList.size(); i++) {
                if (weightList.get(i).weight > Max) {
                    Max = weightList.get(i).weight;
                }
            }
        }
        if (sharedPreferences.getFloat("Target", 0) == 0) {
            lv_weights.setAdapter(new WeightAdapter());
        } else {
            Target = sharedPreferences.getFloat("Target", 0);
            lv_weights.setAdapter(new WeightAdapter2());
        }

        lv_weights.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Position = position;
                return false;
            }
        });
        this.registerForContextMenu(lv_weights);
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


    private class MyCPCheckUpdateCallback implements CPCheckUpdateCallback {

        @Override
        public void onCheckUpdateCallback(AppUpdateInfo info, AppUpdateInfoForInstall infoForInstall) {
            if (infoForInstall != null && !TextUtils.isEmpty(infoForInstall.getInstallPath())) {
                BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), infoForInstall.getInstallPath());
            } else if (info != null) {
                Info = info;
                Toast.makeText(MyActivity.this, "有新版本可更新！", Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(MyActivity.this).setTitle("版本更新！").setMessage("当前版本:" + getVersionName(MyActivity.this) + " → 最新版本:" + Info.getAppVersionName() + "\n更新日志:\n" + Info.getAppChangeLog().replace("<br>", "\n")).setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MyActivity.this, com.xuhui.WeightRecorder.DownLoad.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new AlertDialog.Builder(MyActivity.this).setTitle("升级提示").setMessage("可在“帮助”-“检查升级”中手动检查升级。").setPositiveButton("我知道了", null).show();
                    }
                }).show();
            } else {
                //Toast.makeText(MobileGeomaticsPlatform.this, "当前为最新版本", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        path = context.getFilesDir() + "/";
        File filedir = new File(path);
        if (!filedir.exists()) {
            filedir.mkdir();
        }

        FileName = path + "体重记录" + ".txt";
        File file = new File(FileName);
        try {
            weightList.clear();
            InputStream inputStream = null;
            inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String temp;
            try {
                while ((temp = bufferedReader.readLine()) != null) {
                    String[] numbers = temp.split(" ");
                    Weight weightTemp = new Weight(numbers[0], Double.parseDouble(numbers[1]), numbers[2]);
                    weightList.add(weightTemp);
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (weightList.size() == 0) {
            Max = 0;
        } else if (weightList.size() == 1) {
            Max = weightList.get(weightList.size() - 1).weight;
        } else {
            for (int i = 0; i < weightList.size(); i++) {
                if (weightList.get(i).weight > Max) {
                    Max = weightList.get(i).weight;
                }
            }
        }
        if (sharedPreferences.getFloat("Target", 0) == 0) {
            lv_weights.setAdapter(new WeightAdapter());
        } else {
            Target = sharedPreferences.getFloat("Target", 0);
            lv_weights.setAdapter(new WeightAdapter2());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("操作");
        menu.add(0, 1, Menu.NONE, "删除记录");
        menu.add(0, 2, Menu.NONE, "编辑记录");
    }


    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                try {
                    weightList.remove(Position);
                    if (sharedPreferences.getFloat("Target", 0) == 0) {
                        lv_weights.setAdapter(new WeightAdapter());
                    } else {
                        Target = sharedPreferences.getFloat("Target", 0);
                        lv_weights.setAdapter(new WeightAdapter2());
                    }
                    String Results = "";
                    for (int i = 0; i < weightList.size(); i++) {
                        Results += weightList.get(i).time + " " + weightList.get(i).weight + " " + weightList.get(i).ps + "\n";
                    }

                    File filedir = new File(path);
                    if (!filedir.exists()) {
                        filedir.mkdir();
                    }
                    File file = new File(path, "体重记录" + ".txt");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(Results.replace("\n", "\r\n").getBytes());
                    fos.close();
                    Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                try {
                    Intent intent = new Intent(this, com.xuhui.WeightRecorder.Modify.class);
                    intent.putExtra("Position", Position);
                    startActivityForResult(intent, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "编辑失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return false;
    }

    public void AddRecord(View view) {
        Intent intent = new Intent(this, com.xuhui.WeightRecorder.Add.class);
        startActivityForResult(intent, 1);
    }

    public void DeleteRecords(View view) {
        new AlertDialog.Builder(this).setTitle("确认").setMessage("确定要删除所有记录吗？").setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(FileName);
                if (file.exists()) {
                    file.delete();
                    weightList.clear();
                    lv_weights.setAdapter(new WeightAdapter());
                    Toast.makeText(MyActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                String time = data.getStringExtra("Time");
                double weight = data.getDoubleExtra("Weight", 0);
                String ps = data.getStringExtra("PS");
                Weight tempW = new Weight(time, weight, ps);
                weightList.add(tempW);

                try {
                    String Results = "";
                    for (int i = 0; i < weightList.size(); i++) {
                        Results += weightList.get(i).time + " " + weightList.get(i).weight + " " + weightList.get(i).ps + "\n";
                    }

                    File filedir = new File(path);
                    if (!filedir.exists()) {
                        filedir.mkdir();
                    }
                    File file = new File(path, "体重记录" + ".txt");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(Results.replace("\n", "\r\n").getBytes());
                    fos.close();
                    Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (weightList.size() == 0) {
                    Max = 0;
                } else if (weightList.size() == 1) {
                    Max = weightList.get(weightList.size() - 1).weight;
                } else {
                    for (int i = 0; i < weightList.size(); i++) {
                        if (weightList.get(i).weight > Max) {
                            Max = weightList.get(i).weight;
                        }
                    }
                }
                if (sharedPreferences.getFloat("Target", 0) == 0) {
                    lv_weights.setAdapter(new WeightAdapter());
                } else {
                    Target = sharedPreferences.getFloat("Target", 0);
                    lv_weights.setAdapter(new WeightAdapter2());
                }
                break;
            case 2:
                String timeT = data.getStringExtra("Time");
                double weightT = data.getDoubleExtra("Weight", 0);
                String psT = data.getStringExtra("PS");
                Weight tempWT = new Weight(timeT, weightT, psT);
                weightList.set(Position, tempWT);

                try {
                    String Results = "";
                    for (int i = 0; i < weightList.size(); i++) {
                        Results += weightList.get(i).time + " " + weightList.get(i).weight + " " + weightList.get(i).ps + "\n";
                    }

                    File filedir = new File(path);
                    if (!filedir.exists()) {
                        filedir.mkdir();
                    }
                    File file = new File(path, "体重记录" + ".txt");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(Results.replace("\n", "\r\n").getBytes());
                    fos.close();
                    Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (weightList.size() == 0) {
                    Max = 0;
                } else if (weightList.size() == 1) {
                    Max = weightList.get(weightList.size() - 1).weight;
                } else {
                    for (int i = 0; i < weightList.size(); i++) {
                        if (weightList.get(i).weight > Max) {
                            Max = weightList.get(i).weight;
                        }
                    }
                }
                if (sharedPreferences.getFloat("Target", 0) == 0) {
                    lv_weights.setAdapter(new WeightAdapter());
                } else {
                    Target = sharedPreferences.getFloat("Target", 0);
                    lv_weights.setAdapter(new WeightAdapter2());
                }
                break;
            case 3:
                if (weightList.size() == 0) {
                    Max = 0;
                } else if (weightList.size() == 1) {
                    Max = weightList.get(weightList.size() - 1).weight;
                } else {
                    for (int i = 0; i < weightList.size(); i++) {
                        if (weightList.get(i).weight > Max) {
                            Max = weightList.get(i).weight;
                        }
                    }
                }
                Target = data.getDoubleExtra("Target", 0);
                SharedPreferences sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("Target", (float) Target);
                editor.commit();
                if (Target == 0) {
                    lv_weights.setAdapter(new WeightAdapter());
                } else {
                    lv_weights.setAdapter(new WeightAdapter2());
                }
                break;
        }
    }

    public void Target(View view) {
        Intent intent = new Intent(this, com.xuhui.WeightRecorder.Target.class);
        startActivityForResult(intent, 3);
    }

    public void Help(View view) {
        Intent intent = new Intent(this, com.xuhui.WeightRecorder.Help.class);
        startActivity(intent);
    }

    public void Share(View view) {
        showShare();
    }

    public void OutputRecords(View view) {
        try {
            String Results = "";
            for (int i = 0; i < weightList.size(); i++) {
                Results += weightList.get(i).time + " " + weightList.get(i).weight + " " + weightList.get(i).ps + "\n";
            }
            String pathS = Environment.getExternalStorageDirectory() + "/xuhui/";
            File filedir = new File(pathS);
            if (!filedir.exists()) {
                filedir.mkdir();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH：mm");
            Date curDate = new Date(System.currentTimeMillis());
            File file = new File(filedir, "体重记录" + formatter.format(curDate) + ".txt");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(Results.replace("\n", "\r\n").getBytes());
            fos.close();
            Toast.makeText(this, "已导出到" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "导出失败", Toast.LENGTH_LONG).show();
        }
    }

    public class WeightAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return weightList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View weight_view = View.inflate(MyActivity.this, R.layout.weight_item, null);
            TextView tv_time = (TextView) weight_view.findViewById(R.id.tv_time);
            TextView tv_weight = (TextView) weight_view.findViewById(R.id.tv_weight);
            TextView tv_ps = (TextView) weight_view.findViewById(R.id.tv_ps);
            LinearLayout color = (LinearLayout) weight_view.findViewById(R.id.item_color);
            color.setBackground(getResources().getDrawable(R.drawable.bg_gradient2));
            ViewGroup.LayoutParams layoutParams = color.getLayoutParams();
            layoutParams.width = (int) ((weightList.get(position).weight / Max) * Width);
            tv_time.setText(weightList.get(position).getTime());
            tv_weight.setText(weightList.get(position).getWeight() + " kg");
            tv_ps.setText(weightList.get(position).getPs());
            return weight_view;
        }
    }

    public class WeightAdapter2 extends BaseAdapter {
        @Override
        public int getCount() {
            return weightList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View weight_view = View.inflate(MyActivity.this, R.layout.weight_item_2, null);
            TextView tv_time = (TextView) weight_view.findViewById(R.id.tv_time);
            TextView tv_weight = (TextView) weight_view.findViewById(R.id.tv_weight);
            TextView tv_ps = (TextView) weight_view.findViewById(R.id.tv_ps);
            TextView tv_tip = (TextView) weight_view.findViewById(R.id.tv_tip);
            LinearLayout color = (LinearLayout) weight_view.findViewById(R.id.item_color);
            LinearLayout target = (LinearLayout) weight_view.findViewById(R.id.item_target);
            ViewGroup.LayoutParams layoutParams = color.getLayoutParams();
            if (weightList.get(position).weight > Target) {
                color.setBackground(getResources().getDrawable(R.drawable.bg_gradient));
                tv_tip.setText("(多" + decimalFormatS.format(weightList.get(position).weight - Target) + " kg)");
            } else {
                color.setBackground(getResources().getDrawable(R.drawable.bg_gradient2));
                tv_tip.setText("(少" + decimalFormatS.format(Target - weightList.get(position).weight) + " kg)");
            }
            layoutParams.width = (int) ((weightList.get(position).weight / Max) * Width);
            ViewGroup.LayoutParams layoutParams1 = target.getLayoutParams();
            layoutParams1.width = (int) ((Target / Max) * Width);
            tv_time.setText(weightList.get(position).getTime());
            tv_weight.setText(weightList.get(position).getWeight() + " kg");
            tv_ps.setText(weightList.get(position).getPs());
            return weight_view;
        }
    }

    public void showShare() {
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
    }
}
