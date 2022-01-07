package test;

import com.github.developframework.hickey.core.Hickey;
import org.junit.Test;

import java.util.Map;

/**
 * @author qiushui on 2022-01-06.
 */
public class JUnitTest {

    @Test
    public void test() {
        final Hickey hickey = new Hickey();
        final ApiEndpoint apiEndpoint = hickey.load(ApiEndpoint.class);
        final String json = apiEndpoint.login(
                Map.of(
                        "account", "18324370424",
                        "password", "Sugar0424#",
                        "type", "STAFF",
                        "platform", "PC"
                )
        );
        System.out.println(json);
    }
}
