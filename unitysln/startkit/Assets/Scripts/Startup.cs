using System;
using System.IO;
using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class Startup : MonoBehaviour
{
    private LuaProxy proxyLua;

    // Start is called before the first frame update
    void Awake()
    {
        string vendor = "data";
        // 解析参数
        string[] commandLineArgs = System.Environment.GetCommandLineArgs();
        foreach (string arg in commandLineArgs)
        {
            if (arg.StartsWith("-vendor="))
            {
                vendor = arg.Replace("-vendor=", "").Trim();
            }
        }
        Debug.LogFormat("Vendor is {0}", vendor);
        string svmDir = Path.Combine(Application.persistentDataPath, string.Format("{0}/svm", vendor));
        string appDir = Path.Combine(svmDir, "app");
        string libsDir = Path.Combine(svmDir, "libs");

        proxyLua = new LuaProxy();
        proxyLua.rootMono = this;
        proxyLua.vendor = vendor;
        proxyLua.AddSearchPath(appDir);
        proxyLua.AddSearchPath(libsDir);
        proxyLua.RequireRoot("startup");
        proxyLua.DoAwake();
    }

    void OnEnable()
    {
        Debug.Log("---------------  OnEnable ------------------------");
        proxyLua.DoOnEnable();
    }

    IEnumerator Start()
    {
        Debug.Log("---------------  Start ------------------------");
        yield return new WaitForEndOfFrame();
        proxyLua.DoStart();
    }

    void Update()
    {
        proxyLua.DoUpdate();
    }

    void OnDisable()
    {
        Debug.Log("---------------  OnDisable ------------------------");
        proxyLua.DoOnDisable();
    }

    void OnDestroy()
    {
        Debug.Log("---------------  OnDestroy ------------------------");
        proxyLua.DoOnDestroy();
    }
}
