local Error = require 'oelMVCS.Error'
local activeModels = {}
local activeViews = {}
local activeControllers = {}
local activeServices = {}

local function register(_framework, _logger, _config)
    local config = _config:getField('bootloader')
    for i,v in ipairs(config.bundle) do
        -- 注册数据
        local ModelClass = require(v.name..'.'..v.prefix..'Model')
        if nil ~= ModelClass then
            local model = ModelClass()
            local err = _framework:getStaticPipe():RegisterModel(v.prefix..'Model', model)
            if not Error.IsOK(err) then
                _logger:Error(err:getMessage())
            else
                activeModels[v.prefix..'Model'] = model
            end
        end
        -- 注册视图
        local ViewClass = require(v.name..'.'..v.prefix..'View')
        if nil ~= ModelClass then
            local view = ViewClass()
            err = _framework:getStaticPipe():RegisterView(v.prefix..'View', view)
            if not Error.IsOK(err) then
                _logger:Error(err:getMessage())
            else
                activeViews[v.prefix..'View'] = view
            end
        end
        -- 注册控制器
        local ControllerClass = require(v.name..'.'..v.prefix..'Controller')
        if nil ~= ControllerClass then
            local controller = ControllerClass()
            err = _framework:getStaticPipe():RegisterController(v.prefix..'Controller', controller)
            if not Error.IsOK(err) then
                _logger:Error(err:getMessage())
            else
                activeControllers[v.prefix..'Controller'] = controller
            end
        end
        -- 注册服务
        local ServiceClass = require(v.name..'.'..v.prefix..'Service')
        if nil ~= ServiceClass then
            local service = ServiceClass()
            err = _framework:getStaticPipe():RegisterService(v.prefix..'Service', service)
            if not Error.IsOK(err) then
                _logger:Error(err:getMessage())
            else
                activeServices[v.prefix..'Service'] = service
            end
        end
    end
end

local function prepare(_framework, _logger, _config)
    -- TODO 使用携程的方式顺序执行，并更新整体进度
    -- 执行具有Prepare方法的服务
    for k,v in pairs(activeServices) do
        if nil ~= v.Prepare then
            v:Prepare()
        end
    end
end

local function cancel(_framework, _logger, _config)
    for k,v in pairs(activeServices) do
        _framework:getStaticPipe():CancelService(k)
    end
    for k,v in pairs(activeControllers) do
        _framework:getStaticPipe():CancelController(k)
    end
    for k,v in pairs(activeViews) do
        _framework:getStaticPipe():CancelView(k)
    end
    for k,v in pairs(activeModels) do
        _framework:getStaticPipe():CancelModel(k)
    end
end

local function getModel(_uuid)
    return activeModels[_uuid]
end

local function getView(_uuid)
    return activeViews[_uuid]
end

local function getController(_uuid)
    return activeControllers[_uuid]
end

local function getServive(_uuid)
    return activeServices[_uuid]
end


return {
    Register = register,
    Prepare = prepare,
    GetModel = getModel,
    GetView = getView,
    GetController = getController,
    GetService = getService,
    Cancel = cancel,
}
