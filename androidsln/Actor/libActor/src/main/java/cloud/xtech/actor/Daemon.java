package cloud.xtech.actor;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.ACTIVITY_SERVICE;

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

    public void InjectService(ActorService _service) {
        service_ = _service;
    }

    public void Run() {
        Log.i("ActorPlugin", "Run Daemon ... ");
        new PushTask(ActorPlugin.getAddress(),  ActorPlugin.getPort(), ActorPlugin.getPushInterval(), ActorPlugin.getDomain()).execute();
    }

    public void Stop() {
    }

    private static class PushTask extends AsyncTask<Void, Void, Void> {
        private  int interval_ = 1000;
        private String domain_ = "";
        private String address_ = "";
        private int port_  = 80;

        PushTask(String _address, int _port, int _interval, String _domain) {
            address_ = _address;
            port_ = _port;
            interval_ = _interval;
            domain_ = _domain;
        }

        @Override
        protected Void doInBackground(Void... nothing) {
            String uri = address_+ ":" + port_+"/ogm/actor/Sync/Push";
             while(true) {
                 Log.e("ActorPlugin", "sync push ......");

                 try {
                     HttpURLConnection conn = (HttpURLConnection) new URL(uri).openConnection();
                     //设置请求方式,请求超时信息
                     conn.setRequestMethod("POST");
                     conn.setReadTimeout(5000);
                     conn.setConnectTimeout(5000);
                     //设置运行输入,输出:
                     conn.setDoOutput(true);
                     conn.setDoInput(true);
                     //Post方式不能缓存,需手动设置为false
                     conn.setUseCaches(false);
                     //设置content-type
                     conn.setRequestProperty("Content-Type", "application/json");

                     String req_json = buildRequestJson();
                     DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                     dos.writeBytes(req_json);
                     Log.e("ActorPlugin", req_json);
                     dos.flush();
                     dos.close();

                     if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                     {
                         InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                         BufferedReader bf = new BufferedReader(isr);
                         String receive = null;
                         String reply = "";
                         while((receive = bf.readLine()) != null)
                         {
                             reply += receive + "\n";
                         }
                         isr.close();
                         conn.disconnect();
                         processResponse(reply);
                     }
                     else
                     {
                         Log.e("StatusCode: ", String.valueOf(conn.getResponseCode()));
                     }
                 } catch (ProtocolException e) {
                     e.printStackTrace();
                 } catch (MalformedURLException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

                 try {
                     Thread.sleep(interval_);
                 } catch (InterruptedException  e) {
                     e.printStackTrace();
                 }
             }
        }

        protected  String buildRequestJson()
        {
            JSONObject data = new JSONObject();
            try {
                data.put("domain", domain_);
                JSONObject device = new JSONObject();
                device.put("serialNumber", SystemInfo.getSerialNumber());
                device.put("name", SystemInfo.getName());
                device.put("operatingSystem", "Android");
                device.put("systemVersion", SystemInfo.getSystemVersion());
                device.put("shape", SystemInfo.getShape());
                device.put("battery", SystemInfo.getBattery());
                device.put("volume", SystemInfo.getVolume());
                device.put("brightness", SystemInfo.getBrightness());
                device.put("storage", "SSD");
                device.put("storageBlocks", SystemInfo.getTotalInternalStorageSize());
                device.put("storageAvailable", SystemInfo.getAvailableInternalStorageSize());
                device.put("network", SystemInfo.getNetwork());
                device.put("networkStrength", SystemInfo.getNetworkStrength());
                data.put("device", device);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data.toString();
        }

        protected void processResponse(String _reply) {
            Log.e("ActorPlugin", _reply);
            /*
            if(Enum.ENUMS.POWER.POWER_OFF == _rsp.getProperty().getPower())
            {
                Utility.RunCommand("reboot -p");
                return;
            }
            if(Enum.ENUMS.POWER.POWER_REBOOT == _rsp.getProperty().getPower())
            {
                Utility.RunCommand("reboot");
                return;
            }
            if(Enum.ENUMS.SCREEN.SCREEN_ON == _rsp.getProperty().getScreen())
            {
                SystemInfo.screenOn();
            }
            else if(Enum.ENUMS.SCREEN.SCREEN_OFF == _rsp.getProperty().getScreen())
            {
                SystemInfo.screenOff();
            }
            else if(SystemInfo.getVolume() != _rsp.getProperty().getVolume())
            {
                SystemInfo.setVolume(_rsp.getProperty().getVolume());
            }
            else if(SystemInfo.getBrightness() != _rsp.getProperty().getBrightness())
            {
                SystemInfo.setBrightness(_rsp.getProperty().getBrightness());
            }
            //同步APK
            if(null != _rsp.getProperty().getApplication())
            {
                if(_rsp.getProperty().getApplication().endsWith(".apk"))
                {
                    if(ActorData.activeAPK.isEmpty())
                    {
                        Utility.EnterAPK(_rsp.getProperty().getApplication());
                        ActorData.activeAPK = _rsp.getProperty().getApplication();
                    }
                } else {
                    if(!ActorData.activeAPK.isEmpty())
                    {
                        Utility.ExitAPK();
                        ActorData.activeAPK = "";
                    }
                }
            }
            String encodedString = Base64.encodeToString(_rsp.toByteArray(), Base64.DEFAULT);
            UnityPlayer.UnitySendMessage("__DAEMON__", "HandleSyncData",encodedString);

             */
        }
    }
}

