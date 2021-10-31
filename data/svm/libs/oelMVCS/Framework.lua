--- 框架类
-- @classmod oelMVCS.Framework
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'
local Board = require 'oelMVCS.Board'
local ModelCenter = require 'oelMVCS.ModelCenter'
local ViewCenter = require 'oelMVCS.ViewCenter'
local ControllerCenter = require 'oelMVCS.ControllerCenter'
local ServiceCenter = require 'oelMVCS.ServiceCenter'
local StaticPipe = require 'oelMVCS.StaticPipe'
local DynamicPipe = require 'oelMVCS.DynamicPipe'

local Framework = class()

--- 构造函数
function Framework:_init()
    -- #private
    self.board__ = Board()
    self.staticPipe__ = StaticPipe(self.board__)
    self.dynamicPipe__ = DynamicPipe(self.board__)
    -- #endprivate
end

function Framework:setConfig(_config)
    self.board__:setConfig(_config)
end

function Framework:setLogger(_logger)
    self.board__:setLogger(_logger)
end

function Framework:getDynamicPipe()
    return self.dynamicPipe__
end


function Framework:getStaticPipe()
    return self.staticPipe__
end

function Framework:getDynamicPipe()
    return self.dynamicPipe__
end

--- 框架初始化，完成各层中心的实例化
function Framework:Initialize()
    self.board__:getLogger():Info('initialize framework')
    self.board__:setModelCenter(ModelCenter(self.board__))
    self.board__:setViewCenter(ViewCenter(self.board__))
    self.board__:setControllerCenter(ControllerCenter(self.board__))
    self.board__:setServiceCenter(ServiceCenter(self.board__))
end

--- 框架安装，完成各层中心已注册组件的安装
--- 此过程将调用各派生组件的setup方法
function Framework:Setup()
    self.board__:getLogger():Info("setup framework")
    self.board__:getModelCenter():PreSetup()
    self.board__:getViewCenter():PreSetup()
    self.board__:getControllerCenter():PreSetup()
    self.board__:getServiceCenter():PreSetup()
    self.board__:getModelCenter():Setup()
    self.board__:getViewCenter():Setup()
    self.board__:getControllerCenter():Setup()
    self.board__:getServiceCenter():Setup()
    self.board__:getModelCenter():PostSetup()
    self.board__:getViewCenter():PostSetup()
    self.board__:getControllerCenter():PostSetup()
    self.board__:getServiceCenter():PostSetup()
end

--- 框架拆卸，完成各层中心已注册组件的拆卸
--- 此过程将调用各派生组件的dismantle方法
function Framework:Dismantle()
    self.board__:getLogger():Info("dismantle framework")
    self.board__:getServiceCenter():PreDismantle()
    self.board__:getControllerCenter():PreDismantle()
    self.board__:getViewCenter():PreDismantle()
    self.board__:getModelCenter():PreDismantle()
    self.board__:getServiceCenter():Dismantle()
    self.board__:getControllerCenter():Dismantle()
    self.board__:getViewCenter():Dismantle()
    self.board__:getModelCenter():Dismantle()
    self.board__:getServiceCenter():PostDismantle()
    self.board__:getControllerCenter():PostDismantle()
    self.board__:getViewCenter():PostDismantle()
    self.board__:getModelCenter():PostDismantle()
end

--- 框架销毁，完成各层中心的释放
function Framework:Release()
    self.board__:getLogger():Info("release framework")
    self.board__:setViewCenter(nil)
    self.board__:setServiceCenter(nil)
    self.board__:setModelCenter(nil)
    self.board__:setControllerCenter(nil)
end

return Framework
