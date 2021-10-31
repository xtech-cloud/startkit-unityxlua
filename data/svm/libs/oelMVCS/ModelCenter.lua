--- 数据中心类
-- @classmod oelMVCS.ModelCenter
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'
local Error = require 'oelMVCS.Error'

local ModelCenter = class()

function ModelCenter:_init(_board)
    -- #private
    self.board__ = _board
    self.units__ = {}
    self.status__ = {}
    -- #endprivate
end

function ModelCenter:Register(_uuid, _inner)
    assert(_uuid)
    assert(_inner)
    self.board__:getLogger():Info('register model %s', _uuid)
    if nil ~= self.units__[_uuid] then
        return Error.NewAccessErr('model %s is exists', _uuid)
    end
    self.units__[_uuid] = _inner
    return Error.NewOK()
end

function ModelCenter:Cancel(_uuid)
    self.board__:getLogger():Info('cancel model %s', _uuid)
    if nil == self.units__[_uuid] then
        return Error.NewAccessErr('model %s not found', _uuid)
    end
    self.units__[_uuid] = nil
    return Error.OK
end

function ModelCenter:FindUnit(_uuid)
    return self.units__[_uuid]
end

function ModelCenter:PreSetup()
    self.board__:getLogger():Info('preSetup models')
    for k, v in pairs(self.units__) do
        v:PreSetup()
    end
end

function ModelCenter:Setup()
    self.board__:getLogger():Info('setup models')
    for k, v in pairs(self.units__) do
        v:Setup()
    end
end

function ModelCenter:PostSetup()
    self.board__:getLogger():Info('postSetup models')
    for k, v in pairs(self.units__) do
        v:PostSetup()
    end
end

function ModelCenter:PreDismantle()
    self.board__:getLogger():Info('preDismantle models')
    for k, v in pairs(self.units__) do
        v:PreDismantle()
    end
end

function ModelCenter:Dismantle()
    self.board__:getLogger():Info('dismantle models')
    for k, v in pairs(self.units__) do
        v:Dismantle()
    end
end

function ModelCenter:PostDismantle()
    self.board__:getLogger():Info('postDismantle models')
    for k, v in pairs(self.units__) do
        v:PostDismantle()
    end
end

function ModelCenter:PushStatus(_uuid, _status)
    self.board__:getLogger():Info('push status %s', _uuid)
    if nil ~= self.status__[_uuid] then
        return Error.NewAccessErr('status %s exists', _uuid)
    end
    self.status__[_uuid] = _status
    return Error.NewOK()
end

function ModelCenter:PopStatus(_uuid)
    self.board__:getLogger():Info('pop status %s', _uuid)
    if nil == self.status__[_uuid] then
        return Error.NewAccessErr('status %s not found', _uuid)
    end
    self.status__[_uuid] = nil
    return Error.NewOK()
end

function ModelCenter:FindStatus(_uuid)
    return self.status__[_uuid]
end

function ModelCenter:Broadcast(_action, _status, _data)
    self.board__:getViewCenter():HandleAction(_action, _status, _data)
end

return ModelCenter

