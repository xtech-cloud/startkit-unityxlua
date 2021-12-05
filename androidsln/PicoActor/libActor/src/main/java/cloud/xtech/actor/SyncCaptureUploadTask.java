package cloud.xtech.actor;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SyncCaptureUploadTask extends AsyncTask<Void, Void, Void> {
    private  int interval_ = 1000;
    private String domain_ = "";
    private String address_ = "";
    private int port_  = 80;
    private int retry = 0;

    SyncCaptureUploadTask(String _address, int _port, int _interval, String _domain) {
        address_ = _address;
        port_ = _port;
        interval_ = _interval;
        domain_ = _domain;
    }

    @Override
    protected Void doInBackground(Void... nothing) {
        String uri = address_+ ":" + port_+"/ogm/actor/Sync/Upload";
        while(true) {
            if(isCancelled())
                break;

            try {
                Thread.sleep(interval_);
            } catch (InterruptedException  e) {
                Log.e("ActorPlugin", e.getMessage());
            }

            if(ActorData.latestCapture == null)
                continue;

            Log.i("ActorPlugin", "sync upload ......");
            String capture = Base64.encodeToString(ActorData.latestCapture, Base64.URL_SAFE | Base64.NO_WRAP);
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

                String req_json = buildRequestJson(capture);
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(req_json);
                //Log.e("ActorPlugin", req_json);
                dos.flush();
                dos.close();

                Log.i("ActorPlugin", "StatusCode: " + String.valueOf(conn.getResponseCode()));
            } catch (ProtocolException e) {
                Log.e("ActorPlugin", e.getMessage());
            } catch (MalformedURLException e) {
                Log.e("ActorPlugin: ", e.getMessage());
            } catch (IOException e) {
                Log.e("ActorPlugin", e.getMessage());
            }

            ActorData.latestCapture = null;
        }
        return null;
    }

    protected  String buildRequestJson(String _capture)
    {
        JSONObject data = new JSONObject();
        try {
            data.put("domain", domain_);
            data.put("device", SystemUtility.getSerialNumber());
            data.put("name", "capture");
            data.put("data", _capture);
        } catch (JSONException e) {
            Log.e("ActorPlugin: ", e.getMessage());
        }
        return data.toString();
    }
}
