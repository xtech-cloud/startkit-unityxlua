local class = require 'oelMVCS.class'
local Controller = require 'oelMVCS.Controller'

local SimpleController = class(Controller)

function SimpleController:_init()
    -- #private
    self.view__ = nil
    -- #endprivate
end

function SimpleController:_preSetup()
    self:_getLogger():Info('preSetup SimpleController')
    self.view__ = self:_findView('SimpleView')
    assert(self.view__)
end

function SimpleController:_setup()
    self:_getLogger():Info('setup SimpleController')
end

function SimpleController:_postSetup()
    self:_getLogger():Info('postSetup SimpleController')
end

function SimpleController:_preDismantle()
    self:_getLogger():Info('preDismantle SimpleController')
end

function SimpleController:_dismantle()
    self:_getLogger():Info('dismantle SimpleController')
end

function SimpleController:_postDismantle()
    self:_getLogger():Info('postDismantle SimpleController')
end

function SimpleController:Signin(_reply, _status)
    if 0 ~= _reply:getCode() then
        self.view__:PrintError(_reply:getMessage())
        return
    end
    self.view__:PrintUID(_status)
end

return SimpleController
