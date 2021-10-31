local io = CS.System.IO
local unity = CS.UnityEngine

local assemblyCache = {}

local function loadAll()
    local dir_path = io.Path.Combine(unity.Application.persistentDataPath, G_Vendor)
    dir_path = io.Path.Combine(dir_path, 'assembly')
    if not io.Directory.Exists(dir_path) then
        return
    end

    local files = io.Directory.GetFiles(dir_path)
    for i=0, files.Length-1 do
        local file = files[i]
        local dll_path = io.Path.Combine(dir_path, file)
        local assembly = CS.System.Reflection.Assembly.LoadFrom(dll_path)
        local name = io.Path.GetFileNameWithoutExtension(file)
        assemblyCache[name] = assembly
    end
end

local function find(_name)
    return assemblyCache[_name]
end

return {
    loadAll = loadAll,
    find = find,
}
