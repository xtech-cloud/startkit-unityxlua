# 编译方法

- 使用Android Studio 3.5.x 打开XXXActor目录
- 运行菜单栏 Build -> Rebuild Project
- 在XXXActor/libActor/build/outputs/aar目录得到libActor.aar
- 将libActor.aar放到Unity的Plugin/Android目录



# 报错解决

- 运行时报java类找不到时，从gradle的模块下载目录中拷贝相应的jar文件到Unity的Plugin/Android目录

- Pico SDK 需要将Packages/PicoXR Plugin/Runtiom/Android/tobserviceclient.jar删除，替换为PicoActort/libActor/libs/tobserviceclib.aar

  

