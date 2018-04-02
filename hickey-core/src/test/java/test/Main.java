package test;

import com.github.developframework.hickey.core.HickeyTerminal;

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
        data.put("parameter", "xxx");
        Student student = new Student(1, "Peter");
        data.put("student", student);
        hickeyTerminal.touch("interface-1", data);
    }
}
