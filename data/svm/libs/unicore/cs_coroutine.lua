local util = require 'xlua.util'

local gameobject = CS.UnityEngine.GameObject('Coroutine_Runner')
local coroutine_runner = gameobject:AddComponent(typeof(CS.CoroutineRunner))

return {
    start = function(...)
	    return coroutine_runner:StartCoroutine(util.cs_generator(...))
	end;

	stop = function(coroutine)
	    coroutine_runner:StopCoroutine(coroutine)
	end
}
