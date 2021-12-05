package cloud.xtech.actor;

import android.app.Activity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ActorData
{
    public static Activity activity;

    public static String domain = "";
    public static String address = "";
    public static int port = 0;
    public static String apikey  = "";
    public static int pushInterval = 1000;

    // 本地的应用清单md5
    public static String localApplicationMD5 = "";
    public static String upgradeProgress= "";

    // 等待执行的任务
    public static Map<String,String> taskWaiting = new HashMap<>();
    // 完成的任务
    public static List<String> taskFinish = new LinkedList<>();

    // 当前执行的apk的包名
    public static String activeApplication = "";

    // 最新的截图
    public static byte[] latestCapture = null;
}
