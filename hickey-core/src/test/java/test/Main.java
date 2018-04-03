package test;

import com.github.developframework.hickey.core.HickeyTerminal;
import com.github.developframework.toolkit.http.Option;
import com.github.developframework.toolkit.http.response.HttpResponse;
import com.github.developframework.toolkit.http.response.SimpleHttpResponseBodyProcessor;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiuzhenhao
 */
public class Main {

    public static void main(String[] args) {

        //加载配置文件
        HickeyTerminal hickeyTerminal = new HickeyTerminal("/hickey-demo.xml");
        Option option = hickeyTerminal.getClient().getOption();
        option.setProxy(Proxy.Type.HTTP, "localhost", 1080);
        option.setConnectTimeout(10000);
        option.setReadTimeout(10000);
        hickeyTerminal.start();

        // 准备数据
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("name", "Peter");
        data.put("classId", 1);
        data.put("headerAccept", "application/json");

        // 发起请求
        HttpResponse<SimpleHttpResponseBodyProcessor> httpResponse = hickeyTerminal.touch("student-group", "test-get-student", data);

        // 查看响应信息
        httpResponse.getHeaders().forEach(System.out::println);
        String content = httpResponse.getBodyProcessor().getContent();
        System.out.println(content);
    }

}
