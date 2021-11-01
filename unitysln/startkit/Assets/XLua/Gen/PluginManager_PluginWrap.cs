#if USE_UNI_LUA
using LuaAPI = UniLua.Lua;
using RealStatePtr = UniLua.ILuaState;
using LuaCSFunction = UniLua.CSharpFunctionDelegate;
#else
using LuaAPI = XLua.LuaDLL.Lua;
using RealStatePtr = System.IntPtr;
using LuaCSFunction = XLua.LuaDLL.lua_CSFunction;
#endif

using XLua;
using System.Collections.Generic;


namespace XLua.CSObjectWrap
{
    using Utils = XLua.Utils;
    public class PluginManagerPluginWrap 
    {
        public static void __Register(RealStatePtr L)
        {
			ObjectTranslator translator = ObjectTranslatorPool.Instance.Find(L);
			System.Type type = typeof(PluginManager.Plugin);
			Utils.BeginObjectRegister(type, L, translator, 0, 2, 0, 0);
			
			Utils.RegisterFunc(L, Utils.METHOD_IDX, "Do", _m_Do);
			Utils.RegisterFunc(L, Utils.METHOD_IDX, "Call", _m_Call);
			
			
			
			
			
			Utils.EndObjectRegister(type, L, translator, null, null,
			    null, null, null);

		    Utils.BeginClassRegister(type, L, __CreateInstance, 1, 0, 0);
			
			
            
			
			
			
			Utils.EndClassRegister(type, L, translator);
        }
        
        [MonoPInvokeCallbackAttribute(typeof(LuaCSFunction))]
        static int __CreateInstance(RealStatePtr L)
        {
            
			try {
                ObjectTranslator translator = ObjectTranslatorPool.Instance.Find(L);
				if(LuaAPI.lua_gettop(L) == 5 && (LuaAPI.lua_isnil(L, 2) || LuaAPI.lua_type(L, 2) == LuaTypes.LUA_TSTRING) && translator.Assignable<object>(L, 3) && translator.Assignable<System.Reflection.MethodInfo>(L, 4) && translator.Assignable<System.Reflection.MethodInfo>(L, 5))
				{
					string __name = LuaAPI.lua_tostring(L, 2);
					object __instance = translator.GetObject(L, 3, typeof(object));
					System.Reflection.MethodInfo __miDo = (System.Reflection.MethodInfo)translator.GetObject(L, 4, typeof(System.Reflection.MethodInfo));
					System.Reflection.MethodInfo __miCall = (System.Reflection.MethodInfo)translator.GetObject(L, 5, typeof(System.Reflection.MethodInfo));
					
					var gen_ret = new PluginManager.Plugin(__name, __instance, __miDo, __miCall);
					translator.Push(L, gen_ret);
                    
					return 1;
				}
				
			}
			catch(System.Exception gen_e) {
				return LuaAPI.luaL_error(L, "c# exception:" + gen_e);
			}
            return LuaAPI.luaL_error(L, "invalid arguments to PluginManager.Plugin constructor!");
            
        }
        
		
        
		
        
        
        
        
        [MonoPInvokeCallbackAttribute(typeof(LuaCSFunction))]
        static int _m_Do(RealStatePtr L)
        {
		    try {
            
                ObjectTranslator translator = ObjectTranslatorPool.Instance.Find(L);
            
            
                PluginManager.Plugin gen_to_be_invoked = (PluginManager.Plugin)translator.FastGetCSObj(L, 1);
            
            
                
                {
                    string __method = LuaAPI.lua_tostring(L, 2);
                    object[] __param = (object[])translator.GetObject(L, 3, typeof(object[]));
                    
                    gen_to_be_invoked.Do( __method, __param );
                    
                    
                    
                    return 0;
                }
                
            } catch(System.Exception gen_e) {
                return LuaAPI.luaL_error(L, "c# exception:" + gen_e);
            }
            
        }
        
        [MonoPInvokeCallbackAttribute(typeof(LuaCSFunction))]
        static int _m_Call(RealStatePtr L)
        {
		    try {
            
                ObjectTranslator translator = ObjectTranslatorPool.Instance.Find(L);
            
            
                PluginManager.Plugin gen_to_be_invoked = (PluginManager.Plugin)translator.FastGetCSObj(L, 1);
            
            
                
                {
                    string __method = LuaAPI.lua_tostring(L, 2);
                    object[] __param = (object[])translator.GetObject(L, 3, typeof(object[]));
                    System.Action __callback = translator.GetDelegate<System.Action>(L, 4);
                    
                    gen_to_be_invoked.Call( __method, __param, __callback );
                    
                    
                    
                    return 0;
                }
                
            } catch(System.Exception gen_e) {
                return LuaAPI.luaL_error(L, "c# exception:" + gen_e);
            }
            
        }
        
        
        
        
        
        
		
		
		
		
    }
}
