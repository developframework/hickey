package test;

import com.github.developframework.hickey.core.annotations.Endpoint;
import com.github.developframework.hickey.core.annotations.PairMap;
import com.github.developframework.hickey.core.annotations.Request;
import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.HttpMethod;

import java.util.Map;

/**
 * @author qiushui on 2022-01-06.
 */
@Endpoint("http://localhost:10000")
public interface ApiEndpoint {

    @Request(
            label = "测试一把",
            method = HttpMethod.POST,
            path = "system-service/login",
            contentType = ContentType.APPLICATION_JSON
    )
    String login(@PairMap Map<String, Object> map);
}
