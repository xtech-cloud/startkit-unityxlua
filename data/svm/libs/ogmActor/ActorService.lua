local class = require 'oelMVCS.class'
local Service = require 'oelMVCS.Service'
local Model = require 'oelMVCS.Model'
local http = require 'unicore.http'
local cs_coroutine = require 'unicore.cs_coroutine'
local unity = CS.UnityEngine

local ActorService = class(Service)

function ActorService:_init()
    self:super()
    -- #private
    self.config__ = {}
    self.running = false
    -- #endprivate
end

function ActorService:_preSetup()
    self:_getLogger():Info('preSetup ActorService')
    self.config__ = self:_getConfig():getField('ogmActor')
    self.asyncRequestImplement = http.AsyncRequest
end

function ActorService:_setup()
    self:_getLogger():Info('setup ActorService')
end

function ActorService:_postSetup()
    self:_getLogger():Info('postSetup ActorService')
end

function ActorService:_preDismantle()
    self:_getLogger():Info('preDismantle ActorService')
    self.running__ = false
end

function ActorService:_dismantle()
    self:_getLogger():Info('dismantle ActorService')
end

function ActorService:_postDismantle()
    self:_getLogger():Info('postDismantle ActorService')
end

function ActorService:Prepare()
    self:_getLogger():Info('prepare ActorService')
    local mode = self.config__.mode
    self:_getLogger():Debug('the mode is %s', mode)
    if mode == 'standard' then
        self:RunStandard()
    elseif mode == 'service' then
        self:RunService()
    end
end

function ActorService:RunStandard()
    self.running__ = true
    local method = self.config__.sync.method
    self:_getLogger():Debug('the method is %s', method)
    local sync = nil
    if method == 'push' then
        sync = self._push
    elseif method == 'pull' then
        sync = self._pull
    end

    local coroutineRunner = G_CoroutineRunner
    cs_coroutine.start(coroutineRunner, function()
        sync(self, coroutineRunner)
    end)
end

function ActorService:RunAndroid()
end

function ActorService:_push(_coroutineRunner)
    local uri = self.config__.address..'/actor/Sync/Push'
    -- 参数列表
    local parameter = {}
    parameter.domain = self.config__.domain
    local device = {}
    parameter.device = device
    device.serialNumber = unity.SystemInfo.deviceUniqueIdentifier
    device.name= unity.SystemInfo.deviceModel
    device.operatingSystem = CS.System.String.Format("{0}", unity.SystemInfo.operatingSystemFamily)
    device.systemVersion = unity.SystemInfo.operatingSystem
    device.shape = CS.System.String.Format("{0}", unity.SystemInfo.deviceType)
    device.battery = unity.SystemInfo.batteryLevel
    device.volume = -1
    device.brightness = -1
    device.storage = "unknown"
    device.storageBlocks = -1
    device.storageAvailable= -1
    device.network = "unknown"
    device.networkStrength= -1
    device.program = { }
    local upProperty = {
        ping="30ms",
        pong="50ms",
    }
    parameter.upProperty = upProperty
    local downProperty = self.config__.sync.pull.downProperty
    parameter.downProperty = downProperty

    local onReply = function(_reply)
        self:_getLogger():Debug(_reply)
        -- local reply = Model.Status.New(Model.Status, 0, '')
        --local uid = _reply
        --self.model__:UpdateSignin(reply, uid)
    end
    local onError = function(_err)
        self:_getLogger():Error(_err)
    end
    local options = {}
    options.coroutineRunner = _coroutineRunner
    -- 循环发送
    local interval = self.config__.sync.push.interval
    while(self.running__) do
        coroutine.yield(unity.WaitForSeconds(interval))
        self:_post(uri, parameter, onReply, onError, options)
    end
end

function ActorService:_pull(_coroutineRunner)
    local uri = self.config__.address..'/actor/Sync/Pull'
    -- 参数列表
    local parameter = {}
    parameter.domain = self.config__.domain
    local downProperty = self.config__.sync.pull.downProperty
    parameter.downProperty = downProperty

    local onReply = function(_reply)
        self:_getLogger():Debug(_reply)
        -- local reply = Model.Status.New(Model.Status, 0, '')
        --local uid = _reply
        --self.model__:UpdateSignin(reply, uid)
    end
    local onError = function(_err)
        self:_getLogger():Error(_err)
    end
    local options = {}
    options.coroutineRunner = _coroutineRunner
    -- 循环发送
    local interval = self.config__.sync.push.interval
    while(self.running__) do
        coroutine.yield(unity.WaitForSeconds(interval))
        self:_post(uri, parameter, onReply, onError, options)
    end
end

return ActorService
