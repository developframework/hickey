package test;

import com.github.developframework.hickey.core.annotations.Endpoint;
import com.github.developframework.hickey.core.annotations.Pair;
import com.github.developframework.hickey.core.annotations.Request;
import com.github.developframework.hickey.core.structs.ContentType;
import com.github.developframework.hickey.core.structs.HttpMethod;

@Endpoint(value = "http://localhost:6667", fallback = ApiEndpoint.ApiEndpointFallback.class)
public interface ApiEndpoint {

    @Request(
            label = "测试一把",
            method = HttpMethod.POST,
            path = "test",
            contentType = ContentType.APPLICATION_JSON
    )
    String auth(@Pair("a") String a, @Pair("b") String b);

    class ApiEndpointFallback implements ApiEndpoint {

        @Override
        public String auth(String a, String b) {
            return null;
        }
    }
}
