using System.Collections.Generic;
using System;
using XLua;
using UnityEngine;

public static class ExportToLua
{
    [CSharpCallLua]
    public static List<Type> CSharpCallLua = new List<Type>()
    {
        typeof(System.Action),
        typeof(System.Action<object>),
        typeof(System.Collections.IEnumerator),
        typeof(UnityEngine.Events.UnityAction),
        typeof(UnityEngine.Events.UnityAction<object>),
    };


    [LuaCallCSharp]
    public static List<Type> modules = new List<Type>()
    {
        typeof(PluginManager),
    };
}
