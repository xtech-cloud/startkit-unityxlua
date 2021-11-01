local Error = require 'oelMVCS.Error'
local mvcsBundle = require 'bootloader.mvcsBundle'

local function register(_framework, _logger, _config)
    mvcsBundle.Register(_framework, _logger, _config)
end

local function update()
end

local function boot(_framework, _logger, _config)
    mvcsBundle.Prepare()
    -- 模拟登录按钮被点击时
    local simpleView = mvcsBundle.GetView('SimpleView')
    simpleView:OnSigninClicked()
end

local function cancel(_framework, _logger, _config)
    mvcsBundle.Cancel(_framework, _logger, _config)
end

return {
    Register = register,
    Boot = boot,
    Update = update,
    Cancel = cancel,
}
