--- 控制类
-- @classmod oelMVCS.Controller
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

local Controller = class()
Controller.Inner = Inner

function Controller:_init()
    -- #private
    self.board__ = nil
    -- #endprivate
end

function Controller:_getLogger()
    return self.board__:getLogger()
end

function Controller:_getConfig()
    return self.board__:getConfig()
end

function Controller:_findModel(_uuid)
    local inner = self.board__:getModelCenter():FindUnit(_uuid)
    if inner == nil then
        return nil
    end
    return inner:getUnit()
end

function Controller:_findView(_uuid)
    local inner = self.board__:getViewCenter():FindUnit(_uuid)
    if inner == nil then
        return nil
    end
    return inner:getUnit()
end

function Controller:_findController(_uuid)
    local inner = self.board__:getControllerCenter():FindUnit(_uuid)
    if inner == nil then
        return nil
    end
    return inner:getUnit()
end

function Controller:_preSetup()
end

function Controller:_setup()
end

function Controller:_postSetup()
end

function Controller:_preDismantle()
end

function Controller:_dismantle()
end

function Controller:_postDismantle()
end

return Controller
