package com.yoyo.mobilesafe2020;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("com.yoyo.mobilesafe2020", context.getPackageName());
    }

    @Test
    public void getLocalVersion(){
        //可以获取清单文件中的所有信息
        PackageManager manager = context.getPackageManager();
        try {
            //获取到一个应用程序的信息
            //getPackageName()获取到当前应用程序的包名
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            System.out.println(packageInfo.versionCode+"=============="+packageInfo.versionName);
//            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        return 0;
    }

    @Test
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
                System.out.println("json==========="+result);
                JSONObject jsonObject = new JSONObject(result);
                String versionCode = jsonObject.getString("versionCode");
                String versionName = jsonObject.getString("versionName");
                String apkUrl = jsonObject.getString("downloadUrl");
                String des = jsonObject.getString("versionDes");
                System.out.println("versionCode:"+versionCode+",versionName:"+versionName);

            }
        } catch (MalformedURLException e) {
            System.out.println("url异常");
            e.printStackTrace();
        } catch (ProtocolException e) {
            System.out.println("网络异常");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("服务器异常");
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("json解析异常");
            e.printStackTrace();
        }
    }
}
