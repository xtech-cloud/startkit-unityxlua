package cloud.xtech.actor;

import android.app.Activity;
import android.util.Log;

public class ActorPlugin {

    /// \brief Unity调用此函数注入activity
    public static void InjectActivity(Activity _activity) {
        Log.i("ActorPlugin", "##########################################################################");
        Log.i("ActorPlugin", "##########################################################################");
        Log.i("ActorPlugin", "inject activity: " + _activity.toString());
        Log.i("ActorPlugin", "##########################################################################");
        Log.i("ActorPlugin", "##########################################################################");

        ActorData.activity  = _activity;
        SystemUtility.Setup(_activity);
    }

    /// \brief Unity调用此函数完成插件初始化
    public static void Initialize() {
        Log.i("ActorPlugin", "##########################################################################");
        Log.i("ActorPlugin", "##########################################################################");
        Log.i("ActorPlugin", "initialize");
        Log.i("ActorPlugin", "##########################################################################");
        Log.i("ActorPlugin", "##########################################################################");
        if (null == ActorData.activity ) {
            Log.e("ActorPlugin", "need inject activity");
            return;
        }

        SystemUtility.Initialize();
        // 请求wakelock
        SystemUtility.acquireWakeLock();
    }

    /// \brief Unity调用此函数完成插件清理
    public static void Release()
    {
        Log.i("ActorPlugin", "##########################################################################");
        Log.i("ActorPlugin", "##########################################################################");
        Log.i("ActorPlugin", "release");
        Log.i("ActorPlugin", "##########################################################################");
        Log.i("ActorPlugin", "##########################################################################");
        SystemUtility.Release();
        //释放wakelock
        SystemUtility.releaseWakeLock();
    }

    public static void SetDomain(String _domain) {
        Log.i("ActorPlugin", "SetDomain: " + _domain);
        ActorData.domain = _domain;
    }

    public static void SetAddress(String _address, int _port) {
        Log.i("ActorPlugin", "SetAddress: " + _address + ":" + _port);
        ActorData.address = _address;
        ActorData.port = _port;
    }

    public static void SetApiKey(String _apikey) {
        Log.i("ActorPlugin", "SetApiKey: " + _apikey);
        ActorData.apikey = _apikey;
    }

    public static void SetPushInterval(int _interval)
    {
        Log.i("ActorPlugin", "SetPushInterval:" + _interval);

        ActorData.pushInterval = _interval;
    }
}//class
