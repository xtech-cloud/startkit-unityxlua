package cloud.xtech.actor;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

class Application
{
    private String UUID;
    private String Name;
    private String Version;
    private String Program;
    private String Location;
    private String Url;
    private int Upgrade;

    public String getUUID() {
        return UUID;
    }

    public String getName() {
        return Name;
    }

    public String getVersion() {
        return Version;
    }

    public String getProgram() {
        return Program;
    }

    public String getLocation() {
        return Location;
    }

    public String getUrl() {
        return Url;
    }

    public int getUpgrade() {
        return Upgrade;
    }
}

class UpgradeStatus
{
    private int total;
    private int finish;
    private int progress;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}

public class Upgrader
{
    private UpgradeStatus status;
    private UpgraderTask task;
    private LinkedList<Application> applicationList;
    private Gson gson;
    private String md5;
    private Application currentApplication;

    private UpgraderListener listener = new UpgraderListener() {
        @Override
        public void onProgress(int progress) {
            status.setProgress(progress);
            UnityPlayer.UnitySendMessage("__ACTOR__", "HandleActorUpgradeStatus", gson.toJson(status));
        }

        @Override
        public void onSuccess() {
            Log.i("ActorPlugin", "download success");

            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            String md5file = directory + currentApplication.getUUID() + ".md5";
            try {
                FileOutputStream fos = new FileOutputStream(md5file);
                fos.write(currentApplication.getUrl().getBytes());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            download();
        }

        @Override
        public void onFailed() {
            Log.i("ActorPlugin", "download failed");
            download();
        }

        @Override
        public void onPaused() {
            Log.i("ActorPlugin", "download paused");
            download();
        }

        @Override
        public void onCanceled() {
            Log.i("ActorPlugin", "download canceled");
            download();
        }
    };

    public boolean IsRunning()
    {
        return task != null;
    }

    public boolean Check(String _md5)
    {
        if(_md5.isEmpty())
            return false;
        Log.i("ActorPlugin", "check upgrade:" + _md5);
        return true;
    }

    public void Upgrade(String _md5, String _manifest)
    {
        md5 = _md5;
        if(_manifest.isEmpty())
            return;

        // 判断整个清单是否已经更新过
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String md5file = directory + "upgrade.md5";
        File file = new File(md5file);
        if(file.exists())
        {
            try {
                FileInputStream fis = new FileInputStream(md5file);
                int length = fis.available();
                byte[] buffer = new byte[length];
                fis.read(buffer);
                String res = new String(buffer);
                fis.close();
                if(res.equalsIgnoreCase(_md5))
                {
                    //return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] json = Base64.decode(_manifest, Base64.URL_SAFE);
        String manifest = new String(json);
        Log.i("ActorPlugin", manifest);
        gson = new Gson();
        Application[] applicationAry = gson.fromJson(manifest, new TypeToken<Application[]>(){}.getType());
        applicationList = new LinkedList<Application>();
        for (int i = 0; i< applicationAry.length; i++)
        {
            // 只处理自动更新的
            if(applicationAry[i].getUpgrade() == 2)
            {
                // 忽略已经更新过的
                String md5 = "";
                md5file = directory + applicationAry[i].getUUID() + ".md5";
                file = new File(md5file);
                if(file.exists())
                {
                    try {
                        FileInputStream fis = new FileInputStream(md5file);
                        int length = fis.available();
                        byte[] buffer = new byte[length];
                        fis.read(buffer);
                        if(new String(buffer).equalsIgnoreCase(applicationAry[i].getUrl()))
                            continue;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                applicationList.add(applicationAry[i]);
            }
        }
        status = new UpgradeStatus();
        status.setTotal(applicationList.size());
        download();
    }

    private void download()
    {
        if(null == applicationList)
            return;

        if(applicationList.size() == 0)
        {
            task = null;

            status.setFinish(status.getTotal());
            status.setProgress(100);
            UnityPlayer.UnitySendMessage("__ACTOR__", "HandleActorUpgradeStatus", gson.toJson(status));

            // 保存清单的MD5
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            String md5file = directory + "upgrade.md5";
            try {
                FileOutputStream fos = new FileOutputStream(md5file);
                fos.write(md5.getBytes());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        status.setFinish(status.getTotal()-applicationList.size());
        status.setProgress(0);
        UnityPlayer.UnitySendMessage("__ACTOR__", "HandleActorUpgradeStatus", gson.toJson(status));

        currentApplication = applicationList.poll();

        Log.i("ActorPlugin", "ready to download: " + currentApplication.getName() + " from: " + currentApplication.getUrl());
        task = new UpgraderTask(listener);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentApplication.getUrl());
    }
}
