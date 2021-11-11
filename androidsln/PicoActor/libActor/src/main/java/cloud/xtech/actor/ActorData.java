package cloud.xtech.actor;

import android.app.Activity;

public class ActorData
{
    public static Activity activity;

    public static String domain = "";
    public static String address = "";
    public static int port = 0;
    public static int pushInterval = 1000;

    // 本地的应用清单md5
    public static String localApplicationMD5 = "";
    public static String upgradeProgress= "";

    // 当前执行的apk的包名
    public static String activeApplication = "";
}
