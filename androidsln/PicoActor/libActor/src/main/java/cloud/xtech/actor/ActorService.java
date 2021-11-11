package cloud.xtech.actor;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class ActorService extends Service
{
    private SyncPushTask syncPushTask = null;

    @Override
    public IBinder onBind(Intent intent) {
        return new IActorService.Stub() {
        };
    }

    @Override
    public void onCreate(){
        Log.i("ActorPlugin", "ActorService.onCreate");
        super.onCreate();

        Log.i("ActorPlugin", "run SyncPush Task ...");
        syncPushTask = new SyncPushTask(ActorData.address,  ActorData.port, ActorData.pushInterval, ActorData.domain);
        syncPushTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int onStartCommand(Intent _intent, int _flags, int _startID) {
        Log.i("ActorPlugin", "ActorService.onStartCommand");
        return super.onStartCommand(_intent, _flags, _startID);
    }

    @Override
    public void onDestroy(){
        Log.i("ActorPlugin", "ActorService.onDestroy");
        super.onDestroy();

        Log.i("ActorPlugin", "stop SyncPush Task ...");
        syncPushTask.cancel(true);
    }
}

