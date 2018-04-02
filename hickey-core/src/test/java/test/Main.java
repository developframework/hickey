package test;

import com.github.developframework.hickey.core.HickeyTerminal;
import com.github.developframework.toolkit.http.response.HttpResponse;
import com.github.developframework.toolkit.http.response.SimpleHttpResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiuzhenhao
 */
public class Main {

    public static void main(String[] args) {
        HickeyTerminal hickeyTerminal = new HickeyTerminal("/hickey-template.xml", "/hickey-template2.xml");
        hickeyTerminal.useKite("/kite/kite-student.xml");
        hickeyTerminal.start();

        Map<String, Object> data = new HashMap<>();
        data.put("otherParam", "xxx");
        data.put("id", 1);
        Student student = new Student(1, "Peter");
        data.put("student", student);

        try {
            HttpResponse<SimpleHttpResponseBody> response = hickeyTerminal.touch("groupA", "interface-delete-student", data, SimpleHttpResponseBody.class);
            System.out.println("响应结果：");
            System.out.println(response.getBody().getBodyContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
