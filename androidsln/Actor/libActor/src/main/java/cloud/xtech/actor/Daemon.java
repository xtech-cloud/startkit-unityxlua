package cloud.xtech.actor;

import android.os.AsyncTask;
import android.util.Log;


class Utility
{
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


public class Daemon {

    private ActorService service_ = null;
    private SyncPushTask syncPushTask = null;
    private UpgraderTask upgraderTask = null;

    public void InjectService(ActorService _service) {
        service_ = _service;
    }

    public void Run() {
        Log.i("ActorPlugin", "Run Daemon ... ");

        syncPushTask = new SyncPushTask(ActorPlugin.getAddress(),  ActorPlugin.getPort(), ActorPlugin.getPushInterval(), ActorPlugin.getDomain());
        syncPushTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void Stop() {
        syncPushTask.cancel(true);
    }


}

