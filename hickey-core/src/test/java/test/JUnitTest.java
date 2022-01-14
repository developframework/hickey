package test;

import com.github.developframework.hickey.core.Hickey;
import org.junit.Test;

/**
 * @author qiushui on 2022-01-06.
 */
public class JUnitTest {

    @Test
    public void test() {
        final Hickey hickey = new Hickey();
        final ApiEndpoint apiEndpoint = hickey.load(ApiEndpoint.class);
        final String json = apiEndpoint.auth("1", "2");
        System.out.println(json);
    }
}
