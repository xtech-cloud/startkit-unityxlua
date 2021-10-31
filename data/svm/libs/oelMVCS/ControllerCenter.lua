--- 控制中心类
-- @classmod oelMVCS.ControllerCenter
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'
local Error = require 'oelMVCS.Error'

local ControllerCenter = class()

function ControllerCenter:_init(_board)
    -- #private
    self.board__ = _board
    self.units__ = {}
    -- #endprivate
end

function ControllerCenter:Register(_uuid, _inner)
    assert(_uuid)
    assert(_inner)
    self.board__:getLogger():Info('register controller %s', _uuid)
    if null ~= self.units__[_uuid] then
        return Error.NewAccessErr('controller %s is exists', _uuid)
    end
    self.units__[_uuid] = _inner
    return Error.NewOK()
end

function ControllerCenter:Cancel(_uuid)
    self.board__:getLogger():Info('cancel controller %s', _uuid)
    if nil == self.units__[_uuid] then
        return Error.NewAccessErr('controller %s not found', _uuid)
    end
    self.units__[_uuid] = nil
    return Error.NewOK()
end

function ControllerCenter:FindUnit(_uuid)
    return self.units__[_uuid]
end

function ControllerCenter:PreSetup()
    self.board__:getLogger():Info('preSetup controllers')
    for k, v in pairs(self.units__) do
        v:PreSetup()
    end
end

function ControllerCenter:Setup()
    self.board__:getLogger():Info('setup controllers')
    for k, v in pairs(self.units__) do
        v:Setup()
    end
end

function ControllerCenter:PostSetup()
    self.board__:getLogger():Info('postSetup controllers')
    for k, v in pairs(self.units__) do
        v:PostSetup()
    end
end

function ControllerCenter:PreDismantle()
    self.board__:getLogger():Info('preDismantle controllers')
    for k, v in pairs(self.units__) do
        v:PreDismantle()
    end
end

function ControllerCenter:Dismantle()
    self.board__:getLogger():Info('dismantle controllers')
    for k, v in pairs(self.units__) do
        v:Dismantle()
    end
end

function ControllerCenter:PostDismantle()
    self.board__:getLogger():Info('postDismantle controllers')
    for k, v in pairs(self.units__) do
        v:PostDismantle()
    end
end

return ControllerCenter

