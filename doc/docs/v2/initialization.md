---
description: IOT 物联网 IOTCP 协议 SDK 集成
---

# SDK 初始化

当在工程中集成并引入 SDK 头文件（iotcp.h）后，接下来的第一步便是初始化 SDK，下面给出的是 IOTCP.h 头文件中声明的初始化函数：

```C
/**
 * 初始化 SDK
 * @return 0 表示 初始化成功 其它 表示初始化失败，参见错误码
 **/
uint16_t IOTCP_Init(

    uint8_t mode,                        // 类型 0：非组网模式，1：组网模式

    uint32_t storage_min_address,        // 持久化存储器的可用最小地址
    uint32_t storage_max_address,        // 持久化存储器的可用最大地址

    // 写入数据到持久化存储器，形参为：写入到存储器中的起始地址，数据长度、数据内容指针
    // 返回 实际写入的数据长度
    uint16_t (*write_into_storage)(uint32_t, uint16_t, uint8_t*),

    // 从持久化存储器中读取数据，形参为：读取存储器中的起始地址，加载数据长度，数据的接收指针
    // 返回 实际读取的数据长度
    uint16_t (*read_from_storage)(uint32_t, uint16_t, uint8_t*),

    // 连接 TCP 服务器，方法的参数分别为：TCP地址，连接结果回调
    // 连接结果回调 的 uint16_t 形参代表 连接错误码: 0 表示连接未出错，其它为错误码（具体由模组返回）
    //（当连接成功时，下方的 send_by_tcp 方法应能够发送数据）
    void (*connect_tcp)(TcpAddress*, void (*on_connect_result)(uint16_t)),

    // 以 TCP 方式立即发送信息给服务器，方法的参数分别为：待发送的TCP数据包，发送结果回调
    // 发送结果回调 的 uint16_t 形参代表：0 发送未出错，其它为错误码（具体由模组返回）
    void (*send_by_tcp)(DataPacket*, void (*on_sent_result)(uint16_t)),

    // 断开当前的 TCP 连接，当断开成功后，应当调用 on_disconnected 回调
    void (*disconnect_tcp)(void (*on_disconnected)()),

    // 以 HTTP GET 方式请求服务器，方法的参数分别为：请求ID、服务器地址、响应回调函数
    // 其中 服务器地址 为拼接了查询参数的完整地址，例如：http://node-admin.eiotyun.com/load-balance/index?imei=123456&protocol=IOTCP-2.0&encryption=SIM2
    // 另外，响应回调函数的形参分别为：请求ID、请求响应
    void (*get_by_http)(uint8_t, HttpAddress*, void (*on_response)(uint8_t, HttpResponse*)),

    // 网络状态查询，形参为：查询结果回调
    void (*get_net_status)(void (*on_callback)(NetStatus*)),

    // 型号查询，形参为：类型（0：模块，1、主板，2：其他）、设备ID（非组网模式，该参数始终为 NULL）、回调函数
    // 回调函数的形参分别为：类型（原值带回），设备ID（原值带回），设备型号（若不支持该查询，可以传入 NULL）
    void (*get_version)(uint8_t, DeviceID*, void (*on_callback)(uint8_t, DeviceID*, DeviceVer*)),

    // 错误回调，回调方法的参数为错误码、错误信息
    void (*on_error)(uint16_t, const char *), 

    // 基准时间戳（单位：秒）：必须小于等于设备运行时的时间，但越接近越好，用于作为系统相对时间的参考点
    // 该项配置在确定后请谨慎修改，最好不变，除非设备已运行百年
    uint64_t base_timestamp
);
```

该函数名为`IOTCP_Init`，它共有 13 个形参，其中有 9 个是函数指针，接下来本文会对每一个参数进行详细讲解。

## 模式选择

初始化函数`IOTCP_Init`的第一个参数为`mode`，它代表 SDK 的使用模式，可以传入以下两个值（在 IOTCP.h 中定义）：

```C
#define IOTCP_MODE_NORMAL       0           // 常规模式
#define IOTCP_MODE_CELLULAR     1           // 组网模式
```

* 常规模式，一个设备对应一个通讯模组（或通讯模块）
* 组网模式，多个设备共用一个通讯网关（含通讯模组）

## 持久化存储器

因为 SDK 在运行过程中会向持久化存储器（比如外挂一颗 FLASH 芯片）内存储或读取某些信息，所以在 SDK 初始化的时候用户得告诉它持久化存储器的相关信息，即`IOTCP_Init`的第 2 到第 5 四个参数。

