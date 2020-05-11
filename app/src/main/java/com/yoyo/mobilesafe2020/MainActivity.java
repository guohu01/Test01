package com.yoyo.mobilesafe2020;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {


    private TextView tvVersion;//版本名展示
    private Context context;
    private Activity mActivity;

    private String versionName;
    private String apkUrl;
    private String versionDes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mActivity = this;
        setContentView(R.layout.activity_main);
        tvVersion = findViewById(R.id.tv_version);
        System.out.println("ur:"+Thread.currentThread().getName());
        //资源没法进行及时回收,应使用线程池技术
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                getUpdateInfo();
            }
        }).start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //msg.what:消息码
            switch(msg.what){
                case 1:
                    Toast.makeText(context, "url异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case 2:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case 3:
                    Toast.makeText(context, "服务器异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case 4:
                    Toast.makeText(context, "json解析异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case 5:
                    //有新版本，弹出对话框，让用户选择是否下载
                    showUpdateDialog();
                    break;
            }
        }
    };

    /**
     * 弹出更新对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //设置标题
        builder.setTitle("检查到新版本，v"+versionName);
        //设置内容
        builder.setMessage(versionDes);
        //设置不能点击取消
        builder.setCancelable(false);
        //设置图标
        builder.setIcon(R.drawable.ic_launcher);
        //立即升级按钮
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //开始下载
                initProgressDialog();
                Toast.makeText(context, "正在更新...", Toast.LENGTH_SHORT).show();
            }
        });

        //暂不升级
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //进入主界面
                enterHome();
            }
        });
        //弹出对话框
        builder.show();
    }

    /**
     * 进入主界面
     */
    private void enterHome() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context, HomeActivity.class);
                context.startActivity(intent);
                mActivity.finish();
            }
        }).start();*/

        //定时器解决延时，比直接开启子线程要好
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(context, HomeActivity.class);
                context.startActivity(intent);
                mActivity.finish();
            }
        },2000);


    }

    /**
     * 弹出升级进度条
     */
    private void initProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("准备下载...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    /**
     * 获取本地app版本
     * @return
     */
    private int getLocalVersion(){
        //获取到包管理器
        PackageManager manager = context.getPackageManager();
        try {
            //获取到一个应用程序的信息
            //getPackageName()获取到当前应用程序的包名
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            String versionName = packageInfo.versionName;//版本名
            int versionCode = packageInfo.versionCode;//版本号
            System.out.println("getLocalVersion方法："+packageInfo.versionCode+","+packageInfo.versionName);
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取服务器升级信息
     */
    public void getUpdateInfo(){
        URL url = null;
        try {
            url = new URL("http://10.0.2.2/update.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();//访问网络
            if (connection.getResponseCode()==200){
                //请求和响应都成功了
                InputStream in = connection.getInputStream();
                //对获取到的输入流进行读取
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    response.append(line);
                }
                String result = response.toString();
                System.out.println("json:"+result);
                //解析json串
                JSONObject jsonObject = new JSONObject(result);
                String versionCode = jsonObject.getString("versionCode");
                versionName = jsonObject.getString("versionName");
                apkUrl = jsonObject.getString("downloadUrl");
                versionDes = jsonObject.getString("versionDes");
                if (Integer.parseInt(versionCode)>getLocalVersion()){
                    System.out.println("有新版本，准备下载");
                    //需要显示弹出框
                    handler.sendEmptyMessage(5);
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("url异常");
            handler.sendEmptyMessage(1);
            e.printStackTrace();
        } catch (ProtocolException e) {
            System.out.println("网络异常");
            handler.sendEmptyMessage(2);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("服务器异常");
            handler.sendEmptyMessage(3);
            //比较好的方式，Android推荐
//            handler.sendEmptyMessageDelayed(3,2000);
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("json解析异常");
            handler.sendEmptyMessage(4);
            e.printStackTrace();
        }
    }
}
