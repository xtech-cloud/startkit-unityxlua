local stringx = require 'pl.stringx'
local json = require 'encoding.json'
local cs_coroutine = require 'unicore.cs_coroutine'
local unet = CS.UnityEngine.Networking

function asyncRequest(_url, _method, _params, _onReply, _onError, _options)
    if _options.coroutineRunner == nil then
        _onError('options.coroutineRunner is nil')
        return
    end

    cs_coroutine.start(_options.coroutineRunner, function()
        local request = unet.UnityWebRequest(_url, _method)
        local json_str = json:encode(_params, nil, {stringsAreUtf8=true,})
        print(json_str)
        local bytes = stringx.toBytes(json_str)
        local stream = CS.System.IO.MemoryStream()
        for i, v in ipairs(bytes) do
            stream:WriteByte(v)
        end
        stream:Flush()
        local postBytes = stream:ToArray()
        stream:Close()
        request.uploadHandler = unet.UploadHandlerRaw(postBytes)
        request.downloadHandler = unet.DownloadHandlerBuffer()
        request:SetRequestHeader("Content-Type", "application/json")
        coroutine.yield(request:SendWebRequest())
        if not CS.System.String.IsNullOrEmpty(request.error) then
            _onError(string.format('request %s has error: %s', _url, request.error))
        else
            local text = request.downloadHandler.text
            _onReply(text)
        end
    end)
end

return {
    AsyncRequest = asyncRequest,
}
