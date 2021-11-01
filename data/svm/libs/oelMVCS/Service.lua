--- 服务类
-- @classmod oelMVCS.Service
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'
local Error = require 'oelMVCS.Error'

local Inner = class()

function Inner:_init(_unit, _board)
    assert(_unit)
    assert(_board)
    -- #private
    self.unit__ = _unit
    -- #endprivate
    self.unit__.board__ = _board
end

function Inner:getUnit()
    return self.unit__
end

function Inner:PreSetup()
    self.unit__:_preSetup()
end

function Inner:Setup()
    self.unit__:_setup()
end

function Inner:PostSetup()
    self.unit__:_postSetup()
end

function Inner:PreDismantle()
    self.unit__:_preDismantle()
end

function Inner:Dismantle()
    self.unit__:_dismantle()
end

function Inner:PostDismantle()
    self.unit__:_postDismantle()
end

local Options = class()
function Options:_init()
    self.header = {}
end

local Alias = class()
function Alias:_init()
    self.uri = ''
    self.method = ''
    self.useMock = false
    self.parameter = {}
end


local Service = class()
Service.Inner = Inner
Service.Options = Options

function Service:_init()
    self.useMock = false
    self.MockProcessor = nil
    self.asyncRequestImplement = nil
    -- #private
    self.board__ = nil
    self.aliasMap__ = {}
    -- #endprivate
end

function Service:_getLogger()
    return self.board__:getLogger()
end

function Service:_getConfig()
    return self.board__:getConfig()
end

function Service:_findModel(_uuid)
    local inner = self.board__:getModelCenter():FindUnit(_uuid)
    if inner == nil then
        return nil
    end
    return inner:getUnit()
end

function Service:_preSetup()
end

function Service:_setup()
end

function Service:_postSetup()
end

function Service:_preDismantle()
end

function Service:_dismantle()
end

function Service:_postDismantle()
end

function Service:_send(_url, _method, _useMock, _params, _onReply, _onError, _options)
    if _useMock then
        if nil == self.MockProcessor then
            _onError(Error.NewAccessErr("MockProcessor is nil"))
            return
        end

        self.MockProcessor(_url, _method, _params, _onReply, _onError, _options)
        return
    end
    self:_asyncRequest(_url, _method, _params, _onReply, _onError, _options)
end

function Service:_asyncRequest(_url, _method, _params, _onReply, _onError, _options)
    if nil == self.asyncRequestImplement then
        _onError(Error.NewAccessErr("asyncRequest Not implemented"))
        return
    end
    self.asyncRequestImplement(_url, _method, _params, _onReply, _onError, _options)
end

function Service:_post(_url, _params, _onReply, _onError, _options)
    self:_send(_url, "POST", self.useMock, _params, _onReply, _onError, _options)
end

function Service:_get(_url, _params, _onReply, _onError, _options)
    self:_send(_url, "GET", self.useMock, _params, _onReply, _onError, _options)
end

function Service:_delete(_url, _params, _onReply, _onError, _options)
    self:_send(_url, "DELETE", self.useMock, _params, _onReply, _onError, _options)
end

function Service:_put(_url, _params, _onReply, _onError, _options)
    self:_send(_url, "PUT", self.useMock, _params, _onReply, _onError, _options)
end

function Service:_createAlias(_alias, _url, _method, _useMock, _parameter)
    local alias = Alias()
    alias.uri = _url
    alias.method = _method
    alias.useMock = _useMock
    alias.parameter = _parameter
    self.aliasMap__[_alias] = alias
end

function Service:CallAlias(_alias, _parameterHandler, _onReply, _onError, _options)
    local alias = self.aliasMap__[_alias]
    if nil == alias then
        _onError(Error.NewAccessErr('alias %s not found', _alias))
        return
    end

    if nil ~= _parameterHandler then
        _parameterHandler(alias.parameter)
    end

    self:_send(alias.uri, alias.method, alias.useMock, alias.parameter, _onReply, _onError, _options)
end

return Service