其中`storage_min_address`与`storage_max_address`分别代表用户在持久化存储器内给 SDK 分配的最小存储地址与最大存储地址，一旦指定，SDK 就只会在这个区间内读写，但需要注意的是，这个区间不应小于 0x21FF 个字节。

接下来的`write_into_storage`与`read_from_storage`都是函数指针，它用来告诉 SDK 如何对持久化存储器进行读写，所以在 SDK 初始化前，用户得在自己的工程内定义这两个函数：

```C
/**
 * 将数据写入到持久化存储器
 * @param address_start 开始写的地址
 * @param length 待写入的数据长度
 * @param data 待写入的数据指针
 * @return 实际写入的数据长度
 **/
uint16_t write_into_storage(uint32_t address_start, uint16_t length, uint8_t* data) {
    // TODO：操作持久化存储器（比如 FLASH）

}

/**
 * 从持久化存储器读取数据
 * @param address_start 开始读的地址
 * @param length 需读取的数据长度
 * @param data 读出数据的装载指针
 * @return 实际读出的数据长度
 **/
uint16_t read_from_storage(uint32_t address_start, uint16_t length, uint8_t* data) {
    // TODO：操作持久化存储器（比如 FLASH）

}
```

然后将这两个函数的函数名，作为参数传入`IOTCP_Init`函数。

## TCP 操作接口

初始化函数`IOTCP_Init`的第 6、7、8 三个参数也是函数指针，它们是用来告诉 SDK 如何进行 TCP 操作，所以在 SDK 初始化前，用户同样得在自己的工程内定义这三个函数：

```C
/**
 * 连接 TCP 服务器
 * @param tcp_address TCP 服务器地址（IOTCP.h 中定义的结构体指针）
 * @param on_connect_result 连接结果回调函数（该函数指针由 SDK 传入，无需用户定义，它的 uint16_t 形参表示连接结果，0 表示成功，其它为错误码，具体由模组返回）
 **/
void connect_tcp(TcpAddress* tcp_address, void (*on_connect_result)(uint16_t)) {
    // TODO：在该方法内去连接 TCP 服务器，无论连接成功或失败，用户都要调用 on_connect_result 函数指针告诉 SDK 连接结果
    // 因为在该方法内需要访问外部的通讯模组，可能并不能立即知道连接结果，所以用户可能需要先把 on_connect_result 记录到一个全局变量里，待模组返回连接结果后再调用它通知 SDK
    // 当连接成功建立后，用户的记录当前的连接 ID，以便后续的数据发送使用（下面两个方法会用到）

}

/**
 * 向 TCP 服务器发送数据包
 * @param data_packet 带发送的数据包（IOTCP.h 中定义的结构体指针）
 * @param on_sent_result 发送结果回调函数（该函数指针由 SDK 传入，无需用户定义，它的 uint16_t 形参表示发送结果，0 表示成功，其它为错误码，具体由模组返回）
 **/
void send_by_tcp(DataPacket* data_packet, void (*on_sent_result)(uint16_t)) {
    // TODO：在该方法内使用上一个方法建立的连接去发送数据包，无论发送成功或失败，用户都要调用 on_sent_result 函数指针告诉 SDK 发送结果
    // 注意：只有告诉 SDK 本包数据的发送结果后，SDK 才考虑重发或去发下一包数据！！！
    // 因为在该方法内需要访问外部的通讯模组，可能并不能立即知道发送结果，所以用户可能需要先把 on_sent_result 记录到一个全局变量里，待模组返回发送结果后再调用它通知 SDK

}

/**
 * 断开当前的 TCP 连接（当多次发送结果为失败，SDK 就会触发重连机制，先调用该函数断开连接）
 * 当连接断开成功后，用户应调用 on_disconnected 回调告诉 SDK
 * @param on_disconnected 发送结果回调函数（该函数指针由 SDK 传入，无需用户定义）
 **/
void disconnect_tcp(void (*on_disconnected)()) {
    // TODO：在该方法内去断开已建立的 TCP 连接

}
```

然后将这三个函数的函数名，作为参数传入`IOTCP_Init`函数。另外以上提及的`DataPacket`与`TcpAddress`是在 IOTCP.h 中定义的结构体，如下：

```C
// 数据包
typedef struct {
    uint16_t length;                // 数据长度
    uint8_t *data;                  // 数据起始指针
} DataPacket;

// TCP 地址
typedef struct {
    uint8_t length;           // host 的 长度
    uint8_t *host;            // 服务器 IP 起始指针
    uint16_t port;            // TCP 端口号
} TcpAddress;
```

