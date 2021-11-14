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

        // 开启wifi
        wifiManager_.setWifiEnabled(true);

        // 设定服务绑定回调
        ToBServiceHelper.getInstance().setBindCallBack(new ToBServiceHelper.BindCallBack() {
            @Override
            public void bindCallBack(Boolean status) {
                Log.i("ActorPlugin", "！！！ bind pico ToBService " + status);
            }
        });
        ActorServiceHelper.getInstance().setBindCallBack(new ActorServiceHelper.BindCallBack() {
            @Override
            public void bindCallBack(Boolean status) {
                Log.i("ActorPlugin", "！！！ bind ActorService " + status);
            }
        });
    }

    public static  void Initialize()
    {
        // 绑定服务
        ToBServiceHelper.getInstance().bindTobService(ActorData.activity.getApplicationContext());
        ActorServiceHelper.getInstance().bindTobService(ActorData.activity.getApplicationContext());
    }

    public static  void Release()
    {
        // 解绑服务
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

    /// \brief 获取电量
    public static int getBattery() {
        //系统范围为[0,100]
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
        int volume = 0;
        try{
            //系统范围为[0,15]，需要转换为0,100
            int maxVolume =audioManager_.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume =  audioManager_.getStreamVolume( AudioManager.STREAM_MUSIC );
            volume = currentVolume*100/maxVolume;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return volume;
    }

    /// \brief 设置音量
    public static void setVolume(int _value)
    {
        //转换为系统范围的[0,15]
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


    /// \brief 获取亮度
    public static int getBrightness() {
        int screenBrightness=0;
        try{
            //系统范围为[0,255]，需要转换为0,100
            int maxScreenBrightness = 255;
            int currentScreenBrightness = Settings.System.getInt(ActorData.activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            screenBrightness = currentScreenBrightness*100/maxScreenBrightness;
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
            //转换为系统范围的[0,255]
            //SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
            // SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度
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

    /// \brief 获取WIFI信号类型
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

    /// \brief 获取网络信号强度
    public static int getNetworkStrength() {
        //系统范围为[0,5]，需要转换为0,100
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

    public static void runAPK(String _package)
    {
        if(ActorData.activeApplication.equalsIgnoreCase(_package))
            return;

        // 如果当前正在运行其他应用，先退出
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

        //运行应用，大厅切换到后台
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

        //最优先将大厅切换到前台
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
}
