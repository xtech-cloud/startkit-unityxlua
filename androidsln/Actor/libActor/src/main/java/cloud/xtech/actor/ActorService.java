package cloud.xtech.actor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class ActorService extends Service {
    private Daemon daemon_ = null;
    private PowerManager.WakeLock wakeLock_ = null;
    private WifiManager.WifiLock wifiLock_ = null;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ActorPlugin", "ActorService.onBind");
        return null;
    }

    @Override
    public void onCreate(){
        Log.i("ActorPlugin", "ActorService.onCreate");
        super.onCreate();

        if(null != daemon_)
        {
            Log.i("ActorPlugin", "daemon is running, do nothing");
            return;
        }


        PowerManager pm = (PowerManager) ActorData.activity.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        try {
            // 仅保持CPU运行，不保持屏幕和键盘常量
            wakeLock_ = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "actorservice:wakelock");
            wakeLock_.acquire();
            Log.i("ActorPlugin", "acquire wakelock success");
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        WifiManager wm = (WifiManager) ActorData.activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            // 熄屏状态下保持WIFI活跃
            wifiLock_ = wm.createWifiLock("actorservice:wifilock");
            wifiLock_.acquire();
            Log.i("ActorPlugin", "acquire wifilock success");
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        // 启动守护服务
        daemon_ = new Daemon();
        daemon_.InjectService(this);
        daemon_.Run();
    }

    @Override
    public int onStartCommand(Intent _intent, int _flags, int _startID) {
        Log.i("ActorPlugin", "ActorService.onStartCommand");
        return super.onStartCommand(_intent, _flags, _startID);
    }

    @Override
    public void onDestroy(){
        Log.i("ActorPlugin", "onDestroy");
        super.onDestroy();

        if (null != wakeLock_ && wakeLock_.isHeld()) {
            wakeLock_.release();
            wakeLock_ = null;
            Log.i("ActorPlugin", "release wakelock success");
        }

        if (null != wifiLock_ && wifiLock_.isHeld()) {
            wifiLock_.release();
            wifiLock_ = null;
            Log.i("ActorPlugin", "release wifilock success");
        }

        if(null != daemon_)
        {
            daemon_.Stop();
            daemon_ = null;
            Log.i("ActorPlugin", "stop daemon success");
        }
    }
}
