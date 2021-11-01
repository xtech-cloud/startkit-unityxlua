local class = require 'oelMVCS.class'
local Error = require 'oelMVCS.Error'
local Model = require 'oelMVCS.Model'

local ActorStatus = class(Model.Status)
ActorStatus.NAME = 'ActorStatus'

function ActorStatus:_init()
    self:super()
end


local ActorModel = class(Model)

function ActorModel:_init()
    self:super()
    -- #private
    -- #endprivate
end

function ActorModel:_preSetup()
    self:_getLogger():Info('preSetup ActorModel')
    local err = Error.OK
    self.status_ = self:_spawnStatus(ActorStatus, ActorStatus.NAME, err)
end

function ActorModel:_setup()
    self:_getLogger():Info('setup ActorModel')
end

function ActorModel:_postSetup()
    self:_getLogger():Info('postSetup ActorModel')
end

function ActorModel:_preDismantle()
    self:_getLogger():Info('preDismantle ActorModel')
end

function ActorModel:_dismantle()
    self:_getLogger():Info('dismantle ActorModel')
end

function ActorModel:_postDismantle()
    self:_getLogger():Info('postDismantle ActorModel')
    local err = Error.OK
    self:_killStatus(ActorStatus.NAME)
end

return ActorModel