## HTTP GET 接口

初始化函数`IOTCP_Init`的第 9 个参数也是一个函数指针，它是用来告诉 SDK 如何进行 HTTP GET 请求（用于客户端的负载均衡服务选择），所以在 SDK 初始化前，用户同样得在自己的工程内定义这个函数：

```C
/**
 * 实现 HTTP GET 请求
 * @param request_id 请求 ID（当请求结束后作为形参算入 on_response 回调函数）
 * @param http_address HTTP 请求地址（IOTCP.h 中定义的结构体指针）
 * @param on_response 请求结果回调函数（该函数指针由 SDK 传入，无需用户定义，它的第一个形参为 请求 ID，第二个形参为 HTTP 响应结果）
 **/
void get_by_http(uint8_t request_id, HttpAddress* http_address, void (*on_response)(uint8_t, HttpResponse*)) {
    // TODO：在该方法内做 HTTP GET 请求，当请求有响应时，用户需要调用 on_response 函数指针告诉 SDK 请求结果
    // 因为在该方法内需要访问外部的通讯模组，可能并不能立即知道发送结果，所以用户可能需要先把 on_response 记录到一个全局变量里，待模组返回请求结果后再调用它通知 SDK

}
```

然后将这个函数的函数名，作为参数传入`IOTCP_Init`函数，另外以上提及的`HttpAddress`与`HttpResponse`是在 IOTCP.h 中定义的结构体，如下：

```C
// HTTP 地址
typedef struct {
    uint8_t length;           // address 的长度
    uint8_t *address;         // address 起始指针
} HttpAddress;

// TCP 地址
typedef struct {
    uint16_t status;          // HTTP 状态码
    DataPacket *body;         // 报文体
} HttpResponse;
```

## 网络状态

初始化函数`IOTCP_Init`的第 10 个参数也是一个函数指针，它是用来告诉 SDK 当前的网络状态，所以在 SDK 初始化前，用户同样需要在自己的工程内定义这个函数：

```C
/**
 * 获取网络状态 
 * @param on_callback 回调函数（该函数指针由 SDK 传入，无需用户定义)
 **/
void get_net_status(void (*on_callback)(NetStatus*))
{
	NetStatus net_status;
	net_status.type = "4G";     // 查询通讯模组获取
	net_status.rssi = 30;       // 查询通讯模组获取
	net_status.err_rate = 0;    // 查询通讯模组获取 
	on_callback(&net_status);
}
```

然后将这个函数的函数名，作为参数传入`IOTCP_Init`函数，另外以上提及的`NetStatus`是在 IOTCP.h 中定义的结构体，如下：

```C
// 网络状态
typedef struct {
    char *type;               // 网络类型，固定长度为 2 的字符串
    uint8_t rssi;             // 信号强度
    uint8_t err_rate;         // 误码率
} NetStatus;
```

其中网络类型可以有以下取值：

* 2G：2G 网络
* 3G：3G 网络
* 4G：4G 网络
* 5G：5G 网络
* NB：NB 网络
* WF：Wifi 网络

## 版本查询

初始化函数`IOTCP_Init`的第 11 个参数还是一个函数指针，它是用来执行版本查询操作，所以在 SDK 初始化前，用户同样需要在自己的工程内定义这个函数：

```C
/**
 * 版本查询
 * @param type 部件类型（0：查询模板的版本、1：查询主板的版本、其它：用户自定义）
 *      IOTCP_PartType_NetModule：模块
 *      IOTCP_PartType_MainBoard：主板
 * @param device_id 子设备ID（非组网模式，该参数始终为 NULL）
 * @param on_callback 回调函数（该函数指针由 SDK 传入，无需用户定义）它的
 *          第一个形参为： 查询类型（原值带回），
 *          第二个形参为： 子设备ID（原值带回），
 *          第三个形参为： 设备版本（若不支持该类型的查询，可以传入 NULL）
 **/
void get_version(uint8_t type, DeviceID* device_id, void (*on_callback)(uint8_t, DeviceID*, DeviceVer*)) {
    if (type == IOTCP_PartType_NetModule) {
        // TODO：查询模块的版本，查出后调用 on_callback 回调函数通知 SDK

    } else
    if (type == IOTCP_PartType_MainBoard) {
        // TODO：查询主板的版本，查出后调用 on_callback 回调函数通知 SDK

    } else {
        // 其它用户自定义的版本查询（与后端协商好查询类型即可）
        // 若不需要支持其它版本查询，可直接回调一个 NULL 版本：
        on_callback(type, device_id, NULL);
    }
}
```

