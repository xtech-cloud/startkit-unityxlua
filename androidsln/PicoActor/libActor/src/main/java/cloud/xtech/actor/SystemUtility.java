package cloud.xtech.actor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.pvr.tobservice.ToBServiceHelper;
import com.pvr.tobservice.enums.PBS_PackageControlEnum;
import com.pvr.tobservice.interfaces.IIntCallback;

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
    private static MediaProjectionManager mediaProjectionManager_ = null;
    private static CaptureListener captureListener_ = null;

    public static void Setup(Activity _activity)
    {
        wifiManager_ = (WifiManager) _activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager_ = (AudioManager) _activity .getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        powerManager_ = (PowerManager) _activity.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        activityManager_ = (ActivityManager)  _activity.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        mediaProjectionManager_ = (MediaProjectionManager)  _activity.getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        captureListener_ = new CaptureListener(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/Screenshots");
        Log.i("ActorPlugin", "BRAND: " + Build.BRAND);
        Log.i("ActorPlugin", "MODEL: " + Build.MODEL);
        Log.i("ActorPlugin", "PRODUCT: " + Build.PRODUCT);
        Log.i("ActorPlugin", "DEVICE: " + Build.DEVICE);
        Log.i("ActorPlugin", "HARDWARE: " + Build.HARDWARE);
        Log.i("ActorPlugin", "FINGERPRINT: " + Build.FINGERPRINT);

        // ??????wifi
        wifiManager_.setWifiEnabled(true);

        // ????????????????????????
        ToBServiceHelper.getInstance().setBindCallBack(new ToBServiceHelper.BindCallBack() {
            @Override
            public void bindCallBack(Boolean status) {
                Log.i("ActorPlugin", "????????? bind pico ToBService " + status);
            }
        });
        ActorServiceHelper.getInstance().setBindCallBack(new ActorServiceHelper.BindCallBack() {
            @Override
            public void bindCallBack(Boolean status) {
                Log.i("ActorPlugin", "????????? bind ActorService " + status);
            }
        });
    }

    public static  void Initialize()
    {
        captureListener_.startWatching();
        // ????????????
        ToBServiceHelper.getInstance().bindTobService(ActorData.activity.getApplicationContext());
        ActorServiceHelper.getInstance().bindTobService(ActorData.activity.getApplicationContext());
    }

    public static  void Release()
    {
        captureListener_.stopWatching();
        // ????????????
        ToBServiceHelper.getInstance().unBindTobService(ActorData.activity.getApplicationContext());
        ActorServiceHelper.getInstance().unBindTobService(ActorData.activity.getApplicationContext());
    }

    public  static void acquireWakeLock()
    {
        try {
            ToBServiceHelper.getInstance().getServiceBinder().pbsAcquireWakeLock();
            Log.i("ActorPlugin", "acquire wakelock success");
        } catch (RemoteException e) {
            Log.e("ActorPlugin", "acquire wakelock  failed");
            Log.e("ActorPlugin", e.getMessage());
        }
    }

    public  static void releaseWakeLock()
    {
        try {
            ToBServiceHelper.getInstance().getServiceBinder().pbsReleaseWakeLock();
            Log.i("ActorPlugin", "release wakelock success");
        } catch (RemoteException e) {
            Log.e("ActorPlugin", "release wakelock  failed");
            Log.e("ActorPlugin", e.getMessage());
        }
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

    /// \brief ????????????
    public static int getBattery() {
        //???????????????[0,100]
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
        int volume = 0;
        try{
            //???????????????[0,15]??????????????????0,100
            int maxVolume =audioManager_.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume =  audioManager_.getStreamVolume( AudioManager.STREAM_MUSIC );
            volume = currentVolume*100/maxVolume;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return volume;
    }

    /// \brief ????????????
    public static void setVolume(int _value)
    {
        //????????????????????????[0,15]
        try
        {
            int max = audioManager_.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int value = _value * max / 100;
            audioManager_.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(_value/100.0f*max), 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    /// \brief ????????????
    public static int getBrightness() {
        int screenBrightness=0;
        try{
            //???????????????[0,255]??????????????????0,100
            int maxScreenBrightness = 255;
            int currentScreenBrightness = Settings.System.getInt(ActorData.activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            screenBrightness = currentScreenBrightness*100/maxScreenBrightness;
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
            //????????????????????????[0,255]
            //SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 ???????????????????????????
            // SCREEN_BRIGHTNESS_MODE_MANUAL=0  ???????????????????????????
            Settings.System.putInt(ActorData.activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            int value = _value * 255 / 100;
            Settings.System.putInt(ActorData.activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static String getShape() {
        return "Mobile";
    }

    /// \brief ??????WIFI????????????
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

    /// \brief ????????????????????????
    public static int getNetworkStrength() {
        //???????????????[0,5]??????????????????0,100
        int level = 0;
        try
        {
            WifiInfo info = wifiManager_.getConnectionInfo();
            if (info.getBSSID() == null)
                return 0;
            int maxLevel = 5;
            int currentLevel =  WifiManager.calculateSignalLevel(info.getRssi(), 5);
            level = currentLevel*100/maxLevel;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
        if(ActorData.activeApplication.equalsIgnoreCase(_package))
            return;

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

        Log.e("ActorPlugin", "run "+_package+" success ......");
        ActorData.activeApplication = _package;
    }

    public static void killAPK() {
        Log.i("ActorPlugin", "kill application: " + ActorData.activeApplication);

        //?????????????????????????????????
        //final ActivityManager am = (ActivityManager) ActorData.activity.getSystemService(ACTIVITY_SERVICE) ;
        //am.moveTaskToFront(ActorData.activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);

        try {
            ToBServiceHelper.getInstance().getServiceBinder().pbsKillAppsByPidOrPackageName(null, new String[]{ActorData.activeApplication}, 0);
        } catch (RemoteException e) {
            Log.e("ActorPlugin", e.getMessage());
        }
        Log.e("ActorPlugin", "kill "+ActorData.activeApplication+" success ......");
        ActorData.activeApplication = "";
    }

    public static void InstallApk(String _filepath)
    {
        Log.e("ActorPlugin", "================================================================================");
        Log.e("ActorPlugin", "ready to install:" + _filepath);

        try {
            ToBServiceHelper.getInstance().getServiceBinder().pbsControlAPPManger(PBS_PackageControlEnum.PACKAGE_SILENCE_INSTALL, _filepath, 0, new IIntCallback.Stub(){
                @Override
                public void callback(int _result) throws RemoteException {
                    Log.i("ActorPlugin", "install apk callback: " + _result);
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static int RunCommand(String command) {
        Log.d("ActorPlugin", command);
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            int i = process.waitFor();
            return i;
        } catch (Exception e) {
            Log.e("ActorPlugin", e.getMessage());
            return -1;
        } finally {
            process.destroy();
        }
    }

    public static void Capture()
    {
        Intent captureIntent = mediaProjectionManager_.createScreenCaptureIntent();
        ActorData.activity.startActivityForResult(captureIntent, 1);
    }
}
