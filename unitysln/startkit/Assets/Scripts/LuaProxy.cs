using System;
using System.IO;
using System.Text;
using System.Collections.Generic;
using UnityEngine;
using XLua;

public class LuaProxy
{
    public MonoBehaviour rootMono {get;set;}
    public string vendor {get;set;}

    private LuaEnv rootLuaEnv_ = new LuaEnv();
    private LuaTable scriptEnv_
    {
        get;
        set;
    }

    private Action luaAwake;
    private Action luaEnable;
    private Action luaStart;
    private Action luaUpdate;
    private Action luaDisable;
    private Action luaDestroy;

    private List<string> searchPaths = new List<string>();
    public void AddSearchPath(string _path)
    {
        searchPaths.Add(_path);
    }

    public void RequireRoot(string _entry)
    {
        scriptEnv_ = rootLuaEnv_.NewTable();

        LuaTable meta = rootLuaEnv_.NewTable();
        meta.Set("__index", rootLuaEnv_.Global);
        scriptEnv_.SetMetaTable(meta);
        meta.Dispose();

        rootLuaEnv_.Global.Set<string, MonoBehaviour>("G_RootMono", rootMono);
        rootLuaEnv_.Global.Set<string, MonoBehaviour>("G_CoroutineRunner", rootMono.gameObject.GetComponent<CoroutineRunner>());
        rootLuaEnv_.Global.Set<string, string>("G_Vendor", vendor);
        foreach (string path in searchPaths)
            addLoader(path);
        rootLuaEnv_.DoString(string.Format("require '{0}'", _entry), "LuaBehaviour", scriptEnv_);
        luaAwake = scriptEnv_.Get<Action>("uniAwake");
        luaEnable = scriptEnv_.Get<Action>("uniEnable");
        luaStart = scriptEnv_.Get<Action>("uniStart");
        luaUpdate = scriptEnv_.Get<Action>("uniUpdate");
        luaDisable = scriptEnv_.Get<Action>("uniDisable");
        luaDestroy = scriptEnv_.Get<Action>("uniDestroy");
    }

    public void DoAwake()
    {
        if (null != luaAwake)
            luaAwake();
    }

    public void DoOnEnable()
    {
        if (null != luaEnable)
            luaEnable();
    }

    public void DoStart()
    {
        if (null != luaStart)
            luaStart();
    }

    public void DoUpdate()
    {
        rootLuaEnv_.Tick();
        if (null != luaUpdate)
            luaUpdate();
    }

    public void DoOnDisable()
    {
        if (null != luaDisable)
            luaDisable();
    }

    public void DoOnDestroy()
    {
        if (null != luaDestroy)
            luaDestroy();
        scriptEnv_.Dispose();
    }

    public string ReadFile(string _filepath)
    {
        byte[] bytes = new byte[0];
        try
        {
            var filepath = _filepath;
#if UNITY_ANDROID && !UNITY_EDITOR
            UnityEngine.WWW www = new UnityEngine.WWW(filepath);
            while (true)
            {
                if (!string.IsNullOrEmpty(www.error))
                {
                    System.Threading.Thread.Sleep(50);
                    break;
                }
                if (www.isDone)
                {
                    bytes = www.bytes;
                    break;
                }
            }
#else
            if (File.Exists(filepath))
            {
                bytes = File.ReadAllBytes(filepath);
            }
#endif
        }
        catch (System.Exception e)
        {
            Debug.LogException(e);
        }
        return Encoding.UTF8.GetString(bytes);
    }

    private void addLoader(string _searchPath)
    {
        string searchPath = _searchPath;
        rootLuaEnv_.AddLoader((ref string _filename) =>
        {
            string filename = _filename.Replace(".", "/");
            byte[] bytes = null;
            try
            {
                var filepath = searchPath + "/" + filename + ".lua";
#if UNITY_ANDROID && !UNITY_EDITOR
                UnityEngine.WWW www = new UnityEngine.WWW(filepath);
                while (true)
                {
                    if (!string.IsNullOrEmpty(www.error))
                    {
                        System.Threading.Thread.Sleep(50);
                        break;
                    }
                    if (www.isDone)
                    {
                        bytes = www.bytes;
                        break;
                    }
                }
#else
                if (File.Exists(filepath))
                {
                    bytes = File.ReadAllBytes(filepath);
                }
#endif
            }
            catch (System.Exception e)
            {
                Debug.LogException(e);
            }
            return bytes;
        });
    }
}
