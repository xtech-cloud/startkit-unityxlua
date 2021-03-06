package cloud.xtech.actor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.ACTIVITY_SERVICE;

public class SystemUtility {
    private static WifiManager wifiManager_ = null;
    private static AudioManager audioManager_ = null;
    private static PowerManager powerManager_ = null;
    private static ActivityManager activityManager_ = null;

    public static void Setup(Activity _activity)
    {
        wifiManager_ = (WifiManager) _activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager_ = (AudioManager) _activity .getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        powerManager_ = (PowerManager) _activity.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        activityManager_ = (ActivityManager)  _activity.getApplicationContext().getSystemService(ACTIVITY_SERVICE);

        Log.i("ActorPlugin", "BRAND: " + Build.BRAND);
        Log.i("ActorPlugin", "MODEL: " + Build.MODEL);
        Log.i("ActorPlugin", "PRODUCT: " + Build.PRODUCT);
        Log.i("ActorPlugin", "DEVICE: " + Build.DEVICE);
        Log.i("ActorPlugin", "HARDWARE: " + Build.HARDWARE);
        Log.i("ActorPlugin", "FINGERPRINT: " + Build.FINGERPRINT);

        // ??????wifi
        wifiManager_.setWifiEnabled(true);
    }

    /// \beirf ?????????????????????
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

    /// \beirf ???????????????
    public static String getName(){
        return Build.MODEL;
    }

    /// \beirf ??????????????????
    public static String getVersion(){
        // ??????packagemanager?????????
        PackageManager packageManager = ActorData.activity .getApplicationContext().getPackageManager();
        // getPackageName()????????????????????????
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(ActorData.activity .getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        return packInfo.versionName;
    }

    /// \beirf ??????????????????
    public static String getSystemVersion(){
        return android.os.Build.VERSION.RELEASE;
    }

    /// \brief ????????????????????????[0,100]
    public static int getBattery() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = ActorData.activity .getApplicationContext().registerReceiver(null,ifilter);
        int level = intent.getIntExtra("level", 0);
        int scale = intent.getIntExtra("scale", 0);
        if(0 == scale)
            return -1;
        return level;
    }

    /// \brief ????????????
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

    /// \brief s????????????
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


    /// \brief ????????????
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

    /// \brief ????????????
    public static void setBrightness(int _value)
    {
        try{
            //SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 ???????????????????????????
            // SCREEN_BRIGHTNESS_MODE_MANUAL=0  ???????????????????????????
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

    /// \brief ??????WIFI????????????????????????[0,5]
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

    /// \brief ??????WIFI????????????????????????[0,5]
    public static int getNetworkStrength() {
        WifiInfo info = wifiManager_.getConnectionInfo();
        if (info.getBSSID() == null)
            return -1;
        int level =  WifiManager.calculateSignalLevel(info.getRssi(), 5);
        return level;
    }

    /// \brief ?????????????????????????????????
    static public long getTotalInternalStorageSize() {
        //???????????????????????????
        File path = Environment.getDataDirectory();
        //????????????????????????
        StatFs stat = new StatFs(path.getPath());
        //????????????????????????
        long blockSize = stat.getBlockSizeLong();
        //????????????
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    /// \brief ????????????????????????????????????
    static public long getAvailableInternalStorageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        //????????????????????????
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    /// \brief ??????
    public static void screenOn() {

    }

    /// \brief ??????
    public static void screenOff() {

    }

    public static void runAPK(String _package)
    {
        // ????????????????????????????????????????????????
        if(!ActorData.activeApplication.isEmpty())
        {
            killAPK();
        }
        Intent intent = ActorData.activity .getPackageManager().getLaunchIntentForPackage(_package);
        if(intent == null)
        {
            Log.e("ActorPlugin", "package " + _package + " not found");
            return ;
        }

        //????????????????????????????????????
        try
        {
            ActorData.activity .startActivity(intent);
        }
        catch (ActivityNotFoundException ex)
        {
            Log.e("ActorPlugin", ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void killAPK() {
        Log.i("ActorPlugin", "kill application: " + ActorData.activeApplication);

        //?????????????????????????????????
        final ActivityManager am = (ActivityManager) ActorData.activity.getSystemService(ACTIVITY_SERVICE) ;
        am.moveTaskToFront(ActorData.activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);

        //??????2?????????????????????
        Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                am.killBackgroundProcesses(ActorData.activeApplication);
            }
        };
        mTimer.schedule(mTimerTask, 2000);
    }

    public static void InstallApk(String _filepath)
    {
        Log.e("ActorPlugin", "================================================================================");
        Log.e("ActorPlugin", "ready to install:" + _filepath);

        String[] args = {"pm", "install", "-r", _filepath};
        String result = apkProcess(args);
        Log.e("ActorPlugin", "install log:" + result);
    }

    public static String apkProcess(String[] args) {
        String result = null;
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }
}
