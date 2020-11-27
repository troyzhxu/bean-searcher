---
description: IOT 物联网 IOTCP 协议 SDK 集成
---

# IDE 集成

因为 MDK 是单片机嵌入式开发高频使用用的 IDE，所以本文只介绍 SDK 与 MDK 的集成，与其他 IDE 的集成请参考各自 IDE 的官方文档。

## 与 MDK 集成

1. [下载 SDK](/v2/#sdk-下载)

2. 将 SDK 解压至适当的目录，如图：

![](/sdk_files.png)

3. 用 MDK 打开工程项目，右键工程模块，选择【Manage Project Items...】项

![](/mdk_01.png)

4. 在打开的【Manage Project Items】对话框中，新建一个 IOTCP 组

![](/mdk_02.png)

5. 单击选中 IOTCP 组，点击右侧的【Add Files...】按钮添加之前解压后的 SDK 文件

![](/mdk_03.png)

6. 点击【Close】和【OK】关闭对话框

![](/mdk_04.png)

7. 添加 SDK 头文件所在路径

![](/mdk_05.png)

8. 之后便可以在工程中引入 IOTCP 的头文件，若编译无误，则说明 SDK 已经在 MDK 中集成好了：

```C
#include "iotcp.h"
```

![](/mdk_06.png)

<br/>
