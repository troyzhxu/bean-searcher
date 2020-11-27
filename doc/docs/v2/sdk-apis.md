---
description: IOT 物联网 IOTCP 协议 SDK 集成
---

# 常用 API

当 SDK 初始化无误后（`IOTCP_Init`的返回值为`IOTCP_ERRCODE_OK`），接下来便可以调用本文所列的 API 进行交互了。

## TCP 数据包委托

在 [SDK 初始化 > TCP 操作接口](/v2/initialization.html#tcp-操作接口) 章节我们已经知道 SDK 是如何控制外部的通讯模组进行连接、断开以及发送 TCP 数据包，那么当有数据从服务器端下发，SDK 将如何接收呢？这便要用到在 IOTCP.h 中声明的这个函数了：

```C
/**
 * 每当 TCP 服务器有下发数据时，调用该方法传给 SDK 进行解析处理（拆包组包、解密等）
 **/
void IOTCP_TcpReceive(DataPacket *packet);
```

当用户在当前建立的 TCP 连接上接收到通讯模组传来的服务器下发的数据时，便需调用该函数将数据委托给 SDK 进行处理即可。

## 连接 IOTCP 服务

完做好 [SDK 初始化](/v2/initialization.html) 以及 [TCP 数据包委托](/v2/sdk-apis.html#tcp-数据包委托) 后，接下来便可以真正的连接 IOTCP 服务器：

```C
/**
 * 连接服务器，完成负载均衡服务选择与握手流程
 * 在检测到网络正常时调用
 * @param identity [必填] IMEI 与 ICCID 信息
 * @param on_connected [可空] 连接已建立回调，回调方法的参数为心跳间隔（单位：秒）
 * @param get_heartbeat_data [可空] 心跳数据装载函数，改方法被 SDK 周期性调用
 **/
void IOTCP_Connect(
    // [必填] IMEI 与 ICCID 信息
    ImeiIccid *identity,

    // [可空] 连接已建立回调，回调方法的参数为心跳间隔（单位：秒）
    void (*on_connected)(uint16_t),

    // [可空] 心跳数据装载函数，改方法被 SDK 周期性调用
    void (*get_heartbeat_data)(void on_callback(DataPacket *))
);
```

在连接之前，用户首先需要获取模组的 IMEI 与 ICCID 信息，即`IOTCP_Connect`函数的第一个参数，它是在 IOTCP.h 中定义的结构体，如下：

```C
// IMEI 与 ICCID 信息
typedef struct {
    uint8_t imei_len;         // IMEI 的长度
    uint8_t *imei;            // IMEI 内容指针
    uint8_t iccid_len;        // ICCID 的长度
    uint8_t *iccid;           // ICCID 内容指针
} ImeiIccid;
```

### 连接建立通知

当用户调用`IOTCP_Connect`函数时，SDK 将自动执行负载均衡服务选择、会话秘钥协商、心跳间隔协商等 IOTCP 握手流程。当连接建立后（即握手完成），如果用户传入了第二个参数：`on_connected`，SDK 则会回调它通知用户连接已经建立，当然用户需要先在自己的工程内定义这个函数：

```C
/**
 * IOTCP 连接建立回调函数
 * @param interval 心跳间隔（单位：秒），0 表示无需要心跳
 **/
void on_connected(uint16_t interval) {
    // TODO: 代码执行到这，表示连接已经建立，用户可以在这写自己的处理逻辑，比如上报一些信息

}
```

### 心跳数据委托

另外，当 SDK 与服务器协商的 心跳间隔 不为 0 时，SDK 会按照协商的时间间隔自动发起心跳，默认的心跳 SDK 只会携带设备当前的 [网络状态信息](/v2/initialization.html#网络状态)，如果用户还需要在设备的心跳中携带其它数据（比如其它的状态信息）一起上报给服务器（需要与后端约定好），则只需要实现`IOTCP_Connect`函数的第三个参数即可：

```C
/**
 * 心跳数据委托
 **/
void get_heartbeat_data(void on_callback(DataPacket *)) {
    // TODO: 调用 on_callback 传入需随心跳一同上报的数据包
    
}
```

如果用户不想知道连接是否已经建立，也不想随心跳携带数据，则`IOTCP_Connect`函数的第 2、3 两个参数可以传`NULL`：

```C
IOTCP_Connect(identity, NULL, NULL);
```

另外，在建立连接前，请确保该设备的 IMEI 信息已经在 IOTCP 物联网平台内注册（心跳间隔也是在注册时确定），否则物联网平台会认为该设备未被许可，SDK 则会启动 30 分钟一次的重连尝试机制。当设备因未许可被服务端拒绝时，用户可以在 [错误回调](/v2/initialization.html#错误回调) 内收到以下信息：

错误码 | 错误信息
-|-
`IOTCP_ERRCODE_HANDSHAKE` | `device is not registed`

## 消息上报（Client -> Server）

虽然在心跳内也可以做一些信息上报，但心跳的时间间隔是固定了，用户无法在程序运行中动态修改。所以心跳只能携带一些普通的状态信息，对一些实时事件的上报需求就很难满足，而本接口便为此而生：

```C
/**
 * 消息上报（适用于实时事件）
 * @param level 事件的重要级别（0：普通，1：重要，2：紧急）
 * @param packet 待上报的数据包
 * @param device_id 设备ID（非组网模式下始终传 NULL）
 **/
void IOTCP_Report(
    uint8_t level,
    DataPacket* packet,
    DeviceID *device_id
);
```

### 事件重要级别 

函数`IOTCP_Report`的第 1 个参数`level`表示此次事件的重要级别，在 IOTCP.h 中定义了以下三种级别:

```C
#define IOTCP_REPORT_LEVEL_NORMAL     0    // 上报级别：普通
#define IOTCP_REPORT_LEVEL_IMPORTANT  1    // 上报级别：重要
#define IOTCP_REPORT_LEVEL_URGENT     2    // 上报级别：紧急
```

* 普通：SDK 只做一次上报，无到达确认与重传机制
* 重要：SDK 每 60 秒做一次重传，直到确认消息已达
* 紧急：SDK 每 10 秒做一次重传，直到确认消息已达

当事件的重要级别为`重要`或`紧急`时，若在确认之前已做多次重传或设备即将掉电，则 SDK 会持久化（存入持久化存储器）该事件，待设备上电并恢复网络后再做重传。另外 SDK 还会自动记录事件的发生时间（即调用`IOTCP_Report`函数的时间），在消息上送时会将时间信息一同发给服务器。

函数`IOTCP_Report`的第 2 个参数`packet`为待上报的数据包，用户可以和后端约定，在`packet`内定义自己的一套指令集。

另外，函数`IOTCP_Report`还可以**无视当前的网络连接状态**，即只要有相应的事件发生，都可以直接调用它，无需判断当前的网络和连接状态，SDK 会自动处理这些问题。

## 资源请求（Client -> Server -> Client）

有时设备会向服务器发起请求以索要某些信息，此时简单的 [消息上报](/v2/sdk-apis.html#消息上报（client-server）) 就难以胜任了（配合 [服务端推送](/v2/sdk-apis.html#服务端推送监听（server-client）) 虽然也能够实现，但需要自定义上报和推送的指令并相互配合，不够直接了当），而本接口便为此而生：

```C
/**
 * 资源请求（应答模式）
 * @param packet 请求数据包（用户可以自定义指令）
 * @param on_response 响应回调函数，有两个形参，分别为：
 *      第 1 个形参：响应状态码（0：成功响应，1：请求超时，2：请求栈溢出），
 *      第 2 个形参：响应数据包（服务器应答的信息）
 * @param timeout_seconds 超时时间（单位：秒）
 * @param device_id 设备ID（非组网模式下始终传 NULL）
 **/
void IOTCP_Request(
    DataPacket* packet,
    void (*on_response)(uint8_t, DataPacket*),
    uint8_t timeout_seconds,
    DeviceID *device_id
);
```

该函数同样可以**无视当前的网络连接状态**，但是再请求发起前，用户需要在自己的工程内定义个响应回调函数来作为`IOTCP_Request`的第 2 个参数：

```C
/**
 * 资源请求响应回调
 * @param status 响应状态码（0：成功响应，1：请求超时，2：请求栈溢出）
 * @param packet 响应数据包（服务器应答的信息，方法执行结束时自动释放）
 **/
void on_response(uint8_t status, DataPacket* packet) {
    if (status == IOTCP_RESPONSE_SUCCESS) {
        // TODO: 成功响应处理

    } else {
        // TODO: 请求未成功
        
    }
}
```

响应回调的第一参数是响应的状态码，在 IOTCP.h 中有以下定义：

```C
#define IOTCP_RESPONSE_SUCCESS              0   // 成功响应
#define IOTCP_RESPONSE_TIMEOUT              1   // 请求超时
#define IOTCP_RESPONSE_STACKFULL            2   // 请求栈溢出
```

SDK 默认 **支持 10 个请求同时** 执行，当有第 11 个请求开始并且前 10 个请求都未结束时，则最早的一个请求会因为 **请求栈溢出** 而提前结束。

另外，在一个应用中可能会有多种资源请求，这时就需要用户在自己的工程内定义多个响应回调函数了，在不同的资源请求的地方使用对应的响应回调函数名作为`IOTCP_Request`的第 2 个入参即可。

## 服务端推送监听（Server -> Client）

上述两个接口都是客户端主动发起的信息交互，但有时需要从服务端主动下发指令来控制设备，此时就用到了 SDK 的服务端推送监听接口了：

```C
/**
 * 监听服务端主动下发的透传消息
 **/
void IOTCP_Listen(
    // 服务器下行透传回调函数，形参分别分：
    //        消息ID，
    //        消息数据包（回调执行完自动释放），
    //        设备ID（非组网模式下，始终传 NULL）
    // 返回值
    //        1: 表示 用户将调用 IOTCP_Response 函数手动回复该信息
    //        0: 表示 由 SDK 自动回复该信息（回复空数据包）
    uint8_t (*on_message)(msg_id_t, DataPacket*, DeviceID*)
);
```

在调用`IOTCP_Listen`之前，显然用户需要先在自己的工程内定义一个下行透传回调函数，用来接收服务器下发的数据：

```C
/**
 * 监听服务端主动下发的透传消息
 * @param msg_id 消息ID
 * @param packet 数据包（回调执行完自动释放）
 * @param device_id 设备ID（非组网模式下，始终传 NULL）
 * @return IOTCP_ACK_MANUAL: 表示 用户将调用 IOTCP_Response 函数手动回复该信息，
 *         IOTCP_ACK_AUTO: 表示 由 SDK 自动回复该信息（回复空数据包）
 **/
uint8_t on_message(msg_id_t msg_id, DataPacket* packet, DeviceID* device_id) {
    // TODO: 按与后端约定的规则解析 packet，进行逻辑处理



    return IOTCP_ACK_AUTO;  // 或者 IOTCP_ACK_MANUAL
}
```

然后将这个函数的函数名，作为参数传入`IOTCP_Listen`函数。另外上文提到的`IOTCP_ACK_AUTO`和`IOTCP_ACK_MANUAL`是在 IOTCP.h 中定义的常量：

```C
#define IOTCP_ACK_AUTO           0     // 由 SDK 自动确认消息已收
#define IOTCP_ACK_MANUAL         1     // 手动确认消息已收（调用 IOTCP_Response 函数）
```

当用户在`on_message`函数处理完服务端下发的指令后，如果不需要响应业务指令给服务端，则可以直接返回一个`IOTCP_ACK_AUTO`，此时 SDK 会自动回复一条空数据包给服务器以告知消息已收到，如果用户需要亲自回复一些业务数据给服务端，则可以返回`IOTCP_ACK_MANUAL`，然后使用下面的`IOTCP_Response`函数进行手动回复。

### 消息 ID

上文提到的`msg_id`即是一个消息ID，它用来作为消息在某一时间范围内的唯一标识，也是消息收发确认的依据。

## 响应服务端请求（Client -> Server）

上文提到，当用户接收到服务端推送时，如果服务端还期待客户端响应一些有效的业务数据回去的话，那么用户需要在推送监听回调函数`on_message`中返回一个`IOTCP_ACK_MANUAL`，然后调用`IOTCP_Response`函数来做响应：

```C
/**
 * 应答服务器（下行透传的应答）
 * @param msg_id 消息ID（在推送监听回调中接收到的消息ID，原值带回，
 *          用以标识回复的是哪条消息）
 * @param packet 响应的业务数据包（如果是 malloc 出来的，用户得自己释放）
 * @param device_id 设备ID（非组网模式下，始终传 NULL）
 **/
void IOTCP_Response(
    msg_id_t msg_id,
    DataPacket* packet,
    DeviceID *device_id
);
```

## 时间驱动

从上文提及的自动定时心跳、自动定时重连重发功能中，我们已经知道 SDK 应具有计时的能力，但为了 SDK 的可移植性，SDK 也并不依赖特定的操作系统，那么 SDK 是如何拥有计时的能力的呢？那便是本接口的作用了：

```C
/**
 * 使用者应周期性的调用该接口，来告诉 SDK 时间的流逝
 * @param delt 为现在距上一次调用该方法的时间差（单位：毫秒）
 **/
void IOTCP_UpdateTime(uint16_t delt);
```

使用者应周期性的调用该接口，来告诉 SDK 时间的流逝，理论上该方法调用的频率越高越好，但考虑到单片机的性能，我们建议 50ms 调用一次。

需要注意的是，在`IOTCP_UpdateTime`方法传参时，应使用当前时间减去上一次调用时的时间得出的差值，不应传入一个固定的数值，以免产生累计误差。

## 掉电通知

为了确保消息的完整可靠，SDK 会在掉电时持久化设备当前的状态信息，因此当用户检测到设备掉电时，应调用该方法通知 SDK：

```C
/**
 * 当用户检测到设备掉电时，应调用该方法通知 SDK
 **/
void IOTCP_PowerDown(void);
```

<br/>
