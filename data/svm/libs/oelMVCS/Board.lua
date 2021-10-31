--- 内部通讯主板类
-- @classmod oelMVCS.Board
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'

local Board = class()

--- 构造函数
function Board:_init()
    -- #private
    self.logger__ = nil
    self.config__ = nil
    self.modelCenter__ = nil
    self.viewCenter__ = nil
    self.controllerCenter__ = nil
    self.serviceCenter__ = nil
    -- #endprivate
end

function Board:setLogger(_value)
    self.logger__ = _value
end

function Board:setConfig(_value)
    self.config__ = _value
end

function Board:setModelCenter(_value)
    self.modelCenter__ = _value
end

function Board:setViewCenter(_value)
    self.viewCenter__ = _value
end

function Board:setControllerCenter(_value)
    self.controllerCenter__ = _value
end

function Board:setServiceCenter(_value)
    self.serviceCenter__ = _value
end

function Board:getLogger()
    return self.logger__
end

function Board:getConfig()
    return self.config__
end

function Board:getModelCenter()
    return self.modelCenter__
end

function Board:getViewCenter()
    return self.viewCenter__
end

function Board:getControllerCenter()
    return self.controllerCenter__
end

function Board:getServiceCenter()
    return self.serviceCenter__
end

return Board
