local assembly = require 'unicore.assembly'
local plugin = require 'unicore.plugin'
local UniConfig = require 'UniConfig'
local UniLogger = require 'UniLogger'
local Framework = require 'oelMVCS.Framework'
local mymvcs = require 'mymvcs'

local mvcsFramework = nil
local mvcsLogger = nil
local mvcsConfig = nil

function uniAwake()
    print('uniAwake')
    -- 加载所有程序集
    assembly.loadAll()
    -- 加载所有插件
    -- plugin.loadAll()

    -- 实例化MVCS框架
    -- #begin
    mvcsFramework = Framework()

    mvcsLogger = UniLogger()
    mvcsLogger:setLevel(6)
    mvcsFramework:setLogger(mvcsLogger)

    mvcsConfig = UniConfig()
    mvcsConfig:LoadAll()
    mvcsFramework:setConfig(mvcsConfig)
    -- #end

    -- 初始化MVCS框架
    mvcsFramework:Initialize()

    -- 注册部件
    mymvcs.Register(mvcsFramework, mvcsLogger, mvcsConfig)
end

function uniEnable()
    print('uniEnable')
    mvcsFramework:Setup()
end

function uniStart()
    print('uniStart')
    mymvcs.Boot(mvcsFramework, mvcsLogger, mvcsConfig)
end

function uniUpdate()
    mymvcs.Update()
end

function uniDisable()
    print('uniDisable')
    mvcsFramework:Dismantle()
end

function uniDestroy()
    print('uniDestroy')
    -- 注销部件
    mymvcs.Cancel(mvcsFramework, mvcsLogger, mvcsConfig)
    mvcsFramework:Release()
end


