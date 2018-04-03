# Hickey

Hickey是一个可配置请求信息的Http发送器。

```xml
<dependency>
    <groupId>com.github.developframework</groupId>
    <artifactId>hickey-core</artifactId>
    <version>${version.hickey}</version>
</dependency>
```

### 0. 介绍

通过hickey的xml配置文件描述请求信息，xml基本格式如下：

```xml
<hickey-configuration xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xmlns="https://github.com/developframework/hickey/schema"
                      xsi:schemaLocation="https://github.com/developframework/hickey/schema hickey-configuration-0.1.xsd">
    <!-- 远程接口组 -->
    <remote-interfaces group-name="interfaces-group">
        <!-- 接口组域名前缀 -->
        <domain-prefix></domain-prefix>
        <!-- 远程接口 -->
        <remote-interface id="interface-id">
            <!-- 请求信息 -->
            <request method="GET">
                <!-- 请求地址 -->
                <url></url>
                <!-- url参数列表 -->
                <parameters>
                    <parameter name=""></parameter>
                </parameters>
                <!-- 接口描述 -->
                <description></description>
                <!-- 请求头信息列表 -->
                <headers>
                    <header name=""></header>
                </headers>
                <!-- 请求表单 -->
                <form type="x-www-form-urlencoded">
                    <property name=""></property>
                </form>
                <!-- 请求体（与请求表单不能同时使用） -->
                <body type="JSON">
                    <!-- 请求体内容提供器 -->
                    <provider>
                        <default-provider></default-provider>
                    </provider>
                </body>
            </request>
        </remote-interface>
    </remote-interfaces>
</hickey-configuration>
```

+ 每份xml配置文件中可配置多个`<remote-interfaces>`，每个`<remote-interfaces>`中可以配置多个`<remote-interface>`。并且Hickey框架可以设置多份xml配置文件。
+ 其中的`<form>` 和`<body>`不能共存，只能选其一使用。

### 1. 最简示例

hickey-demo.xml :

```xml
<!-- hickey-configuration 节点忽略 -->
<remote-interfaces group-name="student-group">
    <domain-prefix>http://localhost:8080</domain-prefix>
    <remote-interface id="test-get-student">
        <request method="GET">
            <url>/students/@{id}</url>
        </request>
    </remote-interface>
</remote-interfaces>
```

Demo.java

```java
//加载配置文件
HickeyTerminal hickeyTerminal = new HickeyTerminal("/hickey-demo.xml");
hickeyTerminal.start();

// 准备数据
Map<String, Object> data = new HashMap<>();
data.put("id", 1);

// 发起请求
HttpResponse<SimpleHttpResponseBodyProcessor> httpResponse = hickeyTerminal.touch("student-group", "test-get-student", data);

// 查看响应信息
httpResponse.getHeaders().forEach(System.out::println);
String content = httpResponse.getBodyProcessor().getContent();
System.out.println(content);
```

代码申明了使用student-group组里的test-get-student请求接口信息。

得到远程响应内容：

```
Transfer-Encoding: chunked
Content-Type: application/json;charset=UTF-8
Date: Tue, 03 Apr 2018 14:20:44 GMT
null: HTTP/1.1 200
{"id":1,"name":"Peter"}
```

### 2. 功能

#### 2.1 值替换@{} 

xml配置文件可以使用@{xxx}符号获取传入数据。

```xml
<request method="GET">
    <url>/students/@{id}</url>
    <parameters>
        <parameter name="name">@{name}</parameter>
        <parameter name="class_id">@{classId}</parameter>
    </parameters>
    <headers>
        <header name="Accept">@{headerAccept}</header>
    </headers>
</request>
```

```java
Map<String, Object> data = new HashMap<>();
data.put("id", 1);
data.put("name", "Peter");
data.put("classId", 1);
data.put("headerAccept", "application/json");

hickeyTerminal.touch("", "", data);
```

当然这里的data不仅限于使用Map，你可以使用任何Bean来包装参数。

@{xxx.yyy} 符号内的取值表达式实现于独立项目[expression](https://github.com/developframework/expression)

#### 2.2 body内容提供器

Hickey内置两种内容提供器：

+ `<default-provider>` 默认的提供器

```xml
<body type="TEXT">
    <provider>
        <default-provider>
            content: @{someText}
        </default-provider>
    </provider>
</body>
```

+ `<kite-provider>` 使用独立框架Kite完成内容装配

```xml
<body type="JSON">
    <provider>
        <kite-provider namespace="kite-student" template-id="student-detail"/>
    </provider>
</body>
```

关于Kite的配置请查阅  [Kite](https://github.com/developframework/kite)

自定义内容提供器：

需要继承`BodyProvider`接口

```java
public interface BodyProvider {
    /**
     * 配置文件xml节点的名称
     */
    String xmlElementQName();

    /**
     * 定义配置文件解析过程
     */
    void parseHandle(HickeyConfiguration hickeyConfiguration, RemoteInterfaceRequestBody requestBody, Element element);

    /**
     * 描述如何提供内容
     */
    String provide(Object data);
}
```

并且在`hickeyTerminal.start()`之前添加

```java
BodyProvider myBodyProvider = new MyBodyProvider();
hickeyTerminal.getHickeyConfiguration().addBodyProvider(myBodyProvider);
hickeyTerminal.start();
```

#### 2.3 对Http发送器设置

```java
Option option = hickeyTerminal.getClient().getOption();
// 设置代理服务器
option.setProxy(Proxy.Type.HTTP, "localhost", 1080);
// 设置连接超时时间
option.setConnectTimeout(10000);
// 设置读取超时时间
option.setReadTimeout(10000);
```

#### 2.4 请求响应内容的处理

Hickey内置默认的处理器`SimpleHttpResponseBodyProcessor`，实现对响应内容转成文本。

自定义处理器需要实现接口`HttpResponseBodyProcessor<T, Y>`，有下面三个待实现的方法：

```java
/**
 * 实现如何判断请求成功
 */
protected abstract boolean checkSuccess(HttpURLConnection connection) throws IOException;

/**
 * 实现如何解析请求成功时的Body
 */
protected abstract T parseBodyContent(HttpURLConnection connection) throws IOException;

/**
 * 实现请求失败时的处理
 */
protected abstract Y error(HttpURLConnection connection) throws IOException;
```

在调用touch发送请求时设置使用的处理器

```java
HttpResponse<MyHttpResponseBodyProcessor> httpResponse = hickeyTerminal.touch(group, interfaceId, data, MyHttpResponseBodyProcessor.class);
```





