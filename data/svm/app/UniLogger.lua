local class = require 'oelMVCS.class'
local Logger = require 'oelMVCS.Logger'

local UniLogger = class(Logger)

function UniLogger:_trace(_msg)
    print(string.format('<color=#02cbac>TRACE</color>  - %s', _msg))
end

function UniLogger:_debug(_msg)
    print(string.format('<color=#346cfd>DEBUG</color>  - %s', _msg))
end

function UniLogger:_info(_msg)
    print(string.format('<color=#04fc04>INFO</color>  - %s', _msg))
end

function UniLogger:_warning(_msg)
    print(string.format('<color=#fce204>WARNING</color>  - %s', _msg))
end

function UniLogger:_error(_msg)
    print(string.format('<color=#fc0450>ERROR</color>  - %s', _msg))
end

return UniLogger