然后将这个函数的函数名，作为参数传入`IOTCP_Init`函数，另外以上提及的`DeviceID`与`DeviceVer`是在 IOTCP.h 中定义的结构体，如下：

```C
// 设备ID（子设备）
typedef struct {
    uint8_t length;           // 设备ID 长度
    uint8_t *data;            // 设备ID 数据指针
} DeviceID;

// 设备版本
typedef struct {
    uint8_t h_length;         // 硬件版本长度
    char   *h_version;        // 硬件版本
    uint8_t s_length;         // 软件件版本长度
    char   *s_version;        // 软件版本
} DeviceVer;
```

## 错误回调

初始化函数`IOTCP_Init`的第 12 个参数同样是一个函数指针，它是用来通知用户 SDK 在执行过程中可能发生的问题，所以在 SDK 初始化前，用户也需要在自己的工程内定义这个函数：

```C
/**
 * 错误回调
 * @param code 错误码
 * @param message 错误信息字符串
 **/
void on_error(uint16_t code, const char *message) {
    // TODO: 用户拿到错误码和错误信息后可输出至串口，以便调试

}
```

然后将这个函数的函数名，作为参数传入`IOTCP_Init`函数，另外在 IOTCP.h 中还定义了一些 [错误码](/v2/initialization.html#错误码) 可供参考。

## 基准时间戳

初始化函数`IOTCP_Init`的第 13 个（最后一个）是一个`uint64_t`类型的数值，它表示系统使用的基准时间戳（单位：秒）。

因为在 SDK 内部使用的都是 32 位的相对时间（计时周期约：136 年），所以 SDK 需要知道这个相对时间的参考基准点，这个基准点必须小于等于设备运行时的时间（不然时间计算出来就是负值了），但越接近越好（因为约接近当前时间，设备的剩余可用时间就越长）。

另外 SDK 定义了一个最小基准时间戳（约 2020-10-08 那天）

```C
#define IOTCP_MIN_TIMESTAMP     0x5F800000  // 最小基准时间戳（2020-10-08）
```

当用户传入的数值小于该值时，SDK 将默认使用该值。

## 错误码

初始化函数`IOTCP_Init`还有一个`uint16_t`类型的返回值（错误码），它表示的是初始化过程中发生的错误类型，在 IOTCP.h 中定义了以下一些错误码：

```C
#define IOTCP_ERRCODE_OK                    0    // 成功
#define IOTCP_ERRCODE_MODE                  1    // SDK 模式错误，只能是 0 或 1
#define IOTCP_ERRCODE_STORAGE_SPACE         2    // 存储空间小于 8703 Bytes
#define IOTCP_ERRCODE_STORAGE_METHODS       3    // 持久化存储方法未注册
#define IOTCP_ERRCODE_STORAGE_READ          4    // 持久化存储读出错
#define IOTCP_ERRCODE_STORAGE_WRITE         5    // 持久化存储写出错
#define IOTCP_ERRCODE_TCP_METHODS           6    // TCP 方法未注册
#define IOTCP_ERRCODE_TCP_PACKET            7    // TCP 包处理出错
#define IOTCP_ERRCODE_HTTP_METHODS          8    // HTTP 方法未注册
#define IOTCP_ERRCODE_HTTP_ID               9    // HTTP ID 回调错误
#define IOTCP_ERRCODE_NET_STATUS           10    // 网络状态错误
#define IOTCP_ERRCODE_VERSION_METHODS      11    // 版本查询方法未注册
#define IOTCP_ERRCODE_IDENTITY             12    // IMEI 和 ICCID 非法
#define IOTCP_ERRCODE_SCHEDULE             13    // 调度出错
#define IOTCP_ERRCODE_HANDSHAKE            14    // 握手出错
#define IOTCP_ERRCODE_INVALID_FRAME        15    // 非法数据帧
#define IOTCP_ERRCODE_LISTEN               16    // listen 方法传参错误
#define IOTCP_ERRCODE_UPGRADE              17    // 升级方法传参错误
#define IOTCP_ERRCODE_REPORT               18    // 上报错误
#define IOTCP_ERRCODE_GET_SIGNAL           19    // 获取信号错误
#define IOTCP_ERRCODE_GET_MODEL            20    // 获取型号错误
#define IOTCP_ERRCODE_REBOOT               21    // 重启方法传参错误
#define IOTCP_ERRCODE_OUTOFMEMORY          22    // 堆内存不足
```

<br/>
