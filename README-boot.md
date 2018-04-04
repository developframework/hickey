# hickey-spring-boot-starter

hickey结合spring-boot使用，完成自动装配HickeyTerminal

```xml
<dependency>
    <groupId>com.github.developframework</groupId>
    <artifactId>hickey-spring-boot-starter</artifactId>
    <version>${version.hickey}</version>
</dependency>
```

使用`@EnableHickey` 注解开启

```java
@SpringBootApplication
@EnableKite
@EnableHickey
public class TestApplication {
    
    @Autowired
    private HickeyTerminal hickeyTerminal;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
```

在application.properties或application.yml中可以配置

```properties
# 配置文件扫描路径
hickey.locations = #默认值 classpath*:hickey/*.xml
# 是否开启使用Kite
hickey.useKite = # 默认值 true
# http请求连接超时
hickey.connectTimeout = # 默认值 5000毫秒
# http请求读取超时
hickey.readTimeout = # 默认值 5000毫秒
# 代理服务器类型
hickey.proxy.type = # 可选值  DIRECT，HTTP，SOCKS
# 代理服务器主机地址
hickey.proxy.host = 
# 代理服务器主机端口
hickey.proxy.port =
```

