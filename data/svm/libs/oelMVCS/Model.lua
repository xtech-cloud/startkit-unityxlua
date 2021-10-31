--- 数据类
-- @classmod oelMVCS.Model
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'
local Error = require 'oelMVCS.Error'

local Inner = class()

function Inner:_init(_unit, _board)
    assert(_unit)
    assert(_board)
    -- #private
    self.unit__ = _unit
    -- #endprivate
    self.unit__.board__ = _board
end

function Inner:getUnit()
    return self.unit__
end

function Inner:PreSetup()
    self.unit__:_preSetup()
end

function Inner:Setup()
    self.unit__:_setup()
end

function Inner:PostSetup()
    self.unit__:_postSetup()
end

function Inner:PreDismantle()
    self.unit__:_preDismantle()
end

function Inner:Dismantle()
    self.unit__:_dismantle()
end

function Inner:PostDismantle()
    self.unit__:_postDismantle()
end

function Inner:AddObserver(_action, _handler)
    self.unit__:_addObserver(_action, _handler)
end

function Inner:RemoveObserver(_action, _handler)
    self.unit__:_removeObserver(_action, _handler)
end

local Factory = class()

Factory.New = function(_statusClass, _board, _uuid)
    local status = _statusClass()
    status.board__ = _board
    status.uuid__ = _uuid
    return status
end

local Status = class()
Status.Factory = Factory

function Status:_init()
    -- #protectd
    self.code_ = 0
    self.message_ = ""
    -- #endprotectd
    
    -- #private
    self.uuid__ = ""
    self.board__ = nil
    -- #endprivate
end

function Status:getUuid()
    return self.uuid__
end


function Status:getCode()
    return self.code_
end

function Status:getMessage()
    return self.message_
end

function Status:Access(_uuid)
    return self.board__:getModelCenter():FindStatus(_uuid)
end

function Status.New(_statusClass, _code, _message)
    local status = _statusClass()
    status.code_ = _code
    status.message_ = _message
    return status
end


local Model = class()
Model.Inner = Inner
Model.Status = Status

function Model:_init()
    -- #private
    self.board__ = nil
    self.observers__ = {}
    -- #endprivate

    -- #protected
    self.status_ = nil
    self.property_ = {}
    self.isAllowSetproperty_ = true
    -- #endprotected
end

function Model:_getLogger()
    return self.board__:getLogger()
end

function Model:_getConfig()
    return self.board__:getConfig()
end

function Model:_findModel(_uuid)
    local inner = self.board__:getModelCenter():FindUnit(_uuid)
    if inner == nil then
        return nil
    end
    return inner:getUnit()
end

function Model:_findController(_uuid)
    local inner = self.board__:getControllerCenter():FindUnit(_uuid)
    if inner == nil then
        return nil
    end
    return inner:getUnit()
end

function Model:_preSetup()
end

function Model:_setup()
end

function Model:_postSetup()
end

function Model:_preDismantle()
end

function Model:_dismantle()
end

function Model:_postDismantle()
end

function Model:_spawnStatus(_statusClass, _uuid, _outErr)
    assert(_statusClass)
    local factory = Model.Status.Factory()
    local status = factory.New(_statusClass, self.board_, _uuid)
    _outErr = self.board__:getModelCenter():PushStatus(_uuid, status)
    if not Error.IsOK(_outErr) then
        return nil
    end
    return status
end

function Model:_killStatus(_uuid, _outErr)
    _outErr = self.board__:getModelCenter():PopStatus(_uuid)
end

function Model:_addObserver(_action, _handler)
    if nil == _handler then
        return
    end

    if nil == self.observers__[_actioon] then
        self.observers__[_action] = {}
    end
    table.insert(self.observers__[_action], _handler)
end

function Model:_removeObserver(_action, _handler)
    if nil == _handler then
        return
    end

    if nil == self.observers__[_actioon] then
        return
    end

    local pos = 0
    for i,v in ipairs(self.observers__[_action]) do
        if v == _handler then
            pos = i
            break
        end
    end

    if pos == 0 then
        return
    end

    table.remove(self.observers__[_action], pos)
end



function Model:Boardcast(_action, _data)
    self.board__:getModelCenter():Boardcast(_action, _data)
end


function Model:Bubble(_action, _data)
    local handlers = self.observers__[_action]
    if nil == handlers then
        return
    end

    for i,handler in ipairs(handlers) do
        handler(self.status_, _data)
    end
end

function Model:SetProperty(_key, _value)
    if not self.isAllowSetproperty_ then
        self:getLogger():Error("Not allowed to set a property, the isAllowSetProperty_ is false")
        return
    end
    self.property_[_key] = _value
end

function Model:GetProperty(_key)
    return self.property_[_key]
end

return Model
