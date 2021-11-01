local util = require 'xlua.util'

return {
    --- _runner: CS.CoroutineRunner
    start = function(_runner, ...)
	    return _runner:StartCoroutine(util.cs_generator(...))
	end;

    --- _runner: CS.CoroutineRunner
	stop = function(_runner, coroutine)
	    _runner:StopCoroutine(coroutine)
	end
}
