package test;

import lombok.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qiuzhenhao
 */
@Data
public class Student {
    // 编号
    private int id;
    // 学生名称
    private String name;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
