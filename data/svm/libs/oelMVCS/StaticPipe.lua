--- 静态管线类
-- @classmod oelMVCS.StaticPipe
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'
local Model = require 'oelMVCS.Model'
local View = require 'oelMVCS.View'
local Controller = require 'oelMVCS.Controller'
local Service = require 'oelMVCS.Service'

local StaticPipe = class()

function StaticPipe:_init(_board)
    -- #private
    self.board__ = _board
    -- #endprivate
end

function StaticPipe:RegisterModel(_uuid, _model)
    assert(_uuid)
    assert(_model)
    local inner = Model.Inner(_model, self.board__)
    return self.board__:getModelCenter():Register(_uuid, inner)
end

function StaticPipe:CancelModel(_uuid)
    return self.board__:getModelCenter():Cancel(_uuid)
end

function StaticPipe:RegisterView(_uuid, _view)
    assert(_uuid)
    assert(_view)
    local inner = View.Inner(_view, self.board__)
    return self.board__:getViewCenter():Register(_uuid, inner)
end

function StaticPipe:CancelView(_uuid)
    return self.board__:getViewCenter():Cancel(_uuid)
end

function StaticPipe:RegisterController(_uuid, _controller)
    assert(_uuid)
    assert(_controller)
    local inner = Controller.Inner(_controller, self.board__)
    return self.board__:getControllerCenter():Register(_uuid, inner)
end

function StaticPipe:CancelController(_uuid)
    return self.board__:getControllerCenter():Cancel(_uuid)
end

function StaticPipe:RegisterService(_uuid, _service)
    assert(_uuid)
    assert(_service)
    local inner = Service.Inner(_service, self.board__)
    return self.board__:getServiceCenter():Register(_uuid, inner)
end

function StaticPipe:CancelService(_uuid)
    return self.board__:getServiceCenter():Cancel(_uuid)
end

function StaticPipe:RegisterFacade(_uuid, _facade)
    assert(_uuid)
    assert(_facade)
    return self.board__:getViewCenter():PushFacade(_uuid, _facade)
end

function StaticPipe:CancelFacade(_uuid)
    return self.board__:getViewCenter():PopFacade(_uuid)
end

return StaticPipe
