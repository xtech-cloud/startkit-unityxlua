local class = require 'oelMVCS.class'
local Error = require 'oelMVCS.Error'
local Model = require 'oelMVCS.Model'

local SimpleStatus = class(Model.Status)
SimpleStatus.NAME = 'SimpleStatus'

function SimpleStatus:_init()
    self:super()
    self.uid = ''
end


local SimpleModel = class(Model)

function SimpleModel:_init()
    -- #private
    self.controller__ = nil
    -- #endprivate
end

function SimpleModel:_preSetup()
    self:_getLogger():Info('preSetup SimpleModel')
    local err = Error.OK
    self.status_ = self:_spawnStatus(SimpleStatus, SimpleStatus.NAME, err)
    self.controller__ = self:_findController('SimpleController')
    assert(self.controller__)
end

function SimpleModel:_setup()
    self:_getLogger():Info('setup SimpleModel')
end

function SimpleModel:_postSetup()
    self:_getLogger():Info('postSetup SimpleModel')
end

function SimpleModel:_preDismantle()
    self:_getLogger():Info('preDismantle SimpleModel')
end

function SimpleModel:_dismantle()
    self:_getLogger():Info('dismantle SimpleModel')
end

function SimpleModel:_postDismantle()
    self:_getLogger():Info('postDismantle SimpleModel')
    local err = Error.OK
    self:_killStatus(SimpleStatus.NAME)
end

function SimpleModel:UpdateSignin(_reply, _uid)
    self.status_.uid = _uid
    self.controller__:Signin(_reply, self.status_)
end

return SimpleModel
