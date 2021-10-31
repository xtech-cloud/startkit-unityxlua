--- 日志类
-- @classmod oelMVCS.Logger
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'

local Logger = class()

Logger.Level_None = 0
Logger.Level_Error = 1
Logger.Level_Warning = 2
Logger.Level_Info = 3
Logger.Level_Debug = 4
Logger.Level_Trace = 5
Logger.Level_All = 6

--- 构造函数
function Logger:_init()
    -- #private
    self.level__ = Logger.Level_All
    -- endprivatre
end

--- 设置日志等级
function Logger:setLevel(_level)
    self.level__ = _level
end

function Logger:Trace( ... )
    if self.level__ < Logger.Level_Trace then
        return
    end
    local msg = string.format( ... )
    if self._trace == nil then
        print(string.format('TRACE - %s', msg))
        return
    end
    self:_trace(msg)
end

function Logger:Debug( ... )
    if self.level__ < Logger.Level_Debug then
        return
    end
    local msg = string.format( ... )
    if self._debug == nil then
        print(string.format('DEBUG - %s', msg))
        return
    end
    self:_debug(msg)
end

function Logger:Info( ... )
    if self.level__ < Logger.Level_Info then
        return
    end
    local msg = string.format( ... )
    if self._info == nil then
        print(string.format('INFO - %s', msg))
        return
    end
    self:_info(msg)
end

function Logger:Warning( ... )
    if self.level__ < Logger.Level_Warning then
        return
    end
    local msg = string.format( ... )
    if self._warning == nil then
        print(string.format('WARNING - %s', msg))
        return
    end
    self:_warning(msg)
end

function Logger:Error( ... )
    if self.level__ < Logger.Level_Error then
        return
    end
    local msg = string.format( ... )
    if self._error == nil then
        print(string.format('ERROR - %s', msg))
        return
    end
    self:_error(msg)
end

return Logger
