---
description: IOT 物联网 IOTCP 协议 SDK 集成
---

# 升级/重启

IOTCP 定义了升级与重启相关协议，它允许用户在物联网后台点击『升级』、『重启』按钮或调用开放接口来对设备下发升级或重启指令。

## 升级事件监听

设备需要使用 SDK 提供的以下接口来监听升级事件：

```C
/**
 * 当服务器下发固件升级指令时，SDK 会调用 on_upgrade 函数
 **/
void IOTCP_UpgradeEventListen(
    // 形参：升级事件（函数执行完将自动销毁）
    // 返回值  IOTCP_UPGRADE_OK: 表示 开始下载升级文件
    //        IOTCP_UPGRADE_BUSI: 表示 正在下载其他升级文件
    //        IOTCP_UPGRADE_NOSPACE: 表示 空间不足
    //        IOTCP_UPGRADE_MISMATCH: 表示 硬件版本不匹配
    uint8_t (*on_upgrade)(UpgradeEvent*)
);
```

显然，在调用该函数前，用户需要在自己的工程内定义一个升级监听回调函数：

```C
/**
 * 当服务器下发固件升级指令时，SDK 会调用 on_upgrade 函数
 * @param upgrade_event 升级事件
 * @return IOTCP_UPGRADE_OK: 表示 开始下载升级文件
 *         IOTCP_UPGRADE_BUSI: 表示 正在下载其他升级文件
 *         IOTCP_UPGRADE_NOSPACE: 表示 空间不足
 *         IOTCP_UPGRADE_MISMATCH: 表示 硬件版本不匹配
 **/
uint8_t on_upgrade(UpgradeEvent* upgrade_event) {
    // TODO: 校验升级条件（空间是否充足、硬件版本是否匹配等），然后开始升级过程

    return IOTCP_UPGRADE_OK;
}
```

其中`UpgradeEvent`同样是在 IOTCP.h 中定义的结构体：

```C
// 升级事件
typedef struct {
    /**
     * IOTCP_PartType_NetModule : 升级模块
     * IOTCP_PartType_MainBoard : 升级主板
     **/
    uint8_t type;             // 部件类型（0：模块，1、主板，2：其他用户自定义）
    DeviceVer *target;        // 匹配的硬件版本 与 本次升级的 软件版本
    uint32_t total_bytes;     // 待下载的软件大小（字节数）
    uint16_t url_length;      // 软件下载地址长度
    char *download_url;       // 软件下载地址
} UpgradeEvent;
```

## 升级状态上报

当升级状态有变化时，用户应使用本接口通知 SDK：

```C
/**
 * 当升级状态有变化时，用户应该调用该接口通知 SDK，由 SDK 再上报服务器
 * @param status 当前的升级状态
 * @param device_id 设备ID（非组网模式下，始终传 NULL）
 **/
void IOTCP_UpgradeStatusChange(UpgradeStatus *status, DeviceID *device_id);
```

其中`UpgradeStatus`是在 IOTCP.h 中定义的结构体：

```C
// 升级状态
typedef struct {
    /**
     * IOTCP_PartType_NetModule : 升级模块
     * IOTCP_PartType_MainBoard : 升级主板
     **/
    uint8_t type;             // 部件类型（0：模块，1、主板，2：其他）
    DeviceVer *target;        // 升级的版本
    /**
     * IOTCP_UPGRADE_STATUS_FAILED  : 升级失败
     * IOTCP_UPGRADE_STATUS_SUCCESS : 升级成功
     * IOTCP_UPGRADE_STATUS_CHECKED : 下载完成并校验成功，开始升级
     * IOTCP_UPGRADE_STATUS_INVALID : 下载完成但校验失败，已停止升级
     **/
    uint8_t status;           // 状态
} UpgradeStatus;
```

## 重启事件监听

设备需要使用 SDK 提供的以下接口来监听重启事件：

```C
/**
 * 当服务器下发重启指令时，SDK 会调用 on_reboot 函数
 **/
void IOTCP_RebootEventListen(
    // 重启回调函数，形参为：需重启的设备类型、子设备ID（非组网模式下始终传 NULL）
    void (*on_reboot)(uint8_t, DeviceID*)
);
```

显然，在调用该函数前，用户需要在自己的工程内定义一个重启监听回调函数：

```C
/**
 * 重启监听回调函数
 * @param type 部件类型（0：模块，1、主板，2：其他用户自定义）
 *      IOTCP_PartType_NetModule：模块
 *      IOTCP_PartType_MainBoard：主板
 * @param device_id: 设备ID（非组网模式下，始终传 NULL）
 **/
void on_reboot(uint8_t type, DeviceID *device_id) {
    if (type == IOTCP_PartType_NetModule) {
        // TODO: 重启模块

    } else
    if (type == IOTCP_PartType_MainBoard) {
        // TODO: 重启主板

    } else {
        // TODO: 其它用户自定义的重启

    }
}
```

<br/>
