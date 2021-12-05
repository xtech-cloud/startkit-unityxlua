package cloud.xtech.actor;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.unity3d.player.UnityPlayer;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


class ReplyStatus
{
    private int code;
    private String message;
    public  ReplyStatus()
    {
        code = 0;
        message = "";
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String _value) {
        message = _value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

class SyncReply
{
    private ReplyStatus status;
    private int access;
    private String alias;
    private Map<String,String> property;
    private Map<String,String> task;

    public SyncReply()
    {
        status = new ReplyStatus();
        access = 0;
        alias = "";
        property = new HashMap<String,String>();
        task = new HashMap<String,String>();
    }

    public ReplyStatus getStatus() {
        return status;
    }

    public void setStatus(ReplyStatus status) {
        this.status = status;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Map<String, String> getProperty() {
        return property;
    }

    public void setProperty(Map<String, String> property) {
        this.property = property;
    }

    public Map<String, String> getTask() {
        return task;
    }

    public void setTask(Map<String, String> task) {
        this.task = task;
    }
}


public class SyncPushTask extends AsyncTask<Void, Void, Void> {
    private  int interval_ = 1000;
    private String domain_ = "";
    private String address_ = "";
    private int port_  = 80;
    private Upgrader upgrader;
    private List<String> downPropertyAry;

    SyncPushTask(String _address, int _port, int _interval, String _domain) {
        address_ = _address;
        port_ = _port;
        interval_ = _interval;
        domain_ = _domain;
        downPropertyAry = new LinkedList<String>();
        upgrader = new Upgrader();
    }

    @Override
    protected Void doInBackground(Void... nothing) {
        String uri = address_+ ":" + port_+"/ogm/actor/Sync/Push";
        while(true) {
            if(isCancelled())
                break;;

            Log.i("ActorPlugin", "sync push ......");

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
                conn.setRequestProperty("apikey", ActorData.apikey);

                String req_json = buildRequestJson();
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(req_json);
                Log.e("ActorPlugin", req_json);
                dos.flush();
                dos.close();

                Log.i("ActorPlugin", "StatusCode: " + String.valueOf(conn.getResponseCode()));
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
            } catch (ProtocolException e) {
                Log.e("ActorPlugin", e.getMessage());
            } catch (MalformedURLException e) {
                Log.e("ActorPlugin", e.getMessage());
            } catch (IOException e) {
                Log.e("ActorPlugin", e.getMessage());
            }

            try {
                Thread.sleep(interval_);
            } catch (InterruptedException  e) {
                Log.e("ActorPlugin", e.getMessage());
            }
        }
        return null;
    }

    protected  String buildRequestJson()
    {
        JSONObject data = new JSONObject();
        try {
            data.put("domain", domain_);

            JSONObject device = new JSONObject();
            device.put("serialNumber", SystemUtility.getSerialNumber());
            device.put("name", SystemUtility.getName());
            device.put("operatingSystem", "Android");
            device.put("systemVersion", SystemUtility.getSystemVersion());
            device.put("shape", SystemUtility.getShape());
            device.put("battery", SystemUtility.getBattery());
            device.put("volume", SystemUtility.getVolume());
            device.put("brightness", SystemUtility.getBrightness());
            device.put("storage", "SSD");
            device.put("storageBlocks", SystemUtility.getTotalInternalStorageSize());
            device.put("storageAvailable", SystemUtility.getAvailableInternalStorageSize());
            device.put("network", SystemUtility.getNetwork());
            device.put("networkStrength", SystemUtility.getNetworkStrength());
            data.put("device", device);

            JSONObject upProperty = new JSONObject();
            upProperty.put("_.device.serialnumber."+SystemUtility.getSerialNumber()+".application.md5", ActorData.localApplicationMD5);
            upProperty.put("_.device.serialnumber."+SystemUtility.getSerialNumber()+".upgrade.progress", ActorData.upgradeProgress);
            upProperty.put("_.device.serialnumber."+SystemUtility.getSerialNumber()+".application", ActorData.activeApplication);
            data.put("upProperty", upProperty);

            JSONArray task = new JSONArray();
            for(String cmd:ActorData.taskFinish) {
                task.put(cmd);
            }
            data.put("task", task);
            ActorData.taskFinish.clear();

            JSONArray downProperty = new JSONArray();
            downProperty.put("_.domain.application.md5");

            for(int i=0;i<downPropertyAry.size();i++)
            {
                downProperty.put(downPropertyAry.get(i));
            }
            downPropertyAry.clear();
            data.put("downProperty", downProperty);
        } catch (JSONException e) {
            Log.e("ActorPlugin", e.getMessage());
        }
        return data.toString();
    }

    protected void processResponse(String _reply) {
        Log.i("ActorPlugin", _reply);
        Gson gson = new Gson();
        SyncReply reply = gson.fromJson(_reply, SyncReply.class);

        if(!upgrader.IsRunning())
        {
            String applicationMD5 = "";
            boolean needUpgrade = false;
            if(reply.getProperty().containsKey("_.domain.application.md5"))
            {
                applicationMD5 = reply.getProperty().get("_.domain.application.md5");
                needUpgrade = upgrader.Check(applicationMD5);
                if(needUpgrade)
                {
                    if(reply.getProperty().containsKey("_.domain.application.manifest"))
                    {
                        String applicationManifest = reply.getProperty().get("_.domain.application.manifest");
                        upgrader.Upgrade(applicationManifest);
                    }
                    else
                    {
                        downPropertyAry.add("_.domain.application.manifest");
                    }
                }
            }

            ActorData.taskWaiting = reply.getTask();
            Task.execute();
        }

        UnityPlayer.UnitySendMessage("__ACTOR__", "HandleActorPushReply", _reply);
    }
}
