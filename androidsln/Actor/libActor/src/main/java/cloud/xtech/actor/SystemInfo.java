package cloud.xtech.actor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

public class SystemInfo {
    private static WifiManager wifiManager_ = null;
    private static AudioManager audioManager_ = null;
    private static PowerManager powerManager_ = null;
    private static ActivityManager activityManager_ = null;

    public static void Setup(Activity _activity)
    {
        wifiManager_ = (WifiManager) _activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager_ = (AudioManager) _activity .getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        powerManager_ = (PowerManager) _activity.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        activityManager_ = (ActivityManager)  _activity.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        Log.i("ActorPlugin", "BRAND: " + Build.BRAND);
        Log.i("ActorPlugin", "MODEL: " + Build.MODEL);
        Log.i("ActorPlugin", "PRODUCT: " + Build.PRODUCT);
        Log.i("ActorPlugin", "DEVICE: " + Build.DEVICE);
        Log.i("ActorPlugin", "HARDWARE: " + Build.HARDWARE);
        Log.i("ActorPlugin", "FINGERPRINT: " + Build.FINGERPRINT);

        // 开启wifi
        wifiManager_.setWifiEnabled(true);
    }

    /// \beirf 获取设备序列号
    public static String getSerialNumber(){
        String serial = null;
        try {
            Class<?> c =Class.forName("android.os.SystemProperties");
            Method get =c.getMethod("get", String.class);
            serial = (String)get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /// \beirf 获取设备名
    public static String getName(){
        return Build.MODEL;
    }

    /// \beirf 获取软件版本
    public static String getVersion(){
        // 获取packagemanager的实例
        PackageManager packageManager = ActorData.activity .getApplicationContext().getPackageManager();
        // getPackageName()是你当前类的包名
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(ActorData.activity .getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return packInfo.versionName;
    }

    /// \beirf 获取系统版本
    public static String getSystemVersion(){
        return android.os.Build.VERSION.RELEASE;
    }

    /// \brief 获取电量，范围为[0,100]
    public static int getBattery() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = ActorData.activity .getApplicationContext().registerReceiver(null,ifilter);
        int level = intent.getIntExtra("level", 0);
        int scale = intent.getIntExtra("scale", 0);
        if(0 == scale)
            return -1;
        return level;
    }

    /// \brief 获取音量
    public static int getVolume() {
        int volume =audioManager_.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        try{
            volume =  audioManager_.getStreamVolume( AudioManager.STREAM_MUSIC );
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return volume;
    }

    /// \brief s设置音量
    public static void setVolume(int _value)
    {
        try
        {
            int max = audioManager_.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager_.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(_value/100.0f*max), 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    /// \brief 获取亮度
    public static int getBrightness() {
        int screenBrightness=255;
        try{
            screenBrightness = Settings.System.getInt(ActorData.activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return screenBrightness;
    }

    /// \brief 设置亮度
    public static void setBrightness(int _value)
    {
        try{
            //SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
            // SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度
            Settings.System.putInt(ActorData.activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            Settings.System.putInt(ActorData.activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int)(_value /100f * 255));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static String getShape() {
        return "Mobile";
    }

    /// \brief 获取WIFI信号强度，范围为[0,5]
    public static String getNetwork() {
        ConnectivityManager cm = (ConnectivityManager) ActorData.activity .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if(networkINfo == null)
            return "unknown";

        if(networkINfo.getType() == ConnectivityManager.TYPE_WIFI)
            return "wifi";
        if(networkINfo.getType() == ConnectivityManager.TYPE_ETHERNET)
            return "lan";

        TelephonyManager telephonyManager = (TelephonyManager) ActorData.activity .getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2g";

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3g";

            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4g";

            // need android sdk 29
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5g";

            default:
                return "unknown";
        }

    }

    /// \brief 获取WIFI信号强度，范围为[0,5]
    public static int getNetworkStrength() {
        WifiInfo info = wifiManager_.getConnectionInfo();
        if (info.getBSSID() == null)
            return -1;
        int level =  WifiManager.calculateSignalLevel(info.getRssi(), 5);
        return level;
    }

    /// \brief 获取手机内部空间总大小
    static public long getTotalInternalStorageSize() {
        //获取内部存储根目录
        File path = Environment.getDataDirectory();
        //系统的空间描述类
        StatFs stat = new StatFs(path.getPath());
        //每个区块占字节数
        long blockSize = stat.getBlockSizeLong();
        //区块总数
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    /// \brief 获取手机内部可用空间大小
    static public long getAvailableInternalStorageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    /// \brief 亮屏
    public static void screenOn() {

    }

    /// \brief 熄屏
    public static void screenOff() {

    }
}
