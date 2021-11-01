local class = require 'oelMVCS.class'
local Error = require 'oelMVCS.Error'

local SimpleMock = class()

function SimpleMock.MockProcessor(_url, _method, _params, _onReply, _onError, _options)
    if not 'http://localhost/signin' == _url then
        _onError(Error.NewAccessErr('404 not found'))
        return
    end

    if _params.username == 'admin' and _params.password == '11223344' then
        _onReply('000000001')
    else
        _onError(Error.NewAccessErr('password is not matched'))
    end
end

return SimpleMock
