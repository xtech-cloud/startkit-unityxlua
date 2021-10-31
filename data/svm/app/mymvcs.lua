local Error = require 'oelMVCS.Error'
local SimpleModel = require 'simple.SimpleModel'
local SimpleView = require 'simple.SimpleView'
local SimpleController = require 'simple.SimpleController'
local SimpleService = require 'simple.SimpleService'
local SimpleMock = require 'simple.SimpleMock'

local simpleModel = SimpleModel()
local simpleView = SimpleView()
local simpleController = SimpleController()
local simpleService = SimpleService()

local function register(_framework, _logger)
    local err = _framework:getStaticPipe():RegisterModel('SimpleModel', simpleModel)
    if not Error.IsOK(err) then
        _logger:Error(err:getMessage())
    end
    local err = _framework:getStaticPipe():RegisterView('SimpleView', simpleView)
    if not Error.IsOK(err) then
        _logger:Error(err:getMessage())
    end
    local err = _framework:getStaticPipe():RegisterController('SimpleController', simpleController)
    if not Error.IsOK(err) then
        _logger:Error(err:getMessage())
    end

    simpleService.useMock = true
    simpleService.MockProcessor = SimpleMock.MockProcessor
    local err = _framework:getStaticPipe():RegisterService('SimpleService', simpleService)
    if not Error.IsOK(err) then
        _logger:Error(err:getMessage())
    end
end

local function update()
end

local function boot()
    -- 模拟登录按钮被点击时
    simpleView:OnSigninClicked()
end

local function cancel(_framework)
    _framework:getStaticPipe():CancelModel('SimpleService')
    _framework:getStaticPipe():CancelModel('SimpleController')
    _framework:getStaticPipe():CancelModel('SimpleView')
    _framework:getStaticPipe():CancelModel('SimpleModel')
end

return {
    Register = register,
    Boot = boot,
    Update = update,
    Cancel = cancel,
}
