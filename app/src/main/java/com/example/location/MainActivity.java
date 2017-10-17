package com.example.location;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tool.Configration;
import com.example.tool.GetAir;
import com.example.tool.GetPMInfo;
import com.example.tool.GetWeather;
import com.example.tool.MyBaiduLotion;
import com.example.tool.MyLocation;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    /**
     * 由这个获取经纬度
     */
    MyBaiduLotion myLotion;
    /**
     * 获取完成后由这个获取地点称
     */
    MyLocation myLocation;
    /**
     * 显示天气的textview
     */
    private TextView tv = null;
    /**
     * 位置
     */
    String strlocation = "";

    String weather = "";
    String air = "";

    GetWeather gw;
    GetPMInfo gi;
    GetAir ga;


    Toast toast;
    Timer timer_check_gps_is_ok;
    Timer timer_attain_weather_info;
    Timer timer_attain_air_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**判断是否有网*/
        has_net();
        /**启动类,获取天气信息——初始化ID*/
        getWeatherInfo_and_initView();
        /**timer判断是否获取到了位置*/
        check_has_gps_ok();
    }


    /**
     * 判断是否有网
     */
    // TODO: 2017/10/17 没网的时候，调到指定位置打开网络
    public boolean has_net() {
        boolean netWork = Configration.netWorkIsOPen(MainActivity.this);
        boolean gps = Configration.GPSIsOPen(MainActivity.this);
        if (netWork && gps) {
            Toast.makeText(this, "当前有网", Toast.LENGTH_SHORT).show();
            return true;
        } else if (netWork) {
            toast = Toast.makeText(MainActivity.this, "GPS未开启，请开启GPS", Toast.LENGTH_SHORT);
            toast.show();
        } else if (gps) {
            toast = Toast.makeText(MainActivity.this, "网络未开启，请开启网络", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(MainActivity.this, "GPS和网络未开启，请开启GPS和网络", Toast.LENGTH_SHORT);
            toast.show();
        }
        return false;
    }

    /**
     * timer判断是否获取到了位置
     */
    private void check_has_gps_ok() {
        /**直接开始定位*/
        myLotion = new MyBaiduLotion(MainActivity.this);
        myLocation = new MyLocation();
        myLotion.opetateClient();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText("定位开始,正在获取经纬度...");
            }
        });
        timer_check_gps_is_ok = new Timer();
        timer_check_gps_is_ok.schedule(new TimerTask() {
            @Override
            public void run() {
                if (myLotion != null && myLotion.getIsFinish()&&myLotion.myBDcoordinate != null) {
                    //获取到经纬度后执行获取地方名称
                        timer_check_gps_is_ok.cancel();
                        /**封装经纬度——handler通知获取信息*/
                        strlocation = myLocation.getAddress(myLotion.getLatValue() + "", myLotion.getLongValue() + "");
                        Log.e("111111111","获取到了城市名"+ strlocation);
                        myHandler.sendEmptyMessage(0x123);
                }
            }
        }, 0, 100);
    }

    /**
     * 启动类,获取天气信息
     */
    private void getWeatherInfo_and_initView() {
        /**有种模块化的感觉*/
        gw = new GetWeather();
        gi = new GetPMInfo();
        ga = new GetAir();
        tv = (TextView) findViewById(R.id.tv_loc_info);
    }

    /**
     * handler通知去获取天气信息
     */
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x123:
                    tv.setText(" 获取" + strlocation + "天气中..." + "\n" + "纬度" + myLotion.getLatValue() + " 经度" + myLotion.getLongValue());
                    timer_attain_weather_info = new Timer();
                    timer_attain_weather_info.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                if(!strlocation.isEmpty()){
                                    /**将天气的字符串返回给weather*/
                                    weather = gw.getWeather(new Configration()
                                            .EcodingGB2312(strlocation.substring(0, strlocation.length() - 1)));
                                    Log.e("————————————————","获取到了天气"+ weather);
                                    /**handler去通知获取空气质量*/
                                    myHandler.sendEmptyMessage(0x456);
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    },0,100);
                    break;
                case 0x456:
                    tv.setText("获取空气质量中...");
                    timer_attain_air_info = new Timer();
                    timer_attain_air_info.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(!weather.isEmpty()){
                                timer_attain_air_info.cancel();
                                air = gi.getPMInfoImpl(strlocation.substring(0, strlocation.length() - 1));
                                Log.e("————————————————","获取到了空气质量"+ air);
                                myHandler.sendEmptyMessage(0x258);
                            }
                        }
                    },0,100);
                break;
                case 0x258:
                    tv.setText("天气：" + weather + "\n\n\n\n\n空气质量：" + air);
                break;
               default:
                   Log.i("11111111111", "Handle ERROR message!");
                   break;
            }
        }

    };


    /**
     * 释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myLotion != null)
            myLotion.desClient();
        myLotion = null;
        myLocation = null;
        gw = null;
        gi = null;
        ga = null;
        tv = null;
        strlocation = null;
        weather = null;
        air = null;
        System.gc();
    }
}
