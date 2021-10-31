--- 配置类
-- @classmod oelMVCS.Config
-- 保护成员以_结尾
-- 私有成员以__结尾
-- 保护方法以_开始
-- 私有方法以__开始
local class = require 'oelMVCS.class'

local Config = class()

function Config:_init()
    -- #protected
    self.fields_ = {}
    -- #endprotected
end

function Config:Merge(_content)
end

function Config:Has(_field)
    return nil ~= self.fields_[_field]
end

function Config:getField(_field)
    return self.fields_[_field]
end

return Config
