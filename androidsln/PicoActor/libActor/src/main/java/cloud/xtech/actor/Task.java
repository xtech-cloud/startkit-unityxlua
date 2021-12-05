package cloud.xtech.actor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class Task
{
    public static void execute()
    {
        for (Map.Entry<String,String> entry: ActorData.taskWaiting.entrySet()) {
            Log.i("ActorPlugin", "execute task: " + entry.getKey());
            //只要收到了消息，就代表完成了j任务
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
            else if(entry.getKey().equalsIgnoreCase("/system/reboot")) {
                doSystemReboot();
            }
            else if(entry.getKey().equalsIgnoreCase("/system/shutdown")) {
                doSystemShutdown();
            }
            else if(entry.getKey().equalsIgnoreCase("/system/capture")) {
                doSystemCapture();
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

    private static void doSystemReboot()
    {
        SystemUtility.RunCommand("reboot");
    }

    private static void doSystemShutdown()
    {
        SystemUtility.RunCommand("reboot -p");
    }

    private static void doSystemCapture()
    {
        Intent intent = new Intent("pvr.intent.action.CAPTURE");
        intent.setPackage("com.pvr.shortcut");
        intent.putExtra("action_type", 2);
        ActorData.activity.startService(intent);
        //SystemUtility.Capture();
    }
}
