# 配置
```yaml
# 模式，可选值为[standard,service]
# standard在程序内进行通信
# servie在单独的服务进程内通信
mode: standard
# 远端地址
address: http://localhost
# 域的UUID
domain: c37dc1dfb767e7a4e67f4617c8607e26
# 同步信息
sync:
  # 推送数据
  push:
    # 间隔时间（秒）
    interval: 1
```
