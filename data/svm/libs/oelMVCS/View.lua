--- 视图类
-- @classmod oelMVCS.View
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'

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

function Inner:Handle(_action, _status, _data)
    local handler = self.unit__.handlers__[_action]
    if nil == handler then
        return Error.NewParamErr("handler %s not found", _action)
    end
    handler[_action](_status, _data)
    return Error.NewOK()
end



local View = class()
View.Inner = Inner

function View:_init()
    -- #private
    self.board__ = nil
    self.handlers__ = {}
    -- #endprivate
end

function View:_getLogger()
    return self.board__:getLogger()
end

function View:_getConfig()
    return self.board__:getConfig()
end

function View:_findModel(_uuid)
    local inner = self.board__:getModelCenter():FindUnit(_uuid)
    if inner == nil then
        return nil
    end
    return inner:getUnit()
end

function View:_findView(_uuid)
    local inner = self.board__:getViewCenter():FindUnit(_uuid)
    if inner == nil then
        return nil
    end
    return inner:getUnit()
end

function View:_findService(_uuid)
    local inner = self.board__:getServiceCenter():FindUnit(_uuid)
    if inner == nil then
        return nil
    end
    return inner:getUnit()
end

function View:_findFacade(_uuid)
    return self.board__:getViewCenter():FindFacade(_uuid)
end

function View:_preSetup()
end

function View:_setup()
end

function View:_postSetup()
end

function View:_preDismantle()
end

function View:_dismantle()
end

function View:_postDismantle()
end

function View:_addRouter(_action, _handler)
    self.handlers__[_action] = _handler
end

function View:_removeRouter(_action, _handler)
    self.handlers__[_action] = nil
end

function View:_addObserver(_uuid, _action, _handler)
    local inner = self.board__.getModelCenter():FindUnit(_uuid)
    if nil == inner then
        return
    end
    inner:AddObserver(_action, _handler)
end

function View:_removeRouter(_action, _handler)
    local inner = self.board__.getModelCenter():FindUnit(_uuid)
    if nil == inner then
        return
    end
    inner:RemoveObserver(_action, _handler)
end

return View
