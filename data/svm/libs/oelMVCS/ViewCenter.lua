--- 视图中心类
-- @classmod oelMVCS.ViewCenter
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'
local Error = require 'oelMVCS.Error'

local ViewCenter = class()

function ViewCenter:_init(_board)
    -- #private
    self.board__ = _board
    self.units__ = {}
    self.facades__ = {}
    -- #endprivate
end

function ViewCenter:Register(_uuid, _inner)
    assert(_uuid)
    assert(_inner)
    self.board__:getLogger():Info('register view %s', _uuid)
    if null ~= self.units__[_uuid] then
        return Error.NewAccessErr('view %s is exists', _uuid)
    end
    self.units__[_uuid] = _inner
    return Error.NewOK()
end

function ViewCenter:Cancel(_uuid)
    self.board__:getLogger():Info('cancel view %s', _uuid)
    if nil == self.units__[_uuid] then
        return Error.NewAccessErr('view %s not found', _uuid)
    end
    self.units__[_uuid] = nil
    return Error.NewOK()
end

function ViewCenter:FindUnit(_uuid)
    return self.units__[_uuid]
end

function ViewCenter:PreSetup()
    self.board__:getLogger():Info('preSetup views')
    for k, v in pairs(self.units__) do
        v:PreSetup()
    end
end

function ViewCenter:Setup()
    self.board__:getLogger():Info('setup views')
    for k, v in pairs(self.units__) do
        v:Setup()
    end
end

function ViewCenter:PostSetup()
    self.board__:getLogger():Info('postSetup views')
    for k, v in pairs(self.units__) do
        v:PostSetup()
    end
end

function ViewCenter:PreDismantle()
    self.board__:getLogger():Info('preDismantle views')
    for k, v in pairs(self.units__) do
        v:PreDismantle()
    end
end

function ViewCenter:Dismantle()
    self.board__:getLogger():Info('dismantle views')
    for k, v in pairs(self.units__) do
        v:Dismantle()
    end
end

function ViewCenter:PostDismantle()
    self.board__:getLogger():Info('postDismantle views')
    for k, v in pairs(self.units__) do
        v:PostDismantle()
    end
end

function ViewCenter:HandleAction(_action, _status, _obj)
    for k, v in pairs(self.units__) do
        v:Handle(_action, _status, _obj)
    end
end

function ViewCenter:PushFacade(_uuid, _facade)
    self.board__:getLogger():Info("push facade %s", _uuid)
    local facade = self.facades__[_uuid]
    if nil ~= facade then
        return Error.NewAccessErr("facade %s exists", _uuid)
    end
    self.facades__[_uuid] = _facade
    return Error.NewOK()
end

function ViewCenter:PopFacade(_uuid, _facade)
    self.board__:getLogger():Info("pop facade %s", _uuid)
    local facade = self.facades__[_uuid]
    if nil == facade then
        return Error.NewAccessErr("facade %s not found", _uuid)
    end
    self.facades__[_uuid] = nil
    return Error.NewOK()
end

function ViewCenter:FindFacade(_uuid)
    return self.facades__[_uuid]
end




return ViewCenter

