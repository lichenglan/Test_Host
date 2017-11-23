

## polyhome_b_host_android：B端平板Host

### 主干:
    
- BHost：平板程序的宿主，一切功能的最底层
- BHostDaemon：BHost 守护程序（已不再需要，所以没有维护了）
- BLauncher：插件核心层，用于宿主和插件间通信
- DroidPlugin：360的插件库
- HDownloadManager：对系统下载的封装，主要用于插件更新。
- JPush：极光推送相关支持库（已经被心跳包机制代替）
- PluginAppDemo：个人测试工程，用于检验插件功能。（可删除）
- PluginTest2：个人工程，用于做一些测试。（可删除）
- PluginCommunicateEngine：封装了插件与核心的通信
- DroidPluginManager：封装了插件的安装、检查等常用功能的工具库
- corelib：项目核心层支持库，包含常用工具和部分框架
- lib2：网络层库框架
- processdialog：MD风格对话框兼容库

### 分支：

- poly_900：争对900平板的低性能，阉割了物业、医疗等等功能性插件，只保留了最核心的功能。

