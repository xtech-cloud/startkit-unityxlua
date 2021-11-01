local class = require 'oelMVCS.class'
local Service = require 'oelMVCS.Service'
local Model = require 'oelMVCS.Model'
local Mock = require 'simple.SimpleMock'

local SimpleService = class(Service)

function SimpleService:_init()
    self:super()
    -- #private
    self.model__ = nil
    -- #endprivate
end

function SimpleService:_preSetup()
    self:_getLogger():Info('preSetup SimpleService')
    self.model__ = self:_findModel('SimpleModel')
    assert(self.model__)
    self.useMock = true
    self.MockProcessor = Mock.MockProcessor
end

function SimpleService:_setup()
    self:_getLogger():Info('setup SimpleService')
end

function SimpleService:_postSetup()
    self:_getLogger():Info('postSetup SimpleService')
end

function SimpleService:_preDismantle()
    self:_getLogger():Info('preDismantle SimpleService')
end

function SimpleService:_dismantle()
    self:_getLogger():Info('dismantle SimpleService')
end

function SimpleService:_postDismantle()
    self:_getLogger():Info('postDismantle SimpleService')
end

function SimpleService:Prepare()
    self:_getLogger():Info('prepare SimpleService')
end

function SimpleService:PostSignin(_username, _password)
    local parameter = {}
    parameter.username = _username
    parameter.password = _password
    local onReply = function(_reply)
        self:_getLogger():Debug(_reply)
        local reply = Model.Status.New(Model.Status, 0, '')
        local uid = _reply
        self.model__:UpdateSignin(reply, uid)
    end
    local onError = function(_err)
        local reply = Model.Status.New(Model.Status, _err:getCode(), _err:getMessage())
        self.model__:UpdateSignin(reply, '')
    end
    self:_post("http://localhost/signin", parameter, onReply, onError, nil)
end

return SimpleService
