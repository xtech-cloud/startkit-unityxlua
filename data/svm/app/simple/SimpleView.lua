local class = require 'oelMVCS.class'
local View = require 'oelMVCS.View'

local SimpleView = class(View)

function SimpleView:_init()
    -- #private
    self.service__ = nil
    -- #endprivate
end

function SimpleView:_preSetup()
    self:_getLogger():Info('preSetup SimpleView')
    self.service__ = self:_findService('SimpleService')
    assert(self.service__)
end

function SimpleView:_setup()
    self:_getLogger():Info('setup SimpleView')
end

function SimpleView:_postSetup()
    self:_getLogger():Info('postSetup SimpleView')
end

function SimpleView:_preDismantle()
    self:_getLogger():Info('preDismantle SimpleView')
end

function SimpleView:_dismantle()
    self:_getLogger():Info('dismantle SimpleView')
end

function SimpleView:_postDismantle()
    self:_getLogger():Info('postDismantle SimpleView')
end


function SimpleView:OnSigninClicked(_reply, _uuid)
    local config = self:_getConfig():getField('simple')
    local admin = config.username
    local password = config.password
    self.service__:PostSignin(admin, password)
end

function SimpleView:PrintError(_error)
    self:_getLogger():Error(_error)
end

function SimpleView:PrintUID(_status)
    self:_getLogger():Info(string.format('uid is %s', _status.uid))
end

return SimpleView
