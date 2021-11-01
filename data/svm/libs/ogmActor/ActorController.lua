local class = require 'oelMVCS.class'
local Controller = require 'oelMVCS.Controller'

local ActorController = class(Controller)

function ActorController:_init()
    self:super()
    -- #private
    -- #endprivate
end

function ActorController:_preSetup()
    self:_getLogger():Info('preSetup ActorController')
end

function ActorController:_setup()
    self:_getLogger():Info('setup ActorController')
end

function ActorController:_postSetup()
    self:_getLogger():Info('postSetup ActorController')
end

function ActorController:_preDismantle()
    self:_getLogger():Info('preDismantle ActorController')
end

function ActorController:_dismantle()
    self:_getLogger():Info('dismantle ActorController')
end

function ActorController:_postDismantle()
    self:_getLogger():Info('postDismantle ActorController')
end

return ActorController
