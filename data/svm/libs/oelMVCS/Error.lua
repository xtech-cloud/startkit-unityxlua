--- 错误类
-- @classmod LuaMVCS.Error
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'

local Error = class()

function Error:_init(_code, _message)
    -- #protected
    self.code_ = _code
    self.message_ = _message
    -- #endprotected
end

function Error:getCode()
    return self.code_
end

function Error:getMessage()
    return self.message_
end

function Error:ToString()
    return string.format('%s:%s', self.code_, self.message_)
end

function Error.IsOK(_err)
    return _err:getCode() == 0
end

function Error.NewOK()
    return Error(0, '')
end

function Error.NewNullErr(...)
    local msg = string.format( ... )
    return Error(-1, msg)
end

function Error.NewParamErr(...)
    local msg = string.format( ... )
    return Error(-2, msg)
end

function Error.NewAccessErr(...)
    local msg = string.format( ... )
    return Error(-3, msg)
end


return Error
