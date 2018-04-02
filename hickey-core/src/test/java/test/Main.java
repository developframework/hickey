package test;

import com.github.developframework.hickey.core.HickeyTerminal;
import com.github.developframework.toolkit.http.response.HttpResponse;
import com.github.developframework.toolkit.http.response.SimpleHttpResponseBodyProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiuzhenhao
 */
public class Main {

    public static void main(String[] args) {
        HickeyTerminal hickeyTerminal = new HickeyTerminal("/hickey-template.xml");
        hickeyTerminal.useKite("/kite/kite-student.xml");
        hickeyTerminal.start();

        Map<String, Object> data = new HashMap<>();
        data.put("otherParam", "xxx");
        Student student = new Student(1, "Peter");
        data.put("student", student);

        HttpResponse<SimpleHttpResponseBodyProcessor> httpResponse = hickeyTerminal.touch("groupA", "interface-add-student", data);
        System.out.println(httpResponse.getHttpStatus());
        httpResponse.getHeaders().forEach(System.out::println);
        System.out.println(httpResponse.getBodyProcessor().getContent());
    }
}
