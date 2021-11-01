local class = require 'oelMVCS.class'
local View = require 'oelMVCS.View'

local ActorView = class(View)

function ActorView:_init()
    self:super()
    -- #private
    -- #endprivate
end

function ActorView:_preSetup()
    self:_getLogger():Info('preSetup ActorView')
end

function ActorView:_setup()
    self:_getLogger():Info('setup ActorView')
end

function ActorView:_postSetup()
    self:_getLogger():Info('postSetup ActorView')
end

function ActorView:_preDismantle()
    self:_getLogger():Info('preDismantle ActorView')
end

function ActorView:_dismantle()
    self:_getLogger():Info('dismantle ActorView')
end

function ActorView:_postDismantle()
    self:_getLogger():Info('postDismantle ActorView')
end

return ActorView
