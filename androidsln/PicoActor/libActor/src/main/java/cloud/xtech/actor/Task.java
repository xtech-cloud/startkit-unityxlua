package cloud.xtech.actor;

import android.util.Log;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Task
{
    public static void execute()
    {
        for (Map.Entry<String,String> entry: ActorData.taskWaiting.entrySet()) {
            Log.i("ActorPlugin", "execute task: " + entry.getKey());
            ActorData.taskFinish.add(entry.getKey());
            byte[] bytes = Base64.decode(entry.getValue(), Base64.URL_SAFE);
            String str = new String(bytes);
            Log.i("ActorPlugin", "parameter : " + str);
            JSONObject json = null;
            try {
                json = new JSONObject(str);
            } catch (JSONException e) {
                Log.e("ActorPlugin", e.getMessage());
                continue;
            }
            if(entry.getKey().equalsIgnoreCase("/application/run")) {
                doRunApplication(json);
            }
            else if(entry.getKey().equalsIgnoreCase("/application/exit")) {
                doExitApplication(json);
            }
        }
    }

    private static void doRunApplication(JSONObject _json)
    {
        if(!_json.has("program"))
            return;
        String program = null;
        try {
            program = _json.getString("program");
        } catch (JSONException e) {
            Log.e("ActorPlugin", e.getMessage());
        }
        SystemUtility.runAPK(program);
    }

    private static void doExitApplication(JSONObject _json)
    {
        SystemUtility.killAPK();
    }
}
