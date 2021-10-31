local json = require 'encoding/json'
local class = require 'oelMVCS.class'
local Config = require 'oelMVCS.Config'
local io = CS.System.IO
local unity = CS.UnityEngine

local UniConfig = class(Config)

function UniConfig:LoadAll()
    local dir_path = io.Path.Combine(unity.Application.persistentDataPath, G_Vendor)
    dir_path = io.Path.Combine(dir_path, 'config')
    if not io.Directory.Exists(dir_path) then
        return
    end

    local files = io.Directory.GetFiles(dir_path)
    for i=0, files.Length-1 do
        local file = files[i]
        local config_file = io.Path.Combine(dir_path, file)
        local json_str = io.File.ReadAllText(config_file)
        local filename = io.Path.GetFileNameWithoutExtension(file)
        self.fields_[filename] = json:decode(json_str)
    end
end

return UniConfig
