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

    public static void EnterAPK(String _package) {
        Log.i("ActorPlugin", "run app: " + _package);

        /*
        // 如果当前正在运行其他应用，先退出
        if(!ActorData.activeAPK.isEmpty()) {
            ExitAPK();
        }
        //.apk是追加的，需要去掉
        String pkg = _package.substring(0, _package.length() - 4);
        Intent apk = ActorData.activity .getPackageManager().getLaunchIntentForPackage(pkg);
        if(apk == null)
        {
            Log.e("ActorPlugin", "application " + pkg + " not found");
            return ;
        }
        //运行应用，大厅切换到后台
        ActorData.activity .startActivity(apk);

         */
    }

    public static void ExitAPK() {
        Log.i("libXVP", "exit app ...");

        /*
        //最优先将大厅切换到前台
        final ActivityManager am = (ActivityManager) ActorData.activity.getSystemService(ACTIVITY_SERVICE) ;
        am.moveTaskToFront(ActorData.activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);

        //延迟2秒杀掉应用进程
        final String pkg = ActorData.activeAPK.substring(0, ActorData.activeAPK.length() - 4);
        Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                am.killBackgroundProcesses(pkg);
            }
        };
        mTimer.schedule(mTimerTask, 2000);

         */
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

