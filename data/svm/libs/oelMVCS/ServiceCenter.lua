--- 服务中心类
-- @classmod oelMVCS.ServiceCenter
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'
local Error = require 'oelMVCS.Error'

local ServiceCenter = class()

function ServiceCenter:_init(_board)
    -- #private
    self.board__ = _board
    self.units__ = {}
    -- #endprivate
end

function ServiceCenter:Register(_uuid, _inner)
    assert(_uuid)
    assert(_inner)
    self.board__:getLogger():Info('register service %s', _uuid)
    if null ~= self.units__[_uuid] then
        return Error.NewAccessErr('service %s is exists', _uuid)
    end
    self.units__[_uuid] = _inner
    return Error.NewOK()
end

function ServiceCenter:Cancel(_uuid)
    self.board__:getLogger():Info('cancel service %s', _uuid)
    if nil == self.units__[_uuid] then
        return Error.NewAccessErr('service %s not found', _uuid)
    end
    self.units__[_uuid] = nil
    return Error.NewOK()
end

function ServiceCenter:FindUnit(_uuid)
    return self.units__[_uuid]
end

function ServiceCenter:PreSetup()
    self.board__:getLogger():Info('preSetup services')
    for k, v in pairs(self.units__) do
        v:PreSetup()
    end
end

function ServiceCenter:Setup()
    self.board__:getLogger():Info('setup services')
    for k, v in pairs(self.units__) do
        v:Setup()
    end
end

function ServiceCenter:PostSetup()
    self.board__:getLogger():Info('postSetup services')
    for k, v in pairs(self.units__) do
        v:PostSetup()
    end
end

function ServiceCenter:PreDismantle()
    self.board__:getLogger():Info('preDismantle services')
    for k, v in pairs(self.units__) do
        v:PreDismantle()
    end
end

function ServiceCenter:Dismantle()
    self.board__:getLogger():Info('dismantle services')
    for k, v in pairs(self.units__) do
        v:Dismantle()
    end
end

function ServiceCenter:PostDismantle()
    self.board__:getLogger():Info('postDismantle services')
    for k, v in pairs(self.units__) do
        v:PostDismantle()
    end
end

return ServiceCenter

