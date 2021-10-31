--- 动态管线类
-- @classmod oelMVCS.DynamicPipe
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'
local Model = require 'oelMVCS.Model'

local DynamicPipe = class()

function DynamicPipe:_init(_board)
    -- #private
    self.board__ = _board
    -- #endprivate
end

function DynamicPipe:PushModel(_uuid, _model)
    local inner = Model.Inner(_model, self.board__)
    local err = self.board__:getModelCenter():Register(_uuid, inner)
    if not Error.IsOK(err) then
        return err
    end
    inner:Setup()
    return Error.OK
end

function DynamicPipe:PopModel(_uuid)
    local inner = self.board__:getModelCenter():FindUnit(_uuid)
    if nil == inner then
        return Error.NewAccessErr("model %s not found", _uuid)
    end
    inner:Dismantle();
    return self.board__:getModelCenter():Cancel(_uuid)
end

function DynamicPipe:PushView(_uuid, _view)
    local inner = View.Inner(_view, self.board__)
    local err = self.board__:getViewCenter():Register(_uuid, inner)
    if not Error.IsOK(err) then
        return err
    end
    inner:Setup()
    return Error.OK
end

function DynamicPipe:PopView(_uuid)
    local inner = self.board__:getViewCenter():FindUnit(_uuid)
    if nil == inner then
        return Error.NewAccessErr("view %s not found", _uuid)
    end
    inner:Dismantle();
    return self.board__:getViewCenter():Cancel(_uuid)
end

function DynamicPipe:PushController(_uuid, _controller)
    local inner = Controller.Inner(_controller, self.board__)
    local err = self.board__:getControllerCenter():Register(_uuid, inner)
    if not Error.IsOK(err) then
        return err
    end
    inner:Setup()
    return Error.OK
end

function DynamicPipe:PopController(_uuid)
    local inner = self.board__:getControllerCenter():FindUnit(_uuid)
    if nil == inner then
        return Error.NewAccessErr("controller %s not found", _uuid)
    end
    inner:Dismantle();
    return self.board__:getControllerCenter():Cancel(_uuid)
end

function DynamicPipe:PushService(_uuid, _service)
    local inner = Service.Inner(_service, self.board__)
    local err = self.board__:getServiceCenter():Register(_uuid, inner)
    if not Error.IsOK(err) then
        return err
    end
    inner:Setup()
    return Error.OK
end

function DynamicPipe:PopService(_uuid)
    local inner = self.board__:getServiceCenter():FindUnit(_uuid)
    if nil == inner then
        return Error.NewAccessErr("service %s not found", _uuid)
    end
    inner:Dismantle();
    return self.board__:getServiceCenter():Cancel(_uuid)
end

function DynamicPipe:PushFacade(_uuid, _facade)
    return self.board__:getViewCenter():PushFacade(_uuid, _facade)
end

function DynamicPipe:PopService(_uuid)
    return self.board__:getViewCenter():PopFacade(_uuid, _facade)
end


return DynamicPipe
