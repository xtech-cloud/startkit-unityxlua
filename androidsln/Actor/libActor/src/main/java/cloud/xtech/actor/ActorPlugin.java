package cloud.xtech.actor;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class ActorPlugin {

    private static Intent service_ = null;

    public  static String getDomain() {return domain_;}

    public  static String getAddress() {return address_;}
    public  static int getPort() {return port_;}
    public  static int getPushInterval() {return pushInterval_;}

    private static String domain_ = "";
    private static String address_ = "";
    private static int port_ = 0;
    private static int pushInterval_ = 1000;

    /// \brief Unity调用此函数注入activity
    public static void InjectActivity(Activity _activity) {
        Log.i("ActorPlugin", "inject activity: " + _activity.toString());
        ActorData.activity  = _activity;
        SystemInfo.Setup(_activity);
    }

    /// \brief Unity调用此函数完成插件初始化
    public static void Initialize() {
        Log.i("ActorPlugin", "initialize");
        if (null == ActorData.activity ) {
            Log.e("ActorPlugin", "need inject activity");
            return;
        }
    }

    /// \brief Unity调用此函数完成插件清理
    public static void Release() {
        Log.i("ActorPlugin", "Release");
    }

    public static void SetDomain(String _domain) {
        Log.i("ActorPlugin", "SetDomain: " + _domain);
        domain_ = _domain;
    }

    public static void SetAddress(String _address, int _port) {
        Log.i("ActorPlugin", "SetAddress: " + _address + ":" + _port);
        address_ = _address;
        port_ = _port;
    }

    public static void SetPushInterval(int _interval)
    {
        Log.i("ActorPlugin", "SetPushInterval:" + _interval);

        pushInterval_ = _interval;
    }

    /// \brief 运行后台服务
    public static void runService()
    {
        Log.i("ActorPlugin", "run service");
        service_ = new Intent(ActorData.activity , ActorService.class);
        //ActorData.activity .startForegroundService(service_);
        ActorData.activity .startService(service_);
    }

    /// \brief 结束后台服务
    public static void stopService()
    {
        Log.i("ActorPlugin", "stop service");
        ActorData.activity .stopService(service_);
    }
}//class
